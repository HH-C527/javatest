package com.example.music.service;

import com.example.music.dao.FavoriteDAO;
import com.example.music.dao.PlaylistDAO;
import com.example.music.dao.TagDAO;
import com.example.music.dao.UserDAO;
import com.example.music.entity.Favorite;
import com.example.music.entity.Playlist;
import com.example.music.entity.RecommendItem;
import com.example.music.entity.Tag;
import com.example.music.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 推荐业务：
 * 1. 基于标签的内容推荐（计算匹配占比）
 * 2. 基于用户的协同过滤（UserCF：兴趣相投的人还喜欢）
 * 3. 热门推荐
 * 4. 相似歌单推荐
 */
public class RecommendService {

    private final PlaylistDAO playlistDAO = new PlaylistDAO();
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * 综合推荐：标签匹配 + 协同过滤融合，按得分倒序。
     */
    public List<RecommendItem> recommendForUser(Long userId, int limit) {
        Map<Long, RecommendItem> merged = new LinkedHashMap<Long, RecommendItem>();

        // 1. 标签内容推荐
        for (RecommendItem item : recommendByTags(userId)) {
            merged.put(item.getPlaylist().getId(), item);
        }

        // 2. 协同过滤推荐，与标签推荐融合（分数累加，理由合并）
        for (RecommendItem item : recommendByCollaborativeFiltering(userId)) {
            Long pid = item.getPlaylist().getId();
            if (merged.containsKey(pid)) {
                RecommendItem exist = merged.get(pid);
                exist.setScore(exist.getScore() + item.getScore());
                exist.setReason(exist.getReason() + "；" + item.getReason());
            } else {
                merged.put(pid, item);
            }
        }

        List<RecommendItem> list = new ArrayList<RecommendItem>(merged.values());
        Collections.sort(list, new Comparator<RecommendItem>() {
            @Override
            public int compare(RecommendItem a, RecommendItem b) {
                return Double.compare(b.getScore(), a.getScore());
            }
        });

        if (limit > 0 && list.size() > limit) {
            return new ArrayList<RecommendItem>(list.subList(0, limit));
        }
        return list;
    }

    /**
     * 基于标签的内容推荐（核心：匹配占比）。
     * 思路：
     *   1) 统计用户收藏歌单中出现过的标签及次数，作为「偏好权重」
     *   2) 对未收藏的每个歌单，累加其命中偏好标签的权重
     *   3) 匹配占比 = 命中权重 / 偏好总权重 × 100%
     */
    public List<RecommendItem> recommendByTags(Long userId) {
        List<RecommendItem> result = new ArrayList<RecommendItem>();

        List<Favorite> favorites = favoriteDAO.findByUserId(userId);
        if (favorites == null || favorites.isEmpty()) {
            return result;  // 没有收藏行为，无法做标签推荐
        }

        // 1) 统计偏好标签权重
        Map<String, Integer> tagWeight = new HashMap<String, Integer>();
        Set<Long> favIds = new HashSet<Long>();
        for (Favorite f : favorites) {
            favIds.add(f.getPlaylistId());
            Playlist p = f.getPlaylist();
            if (p == null || p.getTags() == null) {
                continue;
            }
            for (Tag t : p.getTags()) {
                Integer w = tagWeight.get(t.getName());
                tagWeight.put(t.getName(), w == null ? 1 : w + 1);
            }
        }
        if (tagWeight.isEmpty()) {
            return result;
        }
        int totalWeight = 0;
        for (Integer w : tagWeight.values()) {
            totalWeight += w;
        }

        // 2) 遍历所有歌单计算匹配占比
        List<Playlist> all = playlistDAO.findAll();
        for (Playlist p : all) {
            if (favIds.contains(p.getId())) {
                continue;  // 已收藏的不推荐
            }
            List<Tag> tags = tagDAO.findByPlaylistId(p.getId());
            int matchedWeight = 0;
            List<String> matched = new ArrayList<String>();
            for (Tag t : tags) {
                Integer w = tagWeight.get(t.getName());
                if (w != null) {
                    matchedWeight += w;
                    matched.add(t.getName());
                }
            }
            if (matchedWeight == 0) {
                continue;
            }

            double percent = totalWeight > 0 ? matchedWeight * 100.0 / totalWeight : 0;
            RecommendItem item = new RecommendItem();
            item.setPlaylist(p);
            item.setScore(matchedWeight);
            item.setPercent(Math.min(percent, 100.0));
            item.setMatchedTags(matched);
            item.setReason("命中偏好标签 " + join(matched) + "，匹配占比 " + item.getPercentText() + "%");
            result.add(item);
        }

        // 3) 按匹配占比倒序
        Collections.sort(result, new Comparator<RecommendItem>() {
            @Override
            public int compare(RecommendItem a, RecommendItem b) {
                return Double.compare(b.getPercent(), a.getPercent());
            }
        });
        return result;
    }

    /**
     * 基于用户的协同过滤（UserCF）。
     * 思路：找到与当前用户收藏重叠度高的其他用户，把他们收藏过、
     * 而当前用户未收藏的歌单推荐出来；相似度用余弦公式。
     */
    public List<RecommendItem> recommendByCollaborativeFiltering(Long userId) {
        Map<Long, RecommendItem> resultMap = new LinkedHashMap<Long, RecommendItem>();

        List<Favorite> myFavs = favoriteDAO.findByUserId(userId);
        if (myFavs == null || myFavs.isEmpty()) {
            return new ArrayList<RecommendItem>();
        }
        Set<Long> myIds = new HashSet<Long>();
        for (Favorite f : myFavs) {
            myIds.add(f.getPlaylistId());
        }

        // 遍历其他用户，计算兴趣相似度
        for (User other : userDAO.findAll()) {
            if (other.getId().equals(userId)) {
                continue;
            }
            List<Favorite> otherFavs = favoriteDAO.findByUserId(other.getId());
            if (otherFavs == null || otherFavs.isEmpty()) {
                continue;
            }
            Set<Long> otherIds = new HashSet<Long>();
            for (Favorite f : otherFavs) {
                otherIds.add(f.getPlaylistId());
            }

            // 共同收藏数
            int common = 0;
            for (Long id : myIds) {
                if (otherIds.contains(id)) {
                    common++;
                }
            }
            if (common == 0) {
                continue;
            }

            // 余弦相似度
            double similarity = common / Math.sqrt(myIds.size() * (double) otherIds.size());

            // 该用户收藏、而我未收藏的歌单
            for (Long pid : otherIds) {
                if (myIds.contains(pid)) {
                    continue;
                }
                Playlist p = playlistDAO.findById(pid);
                if (p == null) {
                    continue;
                }
                Long key = pid;
                RecommendItem item = resultMap.get(key);
                if (item == null) {
                    item = new RecommendItem();
                    item.setPlaylist(p);
                    item.setScore(0);
                    item.setPercent(0);
                    resultMap.put(key, item);
                }
                item.setScore(item.getScore() + similarity);
                item.setPercent(item.getPercent() + similarity * 100);
                if (item.getMatchedTags().isEmpty()) {
                    item.setReason("与你兴趣相投的「" + other.getNickname() + "」也收藏了它");
                }
            }
        }

        List<RecommendItem> list = new ArrayList<RecommendItem>(resultMap.values());
        for (RecommendItem item : list) {
            item.setPercent(Math.min(item.getPercent(), 100.0));
        }
        Collections.sort(list, new Comparator<RecommendItem>() {
            @Override
            public int compare(RecommendItem a, RecommendItem b) {
                return Double.compare(b.getScore(), a.getScore());
            }
        });
        return list;
    }

    /** 热门推荐：按播放量 */
    public List<Playlist> hotPlaylists(int limit) {
        List<Playlist> list = playlistDAO.findTop(limit);
        playlistDAO.fillTagsAndFavoriteCount(list);
        return list;
    }

    /** 相似歌单：与目标歌单共享标签越多越相似 */
    public List<RecommendItem> similarPlaylists(Long playlistId, int limit) {
        List<RecommendItem> result = new ArrayList<RecommendItem>();

        Playlist target = playlistDAO.findById(playlistId);
        if (target == null || target.getTags() == null || target.getTags().isEmpty()) {
            return result;
        }
        Set<String> targetTags = new HashSet<String>();
        for (Tag t : target.getTags()) {
            targetTags.add(t.getName());
        }

        for (Playlist p : playlistDAO.findAll()) {
            if (p.getId().equals(playlistId)) {
                continue;
            }
            List<Tag> tags = tagDAO.findByPlaylistId(p.getId());
            int common = 0;
            List<String> matched = new ArrayList<String>();
            for (Tag t : tags) {
                if (targetTags.contains(t.getName())) {
                    common++;
                    matched.add(t.getName());
                }
            }
            if (common == 0) {
                continue;
            }
            double percent = common * 100.0 / targetTags.size();
            RecommendItem item = new RecommendItem();
            item.setPlaylist(p);
            item.setScore(common);
            item.setPercent(Math.min(percent, 100.0));
            item.setMatchedTags(matched);
            item.setReason("与当前歌单共享标签 " + join(matched));
            result.add(item);
        }

        Collections.sort(result, new Comparator<RecommendItem>() {
            @Override
            public int compare(RecommendItem a, RecommendItem b) {
                return Double.compare(b.getPercent(), a.getPercent());
            }
        });
        if (limit > 0 && result.size() > limit) {
            return new ArrayList<RecommendItem>(result.subList(0, limit));
        }
        return result;
    }

    /** 推荐占比统计：返回各推荐来源的占比分布（用于页面展示） */
    public Map<String, Double> recommendSourceRatio(Long userId) {
        Map<String, Double> ratio = new LinkedHashMap<String, Double>();
        int tagCount = recommendByTags(userId).size();
        int cfCount = recommendByCollaborativeFiltering(userId).size();
        int total = tagCount + cfCount;
        if (total == 0) {
            ratio.put("标签匹配", 0.0);
            ratio.put("协同过滤", 0.0);
            return ratio;
        }
        ratio.put("标签匹配", tagCount * 100.0 / total);
        ratio.put("协同过滤", cfCount * 100.0 / total);
        return ratio;
    }

    private String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
