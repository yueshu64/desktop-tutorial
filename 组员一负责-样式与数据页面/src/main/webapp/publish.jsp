<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="发布信息" scope="request"/>
<c:set var="nav" value="publish" scope="request"/>
<c:set var="formAction" value="publish" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="narrow">
    <div class="page-head">
        <div>
            <p class="eyebrow">学生功能</p>
            <h1>发布失物 / 拾到</h1>
        </div>
    </div>
    <section class="panel">
        <p class="hint">地点和详情必须填写，漏填会弹出警告。联系方式可以不留，拾到的同学在详情里写明存放位置即可。</p>
        <%@ include file="/WEB-INF/jsp/common/item-form.jsp" %>
    </section>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
