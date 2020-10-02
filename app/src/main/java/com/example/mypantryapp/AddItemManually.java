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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.mypantryapp.domain.PantryItem;
import com.example.mypantryapp.domain.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddItemManually extends Fragment {


    private Spinner spinner;
    private DatabaseReference mDatabase;

    EditText enterIngredientsText;
    String updateIngredientsText;


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

    private Button saveButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String barcodeText;

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
        View v = inflater.inflate(R.layout.fragment_add_item_manually, container, false); // Initialise view

        // POSSIBLY NOT NEEDED. Show bottom navigation.
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);


        // When the camera icon is selected, the user should be navigated to the scan ingredients fragment.
        // First need to set a listener to the whole view. The camera icon is on the far right of the view.
        final TextView ingredientsTitle = v.findViewById(R.id.ingredientsTitle);
        ingredientsTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Check that the right drawable was tapped.
                    int[] textLocation = new int[2];
                    ingredientsTitle.getLocationOnScreen(textLocation);
                    if (event.getRawX() >= textLocation[0] + ingredientsTitle.getWidth() - ingredientsTitle.getTotalPaddingRight()){
                        // If it was, replace fragment.
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanIngredientsFragment()).addToBackStack(null).commit();
                        return true;
                    }
                }
                return true;
            }
        });
        return v;
    }

    /**
     * When the user submits the product, the product should go into the product collection in Firestore.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String pantryRef = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("pantryRef", null);


        // Setup any handles to view objects here
        editTextName =  (EditText) view.findViewById(R.id.productNameInputMan);
        editTextBrand =  (EditText) view.findViewById(R.id.brandInputMan);
        editTextBarcode =  (EditText) view.findViewById(R.id.barcodeInputMan);
        editTextShelfLife = (EditText) view.findViewById(R.id.shelfLifeInputMan);
        editTextQuantity = (EditText) view.findViewById(R.id.QuantityInputMan);
        editTextVolume = (EditText) view.findViewById(R.id.VolumeInputMan);
        enterIngredientsText = (EditText) view.findViewById(R.id.enterIngredientsText);
//        spinnerCategory = (Spinner) findViewById(R.id.CategorySpinMan);


        // Set onclick listener for save button.
        final Button save_manually = getActivity().findViewById(R.id.button_save_man);
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
//              String category = spinnerCategory.getSelectedItem().toString();


                // Put all data into a map
                Map<String, Object> item = new HashMap<>();
                item.put(KEY_NAME, name);
                item.put(KEY_BRAND, brand);
                item.put(KEY_BARCODE, barcode);
                item.put(KEY_SHELFLIFE, shelfLife);
                item.put(KEY_VOLUME, volume);
                item.put(KEY_INGREDIENTS, ingredients);
                //        item.put(KEY_CATEGORY, category);


                //check status of item
                ArrayList<String> docIds = new ArrayList<String>();
                String productdocId = "ERROR";
                //to help document reference for product if in data base
                //PRODUCTS - adding product if not in database.


                if(barcode == 0){
                    //TODO: figure out a good way to do no barcode items - name match?

                }
                else {
                    //has a barcode
                    // Array to hold all ids
                    ArrayList<Long> prodId = new ArrayList<Long>();
                    boolean match = false; //flag for matched barcode
                    db.collection("products").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Product product = documentSnapshot.toObject(Product.class);
                                        docIds.add(documentSnapshot.getId());
                                        // Add each individual product barcode to prodId
                                        prodId.add(product.getBarcodeNum());
                                    }
                                }
                            });
                    //
                    if (prodId.contains(barcode)) {
                        match = true;
                        int index = prodId.indexOf(barcode);
                        productdocId = docIds.get(index);
                    }
                    if (match == false) {
                        //if barcode not in system make new doc for product and save the id
                        productdocId = db.collection("products").document().getId();
                        // Put this data into the 'products' collection in Firestore.
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
                    else{ //if barcode matches
                        //do nothing as product doesn't need to be updated, and product reference has been stored.
                    }

                } //end of no barcode product else

                //PANTRY - Adding entered product to pantry
                ArrayList<String> productsInPantry = new ArrayList<String>(); //list to store product id of pantry items
                ArrayList<Integer> productsInPantryQ = new ArrayList<Integer>();

                db.collection("pantries").document(pantryRef).collection("products").get() //get all in pantry -> products
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                  @Override
                                                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                      for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                          productsInPantry.add(documentSnapshot.getId());
                                                          PantryItem product = documentSnapshot.toObject(PantryItem.class);
                                                          productsInPantryQ.add(product.getQuantity());

                                                      }
                                                  }
                                                  });

                if (productsInPantry.contains(productdocId)) {
                    int index = productsInPantry.indexOf(productdocId);
                    int oldQ = productsInPantryQ.get(index);
                    //TODO UPDATE PRODUCT
                }
                else{
                    //making new product document
                    DocumentReference pantryProdRef = db.collection("pantries").document(pantryRef).collection("products").document(productdocId);

                    //set expiry date
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    //Getting current date
                    Calendar cal = Calendar.getInstance();
                    //Number of Days to add
                    cal.add(Calendar.DAY_OF_MONTH, shelfLife);
                    //Date after adding the days to the current date
                    String newDate = sdf.format(cal.getTime());

                    Map<String, Object> product = new HashMap<>();
                    product.put(KEY_QUANTITY, quantity);
                    product.put(KEY_PRODUCTREF, productdocId);
                    product.put(KEY_EXPIRY, newDate);
                    //TODO get LOCATION AND CATEGORY WORKING? IN SPINNER?????!!
//                        product.put(KEY_LOCATION, shelfLife);
//                        product.put(KEY_CATEGORY, volume);

                    db.collection("pantries").document(pantryRef).collection("products").document(productdocId).set(product)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   enterIngredientsText.setText("");
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







                           } //end of On Click
               }); //end of click listener

        } //end of on view created


    /**
     * Update the ingredients field in onResume to ensure it is displayed
     */
    @Override
    public void onResume() {
        super.onResume();

        // Set the ingredients text
        if (updateIngredientsText != null) {
            enterIngredientsText.setText(updateIngredientsText);
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
