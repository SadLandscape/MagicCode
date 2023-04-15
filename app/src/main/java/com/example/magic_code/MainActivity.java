package com.example.magic_code;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;

import com.example.magic_code.ui.notes.NotesPage;
import com.example.magic_code.ui.profile.ProfilePage;
import com.example.magic_code.ui.scan.ScanPage;
import com.example.magic_code.ui.stories.StoriesPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.magic_code.databinding.ActivityMainBinding;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    HashMap<Integer,Fragment> classMap = new HashMap<Integer,Fragment>(){{
        put(R.id.notes, NotesPage.newInstance());
        put(R.id.scan, ScanPage.newInstance());
        put(R.id.stories, StoriesPage.newInstance());
        put(R.id.profile, ProfilePage.newInstance());
    }};
    BottomNavigationView navbar;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navbar = findViewById(R.id.bottom_navigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.notes, R.id.scan, R.id.stories,R.id.profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    void switchMain(int id){
        getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.container,classMap.get(id),null)
            .commit();
    }
}