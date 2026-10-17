package com.suse.lostfound.filter;

import com.suse.lostfound.entity.User;
import com.suse.lostfound.util.WebUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 未登录不能发布、改资料，也不能进后台。
 * 学生不能进管理员页面。列表、详情、公告和登录注册保持开放。
 */
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = path(req);
        User login = current(req);

        if (path.equals("/admin") || path.startsWith("/admin/")) {
            if (login == null) {
                denyGuest(req, resp);
                return;
            }
            if (!login.isAdmin()) {
                WebUtil.flash(req, "学生账号不能进入管理员页面", "warn");
                WebUtil.redirect(req, resp, "/item?action=list");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        if (isPublic(path, req.getParameter("action"))) {
            chain.doFilter(request, response);
            return;
        }

        if (login == null) {
            denyGuest(req, resp);
            return;
        }
        chain.doFilter(request, response);
    }

    /** 列表和详情公开。登录、注册、退出也公开。发布、修改、个人中心和后台需要登录。 */
    private boolean isPublic(String path, String action) {
        if ("/item".equals(path)) {
            return action == null || action.isEmpty() || "list".equals(action) || "detail".equals(action);
        }
        if ("/user".equals(path)) {
            return action == null || action.isEmpty() || "login".equals(action) || "toLogin".equals(action)
                    || "register".equals(action) || "toRegister".equals(action) || "logout".equals(action);
        }
        return false;
    }

    private void denyGuest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (WebUtil.isAjax(req)) {
            WebUtil.writeJson(resp, "{\"ok\":false,\"needLogin\":true,\"msg\":\"你为游客不可进行对应功能\"}");
            return;
        }
        WebUtil.flash(req, "你为游客不可进行对应功能，请先登录", "warn");
        String from = req.getRequestURI();
        if (req.getQueryString() != null) {
            from = from + "?" + req.getQueryString();
        }
        resp.sendRedirect(req.getContextPath() + "/user?action=toLogin&from=" + WebUtil.urlEncode(from));
    }

    private User current(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute("loginUser");
        return obj instanceof User ? (User) obj : null;
    }

    private String path(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            return uri.substring(ctx.length());
        }
        return uri;
    }

    @Override
    public void destroy() {
    }
}
