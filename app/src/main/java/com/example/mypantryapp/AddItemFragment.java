package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;




public class AddItemFragment extends Fragment {
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override


    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure that bottom navigation is visible
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        View v = inflater.inflate(R.layout.fragment_add_item, container, false);

        // When the barcode icon is selected, the user should be navigated to the barcode fragment.
        ImageButton barcodeIcon = v.findViewById(R.id.barcodeIcon);
        barcodeIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack(null).commit();

                     }

        });




        //Add Manual Button Selected:
        ImageButton manualButton = v.findViewById(R.id.addManuallyButton);
        manualButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManually()).addToBackStack(null).commit();

            }
        });

        return v;
    }





}