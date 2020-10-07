package com.example.mypantryapp;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;

import com.example.mypantryapp.domain.ExampleItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private TextView nameTextView;
    private TextView brandTextView;
    private TextView dietTitleTextView;
    private TextView dietWarningsTextView;
    private EditText quantityEditText;
    private String nameText;
    private String brandText;
    private String idText;
    private String ingredients;
    private String dietWarningsText;
    private Integer shelfLife;
    private boolean isAddItem;
    Integer newQ;

    Button bottomModalShoppingList;

    //Pantry Product Map
    private static final String KEY_PRODUCTREF = "productRef";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_EXPIRY = "expiry";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_CATEGORY = "categoryName";

    private CheckIngredients checkIngredients = new CheckIngredients();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Set any static data/listeners
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false); // Initialise view
        String pantryRef = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("pantryRef", null);
        String shoppingListRef = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("shoppinglistRef", null);

        // If the 'save to pantry' button is pressed, the user should be notified that the product
        // has been added to their personal pantry.
        final Button bottomModalConfirm = v.findViewById(R.id.bottomModalConfirm);
        bottomModalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityText = quantityEditText.getText().toString();
                Integer quantity = parseInt(quantityText);
                db.collection("pantries").document(pantryRef).collection("products").document(idText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists() && document != null) { //see if the product is already in the pantry. If so add the quantity enter to pantry quantity
                                        Long quantity1 = (Long) document.get("quantity");
                                        Integer oldQuantity = (quantity1).intValue();
                                        if (oldQuantity != null) {
                                            newQ = oldQuantity + quantity;
                                        } else {
                                            newQ = quantity; //if not in pantry. make the quantity entered the quantity value
                                        }

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
                                    product.put(KEY_PRODUCTREF, idText);
                                    product.put(KEY_EXPIRY, newDate);
                                    //TODO get LOCATION AND CATEGORY WORKING? IN SPINNER?????!!
//                        product.put(KEY_LOCATION, shelfLife);
//                        product.put(KEY_CATEGORY, volume);

                                    //set product in pantry
                                    db.collection("pantries").document(pantryRef).collection("products").document(idText).set(product)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            }
                        });


                Toast.makeText(getContext(), nameText + " added to pantry", Toast.LENGTH_SHORT).show();
                // Make sure to dismiss the bottom modal.
                dismiss();
            }
        });

        // If the 'add to shopping list' button is pressed, the user should be notified that the product
        // has been added to their shopping list.
        bottomModalShoppingList = v.findViewById(R.id.bottomModalShoppingList);
        bottomModalShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityText = quantityEditText.getText().toString();
                Integer quantity = parseInt(quantityText);
                db.collection("shoppinglists").document(shoppingListRef).collection("products").document(idText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    Map<String, Object> product = new HashMap<>(); //make map of pantry information
                                    product.put(KEY_PRODUCTREF, idText);

                                    //add product to shopping list
                                    db.collection("shoppinglists").document(shoppingListRef).collection("products").document(idText).set(product)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            }
                        });

                Toast.makeText(getContext(), nameText + " added to shopping list", Toast.LENGTH_SHORT).show();
                // Make sure to dismiss the bottom modal.
                dismiss();
            }
        });

        return v;
    }

    /**
     * Initialise the TextViews so they can be accessed in onResume
     *
     * @param view               view
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTextView = view.findViewById(R.id.modalName);
        brandTextView = view.findViewById(R.id.modalBrand);
        dietTitleTextView = view.findViewById(R.id.modalDietTitle);
        dietWarningsTextView = view.findViewById(R.id.modalDietWarnings);
        quantityEditText = (EditText) view.findViewById(R.id.modalQuantity);
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
        DocumentReference docRef = db.collection("products").document(idText);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ingredients = (String) document.get("ingredients");
                        Long sLife = (Long) document.get("shelfLife");
                        if (sLife != null) {
                            shelfLife = sLife.intValue();
                        } else {
                            shelfLife = 1000;
                        }

                        if (ingredients != null) {
                            dietWarningsText = checkIngredients.checkIngredients(ingredients);

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

        if (!isAddItem) {
            bottomModalShoppingList.setVisibility(View.GONE);
        }

    }

    /**
     * This method is called in MainActivity to receive data from AddItemFragment.
     *
     * @param item the item selected
     * @param isAddItem true if displayReceived data comes from AddItemFragment, false otherwise
     */
    protected void displayReceivedData(ExampleItem item, boolean isAddItem) {
        nameText = item.getName();
        brandText = item.getBrand();
        idText = item.getId();
        this.isAddItem = isAddItem;
    }

}

