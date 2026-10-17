<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty itemsReady}">
    <jsp:forward page="/item">
        <jsp:param name="action" value="my"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="我的发布" scope="request"/>
<c:set var="nav" value="my" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="section">
    <div class="container">
        <div class="page-head">
            <div>
                <p class="eyebrow">个人中心</p>
                <h1>我的发布</h1>
            </div>
            <a class="btn btn-green" href="${ctx}/item?action=publishPage">发布新信息</a>
        </div>
        <c:choose>
            <c:when test="${empty items}">
                <div class="panel empty">你还没有发布过信息。</div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>标题</th>
                            <th>类型</th>
                            <th>地点</th>
                            <th>状态</th>
                            <th>时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="it" items="${items}">
                            <tr>
                                <td><a href="${ctx}/item?action=detail&id=${it.itemId}"><c:out value="${it.title}"/></a></td>
                                <td><span class="badge ${it.itemType == '拾到' ? 'badge-found' : 'badge-lost'}"><c:out value="${it.itemType}"/></span></td>
                                <td><c:out value="${it.location}"/></td>
                                <td><span class="badge ${it.status == 1 ? 'badge-solved' : (it.status == 2 ? 'badge-closed' : 'badge-pending')}"><c:out value="${it.statusText}"/></span></td>
                                <td><fmt:formatDate value="${it.createTime}" pattern="MM-dd HH:mm"/></td>
                                <td>
                                    <div class="ops">
                                        <c:if test="${it.status != 2}">
                                            <a class="btn btn-ghost btn-sm" href="${ctx}/item?action=edit&id=${it.itemId}">修改</a>
                                        </c:if>
                                        <c:if test="${it.status == 0}">
                                            <form method="post" action="${ctx}/item" onsubmit="return confirm('确定标记为已解决？');">
                                                <input type="hidden" name="action" value="resolve">
                                                <input type="hidden" name="id" value="${it.itemId}">
                                                <input type="hidden" name="back" value="my">
                                                <button class="btn btn-green btn-sm" type="submit">已解决</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${it.status != 2}">
                                            <form method="post" action="${ctx}/item" onsubmit="return confirm('确定关闭这条信息？');">
                                                <input type="hidden" name="action" value="close">
                                                <input type="hidden" name="id" value="${it.itemId}">
                                                <input type="hidden" name="back" value="my">
                                                <button class="btn btn-ghost btn-sm" type="submit">关闭</button>
                                            </form>
                                        </c:if>
                                        <form method="post" action="${ctx}/item" onsubmit="return confirm('确定删除这条信息？');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${it.itemId}">
                                            <button class="btn btn-danger btn-sm" type="submit">删除</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
