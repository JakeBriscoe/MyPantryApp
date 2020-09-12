package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mypantryapp.domain.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class AddItemFragment extends Fragment {
    @SuppressLint("ClickableViewAccessibility")
    @Nullable

    private static final String TAG = "MainActivity";

    private static final String KEY_NAME = "name";
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Field for collection of products in the firebase.
    private CollectionReference productRef = db.collection("products");


  //  public void onAttach(Context context) {
   //     super.onAttach(context);
    //}

    //Display list of product name from firebase
    public void onStart(){
        super.onStart();

        productRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder bld = new StringBuilder();
                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            Product product = documentSnapshot.toObject(Product.class);

                            String name = product.getName();

                            bld.append("Product Name: ").append(name).append("\n"). append("\n");

                        }
                    //Set the get(attributes) to the textview
                        textViewData.setText(bld);

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

        //----------------------------------------------This was used for testing,  Will need to remove code below-------------------------------------

        textViewData = v.findViewById(R.id.text_view_products);
        ImageButton downloadButton = v.findViewById(R.id.downloadInfo);
        downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                productRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                StringBuilder bld = new StringBuilder();
                                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                    Product product = documentSnapshot.toObject(Product.class);

                                    String name = product.getName();

                                    bld.append("Product Name: ").append(name).append("\n"). append("\n");

                                }
                                textViewData.setText(bld);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, toString());
                            }
                        });
            }
        });
        return v;
    }
}
