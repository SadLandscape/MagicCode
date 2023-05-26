package com.example.magic_code.models;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Board implements Serializable {
    String title;
    String author;
    String id;
    Boolean canEdit;
    Boolean canDelete;
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

    public Boolean canDelete() {
        return canDelete;
    }
    public Boolean canEdit(){
        return canEdit;
    }

    public Board(HashMap<String,Object> data){
        title = (String) data.get("title");
        id = (String) data.get("Id");
        LinkedTreeMap<String,Object> author_ = (LinkedTreeMap) data.get("author");
        author = (String) author_.get("displayName");
        categoryList = new ArrayList<>();
        List<LinkedTreeMap> categoryList_ = (List<LinkedTreeMap>) data.get("categories");
        for (LinkedTreeMap category_ : categoryList_){
            categoryList.add(new Category(new HashMap<>(category_)));
        }
        canDelete = (Boolean) data.get("canDelete");
        canEdit = (Boolean) data.get("canEdit");
    }
}
