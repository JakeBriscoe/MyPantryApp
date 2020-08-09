package com.example.mypantryapp.domain;

import java.util.Date;

public class Pantry {
    private long productId;
    private String productName;
    private int quantity;
    private Date expiry;

    public Pantry(long productId, String productName, int quantity, Date expiry) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.expiry = expiry;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }
}
