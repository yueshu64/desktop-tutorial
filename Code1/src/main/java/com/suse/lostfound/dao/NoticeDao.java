package com.suse.lostfound.dao;

import com.suse.lostfound.entity.Notice;
import com.suse.lostfound.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NoticeDao {
    private static final String SELECT = "SELECT n.notice_id, n.admin_id, n.title, n.content, n.create_time, u.name AS admin_name "
            + "FROM notice n LEFT JOIN `user` u ON n.admin_id = u.user_id ";

    public void insert(Connection conn, Notice notice) throws SQLException {
        String sql = "INSERT INTO notice (admin_id, title, content, create_time) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (notice.getAdminId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, notice.getAdminId());
            }
            ps.setString(2, notice.getTitle());
            ps.setString(3, notice.getContent());
            ps.executeUpdate();
        }
    }

    public void update(Notice notice) {
        String sql = "UPDATE notice SET title = ?, content = ? WHERE notice_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getContent());
            ps.setInt(3, notice.getNoticeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改公告失败", e);
        }
    }

    public void delete(int noticeId) {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noticeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除公告失败", e);
        }
    }

    public Notice findById(int noticeId) {
        String sql = SELECT + "WHERE n.notice_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noticeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询公告失败", e);
        }
    }

    public List<Notice> findLatest(int limit) {
        String sql = SELECT + "ORDER BY n.create_time DESC, n.notice_id DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询最新公告失败", e);
        }
    }

    public List<Notice> findAll() {
        String sql = SELECT + "ORDER BY n.create_time DESC, n.notice_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        } catch (SQLException e) {
            throw new RuntimeException("查询公告列表失败", e);
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM notice";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计公告失败", e);
        }
    }

    private List<Notice> mapList(ResultSet rs) throws SQLException {
        List<Notice> list = new ArrayList<Notice>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    private Notice map(ResultSet rs) throws SQLException {
        Notice notice = new Notice();
        notice.setNoticeId(rs.getInt("notice_id"));
        int adminId = rs.getInt("admin_id");
        if (rs.wasNull()) {
            notice.setAdminId(null);
        } else {
            notice.setAdminId(adminId);
        }
        notice.setTitle(rs.getString("title"));
        notice.setContent(rs.getString("content"));
        notice.setCreateTime(rs.getTimestamp("create_time"));
        notice.setAdminName(rs.getString("admin_name"));
        return notice;
    }
}
