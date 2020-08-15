package com.example.mypantryapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddItemManually extends Fragment {

    /*TAG and KEYS

    private static final String TAG = "AddItemManually";
    private static final String KEY_NAME= "name";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_QUANTITY = "quantity";
    private  static final String KEY_CATEGORY = "category";
    private static final String KEY_DIETARY = "dietary";


    private EditText editTextName;
    private EditText editTextBrand;
    private EditText editTextBarcode;
    private EditText editTextDescription;
    private EditText editTextQuantity;
    private EditText editTextCategory;
    private EditText editTextDietary;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*/getting text from input text fields
                editTextName = getActivity().findViewById(R.id.productNameInputMan);
                editTextBrand = getActivity().findViewById(R.id.brandInputMan);
                editTextBarcode = getActivity().findViewById(R.id.barcodeInputMan);
                editTextDescription = getActivity().findViewById(R.id.descriptionInputMan);
                editTextQuantity = getActivity().findViewById(R.id.QuantityInputMan);
                editTextCategory = getActivity().findViewById(R.id.CategorySpinMan);
                editTextDietary = getActivity().findViewById(R.id.DietSpinMan);
        */
        // Show bottom navigation
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        return inflater.inflate(R.layout.fragment_add_item_manually, container, false);



    }

    /*
    public void addItemMan(View v){
        String name = editTextName.getText().toString();
        String  brand = editTextBrand.getText().toString();
        String barcode = editTextBarcode.getText().toString();
        String description = editTextDescription.getText().toString();
        String quantity = editTextQuantity.getText().toString();
        String category = editTextCategory.getText().toString();
        String dietary = editTextDietary.getText().toString();

        Map<String, Object> item = new HashMap<>();
        item.put(KEY_NAME, name);
        item.put(KEY_BRAND, brand);
        item.put(KEY_BARCODE, barcode);
        item.put(KEY_DESCRIPTION, description);
        item.put(KEY_QUANTITY, quantity);
        item.put(KEY_CATEGORY, category);
        item.put(KEY_DIETARY, dietary);

        db.collection("products").document().set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Item Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });



    }*/
}