package com.example.magic_code.ui.manageBoard.TokensPage;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.TokenAdapter;
import com.example.magic_code.models.ShareToken;

import java.util.ArrayList;
import java.util.List;

public class TokensPage extends Fragment {

    private TokensPageViewModel mViewModel;
    private String authToken;
    private TokenAdapter tokenAdapter;
    private FragmentActivity activity;
    private ArrayList<ShareToken> tokens;
    private String board_id;

    public static TokensPage newInstance(String authToken,String board_id) {
        Bundle args = new Bundle();
        args.putString("authToken",authToken);
        args.putString("board_id",board_id);
        TokensPage fragment = new TokensPage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        board_id = getArguments().getString("board_id");
        tokens = getArguments().getParcelableArrayList("tokens");
        authToken = getArguments().getString("authToken");
        View root_view = inflater.inflate(R.layout.fragment_tokens_page, container, false);
        SwipeRefreshLayout refreshLayout = root_view.findViewById(R.id.tokens_refresh);
        refreshLayout.setOnRefreshListener(() -> new Thread(() -> {
            List<ShareToken> tokens = API.Boards.getTokens(board_id,authToken,activity);
            activity.runOnUiThread(() -> {
                tokenAdapter.updateData(tokens);
                refreshLayout.setRefreshing(false);
            });
        }).start());
        new Thread(()->{
            tokens = new ArrayList<>(API.Boards.getTokens(board_id,authToken,activity));
            activity.runOnUiThread(()->{
                RecyclerView tokensRv = root_view.findViewById(R.id.qr_recycler_view);
                tokenAdapter = new TokenAdapter(tokens,activity,authToken);
                tokensRv.setAdapter(tokenAdapter);
                tokensRv.setLayoutManager(new LinearLayoutManager(activity));
            });
        }).start();
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TokensPageViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

}