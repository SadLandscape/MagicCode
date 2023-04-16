package com.example.magic_code.ui.notes;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.magic_code.CustomAdapter;
import com.example.magic_code.R;
import com.example.magic_code.models.Note;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.example.magic_code.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
            data.put("ID","id_"+i);
            dataset.add(data);
        }
        CustomAdapter adapter = new CustomAdapter(dataset,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView1, position, v) -> Toast.makeText(getActivity(), "ID: "+v.getTag(), Toast.LENGTH_SHORT).show());
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    assert childView != null;
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        Note clickedNote = new Note(dataset.get(position));
                        NoteFragment detailFragment = NoteFragment.newInstance(clickedNote.getId());
                        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.nav_host_fragment_activity_main, detailFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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