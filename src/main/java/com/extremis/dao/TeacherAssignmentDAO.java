package com.extremis.dao;

import com.extremis.model.TeacherAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Teacher-to-Class and Batch assignments.
 */
public class TeacherAssignmentDAO {

    public List<TeacherAssignment> getAllAssignments() {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, f.name as faculty_name, f.faculty_id as faculty_code, " +
                     "c.name as class_name, c.code as class_code, " +
                     "b.name as batch_name, b.code as batch_code " +
                     "FROM teacher_assignments ta " +
                     "JOIN faculties f ON ta.faculty_id = f.id " +
                     "JOIN classes c ON ta.class_id = c.id " +
                     "LEFT JOIN batches b ON ta.batch_id = b.id " +
                     "ORDER BY c.code ASC, f.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TeacherAssignment> getAssignmentsByFacultyId(Long facultyId) {
        List<TeacherAssignment> list = new ArrayList<>();
        if (facultyId == null) return list;
        String sql = "SELECT ta.*, f.name as faculty_name, f.faculty_id as faculty_code, " +
                     "c.name as class_name, c.code as class_code, " +
                     "b.name as batch_name, b.code as batch_code " +
                     "FROM teacher_assignments ta " +
                     "JOIN faculties f ON ta.faculty_id = f.id " +
                     "JOIN classes c ON ta.class_id = c.id " +
                     "LEFT JOIN batches b ON ta.batch_id = b.id " +
                     "WHERE ta.faculty_id = ? " +
                     "ORDER BY c.code ASC, b.code ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createAssignment(TeacherAssignment a) {
        // Enforce duplicate check
        String checkSql = "SELECT COUNT(*) FROM teacher_assignments WHERE faculty_id = ? AND class_id = ? AND (batch_id = ? OR (batch_id IS NULL AND ? IS NULL))";
        String insertSql = "INSERT INTO teacher_assignments (faculty_id, class_id, batch_id, subject, created_at) VALUES (?, ?, ?, ?, datetime('now'))";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setLong(1, a.getFacultyId());
                checkPs.setLong(2, a.getClassId());
                if (a.getBatchId() != null) {
                    checkPs.setLong(3, a.getBatchId());
                    checkPs.setLong(4, a.getBatchId());
                } else {
                    checkPs.setNull(3, java.sql.Types.INTEGER);
                    checkPs.setNull(4, java.sql.Types.INTEGER);
                }
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Already assigned
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, a.getFacultyId());
                ps.setLong(2, a.getClassId());
                if (a.getBatchId() != null) {
                    ps.setLong(3, a.getBatchId());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setString(4, a.getSubject() != null ? a.getSubject().trim() : "General");
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAssignment(Long id) {
        if (id == null) return false;
        String sql = "DELETE FROM teacher_assignments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifies if a faculty member is authorized to manage or override attendance for a student.
     */
    public boolean isFacultyAuthorizedForStudent(Long facultyId, Long studentId) {
        if (facultyId == null || studentId == null) return false;
        String sql = "SELECT COUNT(*) FROM teacher_assignments ta " +
                     "JOIN students s ON s.id = ? " +
                     "WHERE ta.faculty_id = ? " +
                     "AND (ta.class_id = s.class_id OR UPPER(s.branch) = (SELECT UPPER(c.department) FROM classes c WHERE c.id = ta.class_id)) " +
                     "AND (ta.batch_id IS NULL OR ta.batch_id = s.batch_id)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TeacherAssignment mapRow(ResultSet rs) throws SQLException {
        TeacherAssignment ta = new TeacherAssignment();
        ta.setId(rs.getLong("id"));
        ta.setFacultyId(rs.getLong("faculty_id"));
        ta.setClassId(rs.getLong("class_id"));
        long bId = rs.getLong("batch_id");
        if (!rs.wasNull()) {
            ta.setBatchId(bId);
        }
        ta.setSubject(rs.getString("subject"));
        ta.setCreatedAt(rs.getString("created_at"));
        ta.setFacultyName(rs.getString("faculty_name"));
        ta.setFacultyCode(rs.getString("faculty_code"));
        ta.setClassName(rs.getString("class_name"));
        ta.setClassCode(rs.getString("class_code"));
        ta.setBatchName(rs.getString("batch_name"));
        ta.setBatchCode(rs.getString("batch_code"));
        return ta;
    }
}
