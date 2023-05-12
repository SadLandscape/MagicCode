package com.example.magic_code.ui.notes;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.magic_code.MainActivity;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.CustomAdapter;
import com.example.magic_code.R;
import com.example.magic_code.models.Note;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.example.magic_code.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotesPage extends Fragment {

    public static NotesPage newInstance() {
        return new NotesPage();
    }
    private SharedPreferences sharedPreferences;
    private String authToken;
    private Dialog dialog;
    CustomAdapter adapter;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Note> newData = API.Authentication.getNotes(authToken,getContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(newData);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        sharedPreferences = getActivity().getSharedPreferences("MagicPrefs", getContext().MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Note> noteList = API.Authentication.getNotes(authToken,getContext());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        adapter = new CustomAdapter(noteList,getContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);
                        view.findViewById(R.id.floating_action_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NotesPage fragment = NotesPage.newInstance();
                                NavController navController = Navigation.findNavController(requireView());
                                navController.navigate(R.id.action_notes_to_create_notes,fragment.getArguments());
                            }
                        });
                        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                Note clickedNote = noteList.get(position);
                                NoteFragment detailFragment = NoteFragment.newInstance(clickedNote.getId());
                                NavController navController = Navigation.findNavController(requireView());
                                navController.navigate(R.id.action_notes_to_detailed_note_view,detailFragment.getArguments());
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes, container, false);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Loading notes...");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NotesPageViewModel mViewModel = new ViewModelProvider(this).get(NotesPageViewModel.class);
        // TODO: Use the ViewModel
    }

}