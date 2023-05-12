package com.example.magic_code.ui.createNote;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.ui.noteView.NoteFragment;

public class createNote extends Fragment {

    private CreateNoteViewModel mViewModel;
    private SharedPreferences sharedPreferences;
    private AuthenticatedUser currentUser;
    private String authToken;

    public static createNote newInstance() {
        return new createNote();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View view = inflater.inflate(R.layout.fragment_create_note, container, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = API.Authentication.getUser(authToken,getContext());
            }
        }).start();
        Button createButton = view.findViewById(R.id.create_note_button);
        EditText title_edit = view.findViewById(R.id.note_title);
        EditText note_description = view.findViewById(R.id.rich_text_box);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Creating note...");
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createButton.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        API.Notes.createNote(title_edit.getText().toString(), note_description.getText().toString(), authToken, getContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Navigation.findNavController(requireView()).navigateUp();
                            }
                        });
                    }
                }).start();
                createButton.setEnabled(true);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateNoteViewModel.class);
        // TODO: Use the ViewModel
    }

}