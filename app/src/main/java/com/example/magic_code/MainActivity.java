package com.example.magic_code;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Invite;
import com.example.magic_code.ui.boardsView.boardsView;
import com.example.magic_code.ui.invites.InvitesPage;
import com.example.magic_code.ui.noteView.NoteFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.magic_code.databinding.ActivityMainBinding;
import com.google.android.material.shape.MaterialShapeDrawable;

import android.Manifest;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navbar;
    NavController navController;
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Set<String> pendingInvites;
    private AuthenticatedUser currentUser;
    private ActionBar actionBar;
    private String authToken;
    public void showInviteDialog(Invite invite){
        String inviteId = invite.getId();
        String boardName = invite.getBoardName();
        String inviterUsername = invite.getInvUsername();
        String inviterDisplayName = invite.getInvDisplayName();
        if (!inviteId.equals("") && currentUser!=null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attention");
            builder.setMessage("You were invited to \""+boardName+"\" by @"+inviterUsername+" (AKA "+inviterDisplayName+"), if you want to join please go to the invites tab");
            builder.setPositiveButton("Ok", (dialog_, which) -> {
                editor.putString("pendingInvite","");
                editor.commit();
                dialog_.dismiss();
            });
            AlertDialog dialog1 = builder.create();
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
            dialog1.setCancelable(false);
            dialog1.show();
        }
    }

    public void initApp(@Nullable Intent data){
        sharedPreferences = getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        pendingInvites = sharedPreferences.getStringSet("invites",new HashSet<>());
        authToken = sharedPreferences.getString("authToken", "");
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.invite_dialog_bg);
        dialog.setCancelable(false);
        dialog.show();
        actionBar = getSupportActionBar();
        new Thread(() -> {
            if (!API.Authentication.checkAuth(authToken, MainActivity.this)[1]) {
                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                dialog.dismiss();
                startActivity(intent);
                return;
            }
            currentUser = API.Authentication.getUser(authToken, MainActivity.this);
            dialog.dismiss();
            if (data !=null) {
                Uri uri = data.getData();
                if (uri != null && Intent.ACTION_VIEW.equals(data.getAction())) {
                    List<String> parameters = uri.getPathSegments();
                    if (parameters == null) {
                        return;
                    }
                    if (parameters.size() < 2) {
                        return;
                    }
                    String location = parameters.get(parameters.size() - 2);
                    String inviteId = parameters.get(parameters.size() - 1);
                    if (inviteId.equals("")) {
                        return;
                    }
                    if (location.equals("invites")) {
                        new Thread(() -> {
                            Invite invite = API.Invites.validateInvite(inviteId, this);
                            if (invite == null){
                                editor.putString("pendingInvite","");
                                editor.commit();
                                return;
                            }
                            if (!pendingInvites.contains(inviteId)) {
                                Set<String> invites_ = new HashSet<>(pendingInvites);
                                invites_.add(inviteId);
                                editor.putStringSet("invites", invites_);
                                editor.commit();
                                runOnUiThread(()->{
                                    showInviteDialog(invite);
                                    updateBadge();
                                });
                            }
                        }).start();
                    }
                }
            }
            runOnUiThread(() -> {
                binding = ActivityMainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                String pendingInvite = sharedPreferences.getString("pendingInvite", "");
                if (!pendingInvite.equals("")) {
                    new Thread(() -> {
                        Invite invite = API.Invites.validateInvite(pendingInvite, this);
                        if (invite != null) {
                            runOnUiThread(() -> showInviteDialog(invite));
                        }
                    }).start();
                }
                navbar = findViewById(R.id.bottom_navigation);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.boards, R.id.scan, R.id.invitations, R.id.profile)
                        .build();
                navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
                BadgeDrawable badge = navbar.getOrCreateBadge(R.id.invitations);
                if (pendingInvites.size() != 0) {badge.setNumber(pendingInvites.size()); badge.setVisible(true);} else {badge.setVisible(false);}
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            });
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        initApp(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            List<String> parameters = uri.getPathSegments();
            if (parameters == null){
                return;
            }
            if (parameters.size() < 2){
                return;
            }
            String location = parameters.get(parameters.size() - 2);
            String inviteId = parameters.get(parameters.size()-1);
            if (inviteId.equals("")){
                return;
            }
            if (location.equals("invites")){
                new Thread(()->{
                    Invite invite = API.Invites.validateInvite(inviteId, this);
                    if (invite!=null) {
                        if (!pendingInvites.contains(inviteId)) {
                            Set<String> invites_ = new HashSet<>(pendingInvites);
                            invites_.add(inviteId);
                            editor.putStringSet("invites", invites_);
                            editor.commit();
                        }
                        runOnUiThread(() -> {
                            if (!pendingInvites.contains(inviteId)) {
                                Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                                Fragment currentFragment = navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
                                if (currentFragment instanceof InvitesPage) {
                                    InvitesPage invitesPage = (InvitesPage) currentFragment;
                                    if (invitesPage.adapter != null) {
                                        invitesPage.adapter.addInvite(invite);
                                    }
                                }
                            }
                            showInviteDialog(invite);
                            updateBadge();
                        });
                    }
                }).start();
            }
            if (currentUser==null) {
                initApp(null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Please enable permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        Fragment currentFragment = navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (currentFragment instanceof NoteFragment) {
            NoteFragment noteView = (NoteFragment) currentFragment;
            noteView.backPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navbar !=null) {
            updateBadge();
        }
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    public void updateBadge(){
        if (navbar == null){
            return;
        }
        pendingInvites = sharedPreferences.getStringSet("invites",new HashSet<>());
        BadgeDrawable badge = navbar.getOrCreateBadge(R.id.invitations);
        if (pendingInvites.size() != 0) {badge.setNumber(pendingInvites.size()); badge.setVisible(true);} else {badge.setVisible(false);}
    }
}