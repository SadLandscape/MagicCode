package com.example.magic_code.ui.profileSettings;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileSettings extends Fragment {

    private ProfileSettingsViewModel mViewModel;
    private String authToken;
    private AuthenticatedUser currentUser;
    private FragmentActivity activity;

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
        currentUser = API.Authentication.getUser(authToken,activity);
        ((EditText) view.findViewById(R.id.editTextUsername)).setText(currentUser.getUsername());
        ((Button) view.findViewById(R.id.buttonChangePassword)).setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_change_password);

            EditText currentPasswordEditText = dialog.findViewById(R.id.current_password_edittext);
            EditText newPasswordEditText = dialog.findViewById(R.id.new_password_edittext);
            Button okButton = dialog.findViewById(R.id.ok_button);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
            okButton.setOnClickListener(view11 -> {
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                if (currentPassword.equals(newPassword) || currentPassword.isEmpty() || newPassword.isEmpty()){
                    Toast.makeText(activity, "Password cannot be the same or be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, "Successfully changed password!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            dialog.show();
        });
        ((Button) view.findViewById(R.id.buttonFinish)).setOnClickListener(view12 -> Navigation.findNavController(requireView()).navigateUp());
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }
}