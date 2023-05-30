package com.example.magic_code;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.net.Uri;
import android.os.Bundle;

import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.example.magic_code.models.Invite;
import com.example.magic_code.ui.boardsView.boardsView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.magic_code.databinding.ActivityMainBinding;
import android.Manifest;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView navbar;
    NavController navController;
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
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
            builder.setMessage("You were invited to \""+boardName+"\" by @"+inviterUsername+" (AKA "+inviterDisplayName+"), would you like to join?");
            builder.setPositiveButton("Yes", (dialog_, which) -> {
                new Thread(()->{
                    boolean status = API.Invites.acceptInvite(inviteId,authToken,this);
                    runOnUiThread(()->{
                        if (status){
                            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                            if (navHostFragment == null){
                                return;
                            }
                            Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                            if (currentFragment instanceof boardsView){
                                boardsView cFragment = (boardsView) currentFragment;
                                cFragment.refreshBoards();
                            }
                        }
                        editor.putString("pendingInvite","");
                        editor.commit();
                        dialog_.dismiss();
                    });
                }).start();
            });
            builder.setNegativeButton("No", (dialog_,which)->{
                editor.putString("pendingInvite","");
                editor.commit();
            });
            AlertDialog dialog1 = builder.create();
            dialog1.setCancelable(false);
            dialog1.show();
        }
    }

    public void initApp(){
        sharedPreferences = getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authToken = sharedPreferences.getString("authToken", "");
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.show();
        String pendingInvite = sharedPreferences.getString("pendingInvite", "");
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

            runOnUiThread(() -> {
                binding = ActivityMainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
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
                        R.id.boards, R.id.scan, R.id.stories, R.id.profile)
                        .build();
                navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            });
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            List<String> parameters = uri.getPathSegments();
            String location = parameters.get(parameters.size() - 2);
            String inviteId = parameters.get(parameters.size()-1);
            if (inviteId.equals("")){
                return;
            }
            if (location.equals("invites")){
                new Thread(()->{
                    Invite invite = API.Invites.validateInvite(inviteId, this);
                    if (invite!=null) {
                        editor.putString("pendingInvite", inviteId);
                        editor.commit();
                        runOnUiThread(() -> {
                            showInviteDialog(invite);
                        });
                    }
                }).start();
            }
            if (currentUser==null) {
                initApp();
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

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}