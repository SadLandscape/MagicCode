package com.example.magic_code.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Note;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.example.magic_code.utils.ItemClickSupport;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Board board;
    private List<Category> categoryList;
    private Context ctx;
    private String authToken;
    public CategoryAdapter(List<Category> categoryList, Board board, Context ctx, String authToken) {
        this.categoryList = categoryList;
        this.ctx = ctx;
        this.authToken = authToken;
        this.board = board;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        CustomAdapter noteAdapter = new CustomAdapter(category.getNoteList(),board.canEdit(),ctx,authToken);
        holder.notesRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        holder.notesRecyclerView.setAdapter(noteAdapter);
        holder.categoryTitleTextView.setText(category.getTitle());
        if (board.canEdit()) {
            holder.categoryTitleTextView.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(ctx, holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Delete Category");
                        builder.setMessage("Are you sure you want to delete this category?");
                        builder.setPositiveButton("Delete", (dialog, which) -> new Thread(() -> {
                            boolean status = API.Categories.deleteCategory(category.getId(), authToken, ctx);
                            if (status) {
                                categoryList.remove(category);
                            }
                            ((Activity) ctx).runOnUiThread(() -> notifyDataSetChanged());
                        }).start());
                        builder.setNegativeButton("Cancel", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();

                return true;
            });
        }
        holder.expandCollapseButton.setOnClickListener(view -> {
            boolean isExpanded = holder.notesRecyclerView.getVisibility() == View.VISIBLE;
            holder.notesRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(ctx, isExpanded ? R.anim.fade_out : R.anim.fade_in);
            holder.notesRecyclerView.startAnimation(fadeInAnimation);
            holder.expandCollapseButton.setText(isExpanded ? "+" : "-");
        });
        holder.categoryTitleTextView.setOnClickListener(view -> {
            boolean isExpanded = holder.notesRecyclerView.getVisibility() == View.VISIBLE;
            holder.notesRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(ctx, isExpanded ? R.anim.fade_out : R.anim.fade_in);
            holder.notesRecyclerView.startAnimation(fadeInAnimation);
            holder.expandCollapseButton.setText(isExpanded ? "+" : "-");
        });
        ItemClickSupport.addTo(holder.notesRecyclerView).setOnItemClickListener((recyclerView, position1, v) -> {
            Note clickedNote = category.getNoteList().get(position1);
            NoteFragment detailFragment = NoteFragment.newInstance(clickedNote.getId(),board.canEdit(),board);
            NavController navController = Navigation.findNavController(((Activity) ctx), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_board_view_to_detailed_note_view, detailFragment.getArguments());
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    public void updateData(List<Category> newData){
        categoryList.clear();
        categoryList.addAll(newData);
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitleTextView;
        RecyclerView notesRecyclerView;
        Button expandCollapseButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitleTextView = itemView.findViewById(R.id.category_title);
            notesRecyclerView = itemView.findViewById(R.id.notes_recyclerview);
            expandCollapseButton = itemView.findViewById(R.id.expand_collapse_button);
        }
    }
}