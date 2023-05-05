package com.example.magic_code.classes;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.magic_code.ui.login.LoginFragment;
import com.example.magic_code.ui.register.RegisterFragment;

public class AuthenticationPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    public AuthenticationPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LoginFragment.newInstance();
            case 1:
                return RegisterFragment.newInstance();
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
                return "Login";
            case 1:
                return "Register";
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }
}
