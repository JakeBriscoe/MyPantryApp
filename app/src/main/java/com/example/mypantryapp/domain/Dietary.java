package com.example.mypantryapp.domain;

public class Dietary {
    private long dietaryId;
    private String dietaryType;
    private String description;

    public Dietary(long dietaryId, String dietaryType, String description) {
        this.dietaryId = dietaryId;
        this.dietaryType = dietaryType;
        this.description = description;
    }

    public long getDietaryId() {
        return dietaryId;
    }

    public void setDietaryId(long dietaryId) {
        this.dietaryId = dietaryId;
    }

    public String getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(String dietaryType) {
        this.dietaryType = dietaryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
