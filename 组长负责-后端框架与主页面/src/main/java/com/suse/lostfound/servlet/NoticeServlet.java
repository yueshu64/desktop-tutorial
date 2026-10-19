package com.suse.lostfound.servlet;

import com.suse.lostfound.service.NoticeService;
import com.suse.lostfound.util.WebUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 前台公告列表，游客也可以看。 */
@WebServlet("/notice")
public class NoticeServlet extends BaseServlet {
    private final NoticeService noticeService = new NoticeService();

    @Override
    protected void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("notices", noticeService.listAll());
        req.setAttribute("noticeReady", Boolean.TRUE);
        req.setAttribute("nav", "notice");
        req.getRequestDispatcher("/noticeList.jsp").forward(req, resp);
    }
}
