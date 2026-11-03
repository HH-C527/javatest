<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section>
    <h2 class="section-title">
        全部歌单
        <c:if test="${not empty tag}"><span class="sub">标签：${tag}</span></c:if>
        <c:if test="${not empty keyword}"><span class="sub">搜索：${keyword}</span></c:if>
    </h2>

    <%-- 搜索框 --%>
    <form class="search-form" action="${ctx}/playlists" method="get">
        <input type="text" name="keyword" value="${keyword}" placeholder="搜索歌单名称或描述"/>
        <button type="submit">搜索</button>
        <a class="btn-reset" href="${ctx}/playlists">重置</a>
    </form>

    <%-- 标签筛选 --%>
    <div class="tag-cloud">
        <c:forEach var="t" items="${tags}">
            <a class="tag-cloud-item ${t.name == tag ? 'active' : ''}" href="${ctx}/playlists?tag=${t.name}">
                ${t.name} <span class="tag-count">${t.useCount}</span>
            </a>
        </c:forEach>
    </div>

    <div class="card-grid">
        <c:choose>
            <c:when test="${empty playlists}">
                <p class="empty">没有找到匹配的歌单</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="p" items="${playlists}">
                    <div class="card">
                        <div class="card-cover">${p.cover}</div>
                        <h3 class="card-title"><a href="${ctx}/playlist?id=${p.id}">${p.name}</a></h3>
                        <p class="card-desc">${p.description}</p>
                        <div class="tag-row">
                            <c:forEach var="t" items="${p.tags}">
                                <a class="tag-chip" href="${ctx}/playlists?tag=${t.name}">${t.name}</a>
                            </c:forEach>
                        </div>
                        <p class="meta">分类：${p.category} · ${p.songCount} 首 · ♥ ${p.favoriteCount}</p>
                        <div class="card-actions">
                            <a class="btn-fav ${p.favorited ? 'on' : ''}"
                               href="${ctx}/favorite/toggle?playlistId=${p.id}&from=${ctx}/playlists">
                                ${p.favorited ? '♥ 已收藏' : '♡ 收藏'}
                            </a>
                            <a class="btn-detail" href="${ctx}/playlist?id=${p.id}">查看详情</a>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="common/footer.jsp" %>
