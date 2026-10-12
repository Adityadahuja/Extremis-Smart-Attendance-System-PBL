package com.extremis.servlet;

import com.extremis.dao.StudentDAO;
import com.extremis.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/permissions")
public class PermissionServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/permissions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Student student = (Student) session.getAttribute("currentStudent");

        String termsAgreed = req.getParameter("termsAgreed");
        if ("true".equalsIgnoreCase(termsAgreed)) {
            studentDAO.updatePermissions(student.getId(), true);
            student.setPermissionsAccepted(true);
            session.setAttribute("currentStudent", student);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("errorMessage", "You must accept the terms and conditions to continue.");
            req.getRequestDispatcher("/WEB-INF/views/permissions.jsp").forward(req, resp);
        }
    }
}
