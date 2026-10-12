package com.extremis.servlet;

import com.extremis.dao.AttendanceDAO;
import com.extremis.model.Admin;
import com.extremis.model.AttendanceOverrideAudit;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Admin Servlet for viewing immutable attendance override audit records.
 */
@WebServlet("/admin/overrides")
public class AdminOverrideHistoryServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("currentAdmin") : null;
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<AttendanceOverrideAudit> auditLogs = attendanceDAO.getOverrideAuditHistory(200);
        req.setAttribute("auditLogs", auditLogs);

        req.getRequestDispatcher("/WEB-INF/views/admin-overrides.jsp").forward(req, resp);
    }
}
