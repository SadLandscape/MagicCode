package com.example.magic_code.ui.createBoard;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class boardCreate extends Fragment {

    private BoardCreateViewModel mViewModel;
    String authToken;
    SharedPreferences sharedPreferences;
    private FragmentActivity activity;

    public static boardCreate newInstance() {
        return new boardCreate();
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
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", activity.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View root_view = inflater.inflate(R.layout.fragment_board_create, container, false);
        EditText editText = root_view.findViewById(R.id.board_title);
        Button createButton = root_view.findViewById(R.id.create_button_board);
        createButton.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String selectedValue = editText.getText().toString().trim();
                if (TextUtils.isEmpty(selectedValue)) {
                    editText.setError("Name must be at least 3 characters long!");
                    createButton.setEnabled(false);
                } else {
                    createButton.setEnabled(true);
                    editText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        createButton.setOnClickListener(view -> new Thread(() -> {
            API.Boards.createBoard(((EditText)root_view.findViewById(R.id.board_title)).getText().toString(),authToken,activity);
            activity.runOnUiThread(() -> Navigation.findNavController(requireView()).navigateUp());
        }).start());
        return root_view;
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardCreateViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

}