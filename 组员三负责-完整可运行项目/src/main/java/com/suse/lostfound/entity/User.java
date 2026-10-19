package com.suse.lostfound.entity;

import java.util.Date;

/** 用户。role：0 学生，1 管理员。 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String phone;
    private int role;
    private Date createTime;
    /** 管理端展示用，不是 user 表字段。 */
    private int itemCount;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isAdmin() {
        return role == 1;
    }

    public String getRoleText() {
        return role == 1 ? "管理员" : "学生";
    }
}
