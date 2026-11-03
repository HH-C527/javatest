package com.example.music.web;

import com.example.music.entity.Song;
import com.example.music.entity.User;
import com.example.music.service.SongService;
import com.example.music.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 歌曲列表 /songs（支持 keyword 搜索）
 */
@WebServlet(name = "SongServlet", urlPatterns = {"/songs"})
public class SongServlet extends HttpServlet {

    private final SongService songService = new SongService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String keyword = req.getParameter("keyword");
        List<Song> songs = songService.search(keyword);
        User user = SessionUtil.getCurrentUser(req);

        req.setAttribute("songs", songs);
        req.setAttribute("keyword", keyword);
        req.setAttribute("currentUser", user);
        req.setAttribute("activeMenu", "songs");
        req.getRequestDispatcher("/WEB-INF/views/songs.jsp").forward(req, resp);
    }
}
