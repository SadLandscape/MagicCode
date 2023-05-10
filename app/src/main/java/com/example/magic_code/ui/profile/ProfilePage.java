package com.example.magic_code.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.magic_code.MainActivity;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.ui.profileSettings.ProfileSettings;

public class ProfilePage extends Fragment {

    private ProfilePageViewModel mViewModel;
    private String authToken;
    private SharedPreferences sharedPreferences;
    private AuthenticatedUser currentUser;

    public static ProfilePage newInstance() {
        return new ProfilePage();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        currentUser = API.Authentication.getUser(authToken);
        ((TextView) view.findViewById(R.id.text_username)).setText(currentUser.getUsername());
        ((TextView) view.findViewById(R.id.text_email)).setText(currentUser.getEmail());
        ((Button) view.findViewById(R.id.button_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("authToken","");
                editor.apply();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        ((Button) view.findViewById(R.id.button_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileSettings fragment = ProfileSettings.newInstance(authToken);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_profile_to_profile_settings,fragment.getArguments());
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfilePageViewModel.class);
        // TODO: Use the ViewModel
    }

}