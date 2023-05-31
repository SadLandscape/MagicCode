package com.example.magic_code.ui.profileSettings;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.regex.Pattern;

public class ProfileSettings extends Fragment {

    private ProfileSettingsViewModel mViewModel;
    private String authToken;
    private SharedPreferences sharedPreferences;
    private AuthenticatedUser currentUser;
    private SharedPreferences.Editor editor;
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
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authToken = sharedPreferences.getString("authToken","");
        Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Pattern emptyPattern = Pattern.compile("\\s+");
        View view = inflater.inflate(R.layout.profile_settings, container, false);
        Button finishBtn = view.findViewById(R.id.buttonFinish);
        new Thread(()->{
            currentUser = API.Authentication.getUser(authToken,activity);
            activity.runOnUiThread(()->{
                EditText username_edit = view.findViewById(R.id.editTextUsername);
                username_edit.setText(currentUser.getDisplayName());
                username_edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (pattern.matcher(username_edit.getText().toString()).find()){
                            username_edit.setError("Username cannot contain special characters or whitespaces!");
                            finishBtn.setEnabled(false);
                            return;
                        }
                        if (username_edit.getText().toString().equals(currentUser.getDisplayName())){
                            username_edit.setError("Username cannot stay the same!");
                            finishBtn.setEnabled(false);
                            return;
                        }
                        if (!finishBtn.isEnabled()) {
                            finishBtn.setEnabled(true);
                        }
                        username_edit.setError(null);
                    }
                });
                view.findViewById(R.id.buttonChangePassword).setOnClickListener(view1 -> {
                    Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_change_password);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
                    EditText currentPasswordEditText = dialog.findViewById(R.id.current_password_edittext);
                    EditText newPasswordEditText = dialog.findViewById(R.id.new_password_edittext);
                    Button okButton = dialog.findViewById(R.id.ok_button);
                    okButton.setEnabled(false);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);
                    TextWatcher checker = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String password1 = currentPasswordEditText.getText().toString();
                            String password2 = newPasswordEditText.getText().toString();
                            if (password2.length()<5){
                                newPasswordEditText.setError("Password must be at least 5 characters long!");
                                okButton.setEnabled(false);
                            }
                            if (emptyPattern.matcher(password2).find()) {
                                newPasswordEditText.setError("Password cannot contain whitespaces!");
                                okButton.setEnabled(false);
                            }
                            if (password1.equals(password2)) {
                                if (okButton.isEnabled()){
                                    okButton.setEnabled(false);
                                }
                                newPasswordEditText.setError("New password cannot be the same as the old one!");
                            }
                            if(!(password2.length()<5) && !emptyPattern.matcher(password2).find() && !password1.equals(password2)){
                                if (!okButton.isEnabled()){
                                    okButton.setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    };
                    currentPasswordEditText.addTextChangedListener(checker);
                    newPasswordEditText.addTextChangedListener(checker);
                    okButton.setOnClickListener(view11 -> {
                        String currentPassword = currentPasswordEditText.getText().toString();
                        String newPassword = newPasswordEditText.getText().toString();
                        okButton.setEnabled(false);
                        new Thread(()->{
                            String newToken = API.Authentication.changePassword(currentPassword,newPassword,authToken,activity);
                            activity.runOnUiThread(()->{
                                okButton.setEnabled(true);
                                if (authToken == null){
                                    return;
                                }
                                editor.putString("authToken",newToken);
                                editor.commit();
                                dialog.dismiss();
                            });
                        }).start();
                    });

                    dialog.show();
                });
                finishBtn.setOnClickListener(view12 -> {
                    new Thread(() -> {
                        boolean status = API.Authentication.changeDisplayName(username_edit.getText().toString(), authToken, activity);
                        activity.runOnUiThread(() -> {
                            if (!status) {
                                return;
                            }
                            Navigation.findNavController(requireView()).navigateUp();
                        });
                    }).start();
                });
            });
        }).start();
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