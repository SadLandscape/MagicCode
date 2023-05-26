package com.example.magic_code.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

public class Note {
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    String title;
    String text;
    String author;
    String id;
    String shareToken;
    Category category;
    public Note(HashMap<String,Object> data){
        title = (String) data.get("title");
        text = (String) data.get("text");
        LinkedTreeMap<String,Object> author_ = (LinkedTreeMap) data.get("author");
        author = (String) author_.get("displayName");
        category = new Category(new HashMap<>((LinkedTreeMap)data.get("category")));
        id = (String) data.get("Id");
        shareToken = (String) data.get("shareToken");
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
    public Category getCategory(){ return category;}
}
