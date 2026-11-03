package com.example.music.service;

import com.example.music.dao.FavoriteDAO;
import com.example.music.dao.PlaylistDAO;
import com.example.music.dao.TagDAO;
import com.example.music.entity.Playlist;
import com.example.music.entity.Tag;

import java.util.List;

/**
 * 歌单业务
 */
public class PlaylistService {

    private final PlaylistDAO playlistDAO = new PlaylistDAO();
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private final TagDAO tagDAO = new TagDAO();

    /** 全部歌单 */
    public List<Playlist> listAll() {
        List<Playlist> list = playlistDAO.findAll();
        playlistDAO.fillTagsAndFavoriteCount(list);
        return list;
    }

    /** 热门歌单 */
    public List<Playlist> listTop(int limit) {
        List<Playlist> list = playlistDAO.findTop(limit);
        playlistDAO.fillTagsAndFavoriteCount(list);
        return list;
    }

    /** 按标签筛选 */
    public List<Playlist> listByTag(String tagName) {
        List<Playlist> list = playlistDAO.findByTagName(tagName);
        playlistDAO.fillTagsAndFavoriteCount(list);
        return list;
    }

    /** 关键字搜索 */
    public List<Playlist> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listAll();
        }
        List<Playlist> list = playlistDAO.search(keyword.trim());
        playlistDAO.fillTagsAndFavoriteCount(list);
        return list;
    }

    /** 歌单详情（含歌曲、标签、收藏状态） */
    public Playlist getDetail(Long id, Long currentUserId) {
        Playlist p = playlistDAO.findById(id);
        if (p == null) {
            return null;
        }
        if (currentUserId != null) {
            p.setFavorited(favoriteDAO.exists(currentUserId, id));
        }
        return p;
    }

    /** 为列表标记当前用户的收藏状态 */
    public void markFavoriteStatus(List<Playlist> playlists, Long currentUserId) {
        if (playlists == null || currentUserId == null) {
            return;
        }
        for (Playlist p : playlists) {
            p.setFavorited(favoriteDAO.exists(currentUserId, p.getId()));
        }
    }

    /** 全部标签（带使用次数） */
    public List<Tag> listTagsWithCount() {
        return tagDAO.findAllWithCount();
    }

    /** 播放歌单（增加播放量） */
    public void play(Long playlistId) {
        playlistDAO.increasePlayCount(playlistId);
    }
}
