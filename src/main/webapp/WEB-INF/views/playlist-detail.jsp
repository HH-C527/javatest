<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section class="detail">
    <div class="detail-header">
        <div class="detail-cover">${playlist.cover}</div>
        <div class="detail-info">
            <h1>${playlist.name}</h1>
            <p class="detail-desc">${playlist.description}</p>
            <p class="meta">
                分类：${playlist.category} ·
                ${playlist.songCount} 首 ·
                总时长 ${playlist.totalDurationText} ·
                ▶ ${playlist.playCount} 次播放 ·
                ♥ ${playlist.favoriteCount} 人收藏
            </p>
            <div class="tag-row">
                <c:forEach var="t" items="${playlist.tags}">
                    <a class="tag-chip" href="${ctx}/playlists?tag=${t.name}">${t.name}</a>
                </c:forEach>
            </div>
            <a class="btn-fav big ${playlist.favorited ? 'on' : ''}"
               href="${ctx}/favorite/toggle?playlistId=${playlist.id}&from=${ctx}/playlist?id=${playlist.id}">
                ${playlist.favorited ? '♥ 已收藏（点击取消）' : '♡ 收藏歌单'}
            </a>
        </div>
    </div>

    <%-- 歌曲列表 --%>
    <h2 class="section-title">歌曲列表</h2>
    <table class="song-table">
        <thead>
        <tr>
            <th style="width:50px">#</th>
            <th>歌曲</th>
            <th>艺术家</th>
            <th>自然声</th>
            <th>情绪</th>
            <th>时长</th>
            <th>播放量</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${playlist.songs}" varStatus="st">
            <tr>
                <td>${st.index + 1}</td>
                <td>${s.cover} ${s.title}</td>
                <td>${s.artist}</td>
                <td>${s.category}</td>
                <td>${s.mood}</td>
                <td>${s.durationText}</td>
                <td>${s.playCount}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <%-- 相似推荐 --%>
    <c:if test="${not empty similar}">
        <h2 class="section-title">相似歌单推荐 <span class="sub">基于标签重合度</span></h2>
        <div class="card-grid">
            <c:forEach var="item" items="${similar}">
                <div class="card">
                    <div class="card-cover">${item.playlist.cover}</div>
                    <h3 class="card-title">
                        <a href="${ctx}/playlist?id=${item.playlist.id}">${item.playlist.name}</a>
                    </h3>
                    <div class="match-box">
                        <div class="match-label">相似度 <b>${item.percentText}%</b></div>
                        <div class="match-bar">
                            <div class="match-bar-inner" style="width:${item.percent}%"></div>
                        </div>
                        <div class="match-reason">${item.reason}</div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
</section>

<%@ include file="common/footer.jsp" %>
