package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypantryapp.adapter.ShoppingListAdapter;
import com.example.mypantryapp.domain.ExampleItem;
import com.example.mypantryapp.domain.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment {
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    private static final String TAG = "MainActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView mRecyclerView;
    private ShoppingListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<ExampleItem> exampleList = new ArrayList<>();

    String shoppinglistRef;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        iniRecyclerViews(v);
        getShoppingList();

        // POSSIBLY NOT NEEDED: Show bottom navigation.
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        // POSSIBLY NOT NEEDED: Set toolbar title.
        Toolbar mActionBarToolbar = getActivity().findViewById(R.id.toolbar);

        // If no items are in the pantry, tell the user how to add to their pantry
        TextView emptyShoppingList = v.findViewById(R.id.emptyShoppingList);
        if (mAdapter.getItemCount() == 0) {
            emptyShoppingList.setVisibility(View.VISIBLE);
        } else {
            emptyShoppingList.setVisibility(View.GONE);
        }

        return v;
    }

    private void iniRecyclerViews(View v) {
        mRecyclerView = v.findViewById(R.id.recyclerViewCheckItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new ShoppingListAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getShoppingList() {
        shoppinglistRef = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("shoppinglistRef", null);

        db.collection("shoppinglists").document(shoppinglistRef).collection("products").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String id = documentSnapshot.getId();
                            db.collection("products").document(id).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {

                                                    Product product = document.toObject(Product.class);

                                                    // Add each individual product to exampleList
                                                    String name = product.getName();
                                                    String brand = product.getBrand();
                                                    //String id = documentSnapshot.getId();
                                                    String vol = product.getIngredients();
                                                    exampleList.add(new ExampleItem(name, brand, id, vol));

                                                    if (exampleList.size() == queryDocumentSnapshots.size()) {
                                                        mAdapter = new ShoppingListAdapter(exampleList);
                                                        mRecyclerView.setLayoutManager(mLayoutManager);
                                                        mRecyclerView.setAdapter(mAdapter);
                                                    }

                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }


                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                        // These need to be set so that the products are displayed

                                    });
                        }

                    }
                });
    }
}