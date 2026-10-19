package com.suse.lostfound.service;

import com.suse.lostfound.dao.NoticeDao;
import com.suse.lostfound.entity.Notice;
import com.suse.lostfound.util.BizException;
import com.suse.lostfound.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 公告业务。
 * 管理员可以手工发布、修改、删除；
 * 用户注册、发布、解决、关闭、删除等数据库更新成功后，由 {@link #writeSystem} 自动写入一条系统公告。
 */
public class NoticeService {
    private final NoticeDao noticeDao = new NoticeDao();

    public void writeSystem(Connection conn, String title, String content) throws SQLException {
        Notice notice = new Notice();
        notice.setAdminId(null);
        notice.setTitle(cut(title, 80));
        notice.setContent(cut(content, 1000));
        noticeDao.insert(conn, notice);
    }

    public void saveByAdmin(int noticeId, String title, String content, int adminId) {
        title = checkTitle(title);
        content = checkContent(content);
        if (noticeId > 0) {
            Notice old = noticeDao.findById(noticeId);
            if (old == null) {
                throw new BizException("公告不存在或已删除");
            }
            old.setTitle(title);
            old.setContent(content);
            noticeDao.update(old);
            return;
        }
        Notice notice = new Notice();
        notice.setAdminId(adminId);
        notice.setTitle(title);
        notice.setContent(content);
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            noticeDao.insert(conn, notice);
        } catch (SQLException e) {
            throw new RuntimeException("发布公告失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    public void delete(int noticeId) {
        if (noticeDao.findById(noticeId) == null) {
            throw new BizException("公告不存在或已删除");
        }
        noticeDao.delete(noticeId);
    }

    public Notice find(int noticeId) {
        return noticeDao.findById(noticeId);
    }

    public List<Notice> latest(int limit) {
        return noticeDao.findLatest(limit);
    }

    public List<Notice> listAll() {
        return noticeDao.findAll();
    }

    public int count() {
        return noticeDao.count();
    }

    private String checkTitle(String title) {
        if (title == null) {
            throw new BizException("请填写公告标题");
        }
        title = title.trim();
        if (title.isEmpty() || title.length() > 80) {
            throw new BizException("公告标题需在 1 到 80 字之间");
        }
        return title;
    }

    private String checkContent(String content) {
        if (content == null) {
            throw new BizException("请填写公告内容");
        }
        content = content.trim();
        if (content.isEmpty() || content.length() > 1000) {
            throw new BizException("公告内容需在 1 到 1000 字之间");
        }
        return content;
    }

    private String cut(String value, int max) {
        if (value == null) {
            return "";
        }
        String text = value.trim();
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max);
    }
}
