package com.example.mypantryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    TextView nameTextView;
    TextView brandTextView;
    String nameText;
    String brandText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.modalName);
        brandTextView = view.findViewById(R.id.modalBrand);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (nameText != null && brandText != null) {
            nameTextView.setText(nameText);
            brandTextView.setText(brandText);
        }
    }

    protected void displayReceivedData(String name, String brand) {
        nameText = name;
        brandText = brand;
    }

}

