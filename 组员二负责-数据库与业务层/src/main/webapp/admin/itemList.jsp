<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty itemPage}">
    <jsp:forward page="/admin">
        <jsp:param name="action" value="items"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="信息管理" scope="request"/>
<c:set var="adminNav" value="items" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/admin-header.jsp" %>
<form class="filter-bar" action="${ctx}/admin" method="get">
    <input type="hidden" name="action" value="items">
    <select name="type">
        <option value="" ${empty type ? 'selected' : ''}>全部类型</option>
        <option value="丢失" ${type == '丢失' ? 'selected' : ''}>丢失</option>
        <option value="拾到" ${type == '拾到' ? 'selected' : ''}>拾到</option>
    </select>
    <select name="status">
        <option value="" ${empty statusFilter ? 'selected' : ''}>全部状态</option>
        <option value="0" ${statusFilter == '0' ? 'selected' : ''}>待处理</option>
        <option value="1" ${statusFilter == '1' ? 'selected' : ''}>已解决</option>
        <option value="2" ${statusFilter == '2' ? 'selected' : ''}>已关闭</option>
    </select>
    <input type="search" name="keyword" placeholder="标题 / 地点" value="<c:out value='${keyword}'/>">
    <button class="btn btn-green btn-sm" type="submit">筛选</button>
</form>
<p class="result-line">共 ${itemPage.total} 条。删除后会自动发布系统公告。</p>
<div class="table-wrap">
    <table>
        <thead>
        <tr><th>标题</th><th>类型</th><th>地点</th><th>发布人</th><th>状态</th><th>时间</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="it" items="${itemPage.records}">
            <tr>
                <td><a href="${ctx}/item?action=detail&id=${it.itemId}"><c:out value="${it.title}"/></a></td>
                <td><c:out value="${it.itemType}"/></td>
                <td><c:out value="${it.location}"/></td>
                <td><c:out value="${it.publisherName}"/></td>
                <td><c:out value="${it.statusText}"/></td>
                <td><fmt:formatDate value="${it.createTime}" pattern="MM-dd HH:mm"/></td>
                <td>
                    <form method="post" action="${ctx}/admin" onsubmit="return confirm('确定删除这条违规信息？');">
                        <input type="hidden" name="action" value="deleteItem">
                        <input type="hidden" name="id" value="${it.itemId}">
                        <button class="btn btn-danger btn-sm" type="submit">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty itemPage.records}">
            <tr><td colspan="7">没有符合条件的信息。</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<div class="pager">
    <c:if test="${itemPage.totalPages > 1}">
        <c:forEach begin="1" end="${itemPage.totalPages}" var="i">
            <c:url var="pageUrl" value="/admin">
                <c:param name="action" value="items"/>
                <c:param name="type" value="${type}"/>
                <c:param name="status" value="${statusFilter}"/>
                <c:param name="keyword" value="${keyword}"/>
                <c:param name="page" value="${i}"/>
            </c:url>
            <a class="${i == itemPage.page ? 'active' : ''}" href="${pageUrl}">${i}</a>
        </c:forEach>
    </c:if>
</div>
<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
