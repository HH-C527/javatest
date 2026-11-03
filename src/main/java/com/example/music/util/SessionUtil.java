package com.example.music.util;

import com.example.music.dao.UserDAO;
import com.example.music.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 会话工具：管理「当前演示用户」（收藏基于此用户）
 */
public class SessionUtil {

    private static final String KEY_USER = "currentUser";

    /** 获取当前用户，默认为第一个用户 */
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(KEY_USER);
        if (user == null) {
            List<User> users = new UserDAO().findAll();
            if (!users.isEmpty()) {
                user = users.get(0);
                session.setAttribute(KEY_USER, user);
            }
        }
        return user;
    }

    /** 切换当前用户 */
    public static void switchUser(HttpServletRequest request, Long userId) {
        User user = new UserDAO().findById(userId);
        if (user != null) {
            request.getSession().setAttribute(KEY_USER, user);
        }
    }

    /** 当前用户 id（可能为 null） */
    public static Long getCurrentUserId(HttpServletRequest request) {
        User u = getCurrentUser(request);
        return u == null ? null : u.getId();
    }
}
