package com.example.magic_code.classes;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.magic_code.models.Member;
import com.example.magic_code.models.ShareToken;
import com.example.magic_code.ui.login.LoginFragment;
import com.example.magic_code.ui.manageBoard.MembersPage.MembersPage;
import com.example.magic_code.ui.manageBoard.TokensPage.TokensPage;
import com.example.magic_code.ui.register.RegisterFragment;

import java.util.ArrayList;
import java.util.List;

public class BoardSettingsPageAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    private final String board_id;
    private String authToken;

    public BoardSettingsPageAdapter(FragmentManager fm, String board_id, String authToken) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.authToken = authToken;
        this.board_id = board_id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MembersPage.newInstance(authToken,board_id);
            case 1:
                return TokensPage.newInstance(authToken,board_id);
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Members";
            case 1:
                return "Tokens";
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }
}