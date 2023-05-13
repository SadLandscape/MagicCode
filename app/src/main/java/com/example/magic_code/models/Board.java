package com.example.magic_code.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.List;

public class Board {
    String title;
    String author;
    String id;
    List<Category> categoryList;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public Board(HashMap<String,Object> data){
        title = (String) data.get("title");
        id = (String) data.get("Id");
//        LinkedTreeMap<String,Object> author_ = (LinkedTreeMap) data.get("author");
//        author = (String) author_.get("displayName");
        author = (String) data.get("author");
        categoryList = (List<Category>) data.get("categories");
    }
}
