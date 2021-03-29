package com.example.videostatususerlatest.Dashboard;

import android.os.Bundle;
import android.util.Log;


import com.example.videostatususerlatest.CategoriesFragment;
import com.example.videostatususerlatest.Dashboard.ui.HomeFragment;
import com.example.videostatususerlatest.Dashboard.ui.SettingsFragment;
import com.example.videostatususerlatest.Dashboard.ui.UploadFragment;
import com.example.videostatususerlatest.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class Dashboard extends AppCompatActivity {
    private static final String TAG = Dashboard.class.getSimpleName();

    FragmentManager fragmentManager;
    ChipNavigationBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setContentView(R.layout.activity_dashboard);
        bottomBar = findViewById(R.id.bottomBar);

        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, homeFragment)
                    .commit();
        }

        bottomBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.upload:
                        fragment = new CategoriesFragment();
                        break;

                    case R.id.settings:
                        fragment = new SettingsFragment();
                        break;
                }
                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .commit();
                } else {
                    Log.e(TAG, "Error in creating fragment");
                }
            }
        });

    }


}