package com.example.music.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐结果项：歌单 + 推荐分数 + 命中的标签 + 推荐理由
 */
public class RecommendItem {

    private Playlist playlist;
    private double score;                        // 匹配得分
    private double percent;                      // 匹配占比（0-100）
    private List<String> matchedTags = new ArrayList<String>();  // 命中的标签
    private String reason;                       // 推荐理由文本

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public List<String> getMatchedTags() {
        return matchedTags;
    }

    public void setMatchedTags(List<String> matchedTags) {
        this.matchedTags = matchedTags;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /** 占比格式化（保留一位小数） */
    public String getPercentText() {
        return String.format("%.1f", percent);
    }
}
