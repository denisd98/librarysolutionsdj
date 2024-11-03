package com.example.librarysolutionsdj;

public class User {
    private String id;
    private String username;
    private String realname;

    public User(String id, String username, String realname) {
        this.id = id;
        this.username = username;
        this.realname = realname;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }
}
