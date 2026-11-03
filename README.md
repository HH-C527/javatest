# 自然音乐歌单推荐与收藏系统

基于 Java EE（Servlet + JSP + JSTL）与 H2 内嵌数据库的自然音乐歌单 Web 应用，支持歌单浏览、标签筛选、收藏与个性化推荐。

## 技术栈

| 分类 | 技术 | 版本 |
|---|---|---|
| 语言 | Java | 8（JDK 1.8） |
| Web 规范 | Servlet | 4.0（javax.servlet） |
| 视图 | JSP + JSTL + EL | 2.3 / 1.2 / 3.0 |
| 容器 | Apache Tomcat | 9.x |
| 数据库 | H2（文件模式，内嵌） | 1.4.200 |
| 数据访问 | 原生 JDBC + DAO 分层 | - |
| 构建 | Gradle Wrapper | 7.6.4 |

> 注意：本项目使用 `javax.servlet`，仅支持 Tomcat 9。Tomcat 10+ 已改为 `jakarta.servlet`，无法直接运行。

## 功能

- 歌单：列表、详情、按标签筛选、关键字搜索
- 标签：15 个标签，标签云展示使用次数
- 收藏：收藏 / 取消收藏、我的收藏列表、收藏数统计
- 推荐：标签内容推荐（匹配占比）、协同过滤 UserCF、相似歌单、热门兜底
- 歌曲：歌曲库与搜索
- 用户：演示用户切换，推荐结果随收藏变化

## 环境要求

- JDK 1.8 及以上（需配置 JAVA_HOME）
- Apache Tomcat 9.x（下载：https://tomcat.apache.org/download-90.cgi）
- 无需安装 Gradle（项目自带 Wrapper）
- 无需安装数据库（H2 已打包在 WEB-INF/lib）

## 部署步骤

### 1. 克隆项目

```bash
git clone https://github.com/HH-C527/javatest.git
cd javatest
```

仓库已包含 `lib/servlet-api.jar`、`lib/h2-1.4.200.jar`、`lib/jstl-1.2.jar`，克隆后无需联网下载依赖。

### 2. 打包 WAR

Windows：

```powershell
.\gradlew.bat clean war
```

Linux / macOS：

```bash
chmod +x gradlew
./gradlew clean war
```

构建产物：`build/libs/music.war`

### 3. 部署到 Tomcat

先停止正在运行的 Tomcat：

```bash
# Linux / macOS
$CATALINA_HOME/bin/shutdown.sh

# Windows
%CATALINA_HOME%\bin\shutdown.bat
```

复制 WAR 包到 webapps：

```bash
# Linux / macOS
cp build/libs/music.war $CATALINA_HOME/webapps/

# Windows
copy build\libs\music.war %CATALINA_HOME%\webapps\
```

启动 Tomcat：

```bash
# Linux / macOS
$CATALINA_HOME/bin/startup.sh

# Windows
%CATALINA_HOME%\bin\startup.bat
```

### 4. 访问应用

```
http://localhost:8080/music/
```

上下文路径由 WAR 文件名决定：`music.war` 对应 `/music`。
如需直接通过 `http://localhost:8080/` 访问，将包重命名为 `ROOT.war`（会覆盖 Tomcat 自带 ROOT 应用）。

首次启动会自动建表并写入演示数据，Tomcat 日志中可见：

```
[AppInitListener] 开始初始化数据库...
[SchemaInit] 演示数据初始化完成
[SchemaInit] 数据库就绪: C:/Users/<用户名>/javatest-music/music
```

### 5. 停止与卸载

```bash
# 停止 Tomcat
$CATALINA_HOME/bin/shutdown.sh

# 卸载应用
rm -rf $CATALINA_HOME/webapps/music $CATALINA_HOME/webapps/music.war
```

## 页面路由

| 路径 | 说明 |
|---|---|
| /music/ | 首页：热门歌单 + 个性化推荐 + 标签云 |
| /music/playlists | 歌单列表（支持 ?tag=雨声、?keyword=助眠） |
| /music/playlist?id=1 | 歌单详情 + 相似歌单推荐 |
| /music/recommend | 推荐页：匹配占比 + 来源占比 |
| /music/favorites | 我的收藏 |
| /music/songs | 歌曲库（支持 ?keyword=海浪） |
| /music/user/switch?userId=2 | 切换演示用户 |
| /music/favorite/toggle?playlistId=1 | 收藏 / 取消收藏 |

## 开发模式运行（IDEA）

1. 使用 IDEA 打开项目根目录，等待 Gradle 同步完成
2. 安装 Smart Tomcat 插件（或使用 IDEA Ultimate 的 Tomcat 配置）
3. 新建 Smart Tomcat 运行配置：
   - Tomcat Server：本地 Tomcat 9 目录
   - Deployment Directory：`<项目根>/src/main/webapp`
   - Context Path：`/`
   - Server Port：8080
4. 运行前执行 Build -> Rebuild Project
5. 访问 `http://localhost:8080/`

修改 JSP 后刷新浏览器即可生效；修改 Java 代码需先 Rebuild。

## 数据库说明

| 项 | 值 |
|---|---|
| 类型 | H2 文件数据库（MySQL 兼容模式） |
| JDBC URL | jdbc:h2:file:<用户主目录>/javatest-music/music;AUTO_SERVER=TRUE;MODE=MySQL |
| 数据文件 | Windows: C:\Users\<用户名>\javatest-music\music.mv.db；Linux/macOS: ~/javatest-music/music.mv.db |
| 用户名 / 密码 | sa / 空 |
| 初始化 | 应用启动时自动建表；仅当 song 表为空时写入演示数据 |

重置数据：删除 `javatest-music` 目录后重启 Tomcat。

> H2 文件库适合演示与单机部署；生产环境建议迁移到 MySQL（DAO 使用标准 JDBC，修改 DBUtil 中的 URL 与驱动即可）。

## 常见问题

| 现象 | 原因与解决 |
|---|---|
| 访问 / 显示 Tomcat 欢迎页 | 应用上下文为 /music，请访问 http://localhost:8080/music/ |
| 404 | WAR 未解压完成，稍等或查看 logs/catalina.*.log |
| 500 且报 ClassNotFoundException: org.h2.Driver | WAR 内缺少 H2 驱动，执行 gradlew clean war 重新打包 |
| JSP 报找不到标签库 | 缺少 JSTL，确认 WEB-INF/lib/jstl-1.2.jar 存在 |
| 端口被占用 | netstat -ano \| findstr :8080（Windows）或 lsof -i:8080（macOS/Linux），停止占用进程 |
| 中文乱码 | 启动参数加 -Dfile.encoding=UTF-8 |
| 收藏数据丢失 | 数据在启动 Tomcat 的用户主目录，换用户会读到新库 |
| IDEA 报 javax.servlet 不存在 | 确认 lib/servlet-api.jar 已提交并加入模块依赖 |

## 部署检查清单

- [ ] lib 目录下 3 个 jar 已提交（.gitignore 已配置 !lib/*.jar）
- [ ] gradlew clean war 执行成功，产物为 build/libs/music.war
- [ ] WAR 内包含 WEB-INF/classes、WEB-INF/lib、WEB-INF/views
- [ ] Tomcat 版本为 9.x（非 10+）
- [ ] 端口 8080 未被占用
- [ ] 访问 /music/ 返回首页，日志出现 SchemaInit 数据库就绪

## 项目结构

```
javatest/
├── build.gradle
├── gradlew / gradlew.bat
├── lib/                        # 编译期依赖
│   ├── servlet-api.jar
│   ├── h2-1.4.200.jar
│   └── jstl-1.2.jar
└── src/main/
    ├── java/com/example/music/
    │   ├── entity/             # 实体
    │   ├── dao/                # JDBC 数据访问
    │   ├── service/            # 业务与推荐算法
    │   ├── web/                # Servlet 与监听器
    │   └── util/               # 数据库与会话工具
    └── webapp/
        ├── index.jsp
        ├── css/style.css
        └── WEB-INF/
            ├── web.xml
            ├── lib/            # 运行时依赖（h2、jstl）
            └── views/          # JSP 页面
```

## License

仅用于学习与演示，可自由使用与修改。
