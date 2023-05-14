package com.example.magic_code.ui.boardsView;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.BoardAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.ui.boardView.boardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class boardsView extends Fragment {

    private BoardsViewViewModel mViewModel;

    public static boardsView newInstance() {
        return new boardsView();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_boards_view, container, false);
        List<Board> boards = API.Boards.getBoards("",getContext());
        BoardAdapter adapter = new BoardAdapter(boards, new BoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Board board) {
                boardView fragment = boardView.newInstance(board);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_boards_to_board_view,fragment.getArguments());
            }
        });

        RecyclerView recyclerView = root_view.findViewById(R.id.board_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardsViewViewModel.class);
        // TODO: Use the ViewModel
    }


}