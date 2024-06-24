package com.example.sns.model;

public class Comment {
    private int id;
    private String comment;
    private String user_id;
    private int post_id;
    private boolean liked;

    public Comment(int id, String comment, String user_id, int post_id) {
        this.id = id;
        this.comment = comment;
        this.user_id = user_id;
        this.post_id = post_id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getPost_id() {
        return this.post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

}
