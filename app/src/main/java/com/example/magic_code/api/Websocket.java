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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API.API_URL+"/ws")
                .build();
        client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, @NonNull Response response) {
        toast("Websocket connection opened!");
        HashMap<String,Object> payload = new HashMap<String,Object>();
        payload.put("opcode",2);
        payload.put("authToken",authToken);
        payload.put("noteId",noteId);
        webSocket.send(gson.toJson(payload));
        toast("Message sent "+gson.toJson(payload));
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        toast("Message received: "+text);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {}

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        toast("Error1 "+reason);
    }
    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        toast("Error2 "+reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        toast("Error3 "+t);
    }
}
