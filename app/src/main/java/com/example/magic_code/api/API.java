package com.example.magic_code.api;
import com.example.magic_code.models.Note;

import java.util.HashMap;

public class API {
//    public static class Authentication {
//
//    }

    public static class Notes{
        public static Note getNote(String id){
            HashMap<String,Object> exampleNote = new HashMap<String, Object>(){{
                put("Title","Title here");
                put("Author","Author here");
                put("ID","40880f57-8655-487a-b31a-fda5123c442c");
                put("ShareToken","40880f57-8655-487a-b31a-fda5123c442c");
                put("Text","*bold* _italic_ ```raw text```");
            }};
            return new Note(exampleNote);
        }
        public static boolean setNote(Note newNote){
            return true;
        }
    }
}
