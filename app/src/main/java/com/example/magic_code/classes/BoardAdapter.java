package com.example.magic_code.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;
import com.example.magic_code.models.Board;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private List<Board> boards;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Board board);
    }

    public static class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView authorTextView;

        public BoardViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
        }

        public void bind(final Board board, final OnItemClickListener listener) {
            titleTextView.setText(board.getTitle());
            authorTextView.setText(board.getAuthor());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(board);
                }
            });
        }
    }

    public BoardAdapter(List<Board> boards, OnItemClickListener listener) {
        this.boards = boards;
        this.listener = listener;
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item, parent, false);
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
}
