package com.example.mypantryapp.domain;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class PantryItem {
    private Product product;
    private Category category;
    private String kitchenLocation;
    private Date expiry;
    private int quantity;

    public PantryItem(Product product, Date expiry, int quantity, Category category, String kitchenLocation) {
        this.product = product;
        this.quantity = quantity;
        this.category = category;
        this.kitchenLocation = kitchenLocation;
        this.expiry = expiry;
    }

    public PantryItem() {
    }

    public Product getProduct() {
        return product;
    }

    public Date getExpiry() {
        return expiry;
    }

    public int getQuantity() {
        return quantity;
    }

    public Category getCategory() { return category; }

    public String getKitchenLocation() { return kitchenLocation; }

    public PantryItem setProduct(Product product) {
        this.product = product;
        return this;
    }

    public PantryItem setCategory(Category category) {
        this.category = category;
        return this;
    }

    public PantryItem setKitchenLocation(String kitchenLocation) {
        this.kitchenLocation = kitchenLocation;
        return this;
    }

    public PantryItem setExpiry(Date expiry) {
        this.expiry = expiry;
        return this;
    }

    public PantryItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public String toString() {
        return "PantryItem{" +
                "product=" + product +
                ", category=" + category +
                ", kitchenLocation='" + kitchenLocation + '\'' +
                ", expiry=" + expiry +
                ", quantity=" + quantity +
                '}';
    }
}
