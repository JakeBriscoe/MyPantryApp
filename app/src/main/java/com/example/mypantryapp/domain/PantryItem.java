package com.example.mypantryapp.domain;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class PantryItem {
    private String mName;
    private String mBrand;
    private String mId;
    private String mVolume;
    private String mQuantity;

    /**
     * Constructor to set the details
     * @param name the product name
     * @param brand the product brand
     * @param id the product id
     */
    public PantryItem(String name, String brand, String id, String volume, String quantity) {
        mName = name;
        mBrand = brand;
        mId = id;
        mVolume = volume;
        mQuantity = quantity;
    }

    public PantryItem() {
    }

    /**
     * Get the product name
     * @return name
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the product brand
     * @return brand
     */
    public String getBrand() {
        return mBrand;
    }

    /**
     * Get the product id
     * @return id
     */
    public String getId() {
        return mId;
    }

    /**
     * Get the product volume
     * @return volume
     */
    public String getVolume() {
        return mVolume;
    }

    public String getQuantity() {
        return mQuantity;
    }
}
