package com.example.magic_code.ui.noteSettings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.SettingsUserAdapter;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NoteSettings extends Fragment {

    private NoteSettingsViewModel mViewModel;
    private String note_id;
    private Settings settings;

    public static NoteSettings newInstance(String note_id) {
        NoteSettings fragment = new NoteSettings();
        Bundle args = new Bundle();
        args.putString("note_id", note_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Navigation.findNavController(requireView()).navigateUp();
        }
        return true;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        note_id = getArguments().getString("note_id");
        View view = inflater.inflate(R.layout.fragment_note_settings, container, false);
        settings = API.Notes.getSettings(note_id);
        ((TextView)view.findViewById(R.id.title_edittext)).setText(settings.getTitle());
        RecyclerView userListRecyclerView = (RecyclerView) view.findViewById(R.id.users_recyclerview);
        SettingsUserAdapter adapter = new SettingsUserAdapter(settings);
        userListRecyclerView.setAdapter(adapter);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API.Notes.updateSettings(settings);
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = (getActivity()).findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.notes);
        menuItem.setChecked(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NoteSettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}