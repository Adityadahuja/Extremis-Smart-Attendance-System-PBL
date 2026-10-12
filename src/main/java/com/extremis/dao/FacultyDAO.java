package com.extremis.dao;

import com.extremis.model.Faculty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public Faculty findByFacultyId(String facultyId) {
        String sql = "SELECT * FROM faculties WHERE faculty_id = ? COLLATE NOCASE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Faculty getFacultyById(Long id) {
        if (id == null) return null;
        String sql = "SELECT f.*, " +
                     "(SELECT COUNT(*) FROM teacher_assignments ta WHERE ta.faculty_id = f.id) as assignment_count " +
                     "FROM faculties f WHERE f.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Faculty f = mapRow(rs);
                    try { f.setAssignmentCount(rs.getInt("assignment_count")); } catch (SQLException ignored) {}
                    return f;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Faculty> getAllFaculties() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT f.*, " +
                     "(SELECT COUNT(*) FROM teacher_assignments ta WHERE ta.faculty_id = f.id) as assignment_count " +
                     "FROM faculties f ORDER BY f.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Faculty f = mapRow(rs);
                try { f.setAssignmentCount(rs.getInt("assignment_count")); } catch (SQLException ignored) {}
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Faculty> searchFaculties(String query, String department, String status) {
        List<Faculty> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT f.*, " +
                     "(SELECT COUNT(*) FROM teacher_assignments ta WHERE ta.faculty_id = f.id) as assignment_count " +
                     "FROM faculties f WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sb.append(" AND (f.name LIKE ? OR f.faculty_id LIKE ? OR f.email LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("ALL")) {
            sb.append(" AND (UPPER(f.department) = UPPER(?) OR UPPER(f.branch) = UPPER(?)) ");
            params.add(department.trim());
            params.add(department.trim());
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            if ("ACTIVE".equalsIgnoreCase(status)) {
                sb.append(" AND (f.is_active = 1 OR f.is_active IS NULL) ");
            } else if ("INACTIVE".equalsIgnoreCase(status)) {
                sb.append(" AND f.is_active = 0 ");
            }
        }

        sb.append(" ORDER BY f.name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Faculty f = mapRow(rs);
                    try { f.setAssignmentCount(rs.getInt("assignment_count")); } catch (SQLException ignored) {}
                    list.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createFaculty(Faculty f, String plainPassword) {
        String sql = "INSERT INTO faculties (faculty_id, name, email, branch, section, department, password_hash, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getFacultyId().trim().toUpperCase());
            ps.setString(2, f.getName().trim());
            ps.setString(3, f.getEmail().trim());
            ps.setString(4, f.getBranch() != null ? f.getBranch().trim() : "General");
            ps.setString(5, f.getSection() != null ? f.getSection().trim() : "A");
            ps.setString(6, f.getDepartment() != null ? f.getDepartment().trim() : f.getBranch());
            ps.setString(7, DBConnection.hashPassword(plainPassword));
            ps.setInt(8, f.isActive() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFaculty(Faculty f) {
        String sql = "UPDATE faculties SET name = ?, email = ?, branch = ?, section = ?, department = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getName().trim());
            ps.setString(2, f.getEmail().trim());
            ps.setString(3, f.getBranch() != null ? f.getBranch().trim() : "General");
            ps.setString(4, f.getSection() != null ? f.getSection().trim() : "A");
            ps.setString(5, f.getDepartment() != null ? f.getDepartment().trim() : f.getBranch());
            ps.setInt(6, f.isActive() ? 1 : 0);
            ps.setLong(7, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleStatus(Long id, boolean active) {
        String sql = "UPDATE faculties SET is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(Long id, String plainPassword) {
        String sql = "UPDATE faculties SET password_hash = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, DBConnection.hashPassword(plainPassword));
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSessionToken(Long id, String sessionToken) {
        String sql = "UPDATE faculties SET session_token = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionToken);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Faculty mapRow(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setId(rs.getLong("id"));
        f.setFacultyId(rs.getString("faculty_id"));
        f.setName(rs.getString("name"));
        f.setEmail(rs.getString("email"));
        f.setBranch(rs.getString("branch"));
        f.setSection(rs.getString("section"));
        f.setPasswordHash(rs.getString("password_hash"));
        f.setSessionToken(rs.getString("session_token"));

        try {
            int active = rs.getInt("is_active");
            f.setActive(rs.wasNull() || active == 1);
        } catch (SQLException ignored) {
            f.setActive(true);
        }

        try {
            f.setDepartment(rs.getString("department"));
        } catch (SQLException ignored) {}

        return f;
    }
}
