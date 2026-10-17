package com.suse.lostfound.entity;

import java.util.Date;

/** 公告。adminId 为空时表示系统在数据更新后自动发布。 */
public class Notice {
    private int noticeId;
    private Integer adminId;
    private String title;
    private String content;
    private Date createTime;
    private String adminName;

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /** 系统公告显示「系统」，管理员手工公告只显示「管理员发布」，不显示姓名。 */
    public String getAuthorLabel() {
        if (adminId == null) {
            return "系统";
        }
        return "管理员发布";
    }
}
