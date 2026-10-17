<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty noticeReady}">
    <jsp:forward page="/notice"/>
</c:if>
<c:set var="pageTitle" value="公告" scope="request"/>
<c:set var="nav" value="notice" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="narrow">
    <div class="page-head">
        <div>
            <p class="eyebrow">通知公告</p>
            <h1>公告列表</h1>
        </div>
    </div>
    <p class="hint">带「系统」标记的公告，是注册、发布、解决、关闭或删除等数据更新成功后自动发布的。</p>
    <c:choose>
        <c:when test="${empty notices}">
            <div class="panel empty">暂时还没有公告。</div>
        </c:when>
        <c:otherwise>
            <c:forEach var="n" items="${notices}">
                <article class="notice-card" id="n${n.noticeId}">
                    <div class="notice-kicker">
                        <span class="badge ${empty n.adminId ? 'badge-system' : 'badge-found'}"><c:out value="${n.authorLabel}"/></span>
                        <time><fmt:formatDate value="${n.createTime}" pattern="yyyy-MM-dd HH:mm"/></time>
                    </div>
                    <h2><c:out value="${n.title}"/></h2>
                    <p class="prose"><c:out value="${n.content}"/></p>
                </article>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
