package com.extremis.servlet;

import com.extremis.dao.ClassDAO;
import com.extremis.dao.FacultyDAO;
import com.extremis.dao.TeacherAssignmentDAO;
import com.extremis.model.Admin;
import com.extremis.model.BatchEntity;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Admin Servlet for assigning Teachers to Classes, Batches, and Subjects.
 */
@WebServlet("/admin/assignments")
public class AdminAssignmentsServlet extends HttpServlet {

    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final ClassDAO classDAO = new ClassDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("currentAdmin") : null;
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<TeacherAssignment> assignments = assignmentDAO.getAllAssignments();
        List<Faculty> teachers = facultyDAO.getAllFaculties();
        List<ClassEntity> classes = classDAO.getActiveClasses();
        List<BatchEntity> batches = classDAO.getAllBatches();

        req.setAttribute("assignments", assignments);
        req.setAttribute("teachers", teachers);
        req.setAttribute("classes", classes);
        req.setAttribute("batches", batches);

        req.getRequestDispatcher("/WEB-INF/views/admin-assignments.jsp").forward(req, resp);
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
            String facultyIdStr = req.getParameter("facultyId");
            String classIdStr = req.getParameter("classId");
            String batchIdStr = req.getParameter("batchId");
            String subject = req.getParameter("subject");

            if (facultyIdStr == null || facultyIdStr.trim().isEmpty() ||
                classIdStr == null || classIdStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/assignments?error=Teacher+and+Class+are+required");
                return;
            }

            try {
                Long facultyId = Long.parseLong(facultyIdStr.trim());
                Long classId = Long.parseLong(classIdStr.trim());
                Long batchId = null;
                if (batchIdStr != null && !batchIdStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(batchIdStr.trim())) {
                    batchId = Long.parseLong(batchIdStr.trim());
                    // Validate that the batch actually belongs to the class
                    BatchEntity batch = classDAO.getBatchById(batchId);
                    if (batch == null || !batch.getClassId().equals(classId)) {
                        String err = URLEncoder.encode("Invalid class-batch relationship: The selected batch does not belong to the selected class.", StandardCharsets.UTF_8);
                        resp.sendRedirect(req.getContextPath() + "/admin/assignments?error=" + err);
                        return;
                    }
                }

                TeacherAssignment ta = new TeacherAssignment();
                ta.setFacultyId(facultyId);
                ta.setClassId(classId);
                ta.setBatchId(batchId);
                ta.setSubject(subject != null && !subject.trim().isEmpty() ? subject.trim() : "General");

                boolean created = assignmentDAO.createAssignment(ta);
                if (created) {
                    String msg = URLEncoder.encode("Assignment created successfully.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/assignments?success=" + msg);
                } else {
                    String err = URLEncoder.encode("Assignment already exists for this teacher, class, and batch combination.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/assignments?error=" + err);
                }
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/assignments?error=Failed+to+save+assignment");
            return;
        }

        if ("delete".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            try {
                Long id = Long.parseLong(idStr.trim());
                assignmentDAO.deleteAssignment(id);
                String msg = URLEncoder.encode("Assignment removed successfully.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/assignments?success=" + msg);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/assignments?error=Unable+to+remove+assignment");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/assignments");
    }
}
