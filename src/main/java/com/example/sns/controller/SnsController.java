package com.example.sns.controller;

import com.example.sns.model.Comment;
import com.example.sns.model.Post;
import com.example.sns.model.User;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.net.URLEncoder;

@Controller
public class SnsController {
    private final DataSource dataSource;
    private final String URL = "jdbc:mysql://localhost:3306/snsdb";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    @Autowired
    private ServletContext servletContext;

    public SnsController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/main")
    public String main(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("id");
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT p.*, (SELECT COUNT(*) FROM follower f WHERE f.fan = ? AND f.star = p.writer) AS following FROM post p")) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(rs.getInt("id"), rs.getString("title"), rs.getString("content"), rs.getString("file_name"), rs.getString("writer"));
                    post.setFollowing(rs.getInt("following") > 0);
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("posts", posts);
        return "main";
    }

    @PostMapping("/login")
    public String login(@RequestParam("id") String id, @RequestParam("passwd") String passwd, Model model,
            HttpServletRequest request) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ? AND passwd = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, id);
                statement.setString(2, passwd);
                try (ResultSet resultSet = statement.executeQuery()) {
                    HttpSession session = request.getSession();
                    if (resultSet.next()) {
                        session.setAttribute("id", id);
                    } else {
                        session.setAttribute("loginError", "아이디 또는 비밀번호가 잘못되었습니다.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/main";
    }

    @GetMapping("/checkDuplicate")
    public String showDuplicateCheckPage() {
        return "check_duplicate";
    }

    @PostMapping("/checkDuplicate")
    public String checkDuplicate(@RequestParam("id") String id, Model model) {
        boolean isDuplicate = false;

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            isDuplicate = true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isDuplicate) {
            model.addAttribute("message", "이미 사용 중인 아이디입니다.");
        } else {
            model.addAttribute("message", "사용 가능한 아이디입니다.");
        }

        return "check_duplicate";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(User user) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "INSERT INTO users (id, passwd, email, age, name, gender) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.getId());
                preparedStatement.setString(2, user.getPasswd());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setInt(4, user.getAge());
                preparedStatement.setString(5, user.getName());
                preparedStatement.setString(6, user.getGender());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "register_success";
    }

    @GetMapping("/createPost")
    public String createPost() {
        return "createPost";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("title") String title, 
                             @RequestParam("content") String content, 
                             @RequestParam("file") MultipartFile file, 
                             HttpSession session, Model model) {
        String file_name = "";
        if (!file.isEmpty()) {
            file_name = file.getOriginalFilename();
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        String writer = (String) session.getAttribute("id");

        try {conn = DataSourceUtils.getConnection(dataSource);
            pstmt = conn.prepareStatement("INSERT INTO post (title, content, file_name, writer) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, file_name);
            pstmt.setString(4, writer);
            pstmt.executeUpdate();

            if (!file.isEmpty()) {
                String uploadDirPath = servletContext.getRealPath("/uploads");
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                file.transferTo(new File(uploadDir, file_name));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 업로드 중 오류가 발생했습니다.");
            return "createPost";
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return "redirect:/main";
    }
    
    @GetMapping("/editPost")
    public String editPost(@RequestParam("id") int id, Model model) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post(rs.getInt("id"), rs.getString("title"), rs.getString("content"),
                            rs.getString("file_name"), rs.getString("writer"));
                    model.addAttribute("post", post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "editPost";
    }

    @PostMapping("/updatePost")
    public String updatePost(@RequestParam("id") int id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("newFile") MultipartFile newFile,
                             HttpSession session) {
        String file_name = null;
        if (!newFile.isEmpty()) {
            file_name = newFile.getOriginalFilename();
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String oldFileName = null;

            if (file_name != null) {
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT file_name FROM post WHERE id = ?")) {
                    pstmt.setInt(1, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            oldFileName = rs.getString("file_name");
                        }
                    }
                }

                if (oldFileName != null && !oldFileName.isEmpty()) {
                    File oldFile = new File(servletContext.getRealPath("/uploads"), oldFileName);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                newFile.transferTo(new File(servletContext.getRealPath("/uploads"), file_name));
            } else {
                try (PreparedStatement pstmt = conn.prepareStatement("SELECT file_name FROM post WHERE id = ?")) {
                    pstmt.setInt(1, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            file_name = rs.getString("file_name");
                        }
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE post SET title = ?, content = ?, file_name = ? WHERE id = ?")) {
                pstmt.setString(1, title);
                pstmt.setString(2, content);
                pstmt.setString(3, file_name);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @GetMapping("/deletePost")
    public String deletePost(@RequestParam("id") int id) {
        String fileName = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 파일명 조회
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT file_name FROM post WHERE id = ?")) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        fileName = rs.getString("file_name");
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM post WHERE id = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            if (fileName != null && !fileName.isEmpty()) {
                File file = new File("uploads", fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("post_id") int post_id, 
                             @RequestParam("user_id") String user_id, 
                             @RequestParam("comment") String comment, 
                             HttpSession session, 
                             Model model) {
        if (user_id == null || user_id.isEmpty()) {
            return "redirect:/viewPost?id=" + post_id;
        }
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO comment (comment, user_id, post_id) VALUES (?, ?, ?)")) {
            pstmt.setString(1, comment);
            pstmt.setString(2, user_id);
            pstmt.setInt(3, post_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/viewPost?id=" + post_id;
    }

    @GetMapping("/deleteComment")
    public String deleteComment(@RequestParam("id") int id, @RequestParam("post_id") int post_id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM comment WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/viewPost?id=" + post_id;
    }

    @GetMapping("/viewPost")
    public String viewPost(@RequestParam("id") int post_id, Model model, HttpSession session) {
        String user_id = (String) session.getAttribute("id");

        Post post = null;
        List<Comment> comments = new ArrayList<>();
        boolean liked = false;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM post WHERE id = ?")) {
                pstmt.setInt(1, post_id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        post = new Post(rs.getInt("id"), rs.getString("title"), rs.getString("content"),
                                rs.getString("file_name"), rs.getString("writer"));
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM post_likes WHERE user_id = ? AND post_id = ?")) {
                pstmt.setString(1, user_id);
                pstmt.setInt(2, post_id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        liked = true;
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM comment WHERE post_id = ?")) {
                pstmt.setInt(1, post_id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Comment comment = new Comment(rs.getInt("id"), rs.getString("comment"), rs.getString("user_id"),
                                rs.getInt("post_id"));
                        try (PreparedStatement pstmt2 = conn.prepareStatement(
                                "SELECT COUNT(*) FROM comment_likes WHERE user_id = ? AND comment_id = ?")) {
                            pstmt2.setString(1, user_id);
                            pstmt2.setInt(2, comment.getId());
                            try (ResultSet rs2 = pstmt2.executeQuery()) {
                                if (rs2.next() && rs2.getInt(1) > 0) {
                                    comment.setLiked(true);
                                }
                            }
                        }
                        comments.add(comment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (post != null) {
            post.setLiked(liked);
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "viewPost";
    }
    
    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam("id") int id, HttpServletResponse response) {
        String fileName = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT file_name FROM post WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fileName = rs.getString("file_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        if (fileName != null && !fileName.isEmpty()) {
            String uploadDirPath = servletContext.getRealPath("/uploads");
            File file = new File(uploadDirPath, fileName);
            if (file.exists()) {
                response.setContentType("application/octet-stream");
                try {
                    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (InputStream inStream = new FileInputStream(file);
                     OutputStream outStream = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "서버에서 요청한 파일을 찾을 수 없습니다.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 파일 요청입니다.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @PostMapping("/follow")
    public String follow(@RequestParam("star") String star, HttpSession session) {
        String fan = (String) session.getAttribute("id");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO follower (fan, star) VALUES (?, ?)")) {
            pstmt.setString(1, fan);
            pstmt.setString(2, star);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @PostMapping("/unfollow")
    public String unfollow(@RequestParam("star") String star, HttpSession session) {
        String fan = (String) session.getAttribute("id");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM follower WHERE fan = ? AND star = ?")) {
            pstmt.setString(1, fan);
            pstmt.setString(2, star);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @GetMapping("/followerPosts")
    public String followerPosts(Model model, HttpSession session) {
        String user_id = (String) session.getAttribute("id");
    
        if (user_id == null) {
            return "redirect:/main";
        }
    
        List<Post> posts = new ArrayList<>();
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            List<String> followerIds = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT star FROM follower WHERE fan = ?")) {
                pstmt.setString(1, user_id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        followerIds.add(rs.getString("star"));
                    }
                }
            }
    
            if (followerIds.isEmpty()) {
                model.addAttribute("message", "팔로워가 없습니다.");
                model.addAttribute("posts", posts);
                return "followerPosts";
            }
    
            StringBuilder query = new StringBuilder("SELECT * FROM post WHERE writer IN (");
            for (int i = 0; i < followerIds.size(); i++) {
                query.append("?");
                if (i < followerIds.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")");
    
            try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
                for (int i = 0; i < followerIds.size(); i++) {
                    pstmt.setString(i + 1, followerIds.get(i));
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Post post = new Post(rs.getInt("id"), rs.getString("title"), rs.getString("content"), rs.getString("file_name"), rs.getString("writer"));
                        posts.add(post);
                    }
                }
            }
    
            if (posts.isEmpty()) {
                model.addAttribute("message", "팔로워가 작성한 글이 없습니다.");
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        model.addAttribute("posts", posts);
        return "followerPosts";
    }
    

    @PostMapping("/likePost")
    public String likePost(@RequestParam("post_id") int post_id, HttpSession session) {
        String user_id = (String) session.getAttribute("id");
    
        if (user_id == null) {
            return "redirect:/viewPost?id=" + post_id;
        }
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO post_likes (user_id, post_id) VALUES (?, ?)")) {
            pstmt.setString(1, user_id);
            pstmt.setInt(2, post_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/viewPost?id=" + post_id;
    }
    
    @PostMapping("/unlikePost")
    public String unlikePost(@RequestParam("post_id") int post_id, HttpSession session) {
        String user_id = (String) session.getAttribute("id");
    
        if (user_id == null) {
            return "redirect:/viewPost?id=" + post_id;
        }
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM post_likes WHERE user_id = ? AND post_id = ?")) {
            pstmt.setString(1, user_id);
            pstmt.setInt(2, post_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/viewPost?id=" + post_id;
    }
    
    @PostMapping("/likeComment")
    public String likeComment(@RequestParam("comment_id") int comment_id, HttpSession session) {
        String user_id = (String) session.getAttribute("id");
    
        if (user_id == null) {
            return "redirect:/viewPost?id=" + getPostIdByCommentId(comment_id);
        }
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO comment_likes (user_id, comment_id) VALUES (?, ?)")) {
            pstmt.setString(1, user_id);
            pstmt.setInt(2, comment_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/viewPost?id=" + getPostIdByCommentId(comment_id);
    }
    
    @PostMapping("/unlikeComment")
    public String unlikeComment(@RequestParam("comment_id") int comment_id, HttpSession session) {
        String user_id = (String) session.getAttribute("id");
    
        if (user_id == null) {
            return "redirect:/viewPost?id=" + getPostIdByCommentId(comment_id);
        }
    
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM comment_likes WHERE user_id = ? AND comment_id = ?")) {
            pstmt.setString(1, user_id);
            pstmt.setInt(2, comment_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/viewPost?id=" + getPostIdByCommentId(comment_id);
    }
    
    private int getPostIdByCommentId(int comment_id) {
        int post_id = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT post_id FROM comment WHERE id = ?")) {
            pstmt.setInt(1, comment_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    post_id = rs.getInt("post_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post_id;
    }
}