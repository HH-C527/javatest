package com.example.music.entity;

/**
 * 歌曲（自然音乐 / 白噪音）
 */
public class Song {

    private Long id;
    private String title;
    private String artist;
    private String category;      // 自然声类型：雨声、森林、海浪、鸟鸣、溪流、风声
    private String mood;          // 情绪：放松、专注、睡眠、冥想
    private Integer durationSec;  // 时长（秒）
    private Integer playCount;    // 播放次数
    private String cover;         // 封面（emoji 或图片路径）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Integer getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(Integer durationSec) {
        this.durationSec = durationSec;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    /** 时长格式化为 mm:ss */
    public String getDurationText() {
        if (durationSec == null || durationSec <= 0) {
            return "--";
        }
        int m = durationSec / 60;
        int s = durationSec % 60;
        return String.format("%02d:%02d", m, s);
    }
}
