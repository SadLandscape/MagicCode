package com.example.magic_code.api;
import com.example.magic_code.models.Note;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class API {
//    public static class Authentication {
//
//    }

    public static class Notes {
        public static Note getNote(String id) {
            HashMap<String, Object> exampleNote = new HashMap<String, Object>() {{
                put("Title", "Title here");
                put("Author", "Author here");
                put("ID", "40880f57-8655-487a-b31a-fda5123c442c");
                put("ShareToken", "40880f57-8655-487a-b31a-fda5123c442c");
                put("Text", "*bold* _italic_ ```raw text```");
            }};
            return new Note(exampleNote);
        }

        public static boolean setNote(Note newNote) {
            return true;
        }

        public static Settings getSettings(String note_id) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                int finalI = i;
                HashMap<String, String> data = new HashMap<String, String>() {{
                    put("userId", "id_" + finalI);
                    put("email", "email_" + finalI + "@gmail.com");
                    put("username", "username_" + finalI);
                }};
                users.add(new User(data));
            }
            HashMap<String, Object> exampleSettings = new HashMap<String, Object>() {{
                put("Title", "Title here");
                put("Users", users);
            }};
            return new Settings(exampleSettings);
        }
        public static boolean updateSettings(Settings settings){
            return true;
        }
    }
}
