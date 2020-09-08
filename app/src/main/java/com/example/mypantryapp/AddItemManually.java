package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
    private static final String KEY_QUANTITY = "quantity";
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
   ArrayAdapter array_adapt;
    FragmentActivity listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
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
                String barcode = editTextBarcode.getText().toString();
                String shelfLife = editTextShelfLife.getText().toString();
                String quantity = editTextQuantity.getText().toString();
//              String category = spinnerCategory.getSelectedItem().toString();
//              String dietary =spinnerDietary.getSelectedItem().toString()
//              String allergy =spinnerAllergy.getSelectedItem().toString();


                Map<String, Object> item = new HashMap<>();
                item.put(KEY_NAME, name);
                item.put(KEY_BRAND, brand);
                item.put(KEY_BARCODE, barcode);
                item.put(KEY_SHELFLIFE, shelfLife);
                item.put(KEY_QUANTITY, quantity);
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
        updateIngredientsText = transformMessage(message);
    }

    /**
     * Modify the message passed so that unnecessary words are ignored
     *
     * ASSUMPTIONS
     * ------------------------
     * The format will include "Ingredients ...", "May contain ..." and "Contains ..."
     * There is only one occurrence of 'Ingredients' in the snapshot taken
     * The camera picks up the full stops
     *
     * TODO: consider "May be present: ..."
     *
     * @param data the result from scan ingredients
     * @return the modified string
     */
    protected String transformMessage(String data) {

        // Start string after "Ingredients"
        // Get two substrings: "Contains ..." and "May contain ..." based on occurrence and consequent full stop.
        // If "Contains ..." and "May contain ..." doesn't exist, return without altering
        // Otherwise, get rid of everything after the first occurrence, and concatenate the two substrings

        int iIngredients; // This should be at the last occurrence of 'Ingredients'

        // Need to consider the case where the word ingredients is in capitals
        int iLower = data.lastIndexOf("Ingredients");
        int iUpper = data.lastIndexOf("INGREDIENTS");
        if (iLower > iUpper) {
            iIngredients = iLower;
        } else {
            iIngredients = iUpper;
        }

        // If there is no occurrence of 'Ingredients', then there are no ingredients to identify
        if (iIngredients == -1) {
            Toast.makeText(getActivity(), "No ingredients identified", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Fix formatting issues
        // Need to account for the word 'Ingredients'
        String ingredients = data.substring(iIngredients + 11).replace("\n", " ");

        // Start our result at the first occurrence of a letter
        // To account for cases such as "Ingredients: ..." as well as "Ingredients ..."
        ingredients = ingredients.substring(findFirstLetterPosition(ingredients));

        // Get indexes of "May contain ..." and "Contains ..."
        int iMayContain = ingredients.indexOf("May contain");
        int iContains = ingredients.indexOf("Contains");
        String mayContain;
        String contains;

        // Get substrings which contain the "May contain ..." and "Contains ..." information.
        // This is done by taking a substring of ingredients from the trigger word until the next full stop.
        // Need to make sure that the indices are viable.
        if (iMayContain == -1) {
            mayContain = "";
        } else {
            mayContain = ingredients.substring(iMayContain, ingredients.indexOf(".", iMayContain) + 1);
        }

        if (iContains == -1) {
            contains = "";
        } else {
            contains = ingredients.substring(iContains, ingredients.indexOf(".", iContains) + 1);
        }

        // Now decide where to finish the ingredients string based on "Contains ..." and "May contain ..."
        if (iMayContain < iContains && iMayContain != -1) {
            // Both exist, and "May contain ..." comes first
            ingredients = ingredients.substring(0, iMayContain);
        } else if (iContains < iMayContain && iContains != -1) {
            // Both exist, and "Contains ..." comes first
            ingredients = ingredients.substring(0, iContains);
        } else if (iMayContain == -1 && iContains == -1) {
            // Neither exist
            ingredients = ingredients.substring(0, ingredients.indexOf("."));
        } else if (iMayContain != -1) {
            // Only "May contain ..." exists
            ingredients = ingredients.substring(0, iMayContain);
        } else {
            // Only "Contains ..." exists
            ingredients = ingredients.substring(0, iContains);
        }

        return ingredients + "\n\n" + contains + "\n\n" + mayContain;
    }

    /**
     * Helper function
     * @param input the message
     * @return Occurrence of first alphabetical character
     */
    public int findFirstLetterPosition(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isLetter(input.charAt(i))) {
                return i;
            }
        }
        return -1; // not found
    }


}