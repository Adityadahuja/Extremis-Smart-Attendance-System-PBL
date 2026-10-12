package com.extremis.servlet;

import com.extremis.dao.ClassDAO;
import com.extremis.dao.StudentDAO;
import com.extremis.model.Admin;
import com.extremis.model.BatchEntity;
import com.extremis.model.ClassEntity;
import com.extremis.model.Student;
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
 * Admin Servlet for managing Academic Classes and Batches.
 */
@WebServlet("/admin/classes")
public class AdminClassesServlet extends HttpServlet {

    private final ClassDAO classDAO = new ClassDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("currentAdmin") : null;
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<ClassEntity> classes = classDAO.getAllClasses();
        List<BatchEntity> batches = classDAO.getAllBatches();
        List<Student> students = studentDAO.getAllStudents();

        req.setAttribute("classes", classes);
        req.setAttribute("batches", batches);
        req.setAttribute("students", students);

        req.getRequestDispatcher("/WEB-INF/views/admin-classes.jsp").forward(req, resp);
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
        if (action == null) action = "";

        if ("create_class".equalsIgnoreCase(action)) {
            String code = req.getParameter("code");
            String name = req.getParameter("name");
            String department = req.getParameter("department");
            String semester = req.getParameter("semester");

            if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Class+code+and+name+are+required");
                return;
            }

            ClassEntity ce = new ClassEntity();
            ce.setCode(code.trim().toUpperCase());
            ce.setName(name.trim());
            ce.setDepartment(department != null ? department.trim() : "General");
            ce.setSemester(semester != null ? semester.trim() : "");
            ce.setActive(true);

            boolean ok = classDAO.createClass(ce);
            if (ok) {
                String msg = URLEncoder.encode("Class " + ce.getCode() + " created successfully.", StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/admin/classes?success=" + msg);
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Failed+to+create+class+(code+may+already+exist)");
            }
            return;
        }

        if ("edit_class".equalsIgnoreCase(action)) {
            try {
                Long id = Long.parseLong(req.getParameter("id").trim());
                ClassEntity ce = classDAO.getClassById(id);
                if (ce != null) {
                    ce.setCode(req.getParameter("code").trim().toUpperCase());
                    ce.setName(req.getParameter("name").trim());
                    ce.setDepartment(req.getParameter("department").trim());
                    ce.setSemester(req.getParameter("semester").trim());
                    classDAO.updateClass(ce);
                    resp.sendRedirect(req.getContextPath() + "/admin/classes?success=Class+updated+successfully");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Unable+to+update+class");
            return;
        }

        if ("toggle_class".equalsIgnoreCase(action)) {
            try {
                Long id = Long.parseLong(req.getParameter("id").trim());
                boolean active = "true".equalsIgnoreCase(req.getParameter("active"));
                classDAO.toggleClassStatus(id, active);
                resp.sendRedirect(req.getContextPath() + "/admin/classes?success=Class+status+updated");
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Unable+to+change+class+status");
            return;
        }

        if ("create_batch".equalsIgnoreCase(action)) {
            String classIdStr = req.getParameter("classId");
            String code = req.getParameter("code");
            String name = req.getParameter("name");

            if (classIdStr == null || classIdStr.trim().isEmpty() || code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Class,+batch+code,+and+name+are+required");
                return;
            }

            try {
                Long classId = Long.parseLong(classIdStr.trim());
                BatchEntity be = new BatchEntity();
                be.setClassId(classId);
                be.setCode(code.trim().toUpperCase());
                be.setName(name.trim());
                be.setActive(true);

                boolean ok = classDAO.createBatch(be);
                if (ok) {
                    String msg = URLEncoder.encode("Batch " + be.getCode() + " added successfully.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/admin/classes?success=" + msg);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Failed+to+add+batch+(duplicate+code+in+same+class)");
            return;
        }

        if ("edit_batch".equalsIgnoreCase(action)) {
            try {
                Long id = Long.parseLong(req.getParameter("id").trim());
                Long classId = Long.parseLong(req.getParameter("classId").trim());
                BatchEntity be = classDAO.getBatchById(id);
                if (be != null) {
                    be.setClassId(classId);
                    be.setCode(req.getParameter("code").trim().toUpperCase());
                    be.setName(req.getParameter("name").trim());
                    classDAO.updateBatch(be);
                    resp.sendRedirect(req.getContextPath() + "/admin/classes?success=Batch+updated+successfully");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Unable+to+update+batch");
            return;
        }

        if ("toggle_batch".equalsIgnoreCase(action)) {
            try {
                Long id = Long.parseLong(req.getParameter("id").trim());
                boolean active = "true".equalsIgnoreCase(req.getParameter("active"));
                classDAO.toggleBatchStatus(id, active);
                resp.sendRedirect(req.getContextPath() + "/admin/classes?success=Batch+status+updated");
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Unable+to+change+batch+status");
            return;
        }

        if ("assign_student_batch".equalsIgnoreCase(action)) {
            try {
                Long studentId = Long.parseLong(req.getParameter("studentId").trim());
                Long classId = Long.parseLong(req.getParameter("classId").trim());
                String batchIdStr = req.getParameter("batchId");
                Long batchId = (batchIdStr != null && !batchIdStr.trim().isEmpty()) ? Long.parseLong(batchIdStr.trim()) : null;

                studentDAO.assignStudentToClassAndBatch(studentId, classId, batchId);
                resp.sendRedirect(req.getContextPath() + "/admin/classes?success=Student+assigned+to+batch+successfully");
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/classes?error=Failed+to+assign+student");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/classes");
    }
}
