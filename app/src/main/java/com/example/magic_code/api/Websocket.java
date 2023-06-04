package com.example.magic_code.api;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Websocket extends WebSocketListener {
    private final String noteId;
    private final String authToken;
    private final Context ctx;
    private WebSocket websocket;
    private final Gson gson = new Gson();

    public void toast(String message){
        ((Activity)ctx).runOnUiThread(()-> Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show());
    }
    public Websocket(String note_id,String authToken, Context ctx){
        noteId = note_id;
        this.authToken = authToken;
        this.ctx = ctx;
    }

    public void start() {
        new Thread(()->{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API.API_URL+"/ws")
                    .build();
            websocket = client.newWebSocket(request, this);
        }).start();
    }
    public void close(){
        websocket.close(1000,"Member has left the note view");
    }

    @Override
    public void onOpen(WebSocket webSocket, @NonNull Response response) {
        HashMap<String,Object> payload = new HashMap<>();
        payload.put("opcode",2);
        payload.put("authToken",authToken);
        payload.put("noteId",noteId);
        webSocket.send(gson.toJson(payload));
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {}

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        if (code != 1000){
            start();
        }
    }
    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {}

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        new Thread(this::start).start();

    }
}
