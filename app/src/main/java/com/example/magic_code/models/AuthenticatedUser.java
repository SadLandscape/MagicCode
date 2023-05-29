package com.example.magic_code.models;

import com.example.magic_code.models.Note;

import java.util.HashMap;
import java.util.List;

public class AuthenticatedUser {
    String username;
    String email;

    public String getDisplayName() {
        return displayName;
    }

    String displayName;
    List<Note> notes;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public AuthenticatedUser(HashMap<String,Object> data){
        username = (String) data.get("username");
        email = (String) data.get("email");
        displayName = (String) data.get("displayName");
    }
}
