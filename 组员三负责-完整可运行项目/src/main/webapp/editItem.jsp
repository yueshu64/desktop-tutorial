<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty item}">
    <c:redirect url="/item?action=my"/>
</c:if>
<c:set var="pageTitle" value="修改信息" scope="request"/>
<c:set var="nav" value="my" scope="request"/>
<c:set var="formAction" value="update" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="narrow">
    <div class="page-head">
        <div>
            <p class="eyebrow">我的发布</p>
            <h1>修改信息</h1>
        </div>
    </div>
    <section class="panel">
        <p class="hint">当前状态：<c:out value="${item.statusText}"/>。只能修改自己发布、且尚未关闭的信息。</p>
        <%@ include file="/WEB-INF/jsp/common/item-form.jsp" %>
    </section>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
