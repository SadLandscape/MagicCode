package com.example.magic_code.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.MainActivity;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Invite;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.InviteViewHolder> {

    private List<Invite> inviteList;
    private Context ctx;
    private String authToken;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public InvitesAdapter(List<Invite> invites, Context ctx,String authToken) {
        inviteList = invites;
        this.ctx = ctx;
        this.authToken = authToken;
        sharedPreferences = ctx.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.invite_row_item, parent, false);
        return new InviteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {
        Invite invite = inviteList.get(position);
        holder.inviterDetailsTextView.setText(invite.getInvDisplayName()+" (@"+invite.getInvUsername()+")");
        holder.boardNameTextView.setText(invite.getBoardName());
        holder.acceptButton.setOnClickListener(view->{
            new Thread(()->{
                boolean status = API.Invites.acceptInvite(invite.getId(),authToken,ctx);
                ((Activity)ctx).runOnUiThread(()->{
                    if (status){
                        Set<String> invites = new HashSet<>(sharedPreferences.getStringSet("invites", new HashSet<>()));
                        if (invites.contains(invite.getId())){
                            invites.remove(invite.getId());
                            editor.putStringSet("invites",invites);
                            editor.commit();
                            inviteList.remove(invite);
                            notifyItemRemoved(position);
                            ((MainActivity)ctx).updateBadge();
                        }
                    }
                });
            }).start();
        });
        holder.rejectButton.setOnClickListener(view -> {
            Set<String> invites = new HashSet<>(sharedPreferences.getStringSet("invites", new HashSet<>()));
            if (invites.contains(invite.getId())){
                invites.remove(invite.getId());
                editor.putStringSet("invites",invites);
                editor.commit();
                inviteList.remove(invite);
                notifyItemRemoved(position);
                ((MainActivity)ctx).updateBadge();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    public class InviteViewHolder extends RecyclerView.ViewHolder {
        public TextView boardNameTextView;
        public TextView inviterDetailsTextView;
        public Button acceptButton;
        public Button rejectButton;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            boardNameTextView = itemView.findViewById(R.id.invite_board_name);
            inviterDetailsTextView = itemView.findViewById(R.id.invite_inviter_details);
            acceptButton = itemView.findViewById(R.id.invite_accept_btn);
            rejectButton = itemView.findViewById(R.id.invite_reject_btn);
        }
    }

    public void updateData(List<Invite> invites){
        inviteList.clear();
        inviteList.addAll(invites);
        notifyDataSetChanged();
    }

    public void addInvite(Invite invite){
        inviteList.add(invite);
        notifyDataSetChanged();
    }
}