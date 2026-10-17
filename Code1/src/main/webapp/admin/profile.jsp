<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty profile}">
    <jsp:forward page="/admin">
        <jsp:param name="action" value="profile"/>
    </jsp:forward>
</c:if>
<c:set var="pageTitle" value="个人中心" scope="request"/>
<c:set var="adminNav" value="profile" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/admin-header.jsp" %>
<%@ include file="/WEB-INF/jsp/common/profile-form.jsp" %>
<%@ include file="/WEB-INF/jsp/common/admin-footer.jsp" %>
