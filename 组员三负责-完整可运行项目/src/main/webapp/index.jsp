<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty itemPage}">
    <jsp:forward page="/item">
        <jsp:param name="action" value="list"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="首页" scope="request"/>
<c:set var="nav" value="home" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<section class="hero">
    <div class="container hero-inner">
        <div>
            <p class="eyebrow">SICHUAN UNIVERSITY OF SCIENCE &amp; ENGINEERING</p>
            <h1>校园失物招领</h1>
            <div class="hero-actions">
                <a class="btn btn-gold js-auth" data-feature="发布信息" href="${ctx}/item?action=publishPage">发布失物 / 拾到</a>
                <a class="btn btn-ghost" href="${ctx}/notice">查看公告</a>
            </div>
        </div>
        <div class="hero-motto">
            <span>厚德达理</span>
            <span>励志勤工</span>
        </div>
    </div>
</section>
<section class="stats" aria-label="当前数据">
    <div class="stat"><b>${pendingLost}</b><span>待处理的丢失</span></div>
    <div class="stat"><b>${pendingFound}</b><span>待处理的拾到</span></div>
    <div class="stat"><b>${solvedCount}</b><span>已解决</span></div>
</section>
<main class="section">
    <div class="container home-layout">
        <section>
            <form id="filterForm" class="filter-bar" action="${ctx}/item" method="get">
                <input type="hidden" name="action" value="list">
                <select name="type" aria-label="按类型筛选">
                    <option value="" ${empty type ? 'selected' : ''}>全部类型</option>
                    <option value="丢失" ${type == '丢失' ? 'selected' : ''}>丢失</option>
                    <option value="拾到" ${type == '拾到' ? 'selected' : ''}>拾到</option>
                </select>
                <input type="search" name="keyword" maxlength="30" placeholder="按地点 / 标题搜索" value="<c:out value='${keyword}'/>">
                <button class="btn btn-green" type="submit">搜索</button>
                <c:if test="${not empty type or not empty keyword}">
                    <a class="btn btn-ghost" href="${ctx}/item?action=list">清除</a>
                </c:if>
            </form>
            <p class="result-line" id="resultCount">共 ${itemPage.total} 条<c:if test="${not empty type}"> · <c:out value="${type}"/></c:if><c:if test="${not empty keyword}"> · “<c:out value="${keyword}"/>”</c:if></p>
            <div class="item-grid" id="itemGrid">
                <c:choose>
                    <c:when test="${empty itemPage.records}">
                        <div class="empty">没有找到相关信息，换个关键词或类型试试。</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="it" items="${itemPage.records}">
                            <a class="item-card ${it.itemType == '拾到' ? 'type-found' : 'type-lost'} ${it.status != 0 ? 'is-muted' : ''}" href="${ctx}/item?action=detail&id=${it.itemId}">
                                <div class="item-flags">
                                    <span class="badge ${it.itemType == '拾到' ? 'badge-found' : 'badge-lost'}"><c:out value="${it.itemType}"/></span>
                                    <span class="badge ${it.status == 1 ? 'badge-solved' : (it.status == 2 ? 'badge-closed' : 'badge-pending')}"><c:out value="${it.statusText}"/></span>
                                </div>
                                <div class="item-main">
                                    <h3><c:out value="${it.title}"/></h3>
                                    <p><c:out value="${it.location}"/></p>
                                </div>
                                <div class="item-meta">
                                    <div><c:out value="${it.publisherName}"/></div>
                                    <time><fmt:formatDate value="${it.createTime}" pattern="yyyy-MM-dd HH:mm"/></time>
                                </div>
                            </a>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="pager" id="itemPager">
                <c:if test="${itemPage.totalPages > 1}">
                    <c:forEach begin="1" end="${itemPage.totalPages}" var="i">
                        <c:url var="pageUrl" value="/item">
                            <c:param name="action" value="list"/>
                            <c:param name="type" value="${type}"/>
                            <c:param name="keyword" value="${keyword}"/>
                            <c:param name="page" value="${i}"/>
                        </c:url>
                        <a data-page="${i}" class="${i == itemPage.page ? 'active' : ''}" href="${pageUrl}">${i}</a>
                    </c:forEach>
                </c:if>
            </div>
        </section>
        <aside class="panel side-card">
            <h2>最新公告</h2>
            <c:choose>
                <c:when test="${empty notices}">
                    <p class="empty">暂无公告</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="n" items="${notices}">
                        <a class="notice-peek" href="${ctx}/notice#n${n.noticeId}">
                            <strong><c:out value="${n.title}"/></strong>
                            <span><c:out value="${n.authorLabel}"/> · <fmt:formatDate value="${n.createTime}" pattern="MM-dd HH:mm"/></span>
                        </a>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            <p><a class="more" href="${ctx}/notice">全部公告</a></p>
        </aside>
    </div>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
