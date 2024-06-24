package com.example.sns.model;

public class CommentLike {
    private int id;
    private String user_id;
    private int comment_id;

    public CommentLike(int id, String user_id, int comment_id) {
        this.id = id;
        this.user_id = user_id;
        this.comment_id = comment_id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getComment_id() {
        return this.comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    
}
