package com.example.mypantryapp.domain;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class PantryItem {
    private Product product;
    private long expiry;
    private int quantity;

    public PantryItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.expiry =System.currentTimeMillis() + (product.getShelfLife() * 86400000L); // the date from now + shelf life in days
    }

    public PantryItem(Product product, long expiry, int quantity) {
        this(product, quantity);
        this.expiry = expiry;
    }

    public Product getProduct() {
        return product;
    }

    public long getExpiry() {
        return expiry;
    }

    public int getQuantity() {
        return quantity;
    }
}
