package com.example.music.dao;

import com.example.music.entity.Playlist;
import com.example.music.entity.Song;
import com.example.music.entity.Tag;
import com.example.music.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌单数据访问
 */
public class PlaylistDAO {

    private final SongDAO songDAO = new SongDAO();
    private final TagDAO tagDAO = new TagDAO();

    /** 查询全部歌单（按播放量倒序） */
    public List<Playlist> findAll() {
        return query("SELECT * FROM playlist ORDER BY play_count DESC", null);
    }

    /** 热门歌单 */
    public List<Playlist> findTop(int limit) {
        return query("SELECT * FROM playlist ORDER BY play_count DESC LIMIT ?", limit);
    }

    /** 按 id 查询（含歌曲与标签） */
    public Playlist findById(Long id) {
        List<Playlist> list = query("SELECT * FROM playlist WHERE id = ?", id);
        if (list.isEmpty()) {
            return null;
        }
        return fillDetail(list.get(0));
    }

    /** 按标签名筛选歌单 */
    public List<Playlist> findByTagName(String tagName) {
        String sql = "SELECT p.* FROM playlist p"
                + " JOIN playlist_tag pt ON p.id = pt.playlist_id"
                + " JOIN tag t ON pt.tag_id = t.id"
                + " WHERE t.name = ?"
                + " ORDER BY p.play_count DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Playlist> list = new ArrayList<Playlist>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tagName);
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

    /** 关键字搜索（歌单名/描述/分类） */
    public List<Playlist> search(String keyword) {
        String sql = "SELECT * FROM playlist"
                + " WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ? OR LOWER(category) LIKE ?"
                + " ORDER BY play_count DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Playlist> list = new ArrayList<Playlist>();
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

    /** 统计歌单被收藏次数 */
    public int countFavorites(Long playlistId) {
        String sql = "SELECT COUNT(*) FROM favorite WHERE playlist_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, playlistId);
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

    /** 增加播放量 */
    public void increasePlayCount(Long playlistId) {
        String sql = "UPDATE playlist SET play_count = play_count + 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, playlistId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    /** 填充歌单详情（歌曲、标签、收藏数） */
    private Playlist fillDetail(Playlist p) {
        if (p == null) {
            return null;
        }
        List<Song> songs = songDAO.findByPlaylistId(p.getId());
        p.setSongs(songs);
        List<Tag> tags = tagDAO.findByPlaylistId(p.getId());
        p.setTags(tags);
        p.setFavoriteCount(countFavorites(p.getId()));
        return p;
    }

    /** 批量填充列表中的标签与收藏数 */
    public void fillTagsAndFavoriteCount(List<Playlist> playlists) {
        if (playlists == null) {
            return;
        }
        for (Playlist p : playlists) {
            p.setTags(tagDAO.findByPlaylistId(p.getId()));
            p.setFavoriteCount(countFavorites(p.getId()));
        }
    }

    private List<Playlist> query(String sql, Object param) {
        List<Playlist> list = new ArrayList<Playlist>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            if (param != null) {
                if (param instanceof Integer) {
                    ps.setInt(1, (Integer) param);
                } else {
                    ps.setLong(1, ((Long) param).longValue());
                }
            }
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

    private Playlist map(ResultSet rs) throws SQLException {
        Playlist p = new Playlist();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setCover(rs.getString("cover"));
        p.setPlayCount(rs.getInt("play_count"));
        p.setCreateTime(rs.getTimestamp("create_time"));
        return p;
    }
}
