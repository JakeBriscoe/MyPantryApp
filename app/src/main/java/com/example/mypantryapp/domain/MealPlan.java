package com.example.mypantryapp.domain;

import java.util.Date;

public class MealPlan {
    private long planId;
    private String recipe;
    private Date date;
    private int quantity;

    public MealPlan(long planId, String recipe, Date date, int quantity) {
        this.planId = planId;
        this.recipe = recipe;
        this.date = date;
        this.quantity = quantity;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
