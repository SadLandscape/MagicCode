package com.example.magic_code.api;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Invite;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.Modification;
import com.example.magic_code.models.Note;
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
import java.net.UnknownHostException;
import java.util.ArrayList;
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
            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PATCH")) {
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
            Log.d("ERROR","makeRequest: "+e+e.getMessage());
            if (e instanceof SocketTimeoutException){
                return new Object[] {false,"Request timed out, please check your internet and try again!"};
            }
            if (e instanceof ConnectException){
                return new Object[] {false,"Unable to connect, please check your internet and try again!"};
            }
            if (e instanceof UnknownHostException){
                return new Object[] {false,"Unable to connect, please check your internet and try again!"};
            }
            return new Object[] {false,e.toString()};
        }
        return new Object[] {false,"Internal server error! Please try again later"};
    }
    public static class Categories{
        public static boolean changeTitle(String category_id,String newTitle, String authToken, Context ctx){
            HashMap<String, Object> payload = new HashMap<String, Object>() {{
                put("title", newTitle);
            }};
            Object[] response = makeRequest("/api/categories/" + category_id, "PATCH", payload, authToken);
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
            ((Activity)ctx).runOnUiThread(()-> Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show());
            return true;
        }
        public static List<Category> moveCategory(String category_id,Integer direction, String authToken, Context ctx){
            HashMap<String, Object> payload = new HashMap<String, Object>() {{
                put("direction", direction);
            }};
            Object[] response = makeRequest("/api/categories/" + category_id + "/move", "PATCH", payload, authToken);
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
        public static boolean removeMember(Member member,String authToken,Context ctx){
            Object[] response = makeRequest("/api/boards/"+member.getBoardId()+"/members/"+member.getId()+"/delete","DELETE",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(()-> {
                    Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {}.getType());
            ((Activity)ctx).runOnUiThread(()->{
                Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show();
            });
            return true;
        }
        public static boolean updatePermissions(Member member,Boolean readOnly,String authToken,Context ctx){
            HashMap<String, Object> payload = new HashMap<String,Object>(){{
                put("set",readOnly);
            }};
            Object[] response = makeRequest("/api/boards/"+member.getBoardId()+"/members/"+member.getId()+"/permissions/readOnly","PATCH",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(()-> {
                    Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show();
                });
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {}.getType());
            return true;
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

        public static boolean updateTitle(String board_id,String title,String authToken,Context ctx){
            HashMap<String, Object> body = new HashMap<String, Object>() {{
                put("title", title);
            }};
            Object[] response = makeRequest("/api/boards/"+board_id+"/title", "POST", body,authToken);
            boolean status = (boolean) response[0];
            String resp = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, resp, Toast.LENGTH_SHORT).show());
                return false;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show());
            return true;
        }

        public static boolean deleteToken(String tokenId,String authToken,Context ctx){
            Object[] response = makeRequest("/api/tokens/"+tokenId+"/delete","DELETE",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show());
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

        public static List<Member> getMembers(String board_id, String authToken, Context ctx) {
            Object[] response = makeRequest("/api/boards/"+board_id+"/members","GET",null,authToken);
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
            List<Member> memberList = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> members = (List<LinkedTreeMap<String, Object>>) response_data.get("members");
            for (LinkedTreeMap<String,Object> member: members) {
                HashMap<String,Object> member_ = new HashMap<>(member);
                memberList.add(new Member(member_,(String) response_data.get("selfId")));
            }
            return memberList;
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

        public static boolean requestNewCode(String email,String password, String username,Context ctx){
            HashMap<String,Object> requestBody = new HashMap<String,Object>(){{
                put("username",username);
                put("email",email);
                put("password",password);
            }};
            Object[] response = makeRequest("/api/auth/request","POST",requestBody,null);
            boolean status = (boolean) response[0];
            if (!status){
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response[1], Toast.LENGTH_SHORT).show());
                return false;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show());
            return true;
        }

        public static String checkEmailCode(String code,String email, Context ctx){
            HashMap<String, Object> body = new HashMap<String, Object>() {{
                put("email", email);
                put("code", code);
            }};
            Object[] response = makeRequest("/api/auth/verify", "POST", body,null);
            boolean status = (boolean) response[0];
            String resp = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, resp, Toast.LENGTH_SHORT).show());
                return null;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show());
            return (String) response_data.get("authToken");
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
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, resp, Toast.LENGTH_SHORT).show());
                return null;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show());
            return (String) response_data.get("authToken");
        }
        public static boolean register(String username,String email,String password,Context ctx){
            HashMap<String,Object> requestBody = new HashMap<String,Object>(){{
                put("username",username);
                put("email",email);
                put("password",password);
            }};
            Object[] response = makeRequest("/api/auth/register","POST",requestBody,null);
            boolean status = (boolean) response[0];
            if (!status){
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response[1], Toast.LENGTH_SHORT).show());
                return false;
            }
            String rbody = (String) response[1];
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show());
            return true;
        }
        public static boolean[] checkAuth(String authToken,Context ctx){
            Object[] response = makeRequest("/api/auth/checkAuth","GET",null,authToken);
            boolean status = (boolean) response[0];
            String resp = (String) response[1];
            if (!status){
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, resp, Toast.LENGTH_SHORT).show());
                return new boolean[]{false,false};
            }
            HashMap<String, Object> response_data = new Gson().fromJson(resp, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            return new boolean[] {true, (boolean) response_data.get("valid")};

        }

        public static boolean changeDisplayName(String username,String authToken,Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("username",username);
            }};
            Object[] response = makeRequest("/api/usernames/change_username","PATCH",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show());
                return false;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity)ctx).runOnUiThread(()->{
                Toast.makeText(ctx, (String)response_data.get("message"), Toast.LENGTH_SHORT).show();
            });
            return true;
        }

        public static AuthenticatedUser getUser(String authToken,Context ctx){
            Object[] response = makeRequest("/api/auth/users/current","GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show());
                return null;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            return new AuthenticatedUser(response_data);
        }
        public static String changePassword(String old_password,String new_password,String authToken,Context ctx){
            HashMap<String,Object> payload = new HashMap<String,Object>(){{
                put("password",old_password);
                put("newPassword",new_password);
            }};
            Object[] response = makeRequest("/api/auth/change_password","POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show());
                return null;
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            ((Activity) ctx).runOnUiThread(() -> {
                Toast.makeText(ctx, (String) response_data.get("message"), Toast.LENGTH_SHORT).show();
            });
            return (String)response_data.get("authToken");
        }
    }

    public static class Notes {

        public static List<Modification> getHistory(String note_id,String authToken,Context ctx){
            Object[] response = makeRequest("/api/notes/"+note_id+"/history","GET",null,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, (String) rbody, Toast.LENGTH_SHORT).show());
                return new ArrayList<>();
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            List<Modification> modifications = new ArrayList<>();
            List<LinkedTreeMap<String,Object>> mods = (List<LinkedTreeMap<String, Object>>) response_data.get("data");
            for (LinkedTreeMap<String,Object> mod: mods) {
                HashMap<String,Object> mod_ = new HashMap<>(mod);
                modifications.add(new Modification(mod_));
            }
            return modifications;
        }

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
    public static class Invites{
        public static boolean sendInvite(String uid,String board_id,Boolean readOnly,String authToken, Context ctx){
            HashMap<String, Object> payload = new HashMap<String,Object>(){{
                put("boardId",board_id);
                put("uid",uid);
                put("readOnly",readOnly);
            }};
            Object[] response = makeRequest("/api/invites/create", "POST", payload, authToken);
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
        public static Invite validateInvite(String inviteId, Context ctx){
            Object[] response = makeRequest("/api/invites/"+inviteId+"/validate", "GET", null,null);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show());
                return null;
            }
            HashMap<String, String> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, String>>() {
            }.getType());
            return new Invite(response_data);
        }
        public static boolean acceptInvite(String inviteId,String authToken,Context ctx){
            HashMap<String, Object> payload = new HashMap<String,Object>(){{
                put("invite_id",inviteId);
            }};
            Object[] response = makeRequest("/api/invites/use", "POST", payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show());
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
    public static class Search{
        public static List<User> queryUsers(String query,String board_id,String authToken,Context ctx){
            HashMap<String, Object> payload = new HashMap<String,Object>(){{
                put("boardId",board_id);
            }};
            Object[] response = makeRequest("/api/users/search/"+query,"POST",payload,authToken);
            boolean status = (boolean) response[0];
            String rbody = (String) response[1];
            if (!status) {
                ((Activity) ctx).runOnUiThread(() -> Toast.makeText(ctx, rbody, Toast.LENGTH_SHORT).show());
                return new ArrayList<>();
            }
            HashMap<String, Object> response_data = new Gson().fromJson(rbody, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            List<User> userList = new ArrayList<>();
            List<LinkedTreeMap<String,String>> users = (List<LinkedTreeMap<String, String>>) response_data.get("users");
            for (LinkedTreeMap<String,String> user: users) {
                HashMap<String,String> user_ = new HashMap<>(user);
                userList.add(new User(user_));
            }
            return userList;
        }
    }
}
