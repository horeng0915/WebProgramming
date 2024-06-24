<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>글 쓰기</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
</head>
<body id="create">
    <div class="form-container">
        <h1>글 쓰기</h1>
        <form action="/uploadFile" method="post" enctype="multipart/form-data">
            <label for="title"><h3>제목</h3></label>
            <input type="text" name="title" id="title" required><br>
            <label for="content"><h3>내용</h3></label>
            <textarea name="content" id="content" required></textarea><br>
            <label for="file"><h3>이미지/영상 첨부</h3></label>
            <h4 style="text-align:left">반드시 이미지 또는 영상을 올려주세요! 또한 SNS에서 지원하지 않는 확장자는 표시되지 않을 수 있습니다.</h4>
            <input type="file" name="file" id="file"><br>
            <input type="submit" value="업로드">
        </form>
        <button class="btn home-btn"><a href="/main">홈으로</a></button>
    </div>
</body>
</html>