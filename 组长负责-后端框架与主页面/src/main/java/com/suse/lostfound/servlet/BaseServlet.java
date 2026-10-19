package com.suse.lostfound.servlet;

import com.suse.lostfound.util.BizException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 统一接住未处理异常，避免把数据库报错直接甩到页面上。 */
public abstract class BaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    /** 业务异常显示提示页，其它异常不把数据库报错直接暴露给浏览器。 */
    private void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            execute(req, resp);
        } catch (BizException e) {
            req.setAttribute("msg", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("msg", "系统繁忙或数据库连接失败，请稍后再试");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    protected abstract void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
