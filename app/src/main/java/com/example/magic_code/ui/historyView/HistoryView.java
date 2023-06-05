package com.example.magic_code.ui.historyView;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.HistoryAdapter;
import com.example.magic_code.models.Board;
import com.example.magic_code.models.Modification;
import com.example.magic_code.ui.noteView.NoteFragment;

import java.util.List;

public class HistoryView extends Fragment {

    private HistoryViewViewModel mViewModel;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private String authToken;

    public static HistoryView newInstance(String note_id) {
        HistoryView fragment = new HistoryView();
        Bundle args = new Bundle();
        args.putString("note_id", note_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Navigation.findNavController(requireView()).navigateUp();
        }
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root_view = inflater.inflate(R.layout.fragment_history_view, container, false);
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("authToken","");
        RecyclerView historyRv = root_view.findViewById(R.id.history_rv);
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Loading history...");
        dialog.show();
        new Thread(()->{
            List<Modification> modifications = API.Notes.getHistory(getArguments().getString("note_id"),authToken,activity);
            activity.runOnUiThread(()->{
                HistoryAdapter adapter = new HistoryAdapter(modifications,activity,authToken);
                historyRv.setLayoutManager(new LinearLayoutManager(activity));
                historyRv.setAdapter(adapter);
                dialog.dismiss();
            });
        }).start();
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

}