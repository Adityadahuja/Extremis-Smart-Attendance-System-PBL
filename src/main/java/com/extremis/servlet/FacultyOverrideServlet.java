package com.extremis.servlet;

import com.extremis.dao.AttendanceDAO;
import com.extremis.dao.TeacherAssignmentDAO;
import com.extremis.model.Faculty;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Handles manual attendance overrides by authorized faculty members.
 */
@WebServlet("/faculty/override")
public class FacultyOverrideServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Faculty faculty = (session != null) ? (Faculty) session.getAttribute("currentFaculty") : null;

        if (faculty == null || !faculty.isActive()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String studentIdStr = req.getParameter("studentId");
        String dateStr = req.getParameter("date");
        String status = req.getParameter("status");
        String reason = req.getParameter("reason");
        String filterClassId = req.getParameter("classId");
        String filterBatchId = req.getParameter("batchId");
        String startDateParam = req.getParameter("startDate");
        String endDateParam = req.getParameter("endDate");
        String tabParam = req.getParameter("tab");
        String dailyDateParam = req.getParameter("dailyDate");

        String redirectParams = "";
        if (filterClassId != null && !filterClassId.isEmpty()) redirectParams += "&classId=" + filterClassId;
        if (filterBatchId != null && !filterBatchId.isEmpty()) redirectParams += "&batchId=" + filterBatchId;
        if (startDateParam != null && !startDateParam.isEmpty()) redirectParams += "&startDate=" + startDateParam;
        if (endDateParam != null && !endDateParam.isEmpty()) redirectParams += "&endDate=" + endDateParam;
        if (dailyDateParam != null && !dailyDateParam.isEmpty()) redirectParams += "&dailyDate=" + dailyDateParam;
        if (tabParam != null && !tabParam.isEmpty()) redirectParams += "&tab=" + tabParam;
        if (dateStr != null && !dateStr.isEmpty()) redirectParams += "&date=" + dateStr;

        // 1. Validation: Mandatory fields
        if (studentIdStr == null || studentIdStr.trim().isEmpty() ||
            dateStr == null || dateStr.trim().isEmpty() ||
            status == null || status.trim().isEmpty()) {
            String err = URLEncoder.encode("Missing required parameters for attendance override.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/faculty/dashboard?error=" + err + redirectParams);
            return;
        }

        // 2. Validation: Mandatory Reason
        if (reason == null || reason.trim().isEmpty()) {
            String err = URLEncoder.encode("A mandatory reason must be provided for every attendance override.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/faculty/dashboard?error=" + err + redirectParams);
            return;
        }

        Long studentId;
        LocalDate date;
        try {
            studentId = Long.parseLong(studentIdStr.trim());
            date = LocalDate.parse(dateStr.trim());
        } catch (Exception ex) {
            String err = URLEncoder.encode("Invalid student ID or date format.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/faculty/dashboard?error=" + err + redirectParams);
            return;
        }

        // 3. Security Safeguard: Server-side Authorization Check
        // Faculty members may override attendance ONLY for their assigned classes, batches, or subjects
        boolean isAuthorized = assignmentDAO.isFacultyAuthorizedForStudent(faculty.getId(), studentId);

        // Fallback check: if no explicit assignment exists in teacher_assignments table,
        // check legacy faculty branch and section match
        if (!isAuthorized) {
            String err = URLEncoder.encode("Authorization Denied: You are not assigned to this student's class or batch.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/faculty/dashboard?error=" + err + redirectParams);
            return;
        }

        // 4. Execute Override with Immutable Audit Log
        String facultyIdentifier = faculty.getName() + " (" + faculty.getFacultyId() + ")";
        boolean success = attendanceDAO.recordOverride(studentId, date, status, reason.trim(), facultyIdentifier, faculty.getId());

        if (success) {
            String msg = URLEncoder.encode("Attendance record for student successfully updated to " + status.toUpperCase() + ".", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/faculty/dashboard?success=" + msg + redirectParams);
        } else {
            String err = URLEncoder.encode("Failed to update attendance record in database.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/faculty/dashboard?error=" + err + redirectParams);
        }
    }
}
