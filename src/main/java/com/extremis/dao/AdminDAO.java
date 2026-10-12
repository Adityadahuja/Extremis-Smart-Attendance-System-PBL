package com.extremis.dao;

import com.extremis.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username = ? COLLATE NOCASE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
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

    public boolean updateSessionToken(Long id, String sessionToken) {
        String sql = "UPDATE admins SET session_token = ? WHERE id = ?";
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

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getLong("id"));
        a.setUsername(rs.getString("username"));
        a.setName(rs.getString("name"));
        a.setEmail(rs.getString("email"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setSessionToken(rs.getString("session_token"));
        return a;
    }
}
