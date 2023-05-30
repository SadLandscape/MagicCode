package com.example.magic_code.ui.manageBoard.MembersPage;

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
import com.example.magic_code.classes.MemberAdapter;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.ShareToken;

import java.util.ArrayList;
import java.util.List;

public class MembersPage extends Fragment {

    private MembersPageViewModel mViewModel;
    private ArrayList<Member> members;
    private String board_id;
    private FragmentActivity activity;
    private String authToken;
    private MemberAdapter memberAdapter;

    public static MembersPage newInstance(String authToken,String board_id) {
        Bundle args = new Bundle();
        args.putString("authToken",authToken);
        args.putString("board_id",board_id);
        MembersPage fragment = new MembersPage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        board_id = getArguments().getString("board_id");
        authToken = getArguments().getString("authToken");
        View root_view = inflater.inflate(R.layout.fragment_members_page, container, false);
        RecyclerView membersRv = root_view.findViewById(R.id.users_recyclerview);
        SwipeRefreshLayout refreshLayout = root_view.findViewById(R.id.members_refresh);
        new Thread(()->{
            members = new ArrayList<>(API.Boards.getMembers(board_id,authToken,activity));
            activity.runOnUiThread(()->{
                memberAdapter = new MemberAdapter(members,activity,authToken);
                membersRv.setLayoutManager(new LinearLayoutManager(activity));
                membersRv.setAdapter(memberAdapter);
                refreshLayout.setOnRefreshListener(() -> new Thread(() -> {
                    members = new ArrayList<>(API.Boards.getMembers(board_id,authToken,activity));
                    activity.runOnUiThread(() -> {
                        memberAdapter.updateData(members);
                        refreshLayout.setRefreshing(false);
                    });
                }).start());
            });
        }).start();
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MembersPageViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

}