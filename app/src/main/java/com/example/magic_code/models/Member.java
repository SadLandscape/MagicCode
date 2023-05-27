package com.example.magic_code.models;

import java.util.HashMap;

public class Member {
    String id;
    String userId;
    String boardId;
    Boolean canEdit;
    Boolean isDeletable;
    String displayName;
    String email;
    public Member(HashMap<String,Object> data,String selfid){
        id = (String) data.get("Id");
        userId = (String) data.get("userId");
        boardId = (String) data.get("boardId");
        canEdit = (Boolean) data.get("canEdit");
        displayName = (String) data.get("displayName");
        isDeletable = !selfid.equals(userId);
        email = (String) data.get("email");
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public Boolean getDeletable() {
        return isDeletable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getBoardId() {
        return boardId;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }
}
