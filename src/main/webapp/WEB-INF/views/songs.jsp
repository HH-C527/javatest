<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section>
    <h2 class="section-title">
        歌曲库
        <c:if test="${not empty keyword}"><span class="sub">搜索：${keyword}</span></c:if>
    </h2>

    <form class="search-form" action="${ctx}/songs" method="get">
        <input type="text" name="keyword" value="${keyword}" placeholder="搜索歌名、艺术家或自然声类型"/>
        <button type="submit">搜索</button>
        <a class="btn-reset" href="${ctx}/songs">重置</a>
    </form>

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
        <c:choose>
            <c:when test="${empty songs}">
                <tr>
                    <td colspan="7" class="empty">没有找到匹配的歌曲</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="s" items="${songs}" varStatus="st">
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
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</section>

<%@ include file="common/footer.jsp" %>
