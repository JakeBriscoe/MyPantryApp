package com.example.mypantryapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypantryapp.adapter.ExampleAdapter;
import com.example.mypantryapp.adapter.ShoppingListAdapter;
import com.example.mypantryapp.domain.ExampleItem;
import com.example.mypantryapp.domain.Product;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment {
    @SuppressLint("ClickableViewAccessibility")
    @Nullable

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Field for collection of products in checklist in firebase.
    private CollectionReference listRef = db.collection("shoppinglist");

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // POSSIBLY NOT NEEDED: Show bottom navigation.
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        // POSSIBLY NOT NEEDED: Set toolbar title.
        Toolbar mActionBarToolbar = getActivity().findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("[Pantry 1]");

        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    public void onStart() {
        super.onStart();

        mRecyclerView = getActivity().findViewById(R.id.recyclerViewCheckItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        ArrayList<ExampleItem> exampleList = new ArrayList<>();

        listRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Product product = documentSnapshot.toObject(Product.class);

                            // Add each individual product to exampleList
                            String name = product.getName();
                            String brand = product.getBrand();
                            String id = documentSnapshot.getId();
                            String vol = product.getIngredients();
                            exampleList.add(new ExampleItem(name, brand, id, vol));

                        }

                        mAdapter = new ExampleAdapter(exampleList);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);


                    }
                });
    }
}