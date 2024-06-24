<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>아이디 중복 검사</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
</head>
<body>
    <h1>아이디 중복 검사</h1>
    <form method="post" action="/checkDuplicate">
        <label for="username">아이디:</label>
        <input type="text" name="id" id="id" required>
        <button type="submit">중복 검사</button>
    </form>
    <!-- 중복 검사 결과 표시 -->
    <c:if test="${not empty message}">
        <p>${message}</p>
    </c:if>
</body>
</html>