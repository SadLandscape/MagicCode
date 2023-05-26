package com.example.magic_code.api;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Note;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.ShareToken;
import com.example.magic_code.models.User;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
            if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("patch")) {
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
            } else if (responseCode>=400) {
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
                return new Object[] {false,"Request timed out, please check your internet and try again!"};
            }
            if (e instanceof ConnectException){
                return new Object[] {false,"Unable to connect, please check your internet and try again!"};
            }
            return new Object[] {false,e.toString()};
        }
        return new Object[] {false,"Internal server error! Please try again later"};
    }
    public static class Categories{
        public static List<Note> getNotes(){
            List<Note> noteList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                HashMap<String,Object> noteData = new HashMap<String,Object>(){{
                    put("title","Title "+ finalI);
                    put("text","Text "+finalI);
                    put("Id","id_"+finalI);
                    put("shareToken","shareToken_"+finalI);
                    put("Author","author "+finalI);
                }};
                noteList.add(new Note(noteData));
            }
            return noteList;
        }

        public static boolean deleteCategory(String categoryId,String authToken,Context ctx){
            Object[] response = makeRequest("/api/categories/" + categoryId, "DELETE", null, authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            Log.i("ERROR",rbody);
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        public static boolean createCategory(String boardId,String title, String authToken, Context ctx) {
            HashMap<String, Object> payload = new HashMap<String, Object>() {{
                put("title", title);
            }};
            Object[] response = makeRequest("/api/boards/" + boardId + "/categories/create", "POST", payload, authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        public static ArrayList<Category> getCategories(String boardId,String authToken,Context ctx){
            Object[] response = makeRequest("/api/boards/"+boardId+"/categories","GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return new ArrayList<>();
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ArrayList<Category> categoryList = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> categories = (List<LinkedTreeMap<String, Object>>) response_data.get("categories");
            for (LinkedTreeMap<String,Object> category: categories) {
                HashMap<String,Object> category_ = new HashMap<>(category);
                categoryList.add(new Category(category_));
            }
            return categoryList;
        }
    }

    public static class Boards{
        public static List<Board> getBoards(String authToken,Context ctx){
            Object[] response = makeRequest("/api/boards/getBoards","GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return new ArrayList<>();
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            List<Board> boardList = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> boards = (List<LinkedTreeMap<String, Object>>) response_data.get("boards");
            for (LinkedTreeMap<String,Object> board: boards) {
                HashMap<String,Object> board_ = new HashMap<>(board);
                boardList.add(new Board(board_));
            }
            return boardList;
        }
        public static Board getBoard(String board_id,String authToken, Context ctx){
            Object[] response = makeRequest("/api/boards/"+board_id,"GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(()-> {
                        Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
                });
                return null;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {}.getType());
            return new Board(response_data);
        }
        public static ShareToken generateToken(String board_id,String authToken,Boolean readOnly,Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("boardId",board_id);
                put("can_write",!readOnly);
            }};
            Object[] response = makeRequest("/api/tokens/generate","POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {}.getType());
            return new ShareToken(response_data);
        }

        public static List<ShareToken> getTokens(String boardId, String authToken, Context ctx){
            Object[] response = makeRequest("/api/boards/"+boardId+"/tokens","GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return new ArrayList<>();
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            List<ShareToken> tokenList = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> tokens = (List<LinkedTreeMap<String, Object>>) response_data.get("tokens");
            for (LinkedTreeMap<String,Object> token: tokens) {
                HashMap<String,Object> token_ = new HashMap<>(token);
                tokenList.add(new ShareToken(token_));
            }
            return tokenList;
        }

        public static boolean joinBoard(String shareId,String authToken,Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("shareToken",shareId);
            }};
            Object[] response = makeRequest("/api/boards/join","POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        public static boolean createBoard(String title, String authToken, Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("title",title);
            }};
            Object[] response = makeRequest("/api/boards/create","POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        public static boolean deleteBoard(String board_id,String authToken,Context ctx){
            Object[] response = makeRequest("/api/boards/"+board_id,"DELETE",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        public static boolean leaveBoard(String board_id, String authToken, Context ctx) {
            Object[] response = makeRequest("/api/boards/"+board_id+"/leave","POST",new HashMap<>(),authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
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
        public static String register(String username,String email,String password,Context ctx){
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
                return new boolean[]{false,false};
            }
            HashMap<String, Object> response_data = new Gson().fromJson(resp, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            return new boolean[] {true, (boolean) response_data.get("valid")};

        }
//        public static List<Note> getNotes(String authToken,Context ctx){
//            List<Note> exampleNotes = new ArrayList<>();
//            Object[] response = makeRequest("/api/user/notes","GET",null,authToken);
//            boolean status = (boolean) response[0];
//            String rbody = (String) response[1];
//            if (!status) {
//                ((Activity) ctx).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return new ArrayList<Note>();
//            }
//            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
//            }.getType());
//            List<Note> noteList = new ArrayList<>();
//            List<LinkedTreeMap<String,Object>> notes = (List<LinkedTreeMap<String, Object>>) response_data.get("notes");
//            for (LinkedTreeMap<String,Object> note_: notes) {
//                HashMap<String,Object> note = new HashMap<>(note_);
//                noteList.add(new Note(note));
//            }
//            return noteList;
//        }

        public static AuthenticatedUser getUser(String authToken,Context ctx){
            HashMap<String,Object> exampleUser = new HashMap<>();
            exampleUser.put("Username","Example Username");
            exampleUser.put("Email","example@gmail.com");
            exampleUser.put("Boards",Boards.getBoards(authToken,ctx));
            exampleUser.put("displayName","display name");
            return new AuthenticatedUser(exampleUser);
        }
    }

    public static class Notes {

        public static boolean updateNote(String note_id,String title, Category category,String authToken, Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("title",title);
                put("categoryId",category.getId());
            }};
            Object[] response = makeRequest("/api/notes/"+note_id+"/updateSettings","POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show());
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
            });
            return true;
        }

        public static boolean createNote(String title, String body,String category_id, String authToken, Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("title",title);
                put("text",body);
                put("category_id",category_id);
            }};
            Object[] response = makeRequest("/api/notes/createNote","POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }


        public static Note getNote(String id,String authToken,Context ctx) {
            Object[] response = makeRequest("/api/notes/"+id,"GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            return new Note(response_data);
        }

        public static boolean setBody(String noteId,String newText,String authToken,Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("text",newText);
            }};
            Object[] response = makeRequest("/api/notes/"+noteId,"PATCH",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        public static boolean deleteNote(String noteId,String authToken,Context ctx){
            Object[] response = makeRequest("/api/notes/" + noteId, "DELETE", null, authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            Log.i("ERROR",rbody);
            if (!status) {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
    }
}
