package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    private CollectionReference productRef = db.collection("products");

    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public void onStart(){
        super.onStart();

        productRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            Product product = documentSnapshot.toObject(Product.class);

                            String name = product.getName();

                            data+="Product Name: " + name + "\n";



                        }

                        textViewData.setText(data);


                    }

    });

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure that bottom navigation is visible
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);





        View v = inflater.inflate(R.layout.fragment_add_item, container, false);

        // When the barcode icon is selected, the user should be navigated to the barcode fragment.
        ImageButton barcodeIcon = v.findViewById(R.id.barcodeIcon);
        barcodeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanBarcodeFragment()).addToBackStack(null).commit();

            }

        });

        //Add Manual Button Selected:
        ImageButton manualButton = v.findViewById(R.id.downloadInfo);
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManually()).addToBackStack(null).commit();

            }
        });

        textViewData = v.findViewById(R.id.text_view_products);
        ImageButton downloadButton = v.findViewById(R.id.downloadInfo);
        downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                productRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String data = "";
                                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                    Product product = documentSnapshot.toObject(Product.class);

                                    String name = product.getName();

                                    data+="Product Name: " + name + "\n";



                                }

                                textViewData.setText(data);


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