<%@ page pageEncoding="UTF-8" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? '管理后台' : pageTitle}"/> · 四川轻化工大学</title>
    <link rel="icon" href="${ctx}/favicon.svg" type="image/svg+xml">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body class="page-admin">
<div class="admin-shell">
    <aside class="sider">
        <a class="sider-brand" href="${ctx}/admin?action=dashboard">
            <span class="seal" aria-hidden="true">化</span>
            <span>
                <strong>招领管理后台</strong>
                <small>四川轻化工大学</small>
            </span>
        </a>
        <nav class="sider-nav">
            <a class="${adminNav == 'dashboard' ? 'active' : ''}" href="${ctx}/admin?action=dashboard">数据概览</a>
            <a class="${adminNav == 'items' ? 'active' : ''}" href="${ctx}/admin?action=items">信息管理</a>
            <a class="${adminNav == 'users' ? 'active' : ''}" href="${ctx}/admin?action=users">用户管理</a>
            <a class="${adminNav == 'notice' ? 'active' : ''}" href="${ctx}/admin?action=notice">发布公告</a>
            <a class="${adminNav == 'profile' ? 'active' : ''}" href="${ctx}/admin?action=profile">个人中心</a>
        </nav>
        <div class="sider-foot">
            <p>当前管理员<br><strong><c:out value="${loginUser.name}"/></strong></p>
            <a href="${ctx}/item?action=list">返回前台</a>
            <a href="${ctx}/user?action=logout">退出登录</a>
        </div>
    </aside>
    <div class="admin-content">
        <header class="admin-top">
            <div>
                <p class="eyebrow">管理后台</p>
                <h1><c:out value="${empty pageTitle ? '数据概览' : pageTitle}"/></h1>
            </div>
        </header>
        <c:if test="${not empty flash}">
            <div class="alert ${flashType == 'ok' ? 'alert-ok' : 'alert-warn'}"><c:out value="${flash}"/></div>
            <c:remove var="flash" scope="session"/>
            <c:remove var="flashType" scope="session"/>
        </c:if>
        <c:if test="${not empty msg}">
            <div class="alert ${msgType == 'ok' ? 'alert-ok' : 'alert-warn'}"><c:out value="${msg}"/></div>
        </c:if>
        <div class="admin-body">
