<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="提示" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="narrow">
    <section class="panel">
        <p class="eyebrow">四川轻化工大学</p>
        <h1>
            <c:choose>
                <c:when test="${requestScope['javax.servlet.error.status_code'] == 404}">页面不存在</c:when>
                <c:otherwise>暂时无法完成操作</c:otherwise>
            </c:choose>
        </h1>
        <p>
            <c:out value="${empty msg ? '请返回首页继续浏览，或稍后再试。' : msg}"/>
        </p>
        <a class="btn btn-green" href="${ctx}/item?action=list">返回首页</a>
    </section>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
