package com.example.mypantryapp.domain;

public class Product {
    private long productId;
    private float weight;
    private long barcodeNum;
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
        this.productName = productName;
        this.description = description;
        this.brand = brand;
        this.categoryName = categoryName;
        this.dietaryType = dietaryType;
        this.allergens = allergens;
        this.ingredients = ingredients;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getBarcodeNum() {
        return barcodeNum;
    }

    public void setBarcodeNum(long barcodeNum) {
        this.barcodeNum = barcodeNum;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(String dietaryType) {
        this.dietaryType = dietaryType;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
