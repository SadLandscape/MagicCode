package com.example.magic_code.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

public class Modification {
    private String id;

    public String getId() {
        return id;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    private String noteId;
    private String changeMessage;
    private String displayName;
    private Long created_at;
    private String username;

    public Long getCreatedAt() {
        return created_at;
    }

    public Modification(HashMap<String,Object> data){
        id = (String)data.get("id");
        noteId = (String) data.get("note_id");
        changeMessage = (String) data.get("message");
        LinkedTreeMap<String,Object> author_ = (LinkedTreeMap) data.get("author");
        displayName = (String) author_.get("displayName");
        username = (String) author_.get("username");
        created_at = ((Number)data.get("created_at")).longValue();
    }
}
