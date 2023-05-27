package com.example.magic_code.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
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

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView userEmailTextView;
        public SwitchCompat readOnlySwitch;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_textview);
            userEmailTextView = itemView.findViewById(R.id.user_email_textview);
            readOnlySwitch = itemView.findViewById(R.id.switch_read_only);
        }
    }
    public void updateData(List<Member> members){
        memberList.clear();
        memberList.addAll(members);
        notifyDataSetChanged();
    }
}
