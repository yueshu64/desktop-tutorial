package com.suse.lostfound.servlet;

import com.suse.lostfound.entity.LostItem;
import com.suse.lostfound.entity.User;
import com.suse.lostfound.service.LostItemService;
import com.suse.lostfound.service.NoticeService;
import com.suse.lostfound.util.BizException;
import com.suse.lostfound.util.PageResult;
import com.suse.lostfound.util.WebUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 失物信息入口，地址是 /item。
 * action 决定具体功能：list 列表、detail 详情、publish 发布、my 我的发布，
 * 以及修改、删除、标记已解决和关闭。列表和详情对游客开放，其余动作由 LoginFilter 要求登录。
 */
@WebServlet("/item")
public class ItemServlet extends BaseServlet {
    private final LostItemService itemService = new LostItemService();
    private final NoticeService noticeService = new NoticeService();

    @Override
    protected void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = req.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        switch (action) {
            case "list":
                list(req, resp);
                break;
            case "detail":
                detail(req, resp);
                break;
            case "publishPage":
                req.setAttribute("nav", "publish");
                req.getRequestDispatcher("/publish.jsp").forward(req, resp);
                break;
            case "publish":
                publish(req, resp);
                break;
            case "my":
                my(req, resp);
                break;
            case "edit":
                edit(req, resp);
                break;
            case "update":
                update(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            case "resolve":
                change(req, resp, true);
                break;
            case "close":
                change(req, resp, false);
                break;
            default:
                WebUtil.redirect(req, resp, "/item?action=list");
        }
    }

    /** 首页列表。可按丢失/拾到和关键词筛选，每页 6 条，同时带上右侧最新公告。 */
    private void list(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String type = normalizeType(WebUtil.trim(req, "type"));
        String keyword = WebUtil.trim(req, "keyword");
        if (keyword.length() > 30) {
            keyword = keyword.substring(0, 30);
        }
        int page = WebUtil.intParam(req, "page", 1);
        PageResult<LostItem> itemPage = itemService.page(type, keyword, null, page, 6);
        req.setAttribute("itemPage", itemPage);
        req.setAttribute("type", type);
        req.setAttribute("keyword", keyword);
        req.setAttribute("notices", noticeService.latest(4));
        req.setAttribute("pendingLost", itemService.count("丢失", 0));
        req.setAttribute("pendingFound", itemService.count("拾到", 0));
        req.setAttribute("solvedCount", itemService.count(null, 1));
        req.setAttribute("nav", "home");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = WebUtil.intParam(req, "id", 0);
        LostItem item = id > 0 ? itemService.findById(id) : null;
        if (item == null) {
            WebUtil.flash(req, "没有找到这条失物信息", "warn");
            WebUtil.redirect(req, resp, "/item?action=list");
            return;
        }
        req.setAttribute("item", item);
        req.setAttribute("nav", "home");
        req.getRequestDispatcher("/detail.jsp").forward(req, resp);
    }

    /** 读取表单并发布。校验失败时把已填内容带回发布页。 */
    private void publish(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        LostItem item = readItem(req);
        item.setUserId(login.getUserId());
        try {
            int id = itemService.publish(item, login.getName());
            WebUtil.flash(req, "发布成功，系统已自动发布公告", "ok");
            WebUtil.redirect(req, resp, "/item?action=detail&id=" + id);
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            req.setAttribute("draft", item);
            req.setAttribute("nav", "publish");
            req.getRequestDispatcher("/publish.jsp").forward(req, resp);
        }
    }

    private void my(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        req.setAttribute("items", itemService.listByUser(login.getUserId()));
        req.setAttribute("itemsReady", Boolean.TRUE);
        req.setAttribute("nav", "my");
        req.getRequestDispatcher("/myItems.jsp").forward(req, resp);
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        LostItem item = itemService.findById(WebUtil.intParam(req, "id", 0));
        if (item == null || item.getUserId() != login.getUserId()) {
            WebUtil.flash(req, "只能修改自己发布的信息", "warn");
            WebUtil.redirect(req, resp, "/item?action=my");
            return;
        }
        if (item.getStatus() == 2) {
            WebUtil.flash(req, "已关闭的信息不能再修改", "warn");
            WebUtil.redirect(req, resp, "/item?action=my");
            return;
        }
        req.setAttribute("item", item);
        req.setAttribute("nav", "my");
        req.getRequestDispatcher("/editItem.jsp").forward(req, resp);
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User login = WebUtil.current(req);
        LostItem item = readItem(req);
        try {
            itemService.update(item, login.getUserId());
            WebUtil.flash(req, "信息已修改", "ok");
            WebUtil.redirect(req, resp, "/item?action=detail&id=" + item.getItemId());
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            if (item.getPublisherName() == null) {
                LostItem db = itemService.findById(item.getItemId());
                if (db != null) {
                    item.setCreateTime(db.getCreateTime());
                    item.setStatus(db.getStatus());
                }
            }
            req.setAttribute("item", item);
            req.setAttribute("nav", "my");
            req.getRequestDispatcher("/editItem.jsp").forward(req, resp);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            WebUtil.redirect(req, resp, "/item?action=my");
            return;
        }
        User login = WebUtil.current(req);
        try {
            itemService.deleteOwn(WebUtil.intParam(req, "id", 0), login.getUserId(), login.getName());
            WebUtil.flash(req, "已删除，系统已自动发布公告", "ok");
        } catch (BizException e) {
            WebUtil.flash(req, e.getMessage(), "warn");
        }
        WebUtil.redirect(req, resp, "/item?action=my");
    }

    /** resolve 为 true 时标记已解决，否则关闭。back=detail 时操作完回到详情页。 */
    private void change(HttpServletRequest req, HttpServletResponse resp, boolean resolve) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            WebUtil.redirect(req, resp, "/item?action=my");
            return;
        }
        User login = WebUtil.current(req);
        int id = WebUtil.intParam(req, "id", 0);
        String back = "detail".equals(req.getParameter("back")) ? "detail" : "my";
        try {
            if (resolve) {
                itemService.resolve(id, login.getUserId(), login.getName());
                WebUtil.flash(req, "已标记为已解决，系统已自动发布公告", "ok");
            } else {
                itemService.close(id, login.getUserId(), login.getName());
                WebUtil.flash(req, "信息已关闭，系统已自动发布公告", "ok");
            }
        } catch (BizException e) {
            WebUtil.flash(req, e.getMessage(), "warn");
        }
        if ("detail".equals(back)) {
            WebUtil.redirect(req, resp, "/item?action=detail&id=" + id);
        } else {
            WebUtil.redirect(req, resp, "/item?action=my");
        }
    }

    private LostItem readItem(HttpServletRequest req) {
        LostItem item = new LostItem();
        item.setItemId(WebUtil.intParam(req, "id", 0));
        item.setTitle(WebUtil.trim(req, "title"));
        item.setItemType(WebUtil.trim(req, "itemType"));
        item.setLocation(WebUtil.trim(req, "location"));
        item.setContact(WebUtil.trim(req, "contact"));
        String description = req.getParameter("description");
        item.setDescription(description == null ? "" : description.trim());
        return item;
    }

    private String normalizeType(String type) {
        if ("丢失".equals(type) || "拾到".equals(type)) {
            return type;
        }
        return "";
    }
}
