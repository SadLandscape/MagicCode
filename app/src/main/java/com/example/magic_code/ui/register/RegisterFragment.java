package com.example.magic_code.ui.register;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.magic_code.R;
import com.example.magic_code.api.API;

import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private RegisterViewModel mViewModel;
    private String authToken;
    Pattern pattern;
    Pattern emptyPattern;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    public boolean checkValues(String username,String email,String password,String password2){
        return username.isEmpty() || pattern.matcher(username).find() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !password.equals(password2) || password.length() < 5 || password.matches("\\s+");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_register, container, false);
        EditText email_edit = (EditText) root_view.findViewById(R.id.register_email_edittext);
        EditText username_edit = (EditText) root_view.findViewById(R.id.register_username_edittext);
        EditText password_Edit = ((EditText) root_view.findViewById(R.id.register_password_edittext));
        EditText secondPassword_Edit = (EditText) root_view.findViewById(R.id.register_confirm_password_edittext);
        Button checkButton = (Button) root_view.findViewById(R.id.check_username_button);
        Button registerButton = (Button) root_view.findViewById(R.id.register_button);
        pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        emptyPattern = Pattern.compile("\\s+");
        registerButton.setEnabled(false);
        secondPassword_Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password1 = password_Edit.getText().toString();
                String password2 = s.toString();
                if (password1.length()<5){
                    password_Edit.setError("Password must be at least 5 characters long!");
                    registerButton.setEnabled(false);
                }
                if (emptyPattern.matcher(password1).find()) {
                    password_Edit.setError("Password cannot contain whitespaces!");
                    registerButton.setEnabled(false);
                }
                if (!password1.equals(password2)) {
                    if (registerButton.isEnabled()){
                        registerButton.setEnabled(false);
                    }
                    secondPassword_Edit.setError("Passwords do not match");
                }
                if (!checkValues(username_edit.getText().toString(),email_edit.getText().toString(),password1,password2)) {
                    secondPassword_Edit.setError(null);
                    registerButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
                    registerButton.setEnabled(false);
                    return;
                }
                if (!registerButton.isEnabled() && !checkValues(username_edit.getText().toString(),email_edit.getText().toString(),password_Edit.getText().toString(),secondPassword_Edit.getText().toString())) {
                    registerButton.setEnabled(true);
                }
                username_edit.setError(null);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerButton.setEnabled(false);
                root_view.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        authToken = API.Authentication.register(username_edit.getText().toString(),email_edit.getText().toString(),password_Edit.getText().toString(),getContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                root_view.findViewById(R.id.progressBar1).setVisibility(View.GONE);
                                registerButton.setEnabled(true);
                                if (authToken == null){
                                    return;
                                }
                                Intent intent = new Intent();
                                intent.putExtra("authToken",authToken);
                                getActivity().setResult(Activity.RESULT_OK,intent);
                                getActivity().finish();
                            }
                        });
                    }
                }).start();
            }
        });
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email_edit.getText().toString()).matches()) {
                    email_edit.setError("Please enter a valid email address!");
                    registerButton.setEnabled(false);
                    return;
                }
                if (!checkValues(username_edit.getText().toString(),email_edit.getText().toString(),password_Edit.getText().toString(),secondPassword_Edit.getText().toString())) {
                    email_edit.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean[] resp = API.Authentication.checkUsername(username_edit.getText().toString());
                        boolean status = resp[0];
                        boolean isUsernameValid = resp[1];
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkButton.setEnabled(true);
                                if (!isUsernameValid && status){
                                    username_edit.setError("Username already taken");
                                    registerButton.setEnabled(false);
                                    return;
                                }
                                if (!status) {
                                    username_edit.setError("Unable to check username");
                                    if (!checkValues(username_edit.getText().toString(),email_edit.getText().toString(),password_Edit.getText().toString(),secondPassword_Edit.getText().toString()) && !registerButton.isEnabled()){
                                        registerButton.setEnabled(true);
                                    }
                                    return;

                                }
                                username_edit.setError(null);
                                Toast.makeText(getContext(), "Username valid!", Toast.LENGTH_SHORT).show();
                                if (!checkValues(username_edit.getText().toString(),email_edit.getText().toString(),password_Edit.getText().toString(),secondPassword_Edit.getText().toString())){
                                    registerButton.setEnabled(true);
                                }
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
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }

}