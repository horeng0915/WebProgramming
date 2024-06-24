<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>팔로워의 글</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
    <script>
        function showAlert(message) {
            alert(message);
        }

        function isImageFile(fileName) {
            const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp'];
            const ext = fileName.split('.').pop().toLowerCase();
            return imageExtensions.includes(ext);
        }

        function isVideoFile(fileName) {
            const videoExtensions = ['mp4', 'avi', 'webm', 'ogg'];
            const ext = fileName.split('.').pop().toLowerCase();
            return videoExtensions.includes(ext);
        }
    </script>
</head>

<body id="followerPosts">
    <nav id="left">
        <ul>
            <li style="border-top: 1px solid lemonchiffon">
                <a href="/main">Home</a>
            </li>
            <li>
                <a href="/createPost" onclick="return checkLoginCreatePost(this);">Create Post</a>
            </li>
            <li>Find</li>
            <li>My Profile</li>
            <li>Setting</li>
        </ul>
    </nav>
    <header id="title">
        <h1 style="font-size:15px">FollowerPost</h1>
    </header>
    <section id="posts">
        <c:if test="${not empty message}">
            <p>${message}</p>
        </c:if>
        <c:forEach var="post" items="${posts}">
            <table id="post">
                <tr style="height:20px">
                    <td style="width:10%; font-weight:bold; font-size:20px">제목</td>
                    <td>${post.title}</td>
                    <td style="width:10%; font-weight:bold; font-size:20px">작성자</td>
                    <td>${post.writer}</td>
                </tr>
                <tr style="height:150px">
                    <td colspan="4">${post.content}</td>
                </tr>
                <c:if test="${not empty post.file_name}">
                    <tr style="height:200px">
                        <td colspan="4">
                            <script>
                                const fileName = "${post.file_name}";
                                if (isImageFile(fileName)) {
                                    document.write('<img src="${pageContext.request.contextPath}/uploads/' + fileName + '" alt="image" style="max-width:100%; height:auto;"/>');
                                } else if (isVideoFile(fileName)) {
                                    document.write('<video controls style="max-width:100%; height:auto;"><source src="${pageContext.request.contextPath}/uploads/' + fileName + '" type="video/' + fileName.split('.').pop().toLowerCase() + '">Your browser does not support the video tag.</video>');
                                } else {
                                    document.write('<h1>자세히보기를 통해 첨부파일을 확인해주세요!</h1>');
                                }
                            </script>
                        </td>
                    </tr>
                </c:if>
                <tr style="height:20px">
                    <td colspan="4"><button class="btn"><a href="viewPost?id=${post.id}">자세히보기</a></button></td>
                </tr>
            </table>
        </c:forEach>
    </section>
</body>
</html>
