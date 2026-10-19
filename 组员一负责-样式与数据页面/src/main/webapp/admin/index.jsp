<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty dashboardReady}">
    <jsp:forward page="/admin">
        <jsp:param name="action" value="dashboard"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="数据概览" scope="request"/>
<c:set var="adminNav" value="dashboard" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/admin-header.jsp" %>
<div class="stat-grid">
    <a class="stat-card primary" href="${ctx}/admin?action=items"><b>${totalItems}</b><span>总发布数</span></a>
    <a class="stat-card primary" href="${ctx}/admin?action=items&status=1"><b>${solvedItems}</b><span>已解决数</span></a>
    <div class="stat-card"><b>${pendingItems}</b><span>待处理</span></div>
    <a class="stat-card" href="${ctx}/admin?action=users"><b>${userCount}</b><span>注册用户</span></a>
</div>
<section class="panel">
    <h2>最近发布</h2>
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>标题</th><th>类型</th><th>发布人</th><th>状态</th><th>时间</th></tr>
            </thead>
            <tbody>
            <c:forEach var="it" items="${latestItems}">
                <tr>
                    <td><a href="${ctx}/item?action=detail&id=${it.itemId}"><c:out value="${it.title}"/></a></td>
                    <td><c:out value="${it.itemType}"/></td>
                    <td><c:out value="${it.publisherName}"/></td>
                    <td><c:out value="${it.statusText}"/></td>
                    <td><fmt:formatDate value="${it.createTime}" pattern="MM-dd HH:mm"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <p class="hint">公告共 ${noticeCount} 条，已关闭 ${closedItems} 条。删除违规信息后，系统会自动发布公告。</p>
</section>
<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
