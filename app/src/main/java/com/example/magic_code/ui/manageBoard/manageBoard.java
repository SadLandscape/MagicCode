package com.example.magic_code.ui.manageBoard;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.magic_code.MainActivity;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.AuthenticationPagerAdapter;
import com.example.magic_code.classes.BoardSettingsPageAdapter;
import com.example.magic_code.classes.MemberAdapter;
import com.example.magic_code.classes.TokenAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Member;
import com.example.magic_code.models.ShareToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class manageBoard extends Fragment {

    private ManageBoardViewModel mViewModel;
    private String board_id;
    private String authToken;
    private SharedPreferences sharedPreferences;
    private FragmentActivity activity;

    public static manageBoard newInstance(String board_id) {
        manageBoard fragment = new manageBoard();
        Bundle args = new Bundle();
        args.putString("board_id", board_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Navigation.findNavController(requireView()).navigateUp();
            return true;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.manage_board_menu,menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root_view = inflater.inflate(R.layout.fragment_manage_board, container, false);
        board_id = getArguments().getString("board_id");
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        TabLayout tabLayout = root_view.findViewById(R.id.board_tab_layout);
        ViewPager viewPager = root_view.findViewById(R.id.board_view_pager);
        BoardSettingsPageAdapter pagerAdapter = new BoardSettingsPageAdapter(getChildFragmentManager(), board_id,authToken);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        TextInputEditText titleBoard = root_view.findViewById(R.id.title_board_edittext);
        Button saveBtn = root_view.findViewById(R.id.save_button);
        saveBtn.setOnClickListener(v->{
            v.setEnabled(false);
            new Thread(()->{
                API.Boards.updateTitle(board_id,titleBoard.getText().toString(),authToken,activity);
                activity.runOnUiThread(()-> v.setEnabled(true));
            }).start();
        });
        new Thread(()->{
            Board board = API.Boards.getBoard(board_id,authToken,activity);
            activity.runOnUiThread(()->{
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
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

}