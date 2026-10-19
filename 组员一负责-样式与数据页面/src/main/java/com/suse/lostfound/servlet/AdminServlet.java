package com.suse.lostfound.servlet;

import com.suse.lostfound.entity.Notice;
import com.suse.lostfound.entity.User;
import com.suse.lostfound.service.LostItemService;
import com.suse.lostfound.service.NoticeService;
import com.suse.lostfound.service.UserService;
import com.suse.lostfound.util.BizException;
import com.suse.lostfound.util.WebUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 管理员后台：概览、信息、用户、公告和个人中心。 */
@WebServlet("/admin")
public class AdminServlet extends BaseServlet {
    private final LostItemService itemService = new LostItemService();
    private final UserService userService = new UserService();
    private final NoticeService noticeService = new NoticeService();

    /** 后台只允许管理员。学生和游客在这里被拦下，并给出对应提示。 */
    @Override
    protected void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        if (login == null || !login.isAdmin()) {
            WebUtil.flash(req, login == null ? "你为游客不可进行对应功能，请先登录" : "学生账号不能进入管理员页面", "warn");
            WebUtil.redirect(req, resp, login == null ? "/user?action=toLogin" : "/item?action=list");
            return;
        }
        String action = req.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "dashboard";
        }
        switch (action) {
            case "dashboard":
                dashboard(req, resp);
                break;
            case "items":
                items(req, resp);
                break;
            case "deleteItem":
                deleteItem(req, resp, login);
                break;
            case "users":
                users(req, resp);
                break;
            case "deleteUser":
                deleteUser(req, resp, login);
                break;
            case "notice":
                notice(req, resp, 0);
                break;
            case "saveNotice":
                saveNotice(req, resp, login);
                break;
            case "deleteNotice":
                deleteNotice(req, resp);
                break;
            case "profile":
                profile(req, resp, login);
                break;
            default:
                WebUtil.redirect(req, resp, "/admin?action=dashboard");
        }
    }

    private void dashboard(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("totalItems", itemService.count(null, null));
        req.setAttribute("solvedItems", itemService.count(null, 1));
        req.setAttribute("pendingItems", itemService.count(null, 0));
        req.setAttribute("closedItems", itemService.count(null, 2));
        req.setAttribute("userCount", userService.count());
        req.setAttribute("noticeCount", noticeService.count());
        req.setAttribute("latestItems", itemService.page(null, null, null, 1, 5).getRecords());
        req.setAttribute("dashboardReady", Boolean.TRUE);
        req.getRequestDispatcher("/admin/index.jsp").forward(req, resp);
    }

    private void items(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String type = WebUtil.trim(req, "type");
        if (!"丢失".equals(type) && !"拾到".equals(type)) {
            type = "";
        }
        String keyword = WebUtil.trim(req, "keyword");
        String statusFilter = WebUtil.trim(req, "status");
        Integer status = null;
        if ("0".equals(statusFilter) || "1".equals(statusFilter) || "2".equals(statusFilter)) {
            status = Integer.valueOf(statusFilter);
        } else {
            statusFilter = "";
        }
        int page = WebUtil.intParam(req, "page", 1);
        req.setAttribute("itemPage", itemService.page(type, keyword, status, page, 8));
        req.setAttribute("type", type);
        req.setAttribute("keyword", keyword);
        req.setAttribute("statusFilter", statusFilter);
        req.getRequestDispatcher("/admin/itemList.jsp").forward(req, resp);
    }

    private void deleteItem(HttpServletRequest req, HttpServletResponse resp, User login) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            WebUtil.redirect(req, resp, "/admin?action=items");
            return;
        }
        try {
            itemService.deleteByAdmin(WebUtil.intParam(req, "id", 0), login.getName());
            WebUtil.flash(req, "违规信息已删除，系统已自动发布公告", "ok");
        } catch (BizException e) {
            WebUtil.flash(req, e.getMessage(), "warn");
        }
        WebUtil.redirect(req, resp, "/admin?action=items");
    }

    private void users(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("users", userService.listUsers());
        req.setAttribute("usersReady", Boolean.TRUE);
        req.getRequestDispatcher("/admin/userList.jsp").forward(req, resp);
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp, User login) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            WebUtil.redirect(req, resp, "/admin?action=users");
            return;
        }
        try {
            userService.deleteStudent(WebUtil.intParam(req, "id", 0), login.getName());
            WebUtil.flash(req, "学生账号已删除，系统已自动发布公告", "ok");
        } catch (BizException e) {
            WebUtil.flash(req, e.getMessage(), "warn");
        }
        WebUtil.redirect(req, resp, "/admin?action=users");
    }

    private void notice(HttpServletRequest req, HttpServletResponse resp, int editId) throws Exception {
        if (editId <= 0) {
            editId = WebUtil.intParam(req, "id", 0);
        }
        if (editId > 0 && req.getAttribute("editNotice") == null) {
            Notice notice = noticeService.find(editId);
            if (notice == null) {
                WebUtil.flash(req, "公告不存在或已删除", "warn");
                WebUtil.redirect(req, resp, "/admin?action=notice");
                return;
            }
            req.setAttribute("editNotice", notice);
        }
        req.setAttribute("notices", noticeService.listAll());
        req.setAttribute("noticesReady", Boolean.TRUE);
        req.getRequestDispatcher("/admin/publishNotice.jsp").forward(req, resp);
    }

    private void saveNotice(HttpServletRequest req, HttpServletResponse resp, User login) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            WebUtil.redirect(req, resp, "/admin?action=notice");
            return;
        }
        int id = WebUtil.intParam(req, "id", 0);
        String title = WebUtil.trim(req, "title");
        String content = req.getParameter("content") == null ? "" : req.getParameter("content").trim();
        try {
            noticeService.saveByAdmin(id, title, content, login.getUserId());
            WebUtil.flash(req, id > 0 ? "公告已更新" : "公告已发布", "ok");
            WebUtil.redirect(req, resp, "/admin?action=notice");
        } catch (BizException e) {
            Notice draft = new Notice();
            draft.setNoticeId(id);
            draft.setTitle(title);
            draft.setContent(content);
            draft.setAdminId(login.getUserId());
            req.setAttribute("msg", e.getMessage());
            req.setAttribute("editNotice", draft);
            notice(req, resp, id);
        }
    }

    private void deleteNotice(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            WebUtil.redirect(req, resp, "/admin?action=notice");
            return;
        }
        try {
            noticeService.delete(WebUtil.intParam(req, "id", 0));
            WebUtil.flash(req, "公告已删除", "ok");
        } catch (BizException e) {
            WebUtil.flash(req, e.getMessage(), "warn");
        }
        WebUtil.redirect(req, resp, "/admin?action=notice");
    }

    private void profile(HttpServletRequest req, HttpServletResponse resp, User login) throws Exception {
        User fresh = userService.findById(login.getUserId());
        if (fresh != null) {
            fresh.setPassword(null);
            req.setAttribute("profile", fresh);
        }
        req.getRequestDispatcher("/admin/profile.jsp").forward(req, resp);
    }
}
