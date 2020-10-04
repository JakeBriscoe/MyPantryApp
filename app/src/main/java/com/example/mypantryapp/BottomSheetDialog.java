package com.example.mypantryapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private TextView nameTextView;
    private TextView brandTextView;
    private TextView dietTitleTextView;
    private TextView dietWarningsTextView;
    private String nameText;
    private String brandText;
    private String idText;
    private String ingredients;
    private String dietWarningsText;

    private CheckIngredients checkIngredients = new CheckIngredients();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        dietTitleTextView = view.findViewById(R.id.modalDietTitle);
        dietWarningsTextView = view.findViewById(R.id.modalDietWarnings);
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

        // Get the ingredients from the database using the product id
        DocumentReference docRef =  db.collection("products").document(idText);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ingredients = (String) document.get("ingredients");

                        if (ingredients != null) {
                            checkIngredients.setIngredients(ingredients);
                            dietWarningsText = checkIngredients.checkIngredients();

                            if (dietWarningsText.equals("No dietary warnings")) {
                                // Then diet is compatible
                                dietTitleTextView.setText("Compatible with your dietary preferences!");
                            } else {
                                // Then diet is not compatible
                                dietTitleTextView.setText("Incompatible with your dietary preferences:");
                                dietTitleTextView.setTypeface(null, Typeface.BOLD);
                                dietWarningsTextView.setVisibility(View.VISIBLE);
                                dietWarningsTextView.setText(dietWarningsText);
                            }
                        }
//                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

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

