<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SNS</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
    <script>
        function showAlert(message) {
            alert(message);
        }

        function checkLoginCreatePost() {
            const userId = '${sessionScope.id}';
            if (!userId) {
                showAlert("로그인이 필요합니다.");
                return false;
            } 
            return true;
        }

        function checkLoginFollowerPosts() {
            const userId = '${sessionScope.id}';
            if (!userId) {
                showAlert("로그인이 필요합니다.");
                return false;
            } 
            return true;
        }

        function checkLoginAndRedirect(writerId) {
            const userId = '${sessionScope.id}';
            if (!userId) {
                showAlert("로그인이 필요합니다.");
                return false;
            } else if (userId !== writerId) {
                showAlert("권한이 없습니다.");
                return false;
            }
           
        }

        function isImageFile(fileName) {
            const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp'];
            const ext = fileName.split('.').pop().toLowerCase();
            return imageExtensions.includes(ext);
        }

        function isVideoFile(fileName) {
            const videoExtensions = ['mp4', 'avi', 'webm', 'ogg', 'mov'];
            const ext = fileName.split('.').pop().toLowerCase();
            return videoExtensions.includes(ext);
        }
    </script>
</head>
<body id="main">
    <header id="title">
        <h1>SNS</h1>
    </header>
    <nav id="left">
        <ul>
            <li style="border-top: 1px solid lemonchiffon">
                <a href="/main">Home</a>
            </li>
            <li>
                <a href="/createPost" onclick="return checkLoginCreatePost(this);">Create Post</a>
            </li>
            <c:if test="${not empty sessionScope.id}">
                <li><a href="/followerPosts" onclick="return checkLoginFollowerPosts(this);">Follower Post</a></li>
            </c:if>
            <li>Find</li>
            <li>My Profile</li>
            <li>Setting</li>
        </ul>
    </nav>
    <section id="posts">
        <c:forEach var="post" items="${posts}">
            <table id="post">
                <tr style="height:20px">
                    <td style="width:10%; font-weight:bold; font-size:20px">제목</td>
                    <td>${post.title}</td>
                    <td style="width:10%; font-weight:bold; font-size:20px">작성자</td>
                    <td>${post.writer}</td>
                    <td style="width:10%">
                        <c:choose>
                            <c:when test="${post.writer != sessionScope.id}">
                                <c:choose>
                                    <c:when test="${post.following}">
                                        <form action="${pageContext.request.contextPath}/unfollow" method="post" style="display:inline;" onsubmit="return checkLoginCreatePost(this);">
                                            <input type="hidden" name="star" value="${post.writer}">
                                            <button type="submit" class="btn">언팔</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/follow" method="post" style="display:inline;" onsubmit="return checkLoginCreatePost(this);">
                                            <input type="hidden" name="star" value="${post.writer}">
                                            <button type="submit" class="btn">팔로</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>
                <tr style="height:150px">
                    <td colspan="5"><c:out value="${post.content}" escapeXml="false"/></td>
                </tr>
                <c:if test="${not empty post.file_name}">
                    <tr style="height:200px">
                        <td colspan="5">
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
                    <td><button class="btn"><a href="editPost?id=${post.id}" onclick="return checkLoginAndRedirect('${post.writer}');">수정</a></button></td>
                    <td colspan="2"><button class="btn"><a href="viewPost?id=${post.id}">자세히보기</a></button></td>
                    <td colspan="2"><button class="btn"><a href="deletePost?id=${post.id}" onclick="return checkLoginAndRedirect('${post.writer}');">삭제</a></button></td>
                </tr>
            </table>
        </c:forEach>
    </section>
    <aside id="right">
        <div id="login" class="login-box">
            <c:choose>
                <c:when test="${not empty id}">
                    <h4>어서오세요! ${id} 님</h4>
                    <h4>오늘도 좋은 하루 되세요!</h4>
                    <a href="/logout" class="btn">로그아웃</a>
                </c:when>
                <c:otherwise>
                    <h4>로그인</h4>
                    <form action="/login" method="post">
                        아이디<br>
                        <input type="text" name="id" /><br>
                        비밀번호<br>
                        <input type="password" name="passwd" /><br>
                        <input type="submit" value="로그인" class="btn" />
                        <button id="register_btn" class="btn"><a href="/register" target="_blank" id="register">회원가입</a></button>
                    </form>
                    <c:if test="${not empty loginError}">
                        <div class="error">${loginError}</div>
                        <% session.removeAttribute("loginError"); %>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </aside>
    <footer>
        <p>2024학년도 1학기 웹프로그래밍 과제입니다.</p>
    </footer>
    <c:if test="${not empty message}">
        <script>
            showAlert("${message}");
        </script>
    </c:if>
</body>
</html>
