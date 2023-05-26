package com.example.magic_code.ui.noteSettings;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.SettingsUserAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Category;
import com.example.magic_code.models.Note;
import com.example.magic_code.models.Settings;
import com.example.magic_code.models.User;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.w3c.dom.Text;

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
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        note_id = getArguments().getString("note_id");
        board = (Board) getArguments().getSerializable("board");
        View view = inflater.inflate(R.layout.fragment_note_settings, container, false);
        EditText title_edit = view.findViewById(R.id.title_edittext);
        new Thread(()-> {
            note = API.Notes.getNote(note_id,authToken,requireContext());
            List<Category> categories = API.Categories.getCategories(board.getId(),authToken,requireContext());
            selectedCategory = note.getCategory();
            requireActivity().runOnUiThread(() -> {
                List<String> categoryNames = new ArrayList<>();
                for (Category category : categories) {
                    categoryNames.add(category.getTitle());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                MaterialAutoCompleteTextView categoryDropdown = view.findViewById(R.id.choose_category_dropdown);
                categoryDropdown.setText(note.getCategory().getTitle());
                categoryDropdown.setAdapter(adapter);
                categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> selectedCategory = categories.get(position));
                view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(()->{
                            API.Notes.updateNote(note_id,title_edit.getText().toString(),selectedCategory,authToken,requireContext());
                            requireActivity().runOnUiThread(()->{
                                Navigation.findNavController(requireView()).navigateUp();
                            });
                        }).start();
                    }
                });
                ((TextView)view.findViewById(R.id.title_edittext)).setText(note.getTitle());
            });
        }).start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = (getActivity()).findViewById(R.id.bottom_navigation);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.boards);
        menuItem.setChecked(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NoteSettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}