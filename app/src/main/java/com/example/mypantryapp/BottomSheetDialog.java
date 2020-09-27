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
import androidx.fragment.app.FragmentResultListener;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    TextView nameTextView;
    TextView brandTextView;
    String nameText;
    String brandText;
    String idText;

    /**
     * Set any static data/listeners
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false); // Initialise view

        // If the 'save to pantry' button is pressed, the user should be notified that the product
        // has been added to their personal pantry.
        final Button bottomModalConfirm = v.findViewById(R.id.bottomModalConfirm);
        bottomModalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), nameText + " added to pantry", Toast.LENGTH_SHORT).show();
                // Make sure to dismiss the bottom modal.
                dismiss();
            }
        });

        return v;
    }

    /**
     * Initialise the TextViews so they can be accessed in onResume
     * @param view view
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTextView = view.findViewById(R.id.modalName);
        brandTextView = view.findViewById(R.id.modalBrand);
    }

    /**
     * Setting the text of TextViews needs to go in onResume
     */
    @Override
    public void onResume() {
        super.onResume();

        // Setup a listener to know if a barcode has just been scanned and came up with a known product.
        // If so, set the bottom modal details to correspond to the product.
        getParentFragmentManager().setFragmentResultListener("requestProductDetails", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                nameText = bundle.getString("bundleName");
                brandText = bundle.getString("bundleBrand");
                idText = bundle.getString("bundleId");
            }
        });

        // Set the TextViews.
        if (nameText != null && brandText != null) {
            nameTextView.setText(nameText);
            brandTextView.setText(brandText);
        } else if (nameText != null) {
            nameTextView.setText(nameText);
        }
    }

    /**
     * This method is called in MainActivity to receive data from AddItemFragment.
     * @param item the item selected
     */
    protected void displayReceivedData(ExampleItem item) {
        nameText = item.getName();
        brandText = item.getBrand();
        idText = item.getId();
    }

}

