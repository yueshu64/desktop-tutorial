<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty noticesReady}">
    <jsp:forward page="/admin">
        <jsp:param name="action" value="notice"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="发布公告" scope="request"/>
<c:set var="adminNav" value="notice" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/admin-header.jsp" %>
<div class="split">
    <section class="panel">
        <h2>${not empty editNotice && editNotice.noticeId > 0 ? '编辑公告' : '发布公告'}</h2>
        <form class="stack-form admin-form" method="post" action="${ctx}/admin">
            <input type="hidden" name="action" value="saveNotice">
            <c:if test="${not empty editNotice && editNotice.noticeId > 0}">
                <input type="hidden" name="id" value="${editNotice.noticeId}">
            </c:if>
            <label class="field">
                <span>标题</span>
                <input name="title" maxlength="80" required value="<c:out value='${editNotice.title}'/>">
            </label>
            <label class="field">
                <span>内容</span>
                <textarea name="content" maxlength="1000" required><c:out value="${editNotice.content}"/></textarea>
            </label>
            <div class="form-actions">
                <button class="btn btn-green" type="submit">${not empty editNotice && editNotice.noticeId > 0 ? '保存修改' : '发布'}</button>
                <c:if test="${not empty editNotice && editNotice.noticeId > 0}">
                    <a class="btn btn-ghost" href="${ctx}/admin?action=notice">取消编辑</a>
                </c:if>
            </div>
        </form>
    </section>
    <section>
        <div class="table-wrap">
            <table>
                <thead>
                <tr><th>标题</th><th>来源</th><th>时间</th><th>操作</th></tr>
                </thead>
                <tbody>
                <c:forEach var="n" items="${notices}">
                    <tr>
                        <td><c:out value="${n.title}"/></td>
                        <td><c:out value="${n.authorLabel}"/></td>
                        <td><fmt:formatDate value="${n.createTime}" pattern="MM-dd HH:mm"/></td>
                        <td>
                            <div class="ops">
                                <a class="btn btn-ghost btn-sm" href="${ctx}/admin?action=notice&id=${n.noticeId}">编辑</a>
                                <form method="post" action="${ctx}/admin" onsubmit="return confirm('确定删除这条公告？');">
                                    <input type="hidden" name="action" value="deleteNotice">
                                    <input type="hidden" name="id" value="${n.noticeId}">
                                    <button class="btn btn-danger btn-sm" type="submit">删除</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty notices}">
                    <tr><td colspan="4">还没有公告。</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>
</div>
<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
