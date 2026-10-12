package com.extremis.dao;

import com.extremis.model.Notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notices ORDER BY created_at DESC";
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

    public Notice getLatestImportantNotice() {
        String sql = "SELECT * FROM notices WHERE category = 'IMPORTANT' ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasUnreadNotices() {
        String sql = "SELECT COUNT(*) FROM notices WHERE is_read = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Notice mapRow(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setId(rs.getLong("id"));
        n.setCategory(rs.getString("category"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        String caStr = rs.getString("created_at");
        if (caStr != null) {
            try {
                if (caStr.matches("\\d{10,13}")) {
                    long epoch = Long.parseLong(caStr);
                    n.setCreatedAt(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(epoch), java.time.ZoneId.systemDefault()));
                } else if (caStr.length() == 19) {
                    n.setCreatedAt(LocalDateTime.parse(caStr, DT_FMT));
                } else {
                    n.setCreatedAt(LocalDateTime.parse(caStr.replace(" ", "T")));
                }
            } catch (Exception e) {
                n.setCreatedAt(LocalDateTime.now());
            }
        } else {
            n.setCreatedAt(LocalDateTime.now());
        }
        try {
            n.setRead(rs.getInt("is_read") == 1);
        } catch (SQLException ignored) {
            n.setRead(false);
        }
        return n;
    }
}
