package com.example.music.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 歌单
 */
public class Playlist {

    private Long id;
    private String name;
    private String description;
    private String category;      // 分类：助眠、专注、放松、冥想
    private String cover;         // 封面 emoji
    private Integer playCount;
    private Date createTime;

    private List<Song> songs = new ArrayList<Song>();  // 包含的歌曲
    private List<Tag> tags = new ArrayList<Tag>();     // 关联的标签
    private Integer favoriteCount;                     // 被收藏次数
    private boolean favorited;                         // 当前用户是否已收藏

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    /** 歌曲数量 */
    public int getSongCount() {
        return songs == null ? 0 : songs.size();
    }

    /** 总时长文本 */
    public String getTotalDurationText() {
        int total = 0;
        if (songs != null) {
            for (Song s : songs) {
                if (s.getDurationSec() != null) {
                    total += s.getDurationSec();
                }
            }
        }
        int m = total / 60;
        return m + " 分钟";
    }

    /** 标签名称拼接，用于页面展示 */
    public String getTagNames() {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(tags.get(i).getName());
        }
        return sb.toString();
    }
}
