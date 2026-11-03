package com.example.music.dao;

import com.example.music.entity.Song;
import com.example.music.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲数据访问
 */
public class SongDAO {

    /** 查询全部歌曲（按播放量倒序） */
    public List<Song> findAll() {
        String sql = "SELECT * FROM song ORDER BY play_count DESC";
        return query(sql);
    }

    /** 按 id 查询 */
    public Song findById(Long id) {
        String sql = "SELECT * FROM song WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /** 查询某个歌单包含的歌曲 */
    public List<Song> findByPlaylistId(Long playlistId) {
        String sql = "SELECT s.* FROM song s"
                + " JOIN playlist_song ps ON s.id = ps.song_id"
                + " WHERE ps.playlist_id = ?"
                + " ORDER BY ps.sort_order";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Song> list = new ArrayList<Song>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, playlistId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 关键字搜索（歌名/歌手/自然声类型） */
    public List<Song> search(String keyword) {
        String sql = "SELECT * FROM song"
                + " WHERE LOWER(title) LIKE ? OR LOWER(artist) LIKE ? OR LOWER(category) LIKE ?"
                + " ORDER BY play_count DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Song> list = new ArrayList<Song>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            String kw = "%" + (keyword == null ? "" : keyword.toLowerCase()) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 热门歌曲（前 n 首） */
    public List<Song> findTop(int limit) {
        String sql = "SELECT * FROM song ORDER BY play_count DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Song> list = new ArrayList<Song>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 增加播放量 */
    public void increasePlayCount(Long songId) {
        String sql = "UPDATE song SET play_count = play_count + 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, songId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    private List<Song> query(String sql) {
        List<Song> list = new ArrayList<Song>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    private Song map(ResultSet rs) throws SQLException {
        Song s = new Song();
        s.setId(rs.getLong("id"));
        s.setTitle(rs.getString("title"));
        s.setArtist(rs.getString("artist"));
        s.setCategory(rs.getString("category"));
        s.setMood(rs.getString("mood"));
        s.setDurationSec(rs.getInt("duration_sec"));
        s.setPlayCount(rs.getInt("play_count"));
        s.setCover(rs.getString("cover"));
        return s;
    }
}
