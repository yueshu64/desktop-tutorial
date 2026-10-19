package com.suse.lostfound.service;

import com.suse.lostfound.dao.UserDao;
import com.suse.lostfound.entity.User;
import com.suse.lostfound.util.BizException;
import com.suse.lostfound.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** 注册、登录、资料与密码。注册成功并写入数据库后自动发布系统公告。 */
public class UserService {
    private final UserDao userDao = new UserDao();
    private final NoticeService noticeService = new NoticeService();

    public int count() {
        return userDao.count();
    }

    /**
     * 登录成功后返回用户，密码不会放进返回对象。
     * 学生和 11 位管理员学号都能登录；12 位限制只用于注册。
     */
    public User login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            throw new BizException("请输入用户名和密码");
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            throw new BizException("用户名不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new BizException("密码错误");
        }
        user.setPassword(null);
        return user;
    }

    /** 自助注册一律是学生。学号必须是 12 位数字，写入成功后自动发系统公告。 */
    public void register(String username, String password, String confirm, String name, String phone) {
        username = checkUsername(username);
        password = checkPassword(password);
        if (!password.equals(confirm)) {
            throw new BizException("两次输入的密码不一致");
        }
        name = checkName(name);
        phone = checkPhone(phone);
        if (userDao.findByUsername(username) != null) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setPhone(phone);
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            userDao.insert(conn, user);
            noticeService.writeSystem(conn,
                    "系统通知：新用户「" + name + "」完成注册",
                    "用户「" + name + "」（账号 " + username + "）已注册为学生。数据库校验并写入成功后，本公告由系统自动发布。");
            conn.commit();
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            throw new RuntimeException("注册失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    public User findById(int userId) {
        return userDao.findById(userId);
    }

    public List<User> listUsers() {
        List<User> users = userDao.findAll();
        for (User user : users) {
            user.setPassword(null);
        }
        return users;
    }

    public void updateProfile(int userId, String name, String phone) {
        name = checkName(name);
        phone = checkPhone(phone);
        userDao.updateProfile(userId, name, phone);
    }

    public void changePassword(int userId, String oldPassword, String newPassword, String confirm) {
        newPassword = checkPassword(newPassword);
        if (!newPassword.equals(confirm)) {
            throw new BizException("两次输入的新密码不一致");
        }
        User db = userDao.findById(userId);
        if (db == null) {
            throw new BizException("用户不存在");
        }
        if (oldPassword == null || !db.getPassword().equals(oldPassword)) {
            throw new BizException("原密码不正确");
        }
        userDao.updatePassword(userId, newPassword);
    }

    /** 只能删除学生。该学生发布的失物随外键一并删除，并写一条系统公告。 */
    public void deleteStudent(int userId, String operatorName) {
        User target = userDao.findById(userId);
        if (target == null) {
            throw new BizException("用户不存在或已删除");
        }
        if (target.isAdmin()) {
            throw new BizException("不能删除管理员账号");
        }
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            int rows = userDao.deleteStudent(conn, userId);
            if (rows == 0) {
                throw new BizException("只能删除学生账号");
            }
            noticeService.writeSystem(conn,
                    "系统通知：管理员移除了用户「" + target.getName() + "」",
                    "管理员删除了学生账号「" + target.getName() + "」（"
                            + target.getUsername() + "）。该用户发布的失物信息已一并删除。本公告在数据库更新成功后自动发布。");
            conn.commit();
        } catch (BizException e) {
            DBUtil.rollback(conn);
            throw e;
        } catch (SQLException e) {
            DBUtil.rollback(conn);
            throw new RuntimeException("删除用户失败", e);
        } finally {
            DBUtil.close(conn);
        }
    }

    /** 新注册账号必须是 12 位学号：前两位入学学年，末三位班内学号。 */
    private String checkUsername(String username) {
        if (username == null) {
            throw new BizException("请填写用户名");
        }
        username = username.trim();
        if (!username.matches("^\\d{12}$")) {
            throw new BizException("请使用 12 位学号注册。前两位是入学学年，后三位是班内学号");
        }
        return username;
    }

    private String checkPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            throw new BizException("密码长度需为 6 到 20 位");
        }
        return password;
    }

    private String checkName(String name) {
        if (name == null) {
            throw new BizException("请填写姓名");
        }
        name = name.trim();
        if (name.isEmpty() || name.length() > 20) {
            throw new BizException("姓名需在 1 到 20 字之间");
        }
        return name;
    }

    private String checkPhone(String phone) {
        if (phone == null) {
            return "";
        }
        phone = phone.trim();
        if (!phone.isEmpty() && !phone.matches("^1\\d{10}$")) {
            throw new BizException("手机号需为 11 位，或留空");
        }
        return phone;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
