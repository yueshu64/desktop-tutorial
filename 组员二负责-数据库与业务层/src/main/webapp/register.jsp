<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="注册" scope="request"/>
<c:set var="nav" value="register" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<main class="auth-wrap">
    <section class="auth-side">
        <p class="eyebrow">新用户注册</p>
        <h1>加入校园失物招领</h1>
        <p>注册后的账号都是学生。注册信息写入数据库并通过校验后，系统会自动发布一条公告。</p>
    </section>
    <section class="auth-form">
        <h2>创建账号</h2>
        <form class="stack-form" method="post" action="${ctx}/user">
            <input type="hidden" name="action" value="register">
            <label class="field">
                <span>学号</span>
                <input name="username" required minlength="12" maxlength="12" pattern="\d{12}" placeholder="12 位学号，如 241011005001" value="<c:out value='${username}'/>">
                <small class="hint">12 位学号。前两位是入学学年，末三位是班内编号，中间是学院、专业和班号。密码可以与学号相同。</small>
            </label>
            <div class="form-row">
                <label class="field">
                    <span>密码</span>
                    <input type="password" name="password" required minlength="6" maxlength="20" placeholder="可与学号相同">
                </label>
                <label class="field">
                    <span>确认密码</span>
                    <input type="password" name="confirm" required minlength="6" maxlength="20">
                </label>
            </div>
            <div class="form-row">
                <label class="field">
                    <span>姓名</span>
                    <input name="name" required maxlength="20" value="<c:out value='${name}'/>">
                </label>
                <label class="field">
                    <span>手机号</span>
                    <input name="phone" maxlength="11" placeholder="可不填" value="<c:out value='${phone}'/>">
                </label>
            </div>
            <button class="btn btn-green" type="submit">注册</button>
        </form>
        <p class="switch-link">已有账号？ <a href="${ctx}/user?action=toLogin">去登录</a></p>
    </section>
</main>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
