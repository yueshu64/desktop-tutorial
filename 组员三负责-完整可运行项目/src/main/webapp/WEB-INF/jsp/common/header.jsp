<%@ page pageEncoding="UTF-8" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? '校园失物招领' : pageTitle}"/> · 四川轻化工大学</title>
    <link rel="icon" href="${ctx}/favicon.svg" type="image/svg+xml">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body class="page-public">
<header class="site-header">
    <div class="topbar">
        <div class="container topbar-inner">
            <span>四川轻化工大学</span>
            <span class="topbar-en">Sichuan University of Science &amp; Engineering</span>
            <span class="topbar-campus">李白河校区 · 汇东校区 · 宜宾校区</span>
        </div>
    </div>
    <div class="navbar">
        <div class="container nav-inner">
            <a class="brand" href="${ctx}/item?action=list">
                <span class="seal" aria-hidden="true">化</span>
                <span class="brand-text">
                    <strong>校园失物招领</strong>
                    <small>SUSE Lost &amp; Found</small>
                </span>
            </a>
            <nav class="nav-links" aria-label="主导航">
                <a class="${nav == 'home' ? 'active' : ''}" href="${ctx}/item?action=list">首页</a>
                <a class="${nav == 'notice' ? 'active' : ''}" href="${ctx}/notice">公告</a>
                <a class="js-auth ${nav == 'publish' ? 'active' : ''}" data-feature="发布信息" href="${ctx}/item?action=publishPage">我要发布</a>
                <c:if test="${not empty loginUser}">
                    <a class="${nav == 'my' ? 'active' : ''}" href="${ctx}/item?action=my">我的发布</a>
                </c:if>
            </nav>
            <div class="nav-right">
                <c:choose>
                    <c:when test="${empty loginUser}">
                        <a class="btn btn-gold" href="${ctx}/user?action=toLogin">登录</a>
                    </c:when>
                    <c:otherwise>
                        <span class="who"><c:out value="${loginUser.name}"/></span>
                        <a class="link-quiet" href="${ctx}/user?action=logout">退出</a>
                        <c:choose>
                            <c:when test="${loginUser.role == 1}">
                                <a class="btn btn-gold" href="${ctx}/admin?action=profile">个人中心</a>
                            </c:when>
                            <c:otherwise>
                                <a class="btn btn-gold" href="${ctx}/user?action=profile">个人中心</a>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</header>
<c:if test="${nav != 'login' && nav != 'register'}">
    <div class="mode-strip ${empty loginUser ? 'guest' : (loginUser.role == 1 ? 'admin' : 'student')}">
        <div class="container">
            <c:choose>
                <c:when test="${empty loginUser}">
                    当前为游客模式，可以浏览失物与公告；发布和修改需要登录。
                    <a href="${ctx}/user?action=toLogin">去登录</a>
                </c:when>
                <c:when test="${loginUser.role == 1}">
                    当前为管理员 <strong><c:out value="${loginUser.name}"/></strong>。前台可以浏览，从个人中心进入管理功能。
                </c:when>
                <c:otherwise>
                    当前为学生浏览模式，你好，<strong><c:out value="${loginUser.name}"/></strong>。你可以发布信息，并在个人中心管理自己的帖子。
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</c:if>
<c:if test="${not empty flash}">
    <div class="container alert-bar">
        <div class="alert ${flashType == 'ok' ? 'alert-ok' : 'alert-warn'}"><c:out value="${flash}"/></div>
    </div>
    <c:remove var="flash" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>
<c:if test="${not empty msg}">
    <div class="container alert-bar">
        <div class="alert ${msgType == 'ok' ? 'alert-ok' : 'alert-warn'}"><c:out value="${msg}"/></div>
    </div>
</c:if>
