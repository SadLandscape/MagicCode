package com.example.magic_code.ui.invites;

import androidx.fragment.app.FragmentActivity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.magic_code.MainActivity;
import com.example.magic_code.R;
import com.example.magic_code.api.API;
import com.example.magic_code.classes.InvitesAdapter;
import com.example.magic_code.models.Invite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvitesPage extends Fragment {

    private InvitesPageViewModel mViewModel;
    private MainActivity activity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_all){
            editor.putStringSet("invites",new HashSet<>());
            editor.commit();
            if (adapter != null){
                adapter.updateData(new ArrayList<>());
            }
            activity.updateBadge();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String authToken;
    public InvitesAdapter adapter;

    public static InvitesPage newInstance() {
        return new InvitesPage();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_invitations,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sharedPreferences = activity.getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authToken = sharedPreferences.getString("authToken","");
        View root_view = inflater.inflate(R.layout.invites, container, false);
        Set<String> invitesStringList = new HashSet<>(sharedPreferences.getStringSet("invites",new HashSet<>()));
        List<Invite> invites = new ArrayList<>();
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.status_text)).setText("Fetching Invites...");
        dialog.show();
        new Thread(()->{
            for (String inviteId:invitesStringList){
                Invite invite = API.Invites.validateInvite(inviteId,activity);
                if (invite == null){
                    Set<String> invites_ = new HashSet<>(invitesStringList);
                    invites_.remove(inviteId);
                    editor.putStringSet("invites",invites_);
                    editor.commit();
                    continue;
                }
                invites.add(invite);
            }
            activity.runOnUiThread(()->{
                RecyclerView invitesRv = root_view.findViewById(R.id.invites_rv);
                adapter = new InvitesAdapter(invites,activity,authToken);
                invitesRv.setLayoutManager(new LinearLayoutManager(activity));
                invitesRv.setAdapter(adapter);
                dialog.dismiss();
            });
        }).start();
        return root_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InvitesPageViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
    }

}