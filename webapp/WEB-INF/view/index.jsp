<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>Chatroom|聊天室</title>
    <jsp:include page="common/commonfile.jsp"/>
    <script src="${ctx}/public/javascript/userManage.js"></script>
</head>
<body>
<jsp:include page="common/header.jsp"/>
<!-- content start -->
<div class="admin-content" id="main_content" >

</div>
<script>
     userManage.index();
</script>
</body>
</html>

