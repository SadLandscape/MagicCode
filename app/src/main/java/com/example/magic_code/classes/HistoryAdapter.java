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
import com.example.magic_code.models.Modification;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Modification> historyList;
    private Context ctx;
    private String authToken;

    public HistoryAdapter(List<Modification> invites, Context ctx, String authToken) {
        historyList = invites;
        this.ctx = ctx;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.history_item_view, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Modification modification = historyList.get(position);
        holder.authorTextView.setText(modification.getDisplayName()+" (@"+modification.getUsername()+")");
        holder.messageTextView.setText(modification.getChangeMessage());
        holder.dateTimeView.setDate(modification.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public TextView messageTextView;
        public DateTimeView dateTimeView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.history_author_textview);
            messageTextView = itemView.findViewById(R.id.history_message);
            dateTimeView = itemView.findViewById(R.id.dateTimeView);
        }
    }

    public void updateData(List<Modification> modifications){
        historyList.clear();
        historyList.addAll(modifications);
        notifyDataSetChanged();
    }
}