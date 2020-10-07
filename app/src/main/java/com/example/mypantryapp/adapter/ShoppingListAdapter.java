package com.example.mypantryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypantryapp.R;
import com.example.mypantryapp.domain.ProductItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Adapter exists so products can be displayed dynamically in AddItemFragment.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
    private ArrayList<ProductItem> mExampleList;
    private OnItemClickListener mListener;
    private CheckBox checkBox;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public TextView mBrandTextView;
        public TextView mIdTextView;
        public CheckBox mCheckBox;

        /**
         * Constructor to initialise TextViews and set onclick listener.
         * @param itemView itemView
         * @param listener listener
         */
        public ShoppingListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // Initialise the TextViews
            mNameTextView = itemView.findViewById(R.id.checkItem_productName);
            mBrandTextView = itemView.findViewById(R.id.checkItem_brand);
            mIdTextView = itemView.findViewById(R.id.checkItem_id);
            mCheckBox = itemView.findViewById(R.id.checkItem_box);

            // Set the onclick listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                    // Toggle check item
                    if (mCheckBox.isSelected()) {
                        mCheckBox.setSelected(false);
                    } else {
                        mCheckBox.setChecked(true);
                    }
                }
            });
        }
    }

    /**
     * Constructor to set the example list
     * @param exampleList the exampleList passed
     */
    public ShoppingListAdapter(ArrayList<ProductItem> exampleList) {
        mExampleList = exampleList;
    }

    /**
     * Create the view holder
     * @param parent parent
     * @param viewType viewType
     * @return the ExampleViewHolder
     */
    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_shopping_list_item, parent, false);
        return new ShoppingListViewHolder(v, mListener);
    }

    /**
     * Set the TextViews to correspond to the item selected.
     * @param holder holder
     * @param position the position of the item
     */

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        // Identify which item has been pressed.
        ProductItem currentItem = mExampleList.get(position);

        // Set the text to correspond to said item.
        holder.mNameTextView.setText(currentItem.getName());
        holder.mBrandTextView.setText(currentItem.getBrand());
        holder.mIdTextView.setText(currentItem.getId());
    }

    /**
     * Determine size of the example list.
     * @return the list size
     */
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    /**
     * Remove item from dynamic list as well as underlying database
     * @param position
     * @param activity
     */
    public void deleteItem(int position, Activity activity) {
        ProductItem mRecentlyDeletedItem = mExampleList.get(position);
        mExampleList.remove(position);
        notifyItemRemoved(position);

        String shoppinglistRef = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("shoppinglistRef", null);
        String id = mRecentlyDeletedItem.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shoppinglists").document(shoppinglistRef).collection("products")
                .whereEqualTo("productRef", id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.exists()) {  //wait for response
                                doc.getReference().delete();
                            }
                        }
                    }
                });
    }
}
