package com.suse.lostfound.listener;

import com.suse.lostfound.util.DBUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

@WebListener
public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        String path = event.getServletContext().getContextPath();
        System.out.println("[失物招领] 应用已启动，上下文路径: " + (path.isEmpty() ? "/" : path));
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            System.out.println("[失物招领] 数据库连接成功");
        } catch (Throwable e) {
            System.out.println("[失物招领] 数据库连接失败: " + e.getMessage());
        } finally {
            DBUtil.close(conn);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
