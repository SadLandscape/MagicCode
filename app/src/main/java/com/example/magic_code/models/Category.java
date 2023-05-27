package com.example.magic_code.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Category implements Parcelable,Serializable{
    String title;
    String id;
    String author;

    protected Category(Parcel in) {
        title = in.readString();
        id = in.readString();
        author = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getId() {
        return id;
    }

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
        id = (String) data.get("Id");
        title = (String) data.get("title");
        LinkedTreeMap<String,Object> author_ = (LinkedTreeMap) data.get("author");
        author = (String) author_.get("displayName");
        noteList = new ArrayList<Note>();
        for (LinkedTreeMap item: (List<LinkedTreeMap>) data.get("notes")){
            noteList.add(new Note(new HashMap<>(item)));
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(id);
        parcel.writeString(author);
    }
}
