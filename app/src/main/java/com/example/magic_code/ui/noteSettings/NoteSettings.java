package com.example.magic_code.ui.noteSettings;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

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
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Note;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import java.util.ArrayList;
import java.util.List;

public class NoteSettings extends Fragment {

    private NoteSettingsViewModel mViewModel;
    private String note_id;
    private Board board;
    private Note note;
    private SharedPreferences sharedPreferences;
    private Category selectedCategory;
    private String authToken;
    private FragmentActivity activity;

    public static NoteSettings newInstance(String note_id, Board board) {
        NoteSettings fragment = new NoteSettings();
        Bundle args = new Bundle();
        args.putString("note_id", note_id);
        args.putSerializable("board",board);
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
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        note_id = getArguments().getString("note_id");
        board = (Board) getArguments().getSerializable("board");
        View view = inflater.inflate(R.layout.fragment_note_settings, container, false);
        Button saveBtn = view.findViewById(R.id.save_button);
        EditText title_edit = view.findViewById(R.id.title_edittext);
        title_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String note_title = title_edit.getText().toString();
                if (TextUtils.isEmpty(note_title)) {
                    title_edit.setError("This field is required!");
                    saveBtn.setEnabled(false);
                } else {
                    saveBtn.setEnabled(true);
                    title_edit.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        new Thread(()-> {
            note = API.Notes.getNote(note_id,authToken,activity);
            if (note == null){
                Navigation.findNavController(requireView()).navigateUp();
                return;
            }
            List<Category> categories = API.Categories.getCategories(board.getId(),authToken,activity);
            selectedCategory = note.getCategory();
            activity.runOnUiThread(() -> {
                List<String> categoryNames = new ArrayList<>();
                for (Category category : categories) {
                    categoryNames.add(category.getTitle());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, categoryNames);
                MaterialAutoCompleteTextView categoryDropdown = view.findViewById(R.id.choose_category_dropdown);
                categoryDropdown.setText(note.getCategory().getTitle());
                categoryDropdown.setAdapter(adapter);
                categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> selectedCategory = categories.get(position));
                saveBtn.setOnClickListener(view12 -> new Thread(()->{
                    API.Notes.updateNote(note_id,title_edit.getText().toString(),selectedCategory,authToken,activity);
                    activity.runOnUiThread(()->{
                        Navigation.findNavController(requireView()).navigateUp();
                    });
                }).start());
                ((TextView)view.findViewById(R.id.title_edittext)).setText(note.getTitle());
            });
        }).start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NoteSettingsViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

}