package com.info301.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.FragmentResultListener;

import com.info301.mypantryapp.domain.PantryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class AddItemManuallyFragment extends Fragment {


    private Spinner spinner; //TODO
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText enterIngredientsText;
    String updateIngredientsText;
    String productdocId;
    Integer newQ;
    String pantryRef;


    TextView viewDietaryWarning;

    private static final String TAG = "AddItemManually";
    //Product map
    private static final String KEY_NAME= "name";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_BARCODE = "barcodeNum";
    private static final String KEY_SHELFLIFE = "shelfLife";
    private static final String KEY_VOLUME = "volume";

    private static final String KEY_INGREDIENTS = "ingredients";

    //Pantry Product Map
    private static final String KEY_PRODUCTREF = "productRef";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_EXPIRY = "expiry";
    private static final String KEY_LOCATION = "location";
    private  static final String KEY_CATEGORY = "categoryName";

//    private static final String KEY_DIETARY = "dietaryType";
//    private static final String KEY_ALLERGY = "allergens";

    private EditText editTextName;
    private EditText editTextBrand;
    private EditText editTextBarcode;
    private EditText editTextShelfLife;
    private EditText editTextVolume;
    private EditText editTextQuantity;
//    private Spinner spinnerCategory;
//    private Spinner spinnerDietary;
//    private Spinner spinnerAllergy;


    private CheckIngredients checkIngredients = new CheckIngredients();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Set any static listeners and data
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_add_item_manually, container, false);

        // POSSIBLY NOT NEEDED. Show bottom navigation.
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);


        // When the camera icon for 'Ingredients' is selected, the user should be navigated to the scan ingredients fragment.
        // First need to set a listener to the whole view. The camera icon is on the far right of the view.
        final TextView ingredientsTitle = v.findViewById(R.id.ingredientsTitle);
        ingredientsTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] textLocation = new int[2];
                    ingredientsTitle.getLocationOnScreen(textLocation);
                    if (event.getRawX() >= textLocation[0] + ingredientsTitle.getWidth() - ingredientsTitle.getTotalPaddingRight()){

                        // Right drawable was tapped
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanIngredientsFragment()).addToBackStack("ScanIngredientsFragment").commit();
                        return true;
                    }
                }
                return true;
            }
        });

        // When the camera icon for 'Barcode Number' is selected, the user should be navigated to the barcode fragment.
        // First need to set a listener to the whole view. The camera icon is on the far right of the view.
        final TextView barcodeTitle = v.findViewById(R.id.barcodeTitle);
        barcodeTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Check that the right drawable was tapped.
                    int[] textLocation = new int[2];
                    barcodeTitle.getLocationOnScreen(textLocation);
                    if (event.getRawX() >= textLocation[0] + barcodeTitle.getWidth() - barcodeTitle.getTotalPaddingRight()){
                        // If it was, replace fragment.
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack("ScanBarcodeFragment").commit();
                        return true;
                    }
                }
                return true;
            }
        });

        final Button cancelBtn = v.findViewById(R.id.button_cancel_man);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemFragment()).addToBackStack("AddItemFragment").commit();
            }
        });


        return v;

    }

    /**
     * This method is called after the parent Activity's onCreate() method has completed.
     * Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
     * At this point, it is safe to search for activity View objects by their ID, for example.
     * @param savedInstanceState saved instance state
     */

    /**
     * When the user submits the product, the product should go into the product collection in Firestore.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         pantryRef = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("pantryRef", null);


        // Setup any handles to view objects here
        editTextName =  (EditText) view.findViewById(R.id.productNameInputMan);
        editTextBrand =  (EditText) view.findViewById(R.id.brandInputMan);
        editTextBarcode =  (EditText) view.findViewById(R.id.barcodeInputMan);
        editTextShelfLife = (EditText) view.findViewById(R.id.shelfLifeInputMan);
        editTextQuantity = (EditText) view.findViewById(R.id.QuantityInputMan);
        editTextVolume = (EditText) view.findViewById(R.id.VolumeInputMan);
        enterIngredientsText = (EditText) view.findViewById(R.id.enterIngredientsText);
        viewDietaryWarning = (TextView) view.findViewById(R.id.viewDietaryWarning);

//        spinnerCategory = (Spinner) findViewById(R.id.CategorySpinMan); TODO



        // Checks diets as the user types
        enterIngredientsText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String ingredients = enterIngredientsText.getText().toString();
                viewDietaryWarning.setText(checkIngredients.checkIngredients(ingredients));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        Button save_manually = getActivity().findViewById(R.id.button_save_man);
        save_manually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get all the data that has been entered.
                //for product
                String name = editTextName.getText().toString();
                String brand = editTextBrand.getText().toString();
                Long barcode = Long.parseLong(editTextBarcode.getText().toString());
                Integer shelfLife = Integer.parseInt(editTextShelfLife.getText().toString());
                String volume = editTextVolume.getText().toString();
                String ingredients = enterIngredientsText.getText().toString();

                //for pantry
                Integer quantity = Integer.parseInt(editTextQuantity.getText().toString());
//              String category = spinnerCategory.getSelectedItem().toString(); TODO



                Map<String, Object> item = new HashMap<>();
                item.put(KEY_NAME, name);
                item.put(KEY_BRAND, brand);
                item.put(KEY_BARCODE, barcode);
                item.put(KEY_SHELFLIFE, shelfLife);
                item.put(KEY_VOLUME, volume);
                item.put(KEY_INGREDIENTS, ingredients);
//                item.put(KEY_CATEGORY, category);



                //PRODUCTS - adding product if not in database.


                if (barcode == 0) {
                    //TODO: figure out a good way to do no barcode items - name match?
                    String too = "ttt";
                } else {
                    // entry has a barcode

                    boolean match = false; //flag for matched barcode

                    //get barcodes from preferences
                    Set<String> productsBarcodes = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                            .getStringSet("barcodesProd", null); //get array of barcodes

                    //change to arrays to be usable
                    Object[] productsBarcodesArray = productsBarcodes.toArray();

                    // for each code check match for entered barcode
                    for (Object code : productsBarcodesArray) {
                        if (code.equals(barcode.toString())) {
                            match = true; //set flag and break
                            break;
                        }
                    }


                    if (!match) {
                        //if barcode not in system make new doc for product and save the id

                        productdocId = db.collection("products").document().getId();
                        // Put this data into the 'products' collection in Firestore
                        db.collection("products").document(productdocId).set(item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // On success, the data should be cleared.
                                        Toast.makeText(getContext(), "Item Added to database", Toast.LENGTH_SHORT).show();
                                        editTextName.setText("");
                                        editTextBrand.setText("");
                                        editTextBarcode.setText("");
                                        editTextShelfLife.setText("");
                                        editTextQuantity.setText("");
                                        enterIngredientsText.setText("");
                                        editTextVolume.setText("");
                                        helperPantrySet(productdocId, quantity, shelfLife); //send through to helper method, to set up pantry
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                    } else { //if barcode matches (in database)
                        db.collection("products")
                                .whereEqualTo("barcodeNum", barcode) //find product in database where match barcode
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot doc : queryDocumentSnapshots){
                                            if(doc.exists()) {  //wait for response
                                                productdocId = doc.getId(); //set prod ID
                                                helperPantrySet(productdocId, quantity, shelfLife); //send through to helper method, to set up pantry
                                            }
                                        }
                                    }
                                });
                    }
                }//end of no barcode product else
                } //end of On Click
               }); //end of click listener

        } //end of on view created

    private void helperPantrySet(String productDocumentID, Integer quantity, Integer shelfLife){ //helper method as we need prod ID to be set before making pantry item
        //Success and Set
        db.collection("pantries").document(pantryRef).collection("products").document(productdocId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists() && document != null) { //see if the product is already in the pantry. If so add the quantity enter to pantry quantity
                        Log.d(TAG, "Document exists!");
                        PantryItem item = document.toObject(PantryItem.class);
                        Integer oldQ = Integer.parseInt(item.getQuantity());
                        newQ = oldQ + quantity;
                    } else {
                        newQ = quantity; //if not in pantry. make the quantity entered the quantity value
                    }
                    //set expiry date
                    Date c = Calendar.getInstance().getTime(); //get the current date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    //Getting current date
                    Calendar cal = Calendar.getInstance();
                    //Number of Days to add
                    cal.add(Calendar.DAY_OF_MONTH, shelfLife); //add shelf life to current date
                    //Date after adding the days to the current date
                    String newDate = sdf.format(cal.getTime()); //new expiry date

                    Map<String, Object> product = new HashMap<>(); //make map of pantry information
                    product.put(KEY_QUANTITY, newQ);
                    product.put(KEY_PRODUCTREF, productdocId);
                    product.put(KEY_EXPIRY, newDate);
                    //TODO get LOCATION AND CATEGORY WORKING? IN SPINNER?????!!
//                        product.put(KEY_LOCATION, shelfLife);
//                        product.put(KEY_CATEGORY, volume);

                    //set product in pantry
                    db.collection("pantries").document(pantryRef).collection("products").document(productdocId).set(product)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Item added to Pantry", Toast.LENGTH_SHORT).show();
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

            }
        });
        //clear fields
        editTextName.setText("");
        editTextBrand.setText("");
        editTextBarcode.setText("");
        editTextShelfLife.setText("");
        editTextQuantity.setText("");
        enterIngredientsText.setText("");
        editTextVolume.setText("");

    }

    /**
     * Update the ingredients field in onResume to ensure it is displayed
     */
    @Override
    public void onResume() {
        super.onResume();

        if (updateIngredientsText != null) {
            enterIngredientsText.setText(updateIngredientsText);
            viewDietaryWarning.setText(checkIngredients.checkIngredients(updateIngredientsText));
        }

        // Set a listener to see whether a barcode has just been scanned and came up blank.
        // If so, populate the barcode field with the barcode scanned.
        getParentFragmentManager().setFragmentResultListener("requestBarcode", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                // Set the text
                String result = bundle.getString("bundleKey");
                editTextBarcode.setText(result);
            }
        });
    }

    /**
     * Here to get data from another fragment
     * @param message the result of camera in ScanIngredientsFragment
     */
    protected void displayReceivedData(String message) {
        updateIngredientsText = message;
    }
}