<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty usersReady}">
    <jsp:forward page="/admin">
        <jsp:param name="action" value="users"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="用户管理" scope="request"/>
<c:set var="adminNav" value="users" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/admin-header.jsp" %>
<p class="hint">可以查看全部注册用户。学生账号可以删除，删除时会一并移除其发布的信息，并自动发布系统公告。四位管理员账号不能删除。</p>
<div class="table-wrap">
    <table>
        <thead>
        <tr><th>账号</th><th>姓名</th><th>手机</th><th>身份</th><th>发布数</th><th>注册时间</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><c:out value="${u.username}"/></td>
                <td><c:out value="${u.name}"/></td>
                <td><c:out value="${empty u.phone ? '—' : u.phone}"/></td>
                <td><span class="badge ${u.role == 1 ? 'badge-system' : 'badge-found'}"><c:out value="${u.roleText}"/></span></td>
                <td>${u.itemCount}</td>
                <td><fmt:formatDate value="${u.createTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td>
                    <c:if test="${u.role == 0}">
                        <form method="post" action="${ctx}/admin" onsubmit="return confirm('确定删除该学生及其发布的全部信息？');">
                            <input type="hidden" name="action" value="deleteUser">
                            <input type="hidden" name="id" value="${u.userId}">
                            <button class="btn btn-danger btn-sm" type="submit">删除</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
