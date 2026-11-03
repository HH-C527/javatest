package com.example.music.web;

import com.example.music.entity.Favorite;
import com.example.music.entity.User;
import com.example.music.service.FavoriteService;
import com.example.music.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 收藏：列表 /favorites，切换收藏 /favorite/toggle
 */
@WebServlet(name = "FavoriteServlet", urlPatterns = {"/favorites", "/favorite/toggle"})
public class FavoriteServlet extends HttpServlet {

    private final FavoriteService favoriteService = new FavoriteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String path = req.getServletPath();

        if ("/favorite/toggle".equals(path)) {
            toggle(req, resp);
        } else {
            showFavorites(req, resp);
        }
    }

    /** 切换收藏状态 */
    private void toggle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playlistIdStr = req.getParameter("playlistId");
        String back = req.getParameter("from");
        Long userId = SessionUtil.getCurrentUserId(req);

        if (playlistIdStr != null && userId != null) {
            boolean nowFavorited = favoriteService.toggle(userId, Long.valueOf(playlistIdStr));
            req.getSession().setAttribute("flashMessage",
                    nowFavorited ? "已添加到我的收藏" : "已取消收藏");
        }

        if (back != null && !back.trim().isEmpty()) {
            resp.sendRedirect(back);
        } else {
            resp.sendRedirect(req.getContextPath() + "/playlists");
        }
    }

    /** 我的收藏列表 */
    private void showFavorites(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long userId = SessionUtil.getCurrentUserId(req);
        User user = SessionUtil.getCurrentUser(req);

        List<Favorite> favorites = favoriteService.listFavorites(userId);

        req.setAttribute("favorites", favorites);
        req.setAttribute("currentUser", user);
        req.setAttribute("favoriteCount", favoriteService.count(userId));
        req.setAttribute("activeMenu", "favorites");
        req.getRequestDispatcher("/WEB-INF/views/favorites.jsp").forward(req, resp);
    }
}
