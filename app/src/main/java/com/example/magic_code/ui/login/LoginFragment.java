package com.example.magic_code.ui.login;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.magic_code.R;
import com.example.magic_code.api.API;

import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private String authToken;
    private InputMethodManager imm;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        imm = ContextCompat.getSystemService(requireContext(), InputMethodManager.class);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        View rootivew = inflater.inflate(R.layout.fragment_login, container, false);
        EditText email_entry = ((EditText) rootivew.findViewById(R.id.email_edittext));
        EditText password_entry = ((EditText) rootivew.findViewById(R.id.password_edittext));
        Button login_btn = (Button) rootivew.findViewById(R.id.login_button);
        login_btn.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = password_entry.getText().toString();
                String email = email_entry.getText().toString();
                if (password.isEmpty() || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        email_entry.setError("Please enter a valid email address!");
                    }
                    if (login_btn.isEnabled()) {
                        login_btn.setEnabled(false);
                    }
                } else {
                    email_entry.setError(null);
                    login_btn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        password_entry.addTextChangedListener(watcher);
        email_entry.addTextChangedListener(watcher);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.setEnabled(false);
                imm.hideSoftInputFromWindow(rootivew.getWindowToken(),0);
                rootivew.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        authToken = API.Authentication.login(email_entry.getText().toString(),password_entry.getText().toString(),getContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rootivew.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                login_btn.setEnabled(true);
                                if (authToken == null){
                                    password_entry.setText("");
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
        return rootivew;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

}