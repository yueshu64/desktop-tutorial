package com.suse.lostfound.dao;

import com.suse.lostfound.entity.LostItem;
import com.suse.lostfound.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** lost_item 表的增删改查。带 Connection 参数的方法由 Service 放进事务。 */
public class LostItemDao {
    private static final String COLUMNS = "i.item_id, i.user_id, i.title, i.description, i.item_type, i.location, "
            + "i.contact, i.status, i.create_time, u.name AS publisher_name, u.username AS publisher_username";

    /** 插入一条待处理信息，并返回数据库生成的 item_id。 */
    public int insert(Connection conn, LostItem item) throws SQLException {
        String sql = "INSERT INTO lost_item (user_id, title, description, item_type, location, contact, status, create_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, 0, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getUserId());
            ps.setString(2, item.getTitle());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getItemType());
            ps.setString(5, item.getLocation());
            ps.setString(6, item.getContact() == null ? "" : item.getContact());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("发布失物信息失败");
    }

    /** 发布人修改自己的帖子。WHERE 同时限制 item_id 和 user_id，避免改到别人的数据。 */
    public void update(LostItem item) {
        String sql = "UPDATE lost_item SET title = ?, description = ?, item_type = ?, location = ?, contact = ? "
                + "WHERE item_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getTitle());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getItemType());
            ps.setString(4, item.getLocation());
            ps.setString(5, item.getContact() == null ? "" : item.getContact());
            ps.setInt(6, item.getItemId());
            ps.setInt(7, item.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改失物信息失败", e);
        }
    }

    public void updateStatus(Connection conn, int itemId, int status) throws SQLException {
        String sql = "UPDATE lost_item SET status = ? WHERE item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    public void deleteById(Connection conn, int itemId) throws SQLException {
        String sql = "DELETE FROM lost_item WHERE item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        }
    }

    public LostItem findById(int itemId) {
        String sql = "SELECT " + COLUMNS + " FROM lost_item i JOIN `user` u ON i.user_id = u.user_id WHERE i.item_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询失物详情失败", e);
        }
    }

    public List<LostItem> findByUserId(int userId) {
        String sql = "SELECT " + COLUMNS + " FROM lost_item i JOIN `user` u ON i.user_id = u.user_id "
                + "WHERE i.user_id = ? ORDER BY i.create_time DESC, i.item_id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询我的发布失败", e);
        }
    }

    /**
     * 按类型、关键词、状态统计条数。
     * 参数传 null 或空字符串表示这个条件不参与筛选。
     */
    public int count(String type, String keyword, Integer status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM lost_item i WHERE 1 = 1");
        List<Object> params = new ArrayList<Object>();
        appendWhere(sql, params, type, keyword, status);
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            fill(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计失物信息失败", e);
        }
    }

    /** 分页查询。offset 是跳过的行数，pageSize 是本页条数。 */
    public List<LostItem> findPage(String type, String keyword, Integer status, int offset, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS
                + " FROM lost_item i JOIN `user` u ON i.user_id = u.user_id WHERE 1 = 1");
        List<Object> params = new ArrayList<Object>();
        appendWhere(sql, params, type, keyword, status);
        sql.append(" ORDER BY i.create_time DESC, i.item_id DESC LIMIT ?, ?");
        params.add(offset);
        params.add(pageSize);
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            fill(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询失物列表失败", e);
        }
    }

    /** 关键词同时匹配标题、地点和详情，并把 %、_ 转义，避免被当成通配符。 */
    private void appendWhere(StringBuilder sql, List<Object> params, String type, String keyword, Integer status) {
        if (type != null && !type.isEmpty()) {
            sql.append(" AND i.item_type = ?");
            params.add(type);
        }
        if (status != null) {
            sql.append(" AND i.status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (i.title LIKE ? ESCAPE '\\\\' OR i.location LIKE ? ESCAPE '\\\\' OR i.description LIKE ? ESCAPE '\\\\')");
            String like = like(keyword);
            params.add(like);
            params.add(like);
            params.add(like);
        }
    }

    private String like(String keyword) {
        String escaped = keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + escaped + "%";
    }

    private void fill(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value instanceof Integer) {
                ps.setInt(i + 1, (Integer) value);
            } else {
                ps.setString(i + 1, String.valueOf(value));
            }
        }
    }

    private List<LostItem> mapList(ResultSet rs) throws SQLException {
        List<LostItem> list = new ArrayList<LostItem>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    private LostItem map(ResultSet rs) throws SQLException {
        LostItem item = new LostItem();
        item.setItemId(rs.getInt("item_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setItemType(rs.getString("item_type"));
        item.setLocation(rs.getString("location"));
        item.setContact(rs.getString("contact"));
        item.setStatus(rs.getInt("status"));
        item.setCreateTime(rs.getTimestamp("create_time"));
        item.setPublisherName(rs.getString("publisher_name"));
        item.setPublisherUsername(rs.getString("publisher_username"));
        return item;
    }
}
