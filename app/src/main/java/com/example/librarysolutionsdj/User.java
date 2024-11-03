package com.example.librarysolutionsdj;

public class User {
    private String id;
    private String username;
    private String realname;
    private String surname1;
    private String surname2;
    private String userType;

    public User(String id, String username, String realname, String surname1, String surname2, String userType) {
        this.id = id;
        this.username = username;
        this.realname = realname;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.userType = userType;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }

    public String getSurname1() {
        return surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public String getUserType() {
        return userType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
