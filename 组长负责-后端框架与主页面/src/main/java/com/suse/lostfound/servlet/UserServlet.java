package com.suse.lostfound.servlet;

import com.suse.lostfound.entity.User;
import com.suse.lostfound.service.UserService;
import com.suse.lostfound.util.BizException;
import com.suse.lostfound.util.WebUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** 登录、注册、退出、个人资料和密码。 */
@WebServlet("/user")
public class UserServlet extends BaseServlet {
    private final UserService userService = new UserService();

    @Override
    protected void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = req.getParameter("action");
        if (action == null) {
            action = "toLogin";
        }
        switch (action) {
            case "toLogin":
                toLogin(req, resp);
                break;
            case "login":
                login(req, resp);
                break;
            case "toRegister":
                req.setAttribute("nav", "register");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                break;
            case "register":
                register(req, resp);
                break;
            case "logout":
                logout(req, resp);
                break;
            case "profile":
                showProfile(req, resp);
                break;
            case "updateProfile":
                updateProfile(req, resp);
                break;
            case "updatePassword":
                updatePassword(req, resp);
                break;
            default:
                WebUtil.redirect(req, resp, "/user?action=toLogin");
        }
    }

    private void toLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (userService.count() == 0) {
            req.setAttribute("msg", "数据库中还没有用户数据，请先注册账号");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("from", WebUtil.safeFrom(req, req.getParameter("from")));
        req.setAttribute("nav", "login");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    /**
     * 登录成功后换一个新 session，避免沿用游客会话。
     * 管理员进入后台，学生回到首页；如果是从某个页面跳来登录的，则回到那个页面。
     */
    private void login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (userService.count() == 0) {
            req.setAttribute("msg", "数据库中还没有用户数据，请先注册账号");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        try {
            User user = userService.login(req.getParameter("username"), req.getParameter("password"));
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            req.getSession(true).setAttribute("loginUser", user);
            if (user.isAdmin()) {
                WebUtil.redirect(req, resp, "/admin?action=dashboard");
                return;
            }
            String from = WebUtil.safeFrom(req, req.getParameter("from"));
            if (from != null) {
                resp.sendRedirect(from);
            } else {
                WebUtil.redirect(req, resp, "/item?action=list");
            }
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            req.setAttribute("username", WebUtil.trim(req, "username"));
            req.setAttribute("from", WebUtil.safeFrom(req, req.getParameter("from")));
            req.setAttribute("nav", "login");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            userService.register(
                    req.getParameter("username"),
                    req.getParameter("password"),
                    req.getParameter("confirm"),
                    req.getParameter("name"),
                    req.getParameter("phone"));
            WebUtil.flash(req, "注册成功，请登录", "ok");
            WebUtil.redirect(req, resp, "/user?action=toLogin");
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            req.setAttribute("username", WebUtil.trim(req, "username"));
            req.setAttribute("name", WebUtil.trim(req, "name"));
            req.setAttribute("phone", WebUtil.trim(req, "phone"));
            req.setAttribute("nav", "register");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        WebUtil.redirect(req, resp, "/item?action=list");
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        if (login != null && login.isAdmin()) {
            WebUtil.redirect(req, resp, "/admin?action=profile");
            return;
        }
        prepareProfile(req, login, null, null);
        req.setAttribute("nav", "profile");
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    private void updateProfile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        String name = WebUtil.trim(req, "name");
        String phone = WebUtil.trim(req, "phone");
        try {
            userService.updateProfile(login.getUserId(), name, phone);
            User fresh = userService.findById(login.getUserId());
            fresh.setPassword(null);
            req.getSession().setAttribute("loginUser", fresh);
            WebUtil.flash(req, "个人资料已更新", "ok");
            WebUtil.redirect(req, resp, fresh.isAdmin() ? "/admin?action=profile" : "/user?action=profile");
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            prepareProfile(req, login, name, phone);
            forwardProfile(req, resp, login);
        }
    }

    private void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        try {
            userService.changePassword(login.getUserId(),
                    req.getParameter("oldPassword"),
                    req.getParameter("newPassword"),
                    req.getParameter("confirm"));
            WebUtil.flash(req, "密码已修改，请记住新密码", "ok");
            WebUtil.redirect(req, resp, login.isAdmin() ? "/admin?action=profile" : "/user?action=profile");
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            prepareProfile(req, login, null, null);
            forwardProfile(req, resp, login);
        }
    }

    private void prepareProfile(HttpServletRequest req, User login, String name, String phone) {
        User profile = userService.findById(login.getUserId());
        if (profile == null) {
            profile = login;
        }
        profile.setPassword(null);
        if (name != null) {
            profile.setName(name);
        }
        if (phone != null) {
            profile.setPhone(phone);
        }
        req.setAttribute("profile", profile);
    }

    private void forwardProfile(HttpServletRequest req, HttpServletResponse resp, User login) throws Exception {
        if (login.isAdmin()) {
            req.getRequestDispatcher("/admin/profile.jsp").forward(req, resp);
        } else {
            req.setAttribute("nav", "profile");
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        }
    }
}
