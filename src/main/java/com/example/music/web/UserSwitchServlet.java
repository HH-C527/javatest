package com.example.music.web;

import com.example.music.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 切换演示用户 /user/switch?userId=2
 * （演示用：不同用户的收藏不同，推荐结果也会随之变化）
 */
@WebServlet(name = "UserSwitchServlet", urlPatterns = {"/user/switch"})
public class UserSwitchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userIdStr = req.getParameter("userId");
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            SessionUtil.switchUser(req, Long.valueOf(userIdStr.trim()));
            req.getSession().setAttribute("flashMessage", "已切换用户");
        }
        String back = req.getParameter("from");
        if (back != null && !back.trim().isEmpty()) {
            resp.sendRedirect(back);
        } else {
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }
}
