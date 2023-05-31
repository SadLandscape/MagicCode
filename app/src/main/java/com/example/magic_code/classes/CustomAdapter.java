package com.example.magic_code.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final List<Note> localDataSet;
    private final Context context;
    private Boolean canDelete;
    private String authToken;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView authorView;
        private final CardView rootView;
        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.titleTextView);
            authorView = (TextView) view.findViewById(R.id.authorTextView);
            rootView = (CardView) view;
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getAuthorView() {
            return authorView;
        }

        public CardView getRootView() {
            return rootView;
        }
    }

    public CustomAdapter(List<Note> dataSet,Boolean canDelete ,Context ctx,String authToken) {
        localDataSet = dataSet;
        context = ctx;
        this.authToken = authToken;
        this.canDelete = canDelete;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.text_row_item, parent, false);
        return new CustomAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Note note = localDataSet.get(position);
        viewHolder.getTextView().setText(note.getTitle());
        viewHolder.getAuthorView().setText(note.getAuthor());
        viewHolder.getRootView().setTag(note.getId());
        if (canDelete) {
            viewHolder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, viewHolder.getRootView());
                    popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menu_delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Delete Note");
                                builder.setMessage("Are you sure you want to delete this note?");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean status = API.Notes.deleteNote(note.getId(), authToken, context);
                                                if (status) {
                                                    localDataSet.remove(note);
                                                }
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog dialog = builder.create();
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
                                dialog.show();
                                return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(List<Note> newData){
        localDataSet.clear();
        localDataSet.addAll(newData);
        notifyDataSetChanged();
    }
}
