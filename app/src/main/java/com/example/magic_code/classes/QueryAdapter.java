package com.example.magic_code.classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;

import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.UserViewHolder> {

    private List<User> users;
    private Context ctx;
    private String authToken;
    private String board_id;
    private SwitchCompat readOnlySwitch;

    public QueryAdapter(List<User> users, String boardId,SwitchCompat readOnlySwitch,Context ctx,String authToken) {
        this.users = users;
        this.ctx = ctx;
        this.authToken = authToken;
        this.readOnlySwitch = readOnlySwitch;
        board_id = boardId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.query_item_view, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.userNameTextView.setText(user.getDisplayName());
        holder.userEmailTextView.setText(user.getEmail());
        holder.sendBtn.setOnClickListener(v->{
            v.setEnabled(false);
            boolean readOnly = readOnlySwitch.isChecked();
            new Thread(()->{
                boolean status = API.Invites.sendInvite(user.getUserId(),board_id,readOnly,authToken,ctx);
                ((Activity) ctx).runOnUiThread(()->{
                    if (!status){holder.sendBtn.setEnabled(true);return;}
                    holder.sendBtn.setText("Sent");
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView userEmailTextView;
        public Button sendBtn;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.query_username);
            userEmailTextView = itemView.findViewById(R.id.query_email_textview);
            sendBtn = itemView.findViewById(R.id.invite_button);
        }
    }
    public void updateData(List<User> newUsers){
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }
}