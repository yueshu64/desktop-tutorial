<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty profile}">
    <jsp:forward page="/user">
        <jsp:param name="action" value="profile"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="个人中心" scope="request"/>
<c:set var="nav" value="profile" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="section">
    <div class="container">
        <div class="page-head">
            <div>
                <p class="eyebrow">学生账号</p>
                <h1>个人中心</h1>
            </div>
        </div>
        <div class="hub">
            <a href="${ctx}/item?action=publishPage"><strong>发布信息</strong><span>发布丢失或拾到</span></a>
            <a href="${ctx}/item?action=my"><strong>我的发布</strong><span>查看、修改、删除</span></a>
            <a href="${ctx}/item?action=list"><strong>返回首页</strong><span>继续浏览招领信息</span></a>
        </div>
        <%@ include file="/WEB-INF/jsp/common/profile-form.jsp" %>
    </div>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
