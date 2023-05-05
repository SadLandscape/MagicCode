package com.example.magic_code.ui.notes;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.magic_code.classes.CustomAdapter;
import com.example.magic_code.R;
import com.example.magic_code.models.Note;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.example.magic_code.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.HashMap;

public class NotesPage extends Fragment {

    public static NotesPage newInstance() {
        return new NotesPage();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ArrayList<HashMap<String, Object>> dataset = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("Title", "Title" + i);
            data.put("Author", "Author");
            data.put("ID","40880f57-8655-487a-b31a-fda5123c442c");
            data.put("ShareToken","40880f57-8655-487a-b31a-fda5123c442c");
            dataset.add(data);
        }
        CustomAdapter adapter = new CustomAdapter(dataset,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Note clickedNote = new Note(dataset.get(position));
                NoteFragment detailFragment = NoteFragment.newInstance(clickedNote.getId());
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_notes_to_detailed_note_view,detailFragment.getArguments());
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notes, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NotesPageViewModel mViewModel = new ViewModelProvider(this).get(NotesPageViewModel.class);
        // TODO: Use the ViewModel
    }

}