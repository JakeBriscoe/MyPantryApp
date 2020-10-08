package com.info301.mypantryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info301.mypantryapp.adapter.ShoppingListAdapter;
import com.info301.mypantryapp.domain.ProductItem;
import com.info301.mypantryapp.domain.Product;
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

    ArrayList<ProductItem> exampleList = new ArrayList<>();

    String shoppinglistRef;
    SendDetailsShoppingList SM;

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
                        exampleList.clear();
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
                                                    exampleList.add(new ProductItem(name, brand, id, vol));

                                                    if (exampleList.size() == queryDocumentSnapshots.size()) {
                                                        mAdapter = new ShoppingListAdapter(exampleList);
                                                        mRecyclerView.setLayoutManager(mLayoutManager);
                                                        mRecyclerView.setAdapter(mAdapter);

                                                        mAdapter.setOnItemClickListener(new ShoppingListAdapter.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(int position) {
                                                                // Open the bottom modal dialog
                                                                ProductItem selected = exampleList.get(position);
                                                                SM.sendDetailsShoppingList(selected);
                                                            }
                                                        });

                                                        // Set uo helper to delete shopping list item when swiped on
                                                        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                                                                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                                            @Override
                                                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                                                return false;
                                                            }

                                                            @Override
                                                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                                                int position = viewHolder.getAdapterPosition();
                                                                Toast.makeText(getContext(), exampleList.get(position).getName() + " deleted from shopping list", Toast.LENGTH_SHORT).show();
                                                                mAdapter.deleteItem(position, getActivity());
                                                            }
                                                        }).attachToRecyclerView(mRecyclerView);
                                                    }

                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }


                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                        }

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
            SM = (SendDetailsShoppingList) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    /**
     * Interface that MainActivity must implement to send data from
     * ShoppingListFragment to BottomSheetDialog.
     */
    public interface SendDetailsShoppingList {
        void sendDetailsShoppingList(ProductItem item);
    }
}