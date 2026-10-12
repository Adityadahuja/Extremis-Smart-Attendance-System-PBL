package com.extremis.servlet;

import com.extremis.dao.FacultyDAO;
import com.extremis.dao.TeacherAssignmentDAO;
import com.extremis.model.Admin;
import com.extremis.model.Faculty;
import com.extremis.model.TeacherAssignment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Servlet for Teacher Management (CRUD, activation/deactivation, assignments view).
 */
@WebServlet("/admin/teachers")
public class AdminTeachersServlet extends HttpServlet {

    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("currentAdmin") : null;
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String query = req.getParameter("q");
        String dept = req.getParameter("dept");
        String status = req.getParameter("status");

        List<Faculty> teachers = facultyDAO.searchFaculties(query, dept, status);

        // Fetch assignments for each teacher
        Map<Long, List<TeacherAssignment>> teacherAssignmentsMap = new HashMap<>();
        for (Faculty f : teachers) {
            teacherAssignmentsMap.put(f.getId(), assignmentDAO.getAssignmentsByFacultyId(f.getId()));
        }

        req.setAttribute("teachers", teachers);
        req.setAttribute("teacherAssignmentsMap", teacherAssignmentsMap);
        req.setAttribute("searchQuery", query != null ? query : "");
        req.setAttribute("selectedDept", dept != null ? dept : "ALL");
        req.setAttribute("selectedStatus", status != null ? status : "ALL");

        req.getRequestDispatcher("/WEB-INF/views/admin-teachers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("currentAdmin") : null;
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "create";

        if ("create".equalsIgnoreCase(action)) {
            String facultyId = req.getParameter("facultyId");
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String department = req.getParameter("department");
            String password = req.getParameter("password");

            if (facultyId == null || facultyId.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                String err = URLEncoder.encode("All fields including password are required.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=" + err);
                return;
            }

            // Check if teacher ID already exists
            if (facultyDAO.findByFacultyId(facultyId.trim()) != null) {
                String err = URLEncoder.encode("Teacher ID '" + facultyId.trim() + "' is already registered.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=" + err);
                return;
            }

            Faculty f = new Faculty();
            f.setFacultyId(facultyId.trim().toUpperCase());
            f.setName(name.trim());
            f.setEmail(email.trim());
            f.setBranch(department != null ? department.trim() : "General");
            f.setSection("A");
            f.setDepartment(department != null ? department.trim() : "General");
            f.setActive(true);

            boolean ok = facultyDAO.createFaculty(f, password.trim());
            if (ok) {
                String msg = URLEncoder.encode("Teacher " + f.getName() + " added successfully.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=" + msg);
            } else {
                String err = URLEncoder.encode("Failed to create teacher in database.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=" + err);
            }
            return;
        }

        if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String department = req.getParameter("department");

            try {
                Long id = Long.parseLong(idStr.trim());
                Faculty existing = facultyDAO.getFacultyById(id);
                if (existing != null) {
                    existing.setName(name != null ? name.trim() : existing.getName());
                    existing.setEmail(email != null ? email.trim() : existing.getEmail());
                    existing.setDepartment(department != null ? department.trim() : existing.getDepartment());
                    existing.setBranch(department != null ? department.trim() : existing.getBranch());
                    facultyDAO.updateFaculty(existing);
                    String msg = URLEncoder.encode("Teacher profile updated successfully.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=" + msg);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String err = URLEncoder.encode("Error updating teacher.", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=" + err);
            return;
        }

        if ("toggle_status".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            String activeStr = req.getParameter("active");
            try {
                Long id = Long.parseLong(idStr.trim());
                boolean active = "true".equalsIgnoreCase(activeStr);
                facultyDAO.toggleStatus(id, active);
                String msg = URLEncoder.encode("Teacher account status changed to " + (active ? "Active" : "Inactive") + ".", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=" + msg);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=Unable+to+update+status");
            return;
        }

        if ("reset_password".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            String newPassword = req.getParameter("newPassword");
            try {
                Long id = Long.parseLong(idStr.trim());
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    facultyDAO.updatePassword(id, newPassword.trim());
                    String msg = URLEncoder.encode("Teacher password has been reset successfully.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=" + msg);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=Password+cannot+be+empty");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/teachers");
    }
}
