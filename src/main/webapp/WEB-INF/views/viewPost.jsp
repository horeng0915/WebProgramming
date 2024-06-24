<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${post.title}</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/mystyle.css">
    <script>
        function showAlert(message) {
            alert(message);
        }

        function checkLike() {
            const userId = '${sessionScope.id}';
            if (!userId) {
                showAlert("로그인이 필요합니다.");
                return false;
            }
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
            return true;
        }

        function checkLoginAndRedirect_comm(commentId) {
            const userId = '${sessionScope.id}';
            if (!userId) {
                showAlert("로그인이 필요합니다.");
                return false;
            } else if (userId !== commentId) {
                showAlert("권한이 없습니다.");
                return false;
            }
            return true;
        }

        function isImageFile(fileName) {
            const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp'];
            const ext = fileName.split('.').pop().toLowerCase();
            return imageExtensions.includes(ext);
        }

        function isVideoFile(fileName) {
            const videoExtensions = ['mp4', 'webm', 'ogg'];
            const ext = fileName.split('.').pop().toLowerCase();
            return videoExtensions.includes(ext);
        }
    </script>
</head>
<body id="view">
    <div class="post-container">
        <h1><c:out value="${post.title}"/></h1><br>
        <h4>작성자</h4><br>
        <p><c:out value="${post.writer}"/></p><br>
        <p style="width:auto; height:300px; background-color:antiquewhite"><c:out value="${post.content}" escapeXml="false"/></p><br>
        <c:if test="${not empty post.file_name}">
            <script>
                const fileName = "${post.file_name}";
                if (isImageFile(fileName)) {
                    document.write('<img src="${pageContext.request.contextPath}/uploads/' + fileName + '" alt="image" style="max-width:100%; height:auto;"/>');
                } else if (isVideoFile(fileName)) {
                    document.write('<video controls style="max-width:100%; height:auto;"><source src="${pageContext.request.contextPath}/uploads/' + fileName + '" type="video/' + fileName.split('.').pop().toLowerCase() + '">Your browser does not support the video tag.</video>');
                } else {
                    document.write('<p>첨부파일: <a href="${pageContext.request.contextPath}/downloadFile?id=${post.id}">${post.file_name}</a><p>');
                }
            </script>
        </c:if>
        <div class="btn-container">
            <form action="editPost" method="get" onsubmit="return checkLoginAndRedirect('${post.writer}');">
                <input type="hidden" name="id" value="${post.id}">
                <button type="submit" class="btn">수정</button>
            </form>
            <form action="deletePost" method="get" onsubmit="return checkLoginAndRedirect('${post.writer}');">
                <input type="hidden" name="id" value="${post.id}">
                <button type="submit" class="btn">삭제</button>
            </form>
            <button class="btn"><a href="/main">홈으로</a></button>
        </div>
        <c:choose>
            <c:when test="${post.liked}">
                <form action="${pageContext.request.contextPath}/unlikePost" method="post" style="display:inline;" onsubmit="return checkLike(this);">
                    <input type="hidden" name="post_id" value="${post.id}">
                    <button type="submit" class="btn">♥</button>
                </form>
            </c:when>
            <c:otherwise>
                <form action="${pageContext.request.contextPath}/likePost" method="post" style="display:inline;" onsubmit="return checkLike(this);">
                    <input type="hidden" name="post_id" value="${post.id}">
                    <button type="submit" class="btn">♡</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="comment-form-container">
        <h3>댓글 작성</h3>
        <form action="${pageContext.request.contextPath}/addComment" method="post" onsubmit="return checkLike(this);">
            <input type="hidden" name="post_id" value="${post.id}">
            <input type="hidden" name="user_id" value="${sessionScope.id}">
            <textarea name="comment" id="comment" required></textarea><br>
            <button type="submit" class="btn">댓글 작성</button>
        </form>
    </div>

    <div class="comments-container">
        <h3>댓글</h3>
        <hr>
        <c:forEach var="comment" items="${comments}">
            <div class="comment-item">
                <p><strong>${comment.user_id}:</strong> ${comment.comment}</p>
                <form action="${pageContext.request.contextPath}/deleteComment" method="get" onsubmit="return checkLoginAndRedirect_comm('${comment.user_id}');">
                    <input type="hidden" name="id" value="${comment.id}">
                    <input type="hidden" name="post_id" value="${post.id}">
                    <button type="submit" class="btn">댓글 삭제</button>
                </form>
                <c:choose>
                    <c:when test="${comment.liked}">
                        <form action="${pageContext.request.contextPath}/unlikeComment" method="post" style="display:inline;" onsubmit="return checkLike(this);">
                            <input type="hidden" name="comment_id" value="${comment.id}">
                            <button type="submit" class="btn">♥</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/likeComment" method="post" style="display:inline;" onsubmit="return checkLike(this);">
                            <input type="hidden" name="comment_id" value="${comment.id}">
                            <button type="submit" class="btn">♡</button>
                        </form>
                    </c:otherwise>
                </c:choose>
                <hr>
            </div>
        </c:forEach>
    </div>
</body>
</html>
