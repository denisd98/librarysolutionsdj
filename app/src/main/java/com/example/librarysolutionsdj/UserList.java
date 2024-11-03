package com.example.librarysolutionsdj;

public class UserList {
    private String id;
    private String username;

    public UserList(String id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
