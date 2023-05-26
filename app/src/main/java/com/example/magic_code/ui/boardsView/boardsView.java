package com.example.magic_code.ui.boardsView;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.SharedPreferences;
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

    public static boardsView newInstance() {
        return new boardsView();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Loading boards...");
        dialog.show();
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View root_view = inflater.inflate(R.layout.fragment_boards_view, container, false);
        SwipeRefreshLayout refreshLayout = root_view.findViewById(R.id.board_refresh);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Board> boardList = API.Boards.getBoards(authToken,getContext());
                getActivity().runOnUiThread(new Runnable() {
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
                        },authToken,requireContext());

                        RecyclerView recyclerView = root_view.findViewById(R.id.board_recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                        List<Board> boardList = API.Boards.getBoards(authToken,getContext());
                        getActivity().runOnUiThread(new Runnable() {
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


}