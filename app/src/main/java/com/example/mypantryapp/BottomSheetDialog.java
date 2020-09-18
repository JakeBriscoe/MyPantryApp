package com.example.mypantryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    TextView nameTextView;
    TextView brandTextView;

    String nameText;
    String brandText;
    String idText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        Button bottomModalConfirm = v.findViewById(R.id.bottomModalConfirm);
        bottomModalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), nameText + " added to pantry", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

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

            Toast.makeText(getContext(), idText + " selected", Toast.LENGTH_SHORT).show();
        }
    }

    protected void displayReceivedData(ExampleItem item) {
        nameText = item.getName();
        brandText = item.getBrand();
        idText = item.getId();
    }

}

