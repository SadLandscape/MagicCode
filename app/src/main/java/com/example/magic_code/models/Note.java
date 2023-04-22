package com.example.magic_code.models;

import java.util.HashMap;

public class Note {
    String title;
    String text;
    String author;
    String id;
    String shareToken;
    public Note(HashMap<String,Object> data){
        title = (String) data.get("Title");
        text = (String) data.get("Text");
        author = (String) data.get("Author");
        id = (String) data.get("ID");
        shareToken = (String) data.get("ShareToken");
    }
    public String getText(){
        return text;
    }
    public String getId(){
        return id;
    }
    public String getShareToken(){
        return shareToken;
    }
}
