package com.example.mypantryapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Boolean isFirstRun = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.GONE);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        if (isFirstRun) {
            // If it is the first run, hide the nav header and drawer.
            navigationView.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);

            this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();

        } else {
            // Otherwise, show the nav header and drawer.
            navigationView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}