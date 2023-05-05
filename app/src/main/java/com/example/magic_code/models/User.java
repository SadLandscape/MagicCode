package com.example.magic_code.models;

import java.util.HashMap;

public class User {
    String userId;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    String username;
    String email;
    public User(HashMap<String,String> data){
        userId = data.get("userId");
        username = data.get("username");
        email = data.get("email");
    }

}
