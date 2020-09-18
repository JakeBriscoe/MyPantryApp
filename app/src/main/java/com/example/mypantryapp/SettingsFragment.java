package com.example.mypantryapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

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

            Button save = v.findViewById(R.id.saveDetails);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                    Toast.makeText(getContext(), "Your details are saved!", Toast.LENGTH_SHORT).show();

                    navigationView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.nav_pantry1);
                }
            });

            this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();

        } else {
            // Otherwise, show the nav header and drawer.
            navigationView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            Button save = v.findViewById(R.id.saveDetails);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Your details are saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Toolbar mActionBarToolbar = getActivity().findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Settings");

        return v;
    }
}