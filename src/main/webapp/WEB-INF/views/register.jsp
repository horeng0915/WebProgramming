<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SNS</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
    <script src="${pageContext.request.contextPath}/myscript.js"></script>
</head>
<body id="register">
    <div class="form-container">
        <h1>회원가입</h1>
        <form method="post" action="/register" onsubmit="return validatePassword()">
            <fieldset>
                <legend>회원 정보 입력</legend>
                <label for="id">아이디</label>
                <input type="text" name="id" id="id" required />
                <button id="check_btn" type="button" onclick="openDuplicateCheckPage()">중복검사</button><br />
                <label for="password">비밀번호</label>
                <input type="password" name="passwd" id="passwd" required /><br />
                <label for="password2">비밀번호 확인</label>
                <input type="password" name="passwd2" id="passwd2" required />
                <!-- 패스워드 일치 여부 표시 -->
                <span id="password-message"></span><br />
                <label for="name">이름</label>
                <input type="text" name="name" required /><br />
                <label for="email">이메일</label>
                <input type="email" name="email" required />
                <br />
                <label for="age">(만)나이</label>
                <select name="age" required>
                    <c:forEach var="age" begin="1" end="99">
                        <option value="${age}">${age}</option>
                    </c:forEach>
                </select>
                <br />
                <label for="gender">성별</label>
                <input type="radio" name="gender" value="Male" required> 남성
                <input type="radio" name="gender" value="Female" required> 여성<br>
            </fieldset>
            <input type="submit" name="submit" value="제출" class="btn" />
            <input type="reset" name="reset" value="초기화" class="btn" />
        </form>
    </div>
</body>
</html>
