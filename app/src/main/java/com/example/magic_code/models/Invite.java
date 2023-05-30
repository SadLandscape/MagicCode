package com.example.magic_code.models;

import java.util.HashMap;

public class Invite {
    String id;
    String invUsername;
    String invDisplayName;
    String boardName;

    public String getId() {
        return id;
    }

    public String getInvUsername() {
        return invUsername;
    }

    public String getInvDisplayName() {
        return invDisplayName;
    }

    public String getBoardName() {
        return boardName;
    }

    public Invite(HashMap<String,String> data){
        id = data.get("id");
        invUsername = data.get("inviter_username");
        invDisplayName = data.get("inviter_display_name");
        boardName = data.get("board_name");
    }
}
