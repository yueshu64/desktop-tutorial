<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty item}">
    <c:redirect url="/item?action=list"/>
</c:if>
<c:set var="pageTitle" value="${item.title}" scope="request"/>
<c:set var="nav" value="home" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="narrow">
    <div class="detail-top">
        <div>
            <p class="eyebrow">信息详情</p>
            <div class="badge-row">
                <span class="badge ${item.itemType == '拾到' ? 'badge-found' : 'badge-lost'}"><c:out value="${item.itemType}"/></span>
                <span class="badge ${item.status == 1 ? 'badge-solved' : (item.status == 2 ? 'badge-closed' : 'badge-pending')}"><c:out value="${item.statusText}"/></span>
            </div>
            <h1><c:out value="${item.title}"/></h1>
        </div>
        <a class="btn btn-ghost" href="${ctx}/item?action=list">返回列表</a>
    </div>
    <section class="panel">
        <dl class="meta-list">
            <dt>地点</dt><dd><c:out value="${item.location}"/></dd>
            <dt>发布人</dt><dd><c:out value="${item.publisherName}"/></dd>
            <dt>时间</dt><dd><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm"/></dd>
        </dl>
        <div class="prose"><c:out value="${item.description}"/></div>
        <div class="contact-box">
            <span>联系方式</span>
            <strong><c:out value="${empty item.contact ? '未留联系方式，请看详情里的存放说明' : item.contact}"/></strong>
        </div>
        <c:if test="${not empty loginUser && loginUser.userId == item.userId}">
            <div class="owner-bar">
                <c:if test="${item.status != 2}">
                    <a class="btn btn-ghost btn-sm" href="${ctx}/item?action=edit&id=${item.itemId}">修改</a>
                </c:if>
                <c:if test="${item.status == 0}">
                    <form method="post" action="${ctx}/item" onsubmit="return confirm('确定标记为已解决？');">
                        <input type="hidden" name="action" value="resolve">
                        <input type="hidden" name="id" value="${item.itemId}">
                        <input type="hidden" name="back" value="detail">
                        <button class="btn btn-green btn-sm" type="submit">标记已解决</button>
                    </form>
                </c:if>
                <c:if test="${item.status != 2}">
                    <form method="post" action="${ctx}/item" onsubmit="return confirm('确定关闭这条信息？关闭后不能再修改。');">
                        <input type="hidden" name="action" value="close">
                        <input type="hidden" name="id" value="${item.itemId}">
                        <input type="hidden" name="back" value="detail">
                        <button class="btn btn-ghost btn-sm" type="submit">关闭</button>
                    </form>
                </c:if>
                <form method="post" action="${ctx}/item" onsubmit="return confirm('确定删除这条信息？删除后不可恢复。');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${item.itemId}">
                    <button class="btn btn-danger btn-sm" type="submit">删除</button>
                </form>
            </div>
        </c:if>
    </section>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
