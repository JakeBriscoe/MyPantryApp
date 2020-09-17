package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

    private static final String TAG = "MainActivity";

    private static final String KEY_NAME = "name";
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Field for collection of products in the firebase.
    private CollectionReference productRef = db.collection("products");

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Display list of product name from firebase
    public void onStart(){
        super.onStart();

        mRecyclerView = getActivity().findViewById(R.id.recyclerViewItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        ArrayList<ExampleItem> exampleList = new ArrayList<>();

        productRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder bld = new StringBuilder();

                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            Product product = documentSnapshot.toObject(Product.class);

                            String name = product.getName();
                            String brand = product.getBrand();

                            exampleList.add(new ExampleItem(name, brand));

                        }

                        mAdapter = new ExampleAdapter(exampleList);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    }
        });

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure that bottom navigation is visible
        @Nullable
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        View v = inflater.inflate(R.layout.fragment_add_item, container, false);

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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManually(), "addManuallyTag").addToBackStack(null).commit();

            }
        });
        final TextView textAddManually = v.findViewById(R.id.textAddManually);
        textAddManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManually(), "addManuallyTag").addToBackStack(null).commit();

            }
        });

        return v;
    }
}
