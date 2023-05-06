package com.example.magic_code.classes;

import com.example.magic_code.models.Note;

import java.util.HashMap;
import java.util.List;

public class AuthenticatedUser {
    String username;
    String email;
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
        username = (String) data.get("Username");
        email = (String) data.get("Email");
        notes = (List<Note>) data.get("Notes");
    }
}
