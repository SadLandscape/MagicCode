package com.example.magic_code.classes;

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

import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Note;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private List<Board> boards;
    private OnItemClickListener listener;
    private String authToken;
    private Context ctx;

    public interface OnItemClickListener {
        void onItemClick(Board board);
    }

    public class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView authorTextView;

        public BoardViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.categoryTitleTextView);
            authorTextView = itemView.findViewById(R.id.categoryAuthorTextView);
        }

        public void bind(final Board board, final OnItemClickListener listener) {
            titleTextView.setText(board.getTitle());
            authorTextView.setText(board.getAuthor());
            itemView.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(ctx,itemView);
                popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
                popupMenu.getMenu().findItem(R.id.menu_delete).setTitle(board.canDelete() ? "Delete" : "Leave");

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle(board.canDelete() ? "Delete Board" : "Leave Board");
                        builder.setMessage("Are you sure you want to " +(board.canDelete() ?"delete":"leave")+" this board?");
                        builder.setPositiveButton(board.canDelete() ?"Delete" : "Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean status = board.canDelete() ? API.Boards.deleteBoard(board.getId(),authToken,ctx) : API.Boards.leaveBoard(board.getId(),authToken,ctx);
                                        if (status) {
                                            boards.remove(board);
                                        }
                                        ((Activity) ctx).runOnUiThread(BoardAdapter.this::notifyDataSetChanged);
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
                });
                popupMenu.show();

                return true;
            });
            itemView.setOnClickListener(v -> listener.onItemClick(board));
        }
    }

    public BoardAdapter(List<Board> boards, OnItemClickListener listener,String authToken,Context ctx) {
        this.boards = boards;
        this.listener = listener;
        this.authToken = authToken;
        this.ctx = ctx;
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.board_item, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BoardViewHolder holder, int position) {
        holder.bind(boards.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return boards.size();
    }
    public void updateData(List<Board> newData){
        boards.clear();
        boards.addAll(newData);
        notifyDataSetChanged();
    }
}
