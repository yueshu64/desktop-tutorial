package com.suse.lostfound.filter;

import com.suse.lostfound.service.UserService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/** 打开登录页时，如果库里没有任何用户，转到注册页。 */
public class LoginPageFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            if (new UserService().count() == 0) {
                req.setAttribute("msg", "数据库中还没有用户数据，请先注册账号");
                req.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
        } catch (RuntimeException e) {
            req.setAttribute("msg", "数据库连接失败，请确认 MySQL 已启动，并检查 db.properties 中的账号密码");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
