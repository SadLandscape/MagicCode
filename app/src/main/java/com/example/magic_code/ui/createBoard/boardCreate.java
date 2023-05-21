package com.example.magic_code.ui.createBoard;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.magic_code.R;
import com.example.magic_code.api.API;

public class boardCreate extends Fragment {

    private BoardCreateViewModel mViewModel;
    String authToken;
    SharedPreferences sharedPreferences;

    public static boardCreate newInstance() {
        return new boardCreate();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View root_view = inflater.inflate(R.layout.fragment_board_create, container, false);
        root_view.findViewById(R.id.create_button_board).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        API.Boards.createBoard(((EditText)root_view.findViewById(R.id.board_title)).getText().toString(),authToken,getContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Navigation.findNavController(requireView()).navigateUp();
                            }
                        });
                    }
                }).start();
            }
        });
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BoardCreateViewModel.class);
        // TODO: Use the ViewModel
    }

}