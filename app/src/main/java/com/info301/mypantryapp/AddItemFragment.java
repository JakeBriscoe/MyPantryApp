package com.info301.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.info301.mypantryapp.adapter.ProductAdapter;
import com.info301.mypantryapp.domain.Product;
import com.info301.mypantryapp.domain.ProductItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class AddItemFragment extends Fragment {
    @SuppressLint("ClickableViewAccessibility")
    @Nullable

    // Declarations
    private static final String TAG = "MainActivity";
    private static final String KEY_NAME = "name";
    private TextView textViewData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Field for collection of products in the firebase.
    private CollectionReference productRef = db.collection("products");
    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SendDetails SM;
    private ArrayList<ProductItem> exampleList = new ArrayList<>();
    CheckIngredients checkIngredients = new CheckIngredients();

    /**
     * Add onclick listeners to static items
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_add_item, container, false); // Initialise view

        buttons(v); // Set button listeners
        iniRecyclerViews(v); // Initialise views for product recycler view
        getProducts(); // Display products

        SearchView searchView = v.findViewById(R.id.textSearchCommon);
        search(searchView); // Set searchView listener


        // Ensure that bottom navigation is visible.
        @Nullable
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        return v;

    }

    /**
     * Initialise and set listeners for all buttons
     * @param v view
     */
    private void buttons(View v) {
        // When the barcode icon or text is selected, the user should be navigated to the barcode fragment.
        final ImageButton barcodeIcon = v.findViewById(R.id.barcodeIcon);
        barcodeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack("ScanBarcodeFragment").commit();
            }

        });
        final TextView barcodeText = v.findViewById(R.id.textScanBarcode);
        barcodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack("ScanBarcodeFragment").commit();
            }
        });

        // When the add manually icon or text is selected, the user should be navigated to the add item manually fragment.
        final ImageButton manualButton = v.findViewById(R.id.addManuallyButton);
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tag is needed for passing data between fragments
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManuallyFragment(), "AddItemManuallyFragment").addToBackStack(null).commit();
            }
        });
        final TextView textAddManually = v.findViewById(R.id.textAddManually);
        textAddManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tag is needed for passing data between fragments.
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManuallyFragment(), "AddItemManuallyFragment").addToBackStack(null).commit();

            }

        });
    }

    /**
     * Search listener
     * @param searchView searchView
     */
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    /**
     * Initialise views required for dynamic products
     * @param v view
     */
    private void iniRecyclerViews(View v) {
        // These need to be initialised for RecyclerView and CardView
        mRecyclerView = v.findViewById(R.id.recyclerViewItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        // These need to be set so that the products are displayed
        mAdapter = new ProductAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Display all products in database
     */
    private void getProducts() {
        // Store the items
        ArrayList<String> productBCDB = new ArrayList<>(); //array list for product barcode

        productRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        exampleList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Product product = documentSnapshot.toObject(Product.class);
                            // Add each individual product to exampleList
                            exampleList.add(new ProductItem(product.getName(),
                                    product.getBrand(),
                                    documentSnapshot.getId(),
                                    (String) documentSnapshot.get("volume"),
                                    (String) documentSnapshot.get("ingredients"),
                                    (Long) documentSnapshot.get("shelfLife")));

                            Long bCode = product.getBarcodeNum();
                            if (bCode != 0) {
                                productBCDB.add(Long.toString(bCode));
                            }

                        }

                        // These need to be set so that the products are displayed
                        mAdapter = new ProductAdapter(exampleList);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);
                        Set<String> set = new HashSet<>(productBCDB);
                        getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putStringSet("barcodesProd",
                                set).apply();

                        // When the user clicks on a product, they should be prompted to enter the quantity.
                        mAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                // Open the bottom modal dialog
                                ProductItem selected = exampleList.get(position);
                                SM.sendDetails(selected, true, checkIngredients);
                            }
                        });

                    }
                });
    }


    /**
     * Send the product data to BottomSheetDialog
     * @param context the context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SM = (SendDetails) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    /**
     * Interface that MainActivity must implement to send data from
     * AddItemFragment to BottomSheetDialog.
     */
    public interface SendDetails {
        void sendDetails(ProductItem item, boolean isAddItem, CheckIngredients checkIngredients);
    }
}

