package com.example.magic_code.ui.boardsView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.BoardAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.ui.boardView.boardView;
import com.example.magic_code.ui.createBoard.boardCreate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class boardsView extends Fragment {

    private BoardsViewViewModel mViewModel;
    String authToken;
    BoardAdapter adapter;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    private FragmentActivity activity;

    public static boardsView newInstance() {
        return new boardsView();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Loading boards...");
        dialog.show();
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", activity.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View root_view = inflater.inflate(R.layout.fragment_boards_view, container, false);
        SwipeRefreshLayout refreshLayout = root_view.findViewById(R.id.board_refresh);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Board> boardList = API.Boards.getBoards(authToken,activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        adapter = new BoardAdapter(boardList, new BoardAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Board board) {
                                boardView fragment = boardView.newInstance(board);
                                NavController navController = Navigation.findNavController(requireView());
                                navController.navigate(R.id.action_boards_to_board_view,fragment.getArguments());
                            }
                        },authToken,activity);

                        RecyclerView recyclerView = root_view.findViewById(R.id.board_recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Board> boardList = API.Boards.getBoards(authToken,activity);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(boardList);
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        root_view.findViewById(R.id.create_board_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardCreate fragment = boardCreate.newInstance();
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_boards_to_create_board,fragment.getArguments());
            }
        });

        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardsViewViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

    public void refreshBoards(){
        if (isVisible() && activity!=null) {
            dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_loading);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
            dialog.setCancelable(false);
            ((TextView)dialog.findViewById(R.id.status_text)).setText("Refreshing boards...");
            dialog.show();
            new Thread(()->{
                List<Board> boardList = API.Boards.getBoards(authToken, activity);
                activity.runOnUiThread(()-> {
                    dialog.dismiss();
                    adapter.updateData(boardList);
                });
            }).start();
        }
    }

}