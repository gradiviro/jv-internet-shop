<%--
  Created by IntelliJ IDEA.
  User: Home Workstation
  Date: 09.09.2020
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New Product</title>
</head>
<body>
<h4 style="color: blue">${message}</h4>
<form method="post" action="${pageContext.request.contextPath}/products/new">
    Product Name<input type="text" name="name">
    Price<input type="text" name="price">
    <button type="submit">Add!</button>
</form>
<a href="${pageContext.request.contextPath}/">Back to main</a>
</body>
</html>