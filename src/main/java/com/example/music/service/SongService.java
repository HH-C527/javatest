package com.example.music.service;

import com.example.music.dao.SongDAO;
import com.example.music.entity.Song;

import java.util.List;

/**
 * 歌曲业务
 */
public class SongService {

    private final SongDAO songDAO = new SongDAO();

    public List<Song> listAll() {
        return songDAO.findAll();
    }

    public List<Song> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listAll();
        }
        return songDAO.search(keyword.trim());
    }

    public List<Song> listTop(int limit) {
        return songDAO.findTop(limit);
    }

    /** 播放歌曲（增加播放量） */
    public void play(Long songId) {
        songDAO.increasePlayCount(songId);
    }
}
