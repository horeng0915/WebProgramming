<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 완료</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
</head>
<body id="regi_s">
    <div class="register-success-container">
        <h1>회원가입 완료!</h1>
        <h2>가입한 사용자 정보</h2>
        <p>나의 아이디: ${user.id}</p>
        <p>나의 이름: ${user.name}</p>
        <h3>이제 홈으로 돌아가 SNS를 이용할 수 있습니다!</h3>
        <button class="btn home-btn"><a href="/main">홈으로</a></button>
    </div>
</body>
</html>