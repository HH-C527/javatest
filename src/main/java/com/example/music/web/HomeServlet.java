package com.example.music.web;

import com.example.music.dao.UserDAO;
import com.example.music.entity.Playlist;
import com.example.music.entity.RecommendItem;
import com.example.music.entity.Tag;
import com.example.music.entity.User;
import com.example.music.service.FavoriteService;
import com.example.music.service.PlaylistService;
import com.example.music.service.RecommendService;
import com.example.music.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页：热门歌单 + 个性化推荐 + 标签云
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", "/index"})
public class HomeServlet extends HttpServlet {

    private final PlaylistService playlistService = new PlaylistService();
    private final RecommendService recommendService = new RecommendService();
    private final FavoriteService favoriteService = new FavoriteService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        User user = SessionUtil.getCurrentUser(req);
        Long userId = SessionUtil.getCurrentUserId(req);

        // 热门歌单
        List<Playlist> hotPlaylists = playlistService.listTop(6);
        playlistService.markFavoriteStatus(hotPlaylists, userId);

        // 个性化推荐（带匹配占比）
        List<RecommendItem> recommends = recommendService.recommendForUser(userId, 6);
        for (RecommendItem item : recommends) {
            Playlist p = item.getPlaylist();
            if (p != null) {
                List<Playlist> single = new ArrayList<Playlist>();
                single.add(p);
                playlistService.markFavoriteStatus(single, userId);
            }
        }

        // 标签云
        List<Tag> tags = playlistService.listTagsWithCount();

        req.setAttribute("currentUser", user);
        req.setAttribute("users", userDAO.findAll());
        req.setAttribute("hotPlaylists", hotPlaylists);
        req.setAttribute("recommends", recommends);
        req.setAttribute("tags", tags);
        req.setAttribute("favoriteCount", favoriteService.count(userId));
        req.setAttribute("activeMenu", "home");

        req.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(req, resp);
    }
}
