<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section>
    <h2 class="section-title">
        我的收藏
        <c:if test="${not empty currentUser}">
            <span class="sub">${currentUser.avatar} ${currentUser.nickname} · 共 ${favoriteCount} 个</span>
        </c:if>
    </h2>

    <c:choose>
        <c:when test="${empty favorites}">
            <div class="empty-box">
                <p>还没有收藏任何歌单</p>
                <a class="btn-detail" href="${ctx}/playlists">去发现歌单</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card-grid">
                <c:forEach var="f" items="${favorites}">
                    <div class="card">
                        <div class="card-cover">${f.playlist.cover}</div>
                        <h3 class="card-title">
                            <a href="${ctx}/playlist?id=${f.playlist.id}">${f.playlist.name}</a>
                        </h3>
                        <p class="card-desc">${f.playlist.description}</p>
                        <div class="tag-row">
                            <c:forEach var="t" items="${f.playlist.tags}">
                                <a class="tag-chip" href="${ctx}/playlists?tag=${t.name}">${t.name}</a>
                            </c:forEach>
                        </div>
                        <p class="meta">${f.playlist.songCount} 首 · ▶ ${f.playlist.playCount}</p>
                        <div class="card-actions">
                            <a class="btn-fav on"
                               href="${ctx}/favorite/toggle?playlistId=${f.playlist.id}&from=${ctx}/favorites">
                                ♥ 已收藏（点击取消）
                            </a>
                            <a class="btn-detail" href="${ctx}/playlist?id=${f.playlist.id}">查看详情</a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="common/footer.jsp" %>
