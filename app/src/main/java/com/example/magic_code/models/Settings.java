package com.example.magic_code.models;

import java.util.HashMap;
import java.util.List;

public class Settings {
    List<User> users;
    String title;
    public Settings(HashMap<String,Object> data){
        users = (List<User>) data.get("Users");
        title = (String) data.get("Title");
    }

    public List<User> getUsers(){
        return users;
    }

    public String getTitle(){
        return title;
    }
    public void removeUser(User user){
        users.remove(user);
    }
}
