package com.extremis.servlet;

import com.extremis.dao.AttendanceDAO;
import com.extremis.model.AttendanceRecord;
import com.extremis.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Student student = (Student) session.getAttribute("currentStudent");

        String monthStr = req.getParameter("month");
        if (monthStr == null || monthStr.trim().isEmpty()) {
            monthStr = "2026-10";
        }

        String filter = req.getParameter("filter");
        if (filter == null || filter.trim().isEmpty()) {
            filter = "ALL";
        }

        int year = Integer.parseInt(monthStr.substring(0, 4));
        int month = Integer.parseInt(monthStr.substring(5, 7));

        List<AttendanceRecord> records = attendanceDAO.getRecordsByStudentAndMonth(student.getId(), year, month, filter);
        int presentCount = attendanceDAO.countByStatus(student.getId(), year, month, "PRESENT");
        int absentCount = attendanceDAO.countByStatus(student.getId(), year, month, "ABSENT");
        int odCount = attendanceDAO.countByStatus(student.getId(), year, month, "OD");
        int totalCount = attendanceDAO.countTotalMonthClasses(student.getId(), year, month);

        req.setAttribute("records", records);
        req.setAttribute("presentCount", presentCount);
        req.setAttribute("absentCount", absentCount);
        req.setAttribute("odCount", odCount);
        req.setAttribute("totalCount", totalCount > 0 ? totalCount : 25);
        req.setAttribute("selectedMonth", monthStr);
        req.setAttribute("selectedFilter", filter);
        req.setAttribute("activeTab", "report");

        req.getRequestDispatcher("/WEB-INF/views/report.jsp").forward(req, resp);
    }
}
