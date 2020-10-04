package com.example.mypantryapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypantryapp.R;
import com.example.mypantryapp.domain.ExampleItem;
import com.example.mypantryapp.domain.Pantry;
import com.example.mypantryapp.domain.PantryItem;
import com.example.mypantryapp.domain.Product;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    private ArrayList<PantryItem> mExampleList;
    private PantryAdapter.OnItemClickListener mListener;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pantry_item, parent, false);
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
    }

    /**
     * Determine size of the example list.
     * @return the list size
     */
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
