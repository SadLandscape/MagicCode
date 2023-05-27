package com.example.magic_code.ui.createNote;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Category;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import java.util.ArrayList;
import java.util.List;

public class createNote extends Fragment {

    private AuthenticatedUser currentUser;
    private Category selectedCategory;
    private String authToken;
    private FragmentActivity activity;

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
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        View view = inflater.inflate(R.layout.fragment_create_note, container, false);
        new Thread(() -> currentUser = API.Authentication.getUser(authToken,activity)).start();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, categoryNames);
        MaterialAutoCompleteTextView categoryDropdown = view.findViewById(R.id.choose_category_dropdown);
        categoryDropdown.setAdapter(adapter);
        categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> selectedCategory = categories.get(position));

        Button createButton = view.findViewById(R.id.create_note_button);
        EditText title_edit = view.findViewById(R.id.note_title);
        EditText note_description = view.findViewById(R.id.rich_text_box);
        Dialog dialog = new Dialog(activity);
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
                    if (!TextUtils.isEmpty(title_edit.getText().toString().trim())) {
                        createButton.setEnabled(true);
                    }
                    categoryDropdown.setError(null);
                }
            }
        });
        title_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String note_title = title_edit.getText().toString();
                if (TextUtils.isEmpty(note_title)) {
                    title_edit.setError("This field is required!");
                    createButton.setEnabled(false);
                } else {
                    if (!TextUtils.isEmpty(categoryDropdown.getText().toString().trim())) {
                        createButton.setEnabled(true);
                    }
                    title_edit.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        createButton.setOnClickListener(view12 -> {
            createButton.setEnabled(false);
            new Thread(() -> {
                API.Notes.createNote(title_edit.getText().toString(), note_description.getText().toString(),selectedCategory.getId(), authToken, activity);
                activity.runOnUiThread(() -> {
                    dialog.dismiss();
                    Navigation.findNavController(requireView()).navigateUp();
                });
                //TODO add board title
            }).start();
            createButton.setEnabled(true);
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
        CreateNoteViewModel mViewModel = new ViewModelProvider(this).get(CreateNoteViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

}