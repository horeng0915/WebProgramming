package com.example.sns.model;

public class Post {
    private int id;
    private String title;
    private String content;
    private String file_name; // 파일 이름 필드 추가
    private String writer;
    private boolean following;
    private boolean liked;

    public Post(int id, String title, String content, String file_name, String writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.file_name = file_name;
        this.writer = writer;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFile_name() {
        return this.file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getWriter() {
        return this.writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
