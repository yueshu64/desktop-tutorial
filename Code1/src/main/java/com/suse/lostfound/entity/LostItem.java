package com.suse.lostfound.entity;

import java.util.Date;

/**
 * 失物招领信息，对应表 lost_item。
 * itemType 只有「丢失」和「拾到」。
 * status：0 待处理，1 已解决，2 已关闭。已关闭后不能再改状态。
 * publisherName 不是表字段，查询时从 user 表连接出来，给页面显示发布人。
 */
public class LostItem {
    private int itemId;
    private int userId;
    private String title;
    private String description;
    private String itemType;
    private String location;
    private String contact;
    private int status;
    private Date createTime;
    private String publisherName;
    private String publisherUsername;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherUsername() {
        return publisherUsername;
    }

    public void setPublisherUsername(String publisherUsername) {
        this.publisherUsername = publisherUsername;
    }

    /** 把状态数字转成页面上的中文。 */
    public String getStatusText() {
        if (status == 1) {
            return "已解决";
        }
        if (status == 2) {
            return "已关闭";
        }
        return "待处理";
    }
}
