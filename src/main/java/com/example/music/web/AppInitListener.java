package com.example.music.web;

import com.example.music.util.SchemaInit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 应用启动监听：初始化数据库（建表 + 演示数据）
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[AppInitListener] 开始初始化数据库...");
        SchemaInit.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[AppInitListener] 应用已停止");
    }
}
