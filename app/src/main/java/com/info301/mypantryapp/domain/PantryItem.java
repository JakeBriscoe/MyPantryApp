package com.info301.mypantryapp.domain;

public class PantryItem {
    private String mName;
    private String mBrand;
    private String mId;
    private String mVolume;
    private String mQuantity;
    private String mIngredients;
    private String mDietTitle;
    private String mDiet;

    /**
     * Constructor to set the details
     * @param name the product name
     * @param brand the product brand
     * @param id the product id
     */
    public PantryItem(String name, String brand, String id, String volume, String quantity, String ingredients,
                      String dietTitle, String diet) {
        mName = name;
        mBrand = brand;
        mId = id;
        mVolume = volume;
        mQuantity = quantity;
        mIngredients = ingredients;
        mDietTitle = dietTitle;
        mDiet = diet;
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

    public String getIngredients() {
        return mIngredients;
    }

    public String getDietTitle() {
        return mDietTitle;
    }

    public String getDiet() {
        return mDiet;
    }
}
