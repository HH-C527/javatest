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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 推荐页 /recommend：展示推荐结果、匹配占比与推荐来源占比
 */
@WebServlet(name = "RecommendServlet", urlPatterns = {"/recommend"})
public class RecommendServlet extends HttpServlet {

    private final RecommendService recommendService = new RecommendService();
    private final PlaylistService playlistService = new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        Long userId = SessionUtil.getCurrentUserId(req);
        User user = SessionUtil.getCurrentUser(req);

        // 综合推荐
        List<RecommendItem> recommends = recommendService.recommendForUser(userId, 12);
        for (RecommendItem item : recommends) {
            Playlist p = item.getPlaylist();
            if (p != null) {
                List<Playlist> single = new ArrayList<Playlist>();
                single.add(p);
                playlistService.markFavoriteStatus(single, userId);
            }
        }

        // 分类推荐明细
        List<RecommendItem> byTag = recommendService.recommendByTags(userId);
        List<RecommendItem> byCf = recommendService.recommendByCollaborativeFiltering(userId);

        // 推荐来源占比
        Map<String, Double> ratio = recommendService.recommendSourceRatio(userId);

        // 无收藏行为时的兜底热门推荐
        List<Playlist> hot = recommendService.hotPlaylists(6);
        playlistService.markFavoriteStatus(hot, userId);

        boolean hasFavorite = recommends != null && !recommends.isEmpty();

        req.setAttribute("recommends", recommends);
        req.setAttribute("byTag", byTag);
        req.setAttribute("byCf", byCf);
        req.setAttribute("ratio", ratio);
        req.setAttribute("hotPlaylists", hot);
        req.setAttribute("hasFavorite", hasFavorite);
        req.setAttribute("currentUser", user);
        req.setAttribute("activeMenu", "recommend");
        req.getRequestDispatcher("/WEB-INF/views/recommend.jsp").forward(req, resp);
    }
}
