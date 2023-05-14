package com.example.magic_code.models;

import java.util.HashMap;
import java.util.List;

public class Category {
    String title;
    String author;
    List<Note> noteList;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<Note> getNoteList() {
        return noteList;
    }
    public Category(HashMap<String,Object> data){
        title = (String) data.get("title");
        author = (String) data.get("author");
        noteList = (List<Note>) data.get("notes");
    }
}
