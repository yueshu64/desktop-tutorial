<%@ page pageEncoding="UTF-8" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="panel form-panel">
    <h2>基本资料</h2>
    <form class="stack-form" method="post" action="${ctx}/user">
        <input type="hidden" name="action" value="updateProfile">
        <div class="form-row">
            <label class="field">
                <span>登录账号</span>
                <input value="<c:out value='${profile.username}'/>" disabled>
            </label>
            <label class="field">
                <span>身份</span>
                <input value="${profile.roleText}" disabled>
            </label>
        </div>
        <div class="form-row">
            <label class="field">
                <span>姓名</span>
                <input name="name" maxlength="20" required value="<c:out value='${profile.name}'/>">
            </label>
            <label class="field">
                <span>手机号</span>
                <input name="phone" maxlength="11" placeholder="可不填" value="<c:out value='${profile.phone}'/>">
            </label>
        </div>
        <p class="hint">注册时间：<fmt:formatDate value="${profile.createTime}" pattern="yyyy-MM-dd HH:mm"/></p>
        <button class="btn btn-green" type="submit">保存资料</button>
    </form>
</section>
<section class="panel form-panel">
    <h2>修改密码</h2>
    <form class="stack-form" method="post" action="${ctx}/user">
        <input type="hidden" name="action" value="updatePassword">
        <label class="field">
            <span>原密码</span>
            <input type="password" name="oldPassword" required autocomplete="current-password">
        </label>
        <div class="form-row">
            <label class="field">
                <span>新密码</span>
                <input type="password" name="newPassword" required minlength="6" maxlength="20" autocomplete="new-password">
            </label>
            <label class="field">
                <span>确认新密码</span>
                <input type="password" name="confirm" required minlength="6" maxlength="20" autocomplete="new-password">
            </label>
        </div>
        <button class="btn btn-green" type="submit">修改密码</button>
    </form>
</section>
