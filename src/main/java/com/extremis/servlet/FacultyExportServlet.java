package com.extremis.servlet;

import com.extremis.dao.AttendanceDAO;
import com.extremis.dao.ClassDAO;
import com.extremis.dao.TeacherAssignmentDAO;
import com.extremis.dto.StudentAttendanceSummaryDTO;
import com.extremis.dto.StudentSessionHistoryDTO;
import com.extremis.model.ClassEntity;
import com.extremis.model.Faculty;
import com.extremis.model.TeacherAssignment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates authenticated, institutional CSV attendance export reports for faculty.
 */
@WebServlet("/faculty/export")
public class FacultyExportServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();
    private final ClassDAO classDAO = new ClassDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Faculty faculty = (session != null) ? (Faculty) session.getAttribute("currentFaculty") : null;

        if (faculty == null || !faculty.isActive()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }

        String classIdParam = req.getParameter("classId");
        String batchIdParam = req.getParameter("batchId");
        String startParam = req.getParameter("startDate");
        String endParam = req.getParameter("endDate");
        String exportType = req.getParameter("type"); // "summary" or "detailed"

        Long classId = null;
        if (classIdParam != null && !classIdParam.trim().isEmpty()) {
            try { classId = Long.parseLong(classIdParam.trim()); } catch (Exception ignored) {}
        }

        Long batchId = null;
        if (batchIdParam != null && !batchIdParam.trim().isEmpty()) {
            try { batchId = Long.parseLong(batchIdParam.trim()); } catch (Exception ignored) {}
        }

        LocalDate startDate = LocalDate.of(2026, 10, 1);
        if (startParam != null && !startParam.trim().isEmpty()) {
            try { startDate = LocalDate.parse(startParam.trim()); } catch (Exception ignored) {}
        }

        LocalDate endDate = LocalDate.of(2026, 10, 12);
        if (endParam != null && !endParam.trim().isEmpty()) {
            try { endDate = LocalDate.parse(endParam.trim()); } catch (Exception ignored) {}
        }

        // Verify class authorization if classId provided
        if (classId != null) {
            boolean authorized = false;
            List<TeacherAssignment> assignments = assignmentDAO.getAssignmentsByFacultyId(faculty.getId());
            for (TeacherAssignment ta : assignments) {
                if (classId.equals(ta.getClassId())) {
                    authorized = true;
                    break;
                }
            }
            if (!authorized) {
                // Fallback check against faculty branch
                ClassEntity ce = classDAO.getClassById(classId);
                if (ce != null && ce.getDepartment().equalsIgnoreCase(faculty.getBranch())) {
                    authorized = true;
                }
            }
            if (!authorized) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: You are not authorized for this class.");
                return;
            }
        }

        String fileName = "Extremis_Attendance_" + (exportType != null ? exportType : "Summary") + "_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter writer = resp.getWriter();
        // Write UTF-8 Byte Order Mark (BOM) for Excel compatibility
        writer.write('\uFEFF');

        if ("detailed".equalsIgnoreCase(exportType)) {
            // Detailed date-wise attendance records
            writer.println("Student ID,Roll Number,Student Name,Class,Batch,Date,Subject,Status,Check-In Time,Check-Out Time,Recording Method,Overridden,Override Reason,Overridden By");
            List<StudentAttendanceSummaryDTO> summaries = attendanceDAO.getStudentSummaries(faculty.getId(), classId, batchId, startDate, endDate, null, null);
            for (StudentAttendanceSummaryDTO student : summaries) {
                List<StudentSessionHistoryDTO> sessions = attendanceDAO.getStudentSessionHistory(student.getStudentId(), startDate, endDate, false);
                for (StudentSessionHistoryDTO s : sessions) {
                    writer.println(String.format("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                            student.getStudentId(),
                            escapeCsv(student.getRollNo()),
                            escapeCsv(student.getName()),
                            escapeCsv(student.getClassCode() != null ? student.getClassCode() : ""),
                            escapeCsv(student.getBatchCode() != null ? student.getBatchCode() : ""),
                            escapeCsv(s.getDate()),
                            escapeCsv(s.getSubject()),
                            escapeCsv(s.getStatus()),
                            escapeCsv(s.getCheckInTime()),
                            escapeCsv(s.getCheckOutTime()),
                            escapeCsv(s.getRecordingMethod()),
                            s.isOverride() ? "YES" : "NO",
                            escapeCsv(s.getOverrideReason() != null ? s.getOverrideReason() : ""),
                            escapeCsv(s.getOverriddenBy() != null ? s.getOverriddenBy() : "")
                    ));
                }
            }
        } else {
            // Summary table export
            writer.println("Roll Number,Student Name,Class,Batch,Total Conducted Sessions,Present Count,Late Count,Absent Count,Excused Count,Overall Attendance %,Performance Tier,Last Attendance Date,Last Status");
            List<StudentAttendanceSummaryDTO> list = attendanceDAO.getStudentSummaries(faculty.getId(), classId, batchId, startDate, endDate, null, null);
            for (StudentAttendanceSummaryDTO s : list) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%d,%d,%d,%.1f,\"%s\",\"%s\",\"%s\"",
                        escapeCsv(s.getRollNo()),
                        escapeCsv(s.getName()),
                        escapeCsv(s.getClassCode() != null ? s.getClassCode() : ""),
                        escapeCsv(s.getBatchCode() != null ? s.getBatchCode() : ""),
                        s.getTotalSessions(),
                        s.getPresentCount(),
                        s.getLateCount(),
                        s.getAbsentCount(),
                        s.getExcusedCount(),
                        s.getAttendancePercentage(),
                        escapeCsv(s.getPerformanceTier()),
                        escapeCsv(s.getLastDate()),
                        escapeCsv(s.getLastStatus())
                ));
            }
        }

        writer.flush();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }
}
