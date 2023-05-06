package com.example.magic_code.ui.profileSettings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.AuthenticatedUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileSettings extends Fragment {

    private ProfileSettingsViewModel mViewModel;
    private String authToken;
    private AuthenticatedUser currentUser;

    public static ProfileSettings newInstance(String authToken) {
        ProfileSettings fragment = new ProfileSettings();
        Bundle args = new Bundle();
        args.putString("authToken", authToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.profile_settings, container, false);
        currentUser = API.Authentication.getUser(authToken);
        ((EditText) view.findViewById(R.id.editTextUsername)).setText(currentUser.getUsername());
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = (getActivity()).findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.profile);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Navigation.findNavController(requireView()).navigateUp();
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileSettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}