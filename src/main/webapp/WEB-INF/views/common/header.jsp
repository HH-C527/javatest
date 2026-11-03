<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>自然音乐 · 歌单推荐与收藏</title>
    <link rel="stylesheet" href="${ctx}/css/style.css"/>
</head>
<body>

<header class="topbar">
    <div class="topbar-inner">
        <a href="${ctx}/home" class="logo">🎵 自然音乐</a>

        <nav class="nav">
            <a href="${ctx}/home" class="${activeMenu == 'home' ? 'active' : ''}">首页</a>
            <a href="${ctx}/playlists" class="${activeMenu == 'playlists' ? 'active' : ''}">歌单</a>
            <a href="${ctx}/recommend" class="${activeMenu == 'recommend' ? 'active' : ''}">推荐</a>
            <a href="${ctx}/favorites" class="${activeMenu == 'favorites' ? 'active' : ''}">我的收藏</a>
            <a href="${ctx}/songs" class="${activeMenu == 'songs' ? 'active' : ''}">歌曲</a>
        </nav>

        <div class="user-box">
            <c:if test="${not empty currentUser}">
                <span class="user-name">${currentUser.avatar} ${currentUser.nickname}</span>
            </c:if>
            <c:if test="${not empty users}">
                <select class="user-select"
                        onchange="location.href='${ctx}/user/switch?userId=' + this.value + '&from=' + encodeURIComponent(location.pathname + location.search)">
                    <c:forEach var="u" items="${users}">
                        <option value="${u.id}" ${u.id == currentUser.id ? 'selected' : ''}>${u.nickname}</option>
                    </c:forEach>
                </select>
            </c:if>
        </div>
    </div>
</header>

<main class="main">
<c:if test="${not empty flashMessage}">
    <div class="flash">${flashMessage}</div>
    <c:remove var="flashMessage" scope="session"/>
</c:if>
