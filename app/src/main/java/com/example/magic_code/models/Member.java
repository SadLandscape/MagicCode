package com.example.magic_code.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

public class Member implements Serializable, Parcelable {
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

    protected Member(Parcel in) {
        id = in.readString();
        userId = in.readString();
        boardId = in.readString();
        byte tmpCanEdit = in.readByte();
        canEdit = tmpCanEdit == 0 ? null : tmpCanEdit == 1;
        byte tmpIsDeletable = in.readByte();
        isDeletable = tmpIsDeletable == 0 ? null : tmpIsDeletable == 1;
        displayName = in.readString();
        email = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userId);
        parcel.writeString(boardId);
        parcel.writeByte((byte) (canEdit == null ? 0 : canEdit ? 1 : 2));
        parcel.writeByte((byte) (isDeletable == null ? 0 : isDeletable ? 1 : 2));
        parcel.writeString(displayName);
        parcel.writeString(email);
    }
}
