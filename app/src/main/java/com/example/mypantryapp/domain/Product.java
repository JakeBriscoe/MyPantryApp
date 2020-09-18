package com.example.mypantryapp.domain;

public class Product {
    private String productId;
    private String weight;
    private long barcodeNum;
    private int shelfLife; // in days, using 10000 if n/a
    private String name;
    private String description;
    private String brand;
    private String ingredients;

//Domain datatype attribute has been changed, database only takes a string at the moment.

    public Product(String productId, String weight, long barcodeNum, int shelfLife, String name, String description, String brand, String ingredients) {
        this.productId = productId;
        this.weight = weight;
        this.barcodeNum = barcodeNum;
        this.shelfLife = shelfLife;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.ingredients = ingredients;
    }

    public Product(){

    }


    public String getProductId() {
        return productId;
    }

    public String getWeight() {
        return weight;
    }

    public long getBarcodeNum() {
        return barcodeNum;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public String getIngredients() {
        return ingredients;
    }
}
