package com.extremis.servlet;

import com.extremis.dao.AdminDAO;
import com.extremis.dao.FacultyDAO;
import com.extremis.dao.StudentDAO;
import com.extremis.model.Admin;
import com.extremis.model.Faculty;
import com.extremis.model.Student;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Student student = (Student) session.getAttribute("currentStudent");
            if (student != null) {
                studentDAO.clearSessionToken(student.getId());
            }

            Faculty faculty = (Faculty) session.getAttribute("currentFaculty");
            if (faculty != null) {
                facultyDAO.updateSessionToken(faculty.getId(), null);
            }

            Admin admin = (Admin) session.getAttribute("currentAdmin");
            if (admin != null) {
                adminDAO.updateSessionToken(admin.getId(), null);
            }

            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
