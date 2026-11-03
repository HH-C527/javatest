package com.example.music.web;

import com.example.music.entity.Playlist;
import com.example.music.entity.RecommendItem;
import com.example.music.entity.User;
import com.example.music.service.PlaylistService;
import com.example.music.service.RecommendService;
import com.example.music.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 歌单：列表 /playlists（支持 tag、keyword 筛选）与详情 /playlist?id=
 */
@WebServlet(name = "PlaylistServlet", urlPatterns = {"/playlists", "/playlist"})
public class PlaylistServlet extends HttpServlet {

    private final PlaylistService playlistService = new PlaylistService();
    private final RecommendService recommendService = new RecommendService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String path = req.getServletPath();
        Long userId = SessionUtil.getCurrentUserId(req);

        if ("/playlist".equals(path)) {
            showDetail(req, resp, userId);
        } else {
            showList(req, resp, userId);
        }
    }

    /** 歌单列表 */
    private void showList(HttpServletRequest req, HttpServletResponse resp, Long userId)
            throws ServletException, IOException {
        String tag = req.getParameter("tag");
        String keyword = req.getParameter("keyword");

        List<Playlist> playlists;
        if (tag != null && !tag.trim().isEmpty()) {
            playlists = playlistService.listByTag(tag.trim());
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            playlists = playlistService.search(keyword.trim());
        } else {
            playlists = playlistService.listAll();
        }
        playlistService.markFavoriteStatus(playlists, userId);

        req.setAttribute("playlists", playlists);
        req.setAttribute("tag", tag);
        req.setAttribute("keyword", keyword);
        req.setAttribute("tags", playlistService.listTagsWithCount());
        req.setAttribute("currentUser", SessionUtil.getCurrentUser(req));
        req.setAttribute("activeMenu", "playlists");
        req.getRequestDispatcher("/WEB-INF/views/playlist-list.jsp").forward(req, resp);
    }

    /** 歌单详情 */
    private void showDetail(HttpServletRequest req, HttpServletResponse resp, Long userId)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/playlists");
            return;
        }

        Playlist playlist = playlistService.getDetail(Long.valueOf(idStr.trim()), userId);
        if (playlist == null) {
            req.setAttribute("message", "歌单不存在");
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
            return;
        }

        // 相似歌单推荐（带匹配占比）
        List<RecommendItem> similar = recommendService.similarPlaylists(playlist.getId(), 4);

        req.setAttribute("playlist", playlist);
        req.setAttribute("similar", similar);
        req.setAttribute("currentUser", SessionUtil.getCurrentUser(req));
        req.setAttribute("activeMenu", "playlists");
        req.getRequestDispatcher("/WEB-INF/views/playlist-detail.jsp").forward(req, resp);
    }
}
