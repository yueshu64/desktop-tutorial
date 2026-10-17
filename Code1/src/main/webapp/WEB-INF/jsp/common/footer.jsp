<%@ page pageEncoding="UTF-8" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<footer class="site-footer">
    <div class="container footer-grid">
        <div>
            <strong>四川轻化工大学</strong>
            <p>校园失物招领</p>
            <p class="motto">厚德达理 · 励志勤工</p>
        </div>
        <div>
            <span>校区</span>
            <p>李白河校区<br>汇东校区<br>宜宾校区</p>
        </div>
        <div>
            <span>使用说明</span>
            <p>游客可浏览信息与公告。登录后可以发布和修改自己的帖子。管理员登录后进入后台。</p>
        </div>
    </div>
    <div class="footer-bottom">© 2026 四川轻化工大学校园失物招领系统</div>
</footer>

<div class="modal" id="guestModal" hidden>
    <div class="modal-mask" data-close="1"></div>
    <div class="modal-card" role="dialog" aria-modal="true" aria-labelledby="guestTitle">
        <p class="eyebrow">游客模式</p>
        <h3 id="guestTitle">需要登录后才能继续</h3>
        <p id="guestModalText">你为游客不可进行对应功能。</p>
        <p class="modal-ask">是否前往登录？</p>
        <div class="modal-actions">
            <button type="button" class="btn btn-ghost" id="guestCancel">再看看</button>
            <a class="btn btn-gold" id="guestGoLogin" href="${ctx}/user?action=toLogin">去登录</a>
        </div>
    </div>
</div>
<script>
    window.CTX = "${ctx}";
    window.LOGGED_IN = ${not empty sessionScope.loginUser};
</script>
<script src="${ctx}/js/main.js"></script>
</body>
</html>
