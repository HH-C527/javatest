package com.example.music.dao;

import com.example.music.entity.Tag;
import com.example.music.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签数据访问
 */
public class TagDAO {

    /** 查询全部标签（带使用次数，用于标签云） */
    public List<Tag> findAllWithCount() {
        String sql = "SELECT t.*, COUNT(pt.playlist_id) AS use_count"
                + " FROM tag t"
                + " LEFT JOIN playlist_tag pt ON t.id = pt.tag_id"
                + " GROUP BY t.id, t.name, t.category"
                + " ORDER BY use_count DESC, t.id";
        List<Tag> list = new ArrayList<Tag>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Tag t = new Tag();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("name"));
                t.setCategory(rs.getString("category"));
                t.setUseCount(rs.getInt("use_count"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 查询某个歌单的标签 */
    public List<Tag> findByPlaylistId(Long playlistId) {
        String sql = "SELECT t.* FROM tag t"
                + " JOIN playlist_tag pt ON t.id = pt.tag_id"
                + " WHERE pt.playlist_id = ?"
                + " ORDER BY t.id";
        List<Tag> list = new ArrayList<Tag>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, playlistId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Tag t = new Tag();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("name"));
                t.setCategory(rs.getString("category"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 按名称查询标签 */
    public Tag findByName(String name) {
        String sql = "SELECT * FROM tag WHERE name = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                Tag t = new Tag();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("name"));
                t.setCategory(rs.getString("category"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }
}
