package com.example.magic_code.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

public class ShareToken {
    private Integer uses;
    private String Id;
    private Boolean canWrite;
    private String author;
    private Board board;

    public Board getBoard() {
        return board;
    }

    public ShareToken(HashMap<String,Object> data){
        uses = ((Double) data.get("uses")).intValue();
        Id = (String) data.get("Id");
        canWrite = (Boolean) data.get("can_write");
        LinkedTreeMap<String,Object> author_ = (LinkedTreeMap) data.get("author");
        author = (String) author_.get("displayName");
        board = new Board(new HashMap<>((LinkedTreeMap)data.get("board")));

    }

    public Integer getUses() {
        return uses;
    }

    public String getId() {
        return Id;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public String getAuthor() {
        return author;
    }
}
