package com.example.music.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 建表 + 演示数据初始化（应用启动时由 AppInitListener 调用）
 */
public class SchemaInit {

    /** 建表语句 */
    private static final String[] DDL = {
        // 用户表
        "CREATE TABLE IF NOT EXISTS user ("
            + " id BIGINT AUTO_INCREMENT PRIMARY KEY,"
            + " username VARCHAR(50) NOT NULL UNIQUE,"
            + " nickname VARCHAR(100),"
            + " avatar VARCHAR(50)"
            + ")",
        // 歌曲表
        "CREATE TABLE IF NOT EXISTS song ("
            + " id BIGINT AUTO_INCREMENT PRIMARY KEY,"
            + " title VARCHAR(200) NOT NULL,"
            + " artist VARCHAR(100),"
            + " category VARCHAR(50),"
            + " mood VARCHAR(50),"
            + " duration_sec INT DEFAULT 0,"
            + " play_count INT DEFAULT 0,"
            + " cover VARCHAR(100)"
            + ")",
        // 歌单表
        "CREATE TABLE IF NOT EXISTS playlist ("
            + " id BIGINT AUTO_INCREMENT PRIMARY KEY,"
            + " name VARCHAR(200) NOT NULL,"
            + " description VARCHAR(500),"
            + " category VARCHAR(50),"
            + " cover VARCHAR(100),"
            + " play_count INT DEFAULT 0,"
            + " create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")",
        // 标签表
        "CREATE TABLE IF NOT EXISTS tag ("
            + " id BIGINT AUTO_INCREMENT PRIMARY KEY,"
            + " name VARCHAR(50) NOT NULL UNIQUE,"
            + " category VARCHAR(50)"
            + ")",
        // 歌单-歌曲关联表
        "CREATE TABLE IF NOT EXISTS playlist_song ("
            + " playlist_id BIGINT NOT NULL,"
            + " song_id BIGINT NOT NULL,"
            + " sort_order INT DEFAULT 0,"
            + " PRIMARY KEY (playlist_id, song_id)"
            + ")",
        // 歌单-标签关联表
        "CREATE TABLE IF NOT EXISTS playlist_tag ("
            + " playlist_id BIGINT NOT NULL,"
            + " tag_id BIGINT NOT NULL,"
            + " PRIMARY KEY (playlist_id, tag_id)"
            + ")",
        // 收藏表
        "CREATE TABLE IF NOT EXISTS favorite ("
            + " id BIGINT AUTO_INCREMENT PRIMARY KEY,"
            + " user_id BIGINT NOT NULL,"
            + " playlist_id BIGINT NOT NULL,"
            + " create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + " UNIQUE (user_id, playlist_id)"
            + ")"
    };

    /** 演示数据：标签 */
    private static final String[][] TAGS = {
        {"雨声", "自然声"}, {"森林", "自然声"}, {"海浪", "自然声"}, {"鸟鸣", "自然声"},
        {"溪流", "自然声"}, {"风声", "自然声"}, {"篝火", "自然声"}, {"虫鸣", "自然声"},
        {"助眠", "场景"}, {"专注", "场景"}, {"放松", "情绪"}, {"冥想", "场景"},
        {"白噪音", "场景"}, {"清晨", "场景"}, {"夜晚", "场景"}
    };

    /** 演示数据：歌曲（自然音乐） */
    private static final Object[][] SONGS = {
        {"春雨淅沥", "自然之声", "雨声", "放松", 1860, 12000, "🌧️"},
        {"雨林深处", "自然之声", "雨声", "专注", 2400, 8600, "🌴"},
        {"雷雨之夜", "自然之声", "雨声", "助眠", 3600, 15200, "⛈️"},
        {"松林微风", "自然之声", "森林", "放松", 1980, 9800, "🌲"},
        {"森林鸟鸣", "自然之声", "森林", "放松", 1620, 7400, "🐦"},
        {"清晨鸟语", "自然之声", "鸟鸣", "放松", 1500, 11200, "🌅"},
        {"海浪轻拍", "自然之声", "海浪", "助眠", 2700, 13400, "🌊"},
        {"海边日落", "自然之声", "海浪", "放松", 2100, 6900, "🏖️"},
        {"溪流潺潺", "自然之声", "溪流", "专注", 2280, 10500, "🏞️"},
        {"溪畔石子", "自然之声", "溪流", "放松", 1740, 5200, "🪨"},
        {"山谷清风", "自然之声", "风声", "放松", 1920, 6100, "🍃"},
        {"夏夜虫鸣", "自然之声", "虫鸣", "助眠", 2100, 8300, "🦗"},
        {"篝火噼啪", "自然之声", "篝火", "放松", 2400, 9700, "🔥"},
        {"静心冥想", "自然之声", "风声", "冥想", 1800, 4500, "🧘"}
    };

    /** 演示数据：歌单 {名称, 描述, 分类, 封面, 播放量} */
    private static final Object[][] PLAYLISTS = {
        {"雨声助眠精选", "淅沥雨声伴你入眠，缓解失眠与焦虑", "助眠", "🌧️", 52000},
        {"森林漫步", "漫步林间，听风穿松叶与鸟鸣", "放松", "🌲", 38000},
        {"海浪冥想", "规律潮声，帮助进入冥想状态", "冥想", "🌊", 41000},
        {"专注学习白噪音", "屏蔽干扰，提升专注力的白噪音", "专注", "🎧", 68000},
        {"深睡雨夜", "雷雨与夜色，整夜安稳睡眠", "助眠", "⛈️", 45000},
        {"清晨鸟语唤醒", "轻柔鸟鸣，自然醒来的仪式感", "放松", "🌅", 29000},
        {"溪畔静心", "溪水潺潺，安抚烦躁的情绪", "放松", "🏞️", 33000},
        {"篝火夜话", "噼啪火声，适合深夜独处与阅读", "放松", "🔥", 26000}
    };

    /** 歌单包含的歌曲索引（从 1 开始，对应 SONGS 顺序） */
    private static final int[][] PLAYLIST_SONGS = {
        {1, 2, 3},          // 雨声助眠精选
        {4, 5, 11},         // 森林漫步
        {7, 8, 14},         // 海浪冥想
        {2, 9, 10},         // 专注学习白噪音
        {3, 12, 1},         // 深睡雨夜
        {6, 5, 4},          // 清晨鸟语唤醒
        {9, 10, 11},        // 溪畔静心
        {13, 12, 11}        // 篝火夜话
    };

    /** 歌单标签名（对应 TAGS 中的名称） */
    private static final String[][] PLAYLIST_TAGS = {
        {"雨声", "助眠", "夜晚"},     // 雨声助眠精选
        {"森林", "放松", "清晨"},     // 森林漫步
        {"海浪", "冥想", "放松"},     // 海浪冥想
        {"白噪音", "专注", "溪流"},   // 专注学习白噪音
        {"雨声", "助眠", "夜晚"},     // 深睡雨夜
        {"鸟鸣", "清晨", "森林"},     // 清晨鸟语唤醒
        {"溪流", "放松", "专注"},     // 溪畔静心
        {"篝火", "放松", "夜晚"}      // 篝火夜话
    };

    /** 演示用户 */
    private static final Object[][] USERS = {
        {"demo", "小明", "😀"},
        {"alice", "小雅", "🌸"},
        {"bob", "阿哲", "🎵"}
    };

    /** 初始收藏：{用户索引(1起), 歌单索引(1起)} */
    private static final int[][] INITIAL_FAVORITES = {
        {1, 1}, {1, 5}, {1, 4},   // 小明收藏：雨声助眠精选、深睡雨夜、专注学习白噪音
        {2, 2}, {2, 6}, {2, 7},   // 小雅收藏：森林漫步、清晨鸟语唤醒、溪畔静心
        {3, 3}, {3, 8}, {3, 1}    // 阿哲收藏：海浪冥想、篝火夜话、雨声助眠精选
    };

    public static void init() {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            Statement st = conn.createStatement();

            // 1. 建表
            for (String sql : DDL) {
                st.execute(sql);
            }

            // 2. 只在首次（歌曲表为空）时插入演示数据
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM song");
            rs.next();
            int songCount = rs.getInt(1);
            rs.close();

            if (songCount == 0) {
                insertAll(conn);
                System.out.println("[SchemaInit] 演示数据初始化完成");
            }
            st.close();
            System.out.println("[SchemaInit] 数据库就绪: " + DBUtil.getDbPath());
        } catch (SQLException e) {
            System.err.println("[SchemaInit] 初始化失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    /** 插入全部演示数据 */
    private static void insertAll(Connection conn) throws SQLException {
        insertUsers(conn);
        insertTags(conn);
        insertSongs(conn);
        insertPlaylists(conn);
        insertPlaylistSongs(conn);
        insertPlaylistTags(conn);
        insertFavorites(conn);
    }

    private static void insertUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO user (username, nickname, avatar) VALUES (?, ?, ?)";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (Object[] u : USERS) {
            ps.setString(1, (String) u[0]);
            ps.setString(2, (String) u[1]);
            ps.setString(3, (String) u[2]);
            ps.executeUpdate();
        }
        ps.close();
    }

    private static void insertTags(Connection conn) throws SQLException {
        String sql = "INSERT INTO tag (name, category) VALUES (?, ?)";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (String[] t : TAGS) {
            ps.setString(1, t[0]);
            ps.setString(2, t[1]);
            ps.executeUpdate();
        }
        ps.close();
    }

    private static void insertSongs(Connection conn) throws SQLException {
        String sql = "INSERT INTO song (title, artist, category, mood, duration_sec, play_count, cover)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (Object[] s : SONGS) {
            ps.setString(1, (String) s[0]);
            ps.setString(2, (String) s[1]);
            ps.setString(3, (String) s[2]);
            ps.setString(4, (String) s[3]);
            ps.setInt(5, (Integer) s[4]);
            ps.setInt(6, (Integer) s[5]);
            ps.setString(7, (String) s[6]);
            ps.executeUpdate();
        }
        ps.close();
    }

    private static void insertPlaylists(Connection conn) throws SQLException {
        String sql = "INSERT INTO playlist (name, description, category, cover, play_count)"
                + " VALUES (?, ?, ?, ?, ?)";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (Object[] p : PLAYLISTS) {
            ps.setString(1, (String) p[0]);
            ps.setString(2, (String) p[1]);
            ps.setString(3, (String) p[2]);
            ps.setString(4, (String) p[3]);
            ps.setInt(5, (Integer) p[4]);
            ps.executeUpdate();
        }
        ps.close();
    }

    private static void insertPlaylistSongs(Connection conn) throws SQLException {
        String sql = "INSERT INTO playlist_song (playlist_id, song_id, sort_order) VALUES (?, ?, ?)";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < PLAYLIST_SONGS.length; i++) {
            int playlistId = i + 1;
            int[] songIds = PLAYLIST_SONGS[i];
            for (int j = 0; j < songIds.length; j++) {
                ps.setLong(1, playlistId);
                ps.setLong(2, songIds[j]);
                ps.setInt(3, j + 1);
                ps.executeUpdate();
            }
        }
        ps.close();
    }

    private static void insertPlaylistTags(Connection conn) throws SQLException {
        String tagIdSql = "SELECT id FROM tag WHERE name = ?";
        String sql = "INSERT INTO playlist_tag (playlist_id, tag_id) VALUES (?, ?)";
        java.sql.PreparedStatement idPs = conn.prepareStatement(tagIdSql);
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < PLAYLIST_TAGS.length; i++) {
            int playlistId = i + 1;
            for (String tagName : PLAYLIST_TAGS[i]) {
                idPs.setString(1, tagName);
                ResultSet rs = idPs.executeQuery();
                if (rs.next()) {
                    ps.setLong(1, playlistId);
                    ps.setLong(2, rs.getLong(1));
                    ps.executeUpdate();
                }
                rs.close();
            }
        }
        idPs.close();
        ps.close();
    }

    private static void insertFavorites(Connection conn) throws SQLException {
        String sql = "INSERT INTO favorite (user_id, playlist_id) VALUES (?, ?)";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        for (int[] f : INITIAL_FAVORITES) {
            ps.setLong(1, f[0]);
            ps.setLong(2, f[1]);
            ps.executeUpdate();
        }
        ps.close();
    }
}
