package com.suse.lostfound.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** JDBC 连接工具。账号密码放在 resources/db.properties。 */
public final class DBUtil {
    private static String url;
    private static String user;
    private static String password;
    private static String initError;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("未找到 db.properties");
            }
            Properties props = new Properties();
            props.load(in);
            Class.forName(props.getProperty("jdbc.driver", "com.mysql.cj.jdbc.Driver"));
            url = props.getProperty("jdbc.url");
            user = props.getProperty("jdbc.username");
            String envPwd = System.getenv("DB_PASSWORD");
            password = (envPwd != null && !envPwd.isEmpty()) ? envPwd : props.getProperty("jdbc.password");
        } catch (Exception e) {
            initError = e.getMessage();
            e.printStackTrace();
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        if (initError != null || url == null) {
            throw new SQLException("数据库配置加载失败：" + initError);
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // 回滚失败时保留原始异常
            }
        }
    }

    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
                // 关闭连接不影响主流程
            }
        }
    }
}
