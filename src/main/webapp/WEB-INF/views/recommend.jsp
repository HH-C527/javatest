<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section>
    <h2 class="section-title">
        推荐歌单
        <c:if test="${not empty currentUser}">
            <span class="sub">为 ${currentUser.nickname} 定制</span>
        </c:if>
    </h2>

    <%-- 推荐来源占比 --%>
    <c:if test="${hasFavorite}">
        <div class="ratio-box">
            <h3>推荐来源占比</h3>
            <div class="ratio-bar">
                <c:set var="tagRatio" value="${ratio['标签匹配']}"/>
                <c:set var="cfRatio" value="${ratio['协同过滤']}"/>
                <div class="ratio-seg tag" style="width:${tagRatio}%">
                    <c:if test="${tagRatio >= 10}">标签匹配 ${tagRatio}%</c:if>
                </div>
                <div class="ratio-seg cf" style="width:${cfRatio}%">
                    <c:if test="${cfRatio >= 10}">协同过滤 ${cfRatio}%</c:if>
                </div>
            </div>
            <p class="ratio-legend">
                <span class="dot tag"></span>标签匹配 ${byTag.size()} 个
                <span class="dot cf"></span>协同过滤 ${byCf.size()} 个
            </p>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty recommends}">
            <div class="empty-box">
                <p>你还没有收藏记录，先收藏几个歌单，系统就能为你推荐了</p>
                <a class="btn-detail" href="${ctx}/playlists">去收藏歌单</a>
            </div>

            <h3 class="section-sub">先看看大家都在听</h3>
            <div class="card-grid">
                <c:forEach var="p" items="${hotPlaylists}">
                    <div class="card">
                        <div class="card-cover">${p.cover}</div>
                        <h3 class="card-title"><a href="${ctx}/playlist?id=${p.id}">${p.name}</a></h3>
                        <p class="card-desc">${p.description}</p>
                        <div class="card-actions">
                            <a class="btn-fav ${p.favorited ? 'on' : ''}"
                               href="${ctx}/favorite/toggle?playlistId=${p.id}&from=${ctx}/recommend">
                                ${p.favorited ? '♥ 已收藏' : '♡ 收藏'}
                            </a>
                            <span class="meta">▶ ${p.playCount}</span>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
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

                        <c:if test="${item.percent > 0}">
                            <div class="match-box">
                                <div class="match-label">推荐指数 <b>${item.percentText}%</b></div>
                                <div class="match-bar">
                                    <div class="match-bar-inner" style="width:${item.percent}%"></div>
                                </div>
                            </div>
                        </c:if>

                        <div class="match-reason">${item.reason}</div>

                        <div class="card-actions">
                            <a class="btn-fav ${item.playlist.favorited ? 'on' : ''}"
                               href="${ctx}/favorite/toggle?playlistId=${item.playlist.id}&from=${ctx}/recommend">
                                ${item.playlist.favorited ? '♥ 已收藏' : '♡ 收藏'}
                            </a>
                            <span class="meta">▶ ${item.playlist.playCount}</span>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="common/footer.jsp" %>
