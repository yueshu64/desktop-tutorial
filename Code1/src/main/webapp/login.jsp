<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="登录" scope="request"/>
<c:set var="nav" value="login" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="auth-wrap">
    <section class="auth-side">
        <p class="eyebrow">SICHUAN UNIVERSITY OF SCIENCE &amp; ENGINEERING</p>
        <h1>登录失物招领</h1>
        <p>学生登录后进入浏览模式，可以发布和管理自己的信息。管理员账号会自动进入后台。</p>
        <ol>
            <li>游客只能浏览，不能发布。</li>
            <li>没有账号时，先去注册。</li>
            <li>学生使用 12 位学号登录，预设账号的密码与学号相同。</li>
        </ol>
    </section>
    <section class="auth-form">
        <h2>用户登录</h2>
        <form class="stack-form" method="post" action="${ctx}/user">
            <input type="hidden" name="action" value="login">
            <input type="hidden" name="from" value="<c:out value='${from}'/>">
            <label class="field">
                <span>账号</span>
                <input name="username" required maxlength="20" autocomplete="username" placeholder="12 位学号或管理员学号" value="<c:out value='${username}'/>">
            </label>
            <label class="field">
                <span>密码</span>
                <input type="password" name="password" required maxlength="20" autocomplete="current-password">
            </label>
            <button class="btn btn-green" type="submit">登录</button>
        </form>
        <p class="switch-link">还没有账号？ <a href="${ctx}/user?action=toRegister">去注册</a></p>
    </section>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
