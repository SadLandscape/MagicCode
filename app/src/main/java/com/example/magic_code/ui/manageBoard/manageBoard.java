package com.example.magic_code.ui.manageBoard;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.MemberAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.ShareToken;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class manageBoard extends Fragment {

    private ManageBoardViewModel mViewModel;
    private String board_id;
    private String authToken;
    private SharedPreferences sharedPreferences;

    public static manageBoard newInstance(String board_id) {
        manageBoard fragment = new manageBoard();
        Bundle args = new Bundle();
        args.putString("board_id", board_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_manage_board, container, false);
        board_id = getArguments().getString("board_id");
        sharedPreferences = requireActivity().getSharedPreferences("MagicPrefs", requireContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        RecyclerView membersRv = root_view.findViewById(R.id.users_recyclerview);
        TextInputEditText titleBoard = root_view.findViewById(R.id.title_board_edittext);
        new Thread(()->{
            Board board = API.Boards.getBoard(board_id,authToken,requireContext());
            List<ShareToken> tokens = API.Boards.getTokens(board_id,authToken,requireContext());
            List<Member> members = API.Boards.getMembers(board_id,authToken,requireContext());
            requireActivity().runOnUiThread(()->{
                Toast.makeText(requireContext(), ""+ members.size(), Toast.LENGTH_SHORT).show();
                MemberAdapter adapter = new MemberAdapter(members);
                membersRv.setAdapter(adapter);
                membersRv.setLayoutManager(new LinearLayoutManager(requireContext()));
                titleBoard.setText(board.getTitle());
            });

        }).start();
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ManageBoardViewModel.class);
        // TODO: Use the ViewModel
    }

}