package com.example.mypantryapp;

/**
 * Stores details about a product so it can be displayed dynamically in AddItemFragment.
 */
public class ExampleItem {
    private String mName;
    private String mBrand;
    private String mId;

    /**
     * Constructor to set the details
     * @param name the product name
     * @param brand the product brand
     * @param id the product id
     */
    public ExampleItem(String name, String brand, String id) {
        mName = name;
        mBrand = brand;
        mId = id;
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

}
