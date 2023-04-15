package com.example.magic_code.ui.notes;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.magic_code.CustomAdapter;
import com.example.magic_code.R;
import com.example.magic_code.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.HashMap;

public class NotesPage extends Fragment {

    private NotesPageViewModel mViewModel;

    public static NotesPage newInstance() {
        return new NotesPage();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ArrayList<HashMap<String, Object>> dataset = new ArrayList<HashMap<String, Object>>();
        for (int i = 1; i <= 50; i++) {
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("Title", "Title" + i);
            data.put("Author", "Author");
            data.put("ID","id_"+i);
            dataset.add(data);
        }
        CustomAdapter adapter = new CustomAdapter(dataset,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView1, position, v) -> {
            Toast.makeText(getActivity(), "ID: "+v.getTag(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.notes, container, false);
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotesPageViewModel.class);
        // TODO: Use the ViewModel
    }

}