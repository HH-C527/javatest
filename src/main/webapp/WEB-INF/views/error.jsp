<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="common/header.jsp" %>

<section>
    <div class="empty-box">
        <h2>${not empty message ? message : '页面出错了'}</h2>
        <a class="btn-detail" href="${ctx}/home">返回首页</a>
    </div>
</section>

<%@ include file="common/footer.jsp" %>
