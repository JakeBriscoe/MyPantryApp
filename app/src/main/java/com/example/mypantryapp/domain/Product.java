package com.example.mypantryapp.domain;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Product {
    private long productId;
    private float weight;
    private long barcodeNum;
    private long shelfLife; // in days
    private String productName;
    private String description;
    private String brand;
    private String categoryName;
    private String dietaryType;
    private String allergens;
    private String ingredients;

    public Product(long productId, float weight, long barcodeNum, String productName, String description, String brand, String categoryName, String dietaryType, String allergens, String ingredients) {
        this.productId = productId;
        this.weight = weight;
        this.barcodeNum = barcodeNum;
        this.shelfLife = 7;
        this.productName = productName;
        this.description = description;
        this.brand = brand;
        this.categoryName = categoryName;
        this.dietaryType = dietaryType;
        this.allergens = allergens;
        this.ingredients = ingredients;
    }

    public Product(long productId, float weight, long barcodeNum, long shelfLife, String productName, String description, String brand, String categoryName, String dietaryType, String allergens, String ingredients) {
        this(productId, weight, barcodeNum, productName, description, brand, categoryName, dietaryType, allergens, ingredients);
        this.shelfLife = shelfLife;
    }

    public long getProductId() {
        return productId;
    }

    public float getWeight() {
        return weight;
    }

    public long getBarcodeNum() {
        return barcodeNum;
    }

    public long getShelfLife() {
        return shelfLife;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDietaryType() {
        return dietaryType;
    }

    public String getAllergens() {
        return allergens;
    }

    public String getIngredients() {
        return ingredients;
    }
}
