package com.example.magic_code.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.HashMap;

public class ShareToken implements Serializable, Parcelable {
    private Integer uses;
    private String Id;
    private Boolean canWrite;
    private String author;
    private Board board;

    protected ShareToken(Parcel in) {
        if (in.readByte() == 0) {
            uses = null;
        } else {
            uses = in.readInt();
        }
        Id = in.readString();
        byte tmpCanWrite = in.readByte();
        canWrite = tmpCanWrite == 0 ? null : tmpCanWrite == 1;
        author = in.readString();
    }

    public static final Creator<ShareToken> CREATOR = new Creator<ShareToken>() {
        @Override
        public ShareToken createFromParcel(Parcel in) {
            return new ShareToken(in);
        }

        @Override
        public ShareToken[] newArray(int size) {
            return new ShareToken[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (uses == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(uses);
        }
        parcel.writeString(Id);
        parcel.writeByte((byte) (canWrite == null ? 0 : canWrite ? 1 : 2));
        parcel.writeString(author);
    }
}
