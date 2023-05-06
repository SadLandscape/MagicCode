package com.example.magic_code.ui.register;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;

public class RegisterFragment extends Fragment {

    private RegisterViewModel mViewModel;
    private String authToken;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        String email = ((TextView) view.findViewById(R.id.register_email_edittext)).getText().toString();
        String username = ((TextView) view.findViewById(R.id.register_username_edittext)).getText().toString();
        EditText password_Edit = ((EditText) view.findViewById(R.id.register_password_edittext));
        EditText secondPassword_Edit = (EditText) view.findViewById(R.id.register_confirm_password_edittext);
        Button registerButton = (Button) view.findViewById(R.id.register_button);
        registerButton.setEnabled(false);
        secondPassword_Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password1 = password_Edit.getText().toString();
                String password2 = s.toString();
                if (!password1.equals(password2)) {
                    if (registerButton.isEnabled()){
                        registerButton.setEnabled(false);
                    }
                    secondPassword_Edit.setError("Passwords do not match");
                } else {
                    secondPassword_Edit.setError(null);
                    registerButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authToken = API.Authentication.register(username,email,password_Edit.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("authToken",authToken);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }

}