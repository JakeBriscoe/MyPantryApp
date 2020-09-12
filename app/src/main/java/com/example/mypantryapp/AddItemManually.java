package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddItemManually extends Fragment {


    private Spinner spinner;
    private DatabaseReference mDatabase;

    EditText enterIngredientsText;
    String updateIngredientsText;

    private static final String TAG = "AddItemManually";
    private static final String KEY_NAME= "name";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_BARCODE = "barcodeNum";
    private static final String KEY_SHELFLIFE = "shelfLife";
    private static final String KEY_VOLUME = "volume";
//    private  static final String KEY_CATEGORY = "categoryName";
//    private static final String KEY_DIETARY = "dietaryType";
//    private static final String KEY_ALLERGY = "allergens";


    private EditText editTextName;
    private EditText editTextBrand;
    private EditText editTextBarcode;
    private EditText editTextShelfLife;
    private EditText editTextQuantity;
//    private Spinner spinnerCategory;
//    private Spinner spinnerDietary;
//    private Spinner spinnerAllergy;

    private Button saveButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_add_item_manually, container, false);

        // Show bottom navigation
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        // When the camera icon is selected, the user should be navigated to the scan ingredients fragment.
        final TextView ingredientsTitle = v.findViewById(R.id.ingredientsTitle);
        ingredientsTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] textLocation = new int[2];
                    ingredientsTitle.getLocationOnScreen(textLocation);
                    if (event.getRawX() >= textLocation[0] + ingredientsTitle.getWidth() - ingredientsTitle.getTotalPaddingRight()){

                        // Right drawable was tapped
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanIngredientsFragment()).addToBackStack(null).commit();
                        return true;
                    }
                }
                return true;
            }
        });


        return v;

    }
    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup any handles to view objects here
        editTextName =  (EditText) view.findViewById(R.id.productNameInputMan);
        editTextBrand =  (EditText) view.findViewById(R.id.brandInputMan);
        editTextBarcode =  (EditText) view.findViewById(R.id.barcodeInputMan);
        editTextShelfLife = (EditText) view.findViewById(R.id.shelfLifeInputMan);
        editTextQuantity = (EditText) view.findViewById(R.id.QuantityInputMan);

        enterIngredientsText = (EditText) view.findViewById(R.id.enterIngredientsText);

//        spinnerCategory = (Spinner) findViewById(R.id.CategorySpinMan);
//        spinnerDietary = (Spinner) findViewById(R.id.DietSpinMan);
//        spinnerAllergy = (Spinner) findViewById(R.id.AllergenSpinMan);

        Button save_manually = getActivity().findViewById(R.id.button_save_man);
        save_manually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String brand = editTextBrand.getText().toString();
                Integer barcode = Integer.parseInt(editTextBarcode.getText().toString());
                Integer shelfLife = Integer.parseInt(editTextShelfLife.getText().toString());
                String volume = editTextQuantity.getText().toString();
//              String category = spinnerCategory.getSelectedItem().toString();
//              String dietary =spinnerDietary.getSelectedItem().toString()
//              String allergy =spinnerAllergy.getSelectedItem().toString();


                Map<String, Object> item = new HashMap<>();
                item.put(KEY_NAME, name);
                item.put(KEY_BRAND, brand);
                item.put(KEY_BARCODE, barcode);
                item.put(KEY_SHELFLIFE, shelfLife);
                item.put(KEY_VOLUME, volume);
                //        item.put(KEY_CATEGORY, category);
                //        item.put(KEY_DIETARY, dietary);
                //        item.put(KEY_ALLERGY, allergy);

                db.collection("products").document().set(item)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
            }
        });

    }

    /**
     * Update the ingredients field in onResume to ensure it is displayed
     */
    @Override
    public void onResume() {
        super.onResume();

        if (updateIngredientsText != null) {
            enterIngredientsText.setText(updateIngredientsText);
        }
    }

    /**
     * Here to get data from another fragment
     * @param message the result of camera in ScanIngredientsFragment
     */
    protected void displayReceivedData(String message) {
        updateIngredientsText = message;
    }

}
