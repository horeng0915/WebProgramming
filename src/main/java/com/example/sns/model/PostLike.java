package com.example.sns.model;

public class PostLike {
    private int id;
    private String user_id;
    private int post_id;
    
    public PostLike(int id, String user_id, int post_id) {
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
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

    public int getPost_id() {
        return this.post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

}