package com.example.magic_code.ui.boardView;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.CategoryAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Note;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class boardView extends Fragment {

    private BoardViewViewModel mViewModel;

    public static boardView newInstance(Board board) {
        boardView fragment = new boardView();
        Bundle args = new Bundle();
        args.putSerializable("board", board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Navigation.findNavController(requireView()).navigateUp();
            return true;
        }
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root_view = inflater.inflate(R.layout.fragment_board_view, container, false);
        RecyclerView recyclerView = root_view.findViewById(R.id.category_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Category> categories = API.Categories.getCategories("");
        CategoryAdapter adapter = new CategoryAdapter(categories,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardViewViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }
}