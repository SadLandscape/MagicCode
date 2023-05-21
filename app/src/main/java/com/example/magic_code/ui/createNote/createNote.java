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

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.ui.boardView.boardView;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class createNote extends Fragment {

    private CreateNoteViewModel mViewModel;
    private SharedPreferences sharedPreferences;
    private AuthenticatedUser currentUser;
    private Category selectedCategory;
    private String authToken;

    public static createNote newInstance(ArrayList<Category> categories) {
        createNote fragment = new createNote();
        Bundle args = new Bundle();
        args.putParcelableArrayList("category", categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ArrayList<Category> categories = getArguments().getParcelableArrayList("category");
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View view = inflater.inflate(R.layout.fragment_create_note, container, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = API.Authentication.getUser(authToken,getContext());
            }
        }).start();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
        MaterialAutoCompleteTextView categoryDropdown = view.findViewById(R.id.choose_category_dropdown);
        categoryDropdown.setAdapter(adapter);
        categoryDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }
        });

        Button createButton = view.findViewById(R.id.create_note_button);
        EditText title_edit = view.findViewById(R.id.note_title);
        EditText note_description = view.findViewById(R.id.rich_text_box);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Creating note...");
        createButton.setEnabled(false);
        categoryDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String selectedValue = categoryDropdown.getText().toString().trim();
                if (TextUtils.isEmpty(selectedValue)) {
                    categoryDropdown.setError("This field is required!");
                    createButton.setEnabled(false);
                } else {
                    createButton.setEnabled(true);
                    categoryDropdown.setError(null);
                }
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createButton.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        API.Notes.createNote(title_edit.getText().toString(), note_description.getText().toString(),selectedCategory.getId(), authToken, getContext());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Navigation.findNavController(requireView()).navigateUp();
                            }
                        });
                        //TODO add board title
                    }
                }).start();
                createButton.setEnabled(true);
            }
        });
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Navigation.findNavController(requireView()).navigateUp();
            return true;
        }
        return true;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateNoteViewModel.class);
        // TODO: Use the ViewModel
    }

}