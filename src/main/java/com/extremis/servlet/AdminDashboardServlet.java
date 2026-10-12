package com.extremis.servlet;

import com.extremis.dao.AdminDAO;
import com.extremis.dao.AttendanceDAO;
import com.extremis.dao.ClassDAO;
import com.extremis.dao.FacultyDAO;
import com.extremis.dao.StudentDAO;
import com.extremis.dao.TeacherAssignmentDAO;
import com.extremis.model.Admin;
import com.extremis.model.Faculty;
import com.extremis.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ClassDAO classDAO = new ClassDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentAdmin") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Admin admin = (Admin) session.getAttribute("currentAdmin");
        List<Student> allStudents = studentDAO.getAllStudents();
        List<Faculty> allFaculties = facultyDAO.getAllFaculties();

        int activeSessions = 0;
        for (Student s : allStudents) {
            if (s.getSessionToken() != null && !s.getSessionToken().trim().isEmpty()) {
                activeSessions++;
            }
        }

        int todayCheckIns = attendanceDAO.countTodayCheckIns(LocalDate.now());
        int totalClasses = classDAO.getAllClasses().size();
        int totalBatches = classDAO.getAllBatches().size();
        int totalAssignments = assignmentDAO.getAllAssignments().size();
        int totalOverrides = attendanceDAO.getOverrideAuditHistory(500).size();

        req.setAttribute("admin", admin);
        req.setAttribute("students", allStudents);
        req.setAttribute("faculties", allFaculties);
        req.setAttribute("totalStudents", allStudents.size());
        req.setAttribute("totalFaculties", allFaculties.size());
        req.setAttribute("activeSessions", activeSessions);
        req.setAttribute("todayCheckIns", todayCheckIns);
        req.setAttribute("totalClasses", totalClasses);
        req.setAttribute("totalBatches", totalBatches);
        req.setAttribute("totalAssignments", totalAssignments);
        req.setAttribute("totalOverrides", totalOverrides);

        req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentAdmin") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("reset_session".equalsIgnoreCase(action)) {
            String studentIdStr = req.getParameter("studentId");
            if (studentIdStr != null) {
                try {
                    Long studentId = Long.parseLong(studentIdStr);
                    studentDAO.clearSessionToken(studentId);
                    req.getSession().setAttribute("flashSuccess", "Device session lock cleared successfully! The student can now log in on a new device.");
                } catch (NumberFormatException ignored) {}
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
