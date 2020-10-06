package com.example.mypantryapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypantryapp.R;
import com.example.mypantryapp.domain.ExampleItem;

import java.util.ArrayList;

/**
 * Adapter exists so products can be displayed dynamically in AddItemFragment.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
    private ArrayList<ExampleItem> mExampleList;
    private OnItemClickListener mListener;

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
        public TextView mVolumeTextView;

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
            //mVolumeTextView = itemView.findViewById(R.id.checkItem_volume);

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
    public ShoppingListAdapter(ArrayList<ExampleItem> exampleList) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_checkitemlist, parent, false);
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
        ExampleItem currentItem = mExampleList.get(position);

        // Set the text to correspond to said item.
        holder.mNameTextView.setText(currentItem.getName());
        holder.mBrandTextView.setText(currentItem.getBrand());
        holder.mIdTextView.setText(currentItem.getId());
        //holder.mVolumeTextView.setText(currentItem.getVolume());
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
