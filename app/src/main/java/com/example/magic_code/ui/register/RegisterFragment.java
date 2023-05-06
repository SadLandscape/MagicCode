package com.example.magic_code.ui.register;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        String password = ((TextView) view.findViewById(R.id.register_password_edittext)).toString();
        ((Button) view.findViewById(R.id.register_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authToken = API.Authentication.register(username,email,password);
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