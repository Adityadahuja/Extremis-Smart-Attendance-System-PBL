package com.extremis.filter;

import com.extremis.model.Admin;
import com.extremis.model.Faculty;
import com.extremis.model.Student;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter that protects Student, Faculty, and Admin endpoints.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allow static resources and public endpoints
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") ||
            path.startsWith("/uploads/") || path.equals("/login") || path.equals("/logout")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);

        // 1. Faculty Endpoints
        if (path.startsWith("/faculty/")) {
            Faculty faculty = (session != null) ? (Faculty) session.getAttribute("currentFaculty") : null;
            if (faculty == null || !faculty.isActive()) {
                if (session != null) session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        // 2. Admin Endpoints
        if (path.startsWith("/admin/")) {
            Admin admin = (session != null) ? (Admin) session.getAttribute("currentAdmin") : null;
            if (admin == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        // 3. Student Endpoints
        Student student = (session != null) ? (Student) session.getAttribute("currentStudent") : null;

        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if student accepted required permissions
        if (!student.isPermissionsAccepted() && !path.equals("/permissions")) {
            response.sendRedirect(request.getContextPath() + "/permissions");
            return;
        }

        chain.doFilter(req, res);
    }
}
