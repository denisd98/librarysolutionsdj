package com.example.librarysolutionsdj.Users;

public class MockServer {
    public static String simulateModifyUserRequest() {
        return "MODIFY_USER_OK";
    }

    public static String simulateCreateUserRequest() {
        return "USER_CREATED";
    }

    public static String simulateModifyUserError() {
        return "MODIFY_USER_ERROR";
    }

    public static String simulateModifyAuthorRequest() {
        return "MODIFY_AUTHOR_OK";
    }

    public static String simulateCreateAuthorRequest() {
        return "AUTHOR_CREATED";
    }
}
