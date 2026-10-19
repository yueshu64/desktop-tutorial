package com.suse.lostfound.service;

import com.suse.lostfound.dao.LostItemDao;
import com.suse.lostfound.entity.LostItem;
import com.suse.lostfound.util.BizException;
import com.suse.lostfound.util.DBUtil;
import com.suse.lostfound.util.PageResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 失物信息的发布、查询、修改、状态变更和删除。
 * 发布、解决、关闭、删除在写库成功后，会在同一个事务里自动写一条系统公告。
 */
public class LostItemService {
    private final LostItemDao lostItemDao = new LostItemDao();
    private final NoticeService noticeService = new NoticeService();

    /**
     * 发布信息。地点和详情必填，联系方式可以留空。
     * 帖子和系统公告一起提交；公告写失败时，帖子也会回滚。
     */
    public int publish(LostItem item, String publisherName) {
        validate(item);
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            int id = lostItemDao.insert(conn, item);
            item.setItemId(id);
            noticeService.writeSystem(conn,
                    "系统通知：新的" + item.getItemType() + "信息《" + brief(item.getTitle()) + "》",
                    "用户" + publisherName + "发布了一条【" + item.getItemType() + "】信息。地点："
                            + item.getLocation() + "。请相关同学留意。本公告在信息写入数据库后自动发布。");
            conn.commit();
            return id;
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            throw new RuntimeException("发布失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    /** 只有发布人本人可以修改，已关闭的信息不能再改。 */
    public void update(LostItem item, int operatorId) {
        LostItem db = mustOwn(item.getItemId(), operatorId);
        if (db.getStatus() == 2) {
            throw new BizException("已关闭的信息不能再修改");
        }
        validate(item);
        item.setUserId(operatorId);
        lostItemDao.update(item);
    }

    /** 待处理改为已解决。已解决不能再改回待处理。 */
    public void resolve(int itemId, int operatorId, String operatorName) {
        changeStatus(itemId, operatorId, operatorName, 1, "已标记为已解决");
    }

    /** 待处理或已解决都可以关闭。关闭后不能再修改。 */
    public void close(int itemId, int operatorId, String operatorName) {
        changeStatus(itemId, operatorId, operatorName, 2, "已关闭");
    }

    /** 发布人删除自己的帖子，并自动写系统公告。 */
    public void deleteOwn(int itemId, int operatorId, String operatorName) {
        LostItem db = mustOwn(itemId, operatorId);
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            lostItemDao.deleteById(conn, itemId);
            noticeService.writeSystem(conn,
                    "系统通知：发布者删除了《" + brief(db.getTitle()) + "》",
                    "用户" + operatorName + "删除了自己发布的【" + db.getItemType() + "】信息《"
                            + db.getTitle() + "》。本公告在数据库更新成功后自动发布。");
            conn.commit();
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            throw new RuntimeException("删除失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    /** 管理员删除违规信息。公告里只写「管理员」，不写管理员姓名。 */
    public void deleteByAdmin(int itemId, String adminName) {
        LostItem db = lostItemDao.findById(itemId);
        if (db == null) {
            throw new BizException("信息不存在或已删除");
        }
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            lostItemDao.deleteById(conn, itemId);
            noticeService.writeSystem(conn,
                    "系统通知：管理员删除了违规信息《" + brief(db.getTitle()) + "》",
                    "管理员删除了用户" + db.getPublisherName() + "发布的【"
                            + db.getItemType() + "】信息《" + db.getTitle() + "》。地点："
                            + db.getLocation() + "。本公告在数据库更新成功后自动发布。");
            conn.commit();
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            throw new RuntimeException("删除违规信息失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    public LostItem findById(int itemId) {
        return lostItemDao.findById(itemId);
    }

    public List<LostItem> listByUser(int userId) {
        return lostItemDao.findByUserId(userId);
    }

    /**
     * 分页。type 取「丢失」或「拾到」，keyword 匹配标题、地点、详情，status 为 null 时不过滤状态。
     * 页码超出总页数时，自动回到最后一页。
     */
    public PageResult<LostItem> page(String type, String keyword, Integer status, int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 6;
        }
        int total = lostItemDao.count(type, keyword, status);
        int totalPages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }
        int offset = (page - 1) * pageSize;
        PageResult<LostItem> result = new PageResult<LostItem>();
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(total);
        if (total == 0) {
            return result;
        }
        result.setRecords(lostItemDao.findPage(type, keyword, status, offset, pageSize));
        return result;
    }

    public int count(String type, Integer status) {
        return lostItemDao.count(type, null, status);
    }

    private void changeStatus(int itemId, int operatorId, String operatorName, int status, String actionText) {
        LostItem db = mustOwn(itemId, operatorId);
        if (db.getStatus() == status) {
            throw new BizException("当前已经是「" + db.getStatusText() + "」");
        }
        if (db.getStatus() == 2) {
            throw new BizException("已关闭的信息不能再变更状态");
        }
        if (status == 1 && db.getStatus() != 0) {
            throw new BizException("只有待处理的信息可以标记为已解决");
        }
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            lostItemDao.updateStatus(conn, itemId, status);
            noticeService.writeSystem(conn,
                    "系统通知：《" + brief(db.getTitle()) + "》" + actionText,
                    "用户" + operatorName + "将【" + db.getItemType() + "】信息《" + db.getTitle()
                            + "》" + actionText + "。地点：" + db.getLocation()
                            + "。本公告在状态更新写入数据库后自动发布。");
            conn.commit();
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            throw new RuntimeException("更新状态失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    /** 确认这条信息存在，并且操作人就是发布人。 */
    private LostItem mustOwn(int itemId, int operatorId) {
        LostItem db = lostItemDao.findById(itemId);
        if (db == null) {
            throw new BizException("信息不存在或已删除");
        }
        if (db.getUserId() != operatorId) {
            throw new BizException("只能操作自己发布的信息");
        }
        return db;
    }

    /** 标题、地点、详情必填。联系方式选填，最长 50 字。 */
    private void validate(LostItem item) {
        item.setTitle(required(item.getTitle(), 50, "标题需在 1 到 50 字之间"));
        item.setDescription(required(item.getDescription(), 500, "详情为必填，请填写物品特征或存放说明"));
        item.setLocation(required(item.getLocation(), 100, "地点为必填，请填写具体地点"));
        String contact = item.getContact() == null ? "" : item.getContact().trim();
        if (contact.length() > 50) {
            throw new BizException("联系方式不能超过 50 字");
        }
        item.setContact(contact);
        if (!"丢失".equals(item.getItemType()) && !"拾到".equals(item.getItemType())) {
            throw new BizException("类型只能是「丢失」或「拾到」");
        }
    }

    private String required(String value, int max, String message) {
        if (value == null) {
            throw new BizException(message);
        }
        value = value.trim();
        if (value.isEmpty() || value.length() > max) {
            throw new BizException(message);
        }
        return value;
    }

    private String brief(String title) {
        if (title == null) {
            return "";
        }
        return title.length() <= 24 ? title : title.substring(0, 24) + "…";
    }
}
