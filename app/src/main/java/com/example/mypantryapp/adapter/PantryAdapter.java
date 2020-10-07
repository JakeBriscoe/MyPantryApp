package com.example.mypantryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.example.mypantryapp.R;
import com.example.mypantryapp.domain.PantryItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> implements Filterable {
    private ArrayList<PantryItem> mExampleList;
    private List<PantryItem> exampleListFull;
    private PantryAdapter.OnItemClickListener mListener;

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(PantryAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public TextView mBrandTextView;
        public TextView mIdTextView;
        public TextView mVolumeTextView;
        public TextView mQuantityTextView;
        public TextView mIngredientsTextView;
        public TextView mDietTitleTextView;
        public TextView mDietTextView;

        public RelativeLayout expandableView;
        public CardView cardView;

        public TextView emptyPantry;

        /**
         * Constructor to initialise TextViews and set onclick listener.
         * @param itemView itemView
         * @param listener listener
         */
        public PantryViewHolder(@NonNull View itemView, PantryAdapter.OnItemClickListener listener) {
            super(itemView);

            // Initialise the TextViews
            mNameTextView = itemView.findViewById(R.id.pantryItems_productName);
            mBrandTextView = itemView.findViewById(R.id.pantryItems_brand);
            mIdTextView = itemView.findViewById(R.id.pantryItems_id);
            mVolumeTextView = itemView.findViewById(R.id.pantryItems_volume);
            mQuantityTextView = itemView.findViewById(R.id.pantryItems_quantity);
            mIngredientsTextView = itemView.findViewById(R.id.pantryItems_ingredients);
            mDietTitleTextView = itemView.findViewById(R.id.pantryItems_dietTitle);
            mDietTextView = itemView.findViewById(R.id.pantryItems_diet);

            expandableView = itemView.findViewById(R.id.exandableProductDetails);
            cardView = itemView.findViewById(R.id.pantryItems_cardView);

            emptyPantry = itemView.findViewById(R.id.emptyPantry);

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

                    // Expand and collapse the view
                    if (expandableView.getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        expandableView.setVisibility(View.VISIBLE);
                    } else {
                        expandableView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    /**
     * Constructor to set the example list
     * @param exampleList the exampleList passed
     */
    public PantryAdapter(ArrayList<PantryItem> exampleList) {
        mExampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    /**
     * Empty constructor
     */
    public PantryAdapter() {
    }

    /**
     * Create the view holder
     * @param parent parent
     * @param viewType viewType
     * @return the ExampleViewHolder
     */
    @NonNull
    @Override
    public PantryAdapter.PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_pantry_item, parent, false);
        return new PantryAdapter.PantryViewHolder(v, mListener);
    }

    /**
     * Set the TextViews to correspond to the item selected.
     * @param holder holder
     * @param position the position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull PantryAdapter.PantryViewHolder holder, int position) {
        // Identify which item has been pressed.
        PantryItem currentItem = mExampleList.get(position);

        // Set the text to correspond to said item.
        holder.mNameTextView.setText(currentItem.getName());
        holder.mBrandTextView.setText(currentItem.getBrand());
        holder.mIdTextView.setText(currentItem.getId());
        holder.mVolumeTextView.setText(currentItem.getVolume());
        holder.mQuantityTextView.setText(currentItem.getQuantity());

        String ingredients = currentItem.getIngredients();
        if (ingredients.equals("")) {
            holder.mIngredientsTextView.setText("No ingredients stored");
            holder.mDietTitleTextView.setVisibility(View.GONE);
            holder.mDietTextView.setVisibility(View.GONE);
        } else {
            holder.mIngredientsTextView.setText(ingredients);
            holder.mDietTitleTextView.setText(currentItem.getDietTitle());
            holder.mDietTextView.setText(currentItem.getDiet());
        }
    }

    /**
     * Determine size of the example list.
     * @return the list size
     */
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<PantryItem> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (PantryItem item : exampleListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mExampleList.clear();
            mExampleList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        public String deleteId(int position) {
            return mExampleList.get(position).getId();
        }
    };

    /**
     * Remove item from dynamic list as well as underlying database
     * @param position
     * @param activity
     */
    public void deleteItem(int position, Activity activity) {
        PantryItem mRecentlyDeletedItem = mExampleList.get(position);
        mExampleList.remove(position);
        notifyItemRemoved(position);

        String pantryRef = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("pantryRef", null);
        String id = mRecentlyDeletedItem.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pantries").document(pantryRef).collection("products")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {  //wait for response
                            documentSnapshot.getReference().delete();
                        }
                    }
                });
    }
}
