package com.suse.lostfound.dao;

import com.suse.lostfound.entity.User;
import com.suse.lostfound.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, password, name, phone, role, create_time FROM `user` WHERE username = ?";
        return queryOne(sql, username);
    }

    public User findById(int userId) {
        String sql = "SELECT user_id, username, password, name, phone, role, create_time FROM `user` WHERE user_id = ?";
        return queryOne(sql, userId);
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM `user`";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计用户失败", e);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT u.user_id, u.username, u.password, u.name, u.phone, u.role, u.create_time, "
                + "(SELECT COUNT(*) FROM lost_item i WHERE i.user_id = u.user_id) AS item_count "
                + "FROM `user` u ORDER BY u.role DESC, u.create_time ASC";
        List<User> list = new ArrayList<User>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = map(rs);
                user.setItemCount(rs.getInt("item_count"));
                list.add(user);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询用户列表失败", e);
        }
    }

    public int insert(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO `user` (username, password, name, phone, role, create_time) VALUES (?, ?, ?, ?, 0, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getPhone() == null ? "" : user.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("注册用户失败");
    }

    public void updateProfile(int userId, String name, String phone) {
        String sql = "UPDATE `user` SET name = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新个人资料失败", e);
        }
    }

    public void updatePassword(int userId, String password) {
        String sql = "UPDATE `user` SET password = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, password);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新密码失败", e);
        }
    }

    /** 只删除学生账号，管理员账号在 SQL 层再挡一次。 */
    public int deleteStudent(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM `user` WHERE user_id = ? AND role = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }

    private User queryOne(String sql, Object param) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param instanceof Integer) {
                ps.setInt(1, (Integer) param);
            } else {
                ps.setString(1, String.valueOf(param));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getInt("role"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        return user;
    }
}
