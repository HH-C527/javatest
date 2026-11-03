<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section class="hero">
    <h1>听见自然的声音</h1>
    <p>雨声、森林、海浪…… 发现属于你的自然音乐歌单</p>
    <c:if test="${not empty currentUser}">
        <p class="hero-user">当前用户：${currentUser.avatar} ${currentUser.nickname}（已收藏 ${favoriteCount} 个歌单）</p>
    </c:if>
</section>

<%-- 个性化推荐 --%>
<c:if test="${not empty recommends}">
    <section>
        <h2 class="section-title">为你推荐 <span class="sub">基于你的收藏偏好</span></h2>
        <div class="card-grid">
            <c:forEach var="item" items="${recommends}">
                <div class="card">
                    <div class="card-cover">${item.playlist.cover}</div>
                    <h3 class="card-title">
                        <a href="${ctx}/playlist?id=${item.playlist.id}">${item.playlist.name}</a>
                    </h3>
                    <p class="card-desc">${item.playlist.description}</p>

                    <div class="tag-row">
                        <c:forEach var="t" items="${item.playlist.tags}">
                            <a class="tag-chip" href="${ctx}/playlists?tag=${t.name}">${t.name}</a>
                        </c:forEach>
                    </div>

                    <div class="match-box">
                        <div class="match-label">匹配占比 <b>${item.percentText}%</b></div>
                        <div class="match-bar">
                            <div class="match-bar-inner" style="width:${item.percent}%"></div>
                        </div>
                        <div class="match-reason">${item.reason}</div>
                    </div>

                    <div class="card-actions">
                        <a class="btn-fav ${item.playlist.favorited ? 'on' : ''}"
                           href="${ctx}/favorite/toggle?playlistId=${item.playlist.id}&from=${ctx}/home">
                            ${item.playlist.favorited ? '♥ 已收藏' : '♡ 收藏'}
                        </a>
                        <span class="meta">▶ ${item.playlist.playCount} 次播放</span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</c:if>

<%-- 热门歌单 --%>
<section>
    <h2 class="section-title">热门歌单</h2>
    <div class="card-grid">
        <c:forEach var="p" items="${hotPlaylists}">
            <div class="card">
                <div class="card-cover">${p.cover}</div>
                <h3 class="card-title"><a href="${ctx}/playlist?id=${p.id}">${p.name}</a></h3>
                <p class="card-desc">${p.description}</p>
                <div class="tag-row">
                    <c:forEach var="t" items="${p.tags}">
                        <a class="tag-chip" href="${ctx}/playlists?tag=${t.name}">${t.name}</a>
                    </c:forEach>
                </div>
                <div class="card-actions">
                    <a class="btn-fav ${p.favorited ? 'on' : ''}"
                       href="${ctx}/favorite/toggle?playlistId=${p.id}&from=${ctx}/home">
                        ${p.favorited ? '♥ 已收藏' : '♡ 收藏'}
                    </a>
                    <span class="meta">▶ ${p.playCount}</span>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<%-- 标签云 --%>
<section>
    <h2 class="section-title">按标签浏览</h2>
    <div class="tag-cloud">
        <c:forEach var="t" items="${tags}">
            <a class="tag-cloud-item" href="${ctx}/playlists?tag=${t.name}">
                ${t.name} <span class="tag-count">${t.useCount}</span>
            </a>
        </c:forEach>
    </div>
</section>

<%@ include file="common/footer.jsp" %>
