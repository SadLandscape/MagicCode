package com.example.magic_code.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;

import java.util.List;

public class SettingsUserAdapter extends RecyclerView.Adapter<SettingsUserAdapter.UserViewHolder> {

    private List<User> userList;
    private Settings settings;
    private OnUserRemoveClickListener removeClickListener = new OnUserRemoveClickListener();

    public SettingsUserAdapter(Settings settings) {
        userList = settings.getUsers();
        this.settings = settings;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_view, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getUsername());
        holder.userEmailTextView.setText(user.getEmail());

        holder.removeUserButton.setOnClickListener(view -> {
            if (removeClickListener != null) {
                removeClickListener.onUserRemoveClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setRemoveClickListener(OnUserRemoveClickListener removeClickListener) {
        this.removeClickListener = removeClickListener;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView userEmailTextView;
        public ImageButton removeUserButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_textview);
            userEmailTextView = itemView.findViewById(R.id.user_email_textview);
            removeUserButton = itemView.findViewById(R.id.remove_user_button);
        }
    }

    public class OnUserRemoveClickListener {
        void onUserRemoveClick(User user){
            settings.removeUser(user);
            notifyDataSetChanged();
        }
    }
}
