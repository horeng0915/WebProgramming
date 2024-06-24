<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>글 수정</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
</head>
<body id="edit">
    <div class="form-container">
        <h1>글 수정</h1>
        <form action="/updatePost" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="${post.id}">
            <label for="title">제목:</label>
            <input type="text" name="title" id="title" value="${post.title}" required><br>
            <label for="content">내용:</label>
            <textarea name="content" id="content" required>${post.content}</textarea><br>
            <label for="file_name">기존 첨부 파일:</label>
            <p>${post.file_name}</p><br>
            <label for="newFile">새 첨부 파일:</label>
            <input type="file" name="newFile" id="newFile"><br>
            <input type="submit" value="수정" class="btn">
        </form>
        <button class="btn home-btn"><a href="/main">홈으로</a></button>
    </div>
</body>
</html>
