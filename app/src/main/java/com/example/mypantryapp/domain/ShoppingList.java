package com.example.mypantryapp.domain;

import java.util.Date;

public class ShoppingList {
    private long listId;
    private Date date;
    private String productName;
    private int quantity;
    private Boolean checked;

    public ShoppingList(long listId, Date date, String productName, int quantity, Boolean checked) {
        this.listId = listId;
        this.date = date;
        this.productName = productName;
        this.quantity = quantity;
        this.checked = checked;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
