package com.example.music.dao;

import com.example.music.entity.Favorite;
import com.example.music.entity.Playlist;
import com.example.music.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏数据访问
 */
public class FavoriteDAO {

    private final PlaylistDAO playlistDAO = new PlaylistDAO();

    /** 查询某用户收藏的歌单 */
    public List<Favorite> findByUserId(Long userId) {
        String sql = "SELECT * FROM favorite WHERE user_id = ? ORDER BY create_time DESC";
        List<Favorite> list = new ArrayList<Favorite>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Favorite f = new Favorite();
                f.setId(rs.getLong("id"));
                f.setUserId(rs.getLong("user_id"));
                f.setPlaylistId(rs.getLong("playlist_id"));
                f.setCreateTime(rs.getTimestamp("create_time"));
                Playlist p = playlistDAO.findById(f.getPlaylistId());
                f.setPlaylist(p);
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 是否已收藏 */
    public boolean exists(Long userId, Long playlistId) {
        String sql = "SELECT COUNT(*) FROM favorite WHERE user_id = ? AND playlist_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return false;
    }

    /** 添加收藏（已存在则忽略） */
    public boolean add(Long userId, Long playlistId) {
        if (exists(userId, playlistId)) {
            return false;
        }
        String sql = "INSERT INTO favorite (user_id, playlist_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        return false;
    }

    /** 取消收藏 */
    public boolean remove(Long userId, Long playlistId) {
        String sql = "DELETE FROM favorite WHERE user_id = ? AND playlist_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, playlistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        return false;
    }

    /** 收藏数 */
    public int countByUser(Long userId) {
        String sql = "SELECT COUNT(*) FROM favorite WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    /**
     * 查询收藏了指定歌单的用户 id（用于协同过滤推荐）
     */
    public List<Long> findUserIdsByPlaylist(Long playlistId) {
        String sql = "SELECT user_id FROM favorite WHERE playlist_id = ?";
        List<Long> list = new ArrayList<Long>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, playlistId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }
}
