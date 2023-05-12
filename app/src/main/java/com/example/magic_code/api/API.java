package com.example.magic_code.api;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Note;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class API {
    public static String API_URL =  "https://magiccode-backend.armenkhachatry5.repl.co";
    public static Object[] makeRequest(String endpoint,String method,@Nullable HashMap<String,Object> json,@Nullable String authToken){
        try {
            URL url = new URL(API_URL+endpoint);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod(method.toUpperCase());
            if (method.toUpperCase().equals("POST") || method.toUpperCase().equals("PATCH")) {
                conn.setRequestProperty("Content-Type", "application/json");
            }
            conn.setRequestProperty("Accept", "application/json");
            if (authToken!=null){
                conn.setRequestProperty("authToken",authToken);
            }
            if (method.equalsIgnoreCase("POST")) {
                conn.setDoOutput(true);
            }
            if (method.equalsIgnoreCase("post")) {
                String requestBody = new JSONObject(json).toString();
                OutputStream os = conn.getOutputStream();
                os.write(requestBody.getBytes());
                os.flush();
                os.close();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return new Object[] {true,response.toString()};
            } else if (responseCode == 400) {
                InputStream errorStream = conn.getErrorStream();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                String errorLine;
                StringBuilder response = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    response.append(errorLine);
                }
                Log.d("ERROR", "makeRequest: "+response);
                return new Object[] {false,((HashMap<String,Object>)new Gson().fromJson(response.toString(), new TypeToken<HashMap<String, Object>>(){}.getType())).get("error").toString()};
            }
            conn.disconnect();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException){
                Log.d("ERROR", "makeRequest: "+e);
                return new Object[] {false,"Request timed out, please check your internet and try again!"};
            }
            if (e instanceof ConnectException){
                return new Object[] {false,"Unable to connect, please check your internet and try again!"};
            }
            return new Object[] {false,e.toString()};
        }
        return new Object[] {false,"Internal server error! Please try again later"};
    }


    public static class Authentication {
        public static boolean[] checkUsername(String username){
            Object[] response = makeRequest("/api/usernames/"+username,"GET",null,null);
            boolean status = (boolean) response[0];
            if (!status){
                return new boolean[] {false,false};
            }
            String rbody = (String) response[1];
            Log.d("ERROR", "checkUsername: "+rbody);
            return new boolean[]{true,(boolean)((HashMap<String,Object>)new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {}.getType())).get("available")};
        }
        public static String login(String email, String password, Context ctx) {
            HashMap<String, Object> body = new HashMap<String, Object>() {{
                put("email", email);
                put("password", password);
            }};
            Object[] response = makeRequest("/api/auth/login", "POST", body,null);
            boolean status = (boolean) response[0];
            String resp = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, resp, Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return (String) response_data.get("authToken");
        }
        public static  String register(String username,String email,String password,Context ctx){
            HashMap<String,Object> requestBody = new HashMap<String,Object>(){{
                put("username",username);
                put("email",email);
                put("password",password);
            }};
            Object[] response = makeRequest("/api/auth/register","POST",requestBody,null);
            boolean status = (boolean) response[0];
            if (!status){
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String)response[1], Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return (String) response_data.get("authToken");
        }
        public static boolean[] checkAuth(String authToken,Context ctx){
            Object[] response = makeRequest("/api/auth/checkAuth","GET",null,authToken);
            boolean status = (boolean) response[0];
            String resp = (String) response[1];
            if (!status){
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String)response[1], Toast.LENGTH_SHORT).show();
                    }
                });
                return new boolean[]{false,false};
            }
            HashMap<String, Object> response_data = new Gson().fromJson(resp, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return new boolean[] {true, (boolean) response_data.get("valid")};

        }
        public static List<Note> getNotes(String authToken){
            List<Note> exampleNotes = new ArrayList<>();
            for (int i=0;i<100;i++){
                exampleNotes.add(Notes.getNote("note_"+i));
            }
            return exampleNotes;
        }
        public static AuthenticatedUser getUser(String authToken){
            HashMap<String,Object> exampleUser = new HashMap<>();
            exampleUser.put("Username","Example Username");
            exampleUser.put("Email","example@gmail.com");
            exampleUser.put("Notes",getNotes(authToken));
            exampleUser.put("displayName","display name");
            return new AuthenticatedUser(exampleUser);
        }
    }

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
                    put("displayName","display name");
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
