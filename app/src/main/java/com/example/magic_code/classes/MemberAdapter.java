package com.example.magic_code.classes;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.UserViewHolder> {

    private List<Member> memberList;
    private Context ctx;
    private String authToken;

    public MemberAdapter(List<Member> members, Context ctx,String authToken) {
        memberList = members;
        this.ctx = ctx;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.user_item_view, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.userNameTextView.setText(member.getDisplayName());
        holder.userEmailTextView.setText(member.getEmail());
        holder.readOnlySwitch.setChecked(!member.getCanEdit());
        if (!member.getDeletable()) {
            Log.d("MEMBERS", member.getEmail()+" || "+position);
            holder.readOnlySwitch.setEnabled(false);
            holder.removeUserBtn.setEnabled(false);
            holder.removeUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }
        else{
            holder.readOnlySwitch.setEnabled(true);
            holder.removeUserBtn.setEnabled(true);
            holder.removeUserBtn.setBackgroundTintList(ColorStateList.valueOf(0xffef5350));
        }
        Log.d("MEMBERS", "onBindViewHolder: "+member.getEmail()+" || "+member.getDeletable()+" || "+position);
        holder.removeUserBtn.setOnClickListener(v->{
            new Thread(()->{
                boolean status = API.Boards.removeMember(member,authToken,ctx);
                ((Activity)ctx).runOnUiThread(()->{
                    if (status){
                        memberList.remove(member);
                        notifyItemRemoved(position);
                    }
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView userEmailTextView;
        public SwitchCompat readOnlySwitch;
        public AppCompatImageButton removeUserBtn;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_textview);
            userEmailTextView = itemView.findViewById(R.id.user_email_textview);
            readOnlySwitch = itemView.findViewById(R.id.switch_read_only);
            removeUserBtn = itemView.findViewById(R.id.remove_user_button);
        }
    }
    public void updateData(List<Member> members){
        memberList.clear();
        memberList.addAll(members);
        notifyDataSetChanged();
    }
}
