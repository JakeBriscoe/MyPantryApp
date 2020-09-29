package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypantryapp.domain.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


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
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SendDetails SM;

    /**
     * Display all products in database.
     */
    public void onStart(){
        super.onStart();

        // These need to be initialised for RecyclerView and CardView
        mRecyclerView = getActivity().findViewById(R.id.recyclerViewItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        // Store the items
        ArrayList<ExampleItem> exampleList = new ArrayList<>();

        productRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            Product product = documentSnapshot.toObject(Product.class);
                            // Add each individual product to exampleList
                            exampleList.add(new ExampleItem(product.getName(),
                                    product.getBrand(),
                                    documentSnapshot.getId(),
                                    (String) documentSnapshot.get("volume")));

                        }

                        // These need to be set so that the products are displayed
                        mAdapter = new ExampleAdapter(exampleList);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                        // When the user clicks on a product, they should be prompted to enter the quantity.
                        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                // Open the bottom modal dialog
                                ExampleItem selected = exampleList.get(position);
                                SM.sendDetails(selected);
                            }
                        });

                    }
                });

    }

    /**
     * Add onclick listeners to static items
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_add_item, container, false); // Initialise view

        // POSSIBLY NOT NEEDED. Ensure that bottom navigation is visible.
        @Nullable
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        // POSSIBLY NOT NEEDED. Set the toolbar title.
        Toolbar mActionBarToolbar = getActivity().findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("[Pantry 1]");

        // When the barcode icon or text is selected, the user should be navigated to the barcode fragment.
        final ImageButton barcodeIcon = v.findViewById(R.id.barcodeIcon);
        barcodeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack(null).commit();
            }

        });
        final TextView barcodeText = v.findViewById(R.id.textScanBarcode);
        barcodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack(null).commit();
            }
        });

        // When the add manually icon or text is selected, the user should be navigated to the add item manually fragment.
        final ImageButton manualButton = v.findViewById(R.id.addManuallyButton);
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tag is needed for passing data between fragments
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManually(), "addManuallyTag").addToBackStack(null).commit();
            }
        });
        final TextView textAddManually = v.findViewById(R.id.textAddManually);
        textAddManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tag is needed for passing data between fragments.
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManually(), "addManuallyTag").addToBackStack(null).commit();

            }
        });
        return v;
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
        void sendDetails(ExampleItem item);
    }

}
