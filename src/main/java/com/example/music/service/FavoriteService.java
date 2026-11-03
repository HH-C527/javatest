package com.example.music.service;

import com.example.music.dao.FavoriteDAO;
import com.example.music.entity.Favorite;

import java.util.List;

/**
 * 收藏业务
 */
public class FavoriteService {

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();

    /**
     * 切换收藏状态。
     *
     * @return true 表示切换后为「已收藏」，false 表示「已取消收藏」
     */
    public boolean toggle(Long userId, Long playlistId) {
        if (userId == null || playlistId == null) {
            return false;
        }
        if (favoriteDAO.exists(userId, playlistId)) {
            favoriteDAO.remove(userId, playlistId);
            return false;
        }
        favoriteDAO.add(userId, playlistId);
        return true;
    }

    /** 我的收藏列表 */
    public List<Favorite> listFavorites(Long userId) {
        return favoriteDAO.findByUserId(userId);
    }

    /** 收藏数量 */
    public int count(Long userId) {
        return favoriteDAO.countByUser(userId);
    }

    /** 是否已收藏 */
    public boolean isFavorited(Long userId, Long playlistId) {
        return favoriteDAO.exists(userId, playlistId);
    }
}
