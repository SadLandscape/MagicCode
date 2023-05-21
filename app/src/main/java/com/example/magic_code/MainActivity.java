package com.example.magic_code;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.magic_code.api.API;
import com.example.magic_code.models.AuthenticatedUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.magic_code.databinding.ActivityMainBinding;
import android.Manifest;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView navbar;
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AuthenticatedUser currentUser;
    private ActionBar actionBar;
    private String authToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("MagicPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authToken = sharedPreferences.getString("authToken","");
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.show();
        actionBar = getSupportActionBar();
        new Thread(new Runnable() {
        @Override
        public void run() {
            if (!API.Authentication.checkAuth(authToken,MainActivity.this)[1]){
                Intent intent = new Intent(MainActivity.this,AuthenticationActivity.class);
                dialog.dismiss();
                startActivityForResult(intent,128);
                return;
            }
            currentUser = API.Authentication.getUser(authToken,MainActivity.this);
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding = ActivityMainBinding.inflate(getLayoutInflater());
                    setContentView(binding.getRoot());
                    navbar = findViewById(R.id.bottom_navigation);
                    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                            R.id.boards, R.id.scan, R.id.stories,R.id.profile)
                            .build();
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                    NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                    NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 128 && data!=null) {
            authToken = data.getStringExtra("authToken");
            editor.putString("authToken",authToken);
            editor.apply();
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