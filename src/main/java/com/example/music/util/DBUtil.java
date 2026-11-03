package com.example.music.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具（H2 文件数据库，数据持久化在用户主目录）
 */
public class DBUtil {

    /** 数据库文件目录：C:\Users\<用户名>\javatest-music\music.mv.db */
    private static final String DB_PATH =
            (System.getProperty("user.home") + "/javatest-music/music").replace("\\", "/");

    private static final String URL = "jdbc:h2:file:" + DB_PATH + ";AUTO_SERVER=TRUE;MODE=MySQL";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 驱动加载失败，请确认 h2 jar 在 WEB-INF/lib 下", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn, Statement st, ResultSet rs) {
        close(rs);
        close(st);
        close(conn);
    }

    public static void close(AutoCloseable... resources) {
        if (resources == null) {
            return;
        }
        for (AutoCloseable r : resources) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignored) {
                    // 关闭失败忽略
                }
            }
        }
    }

    public static String getDbPath() {
        return DB_PATH;
    }
}
