package com.example.mypantryapp.domain;

public class Allergen {
    private long allergenId;
    private String allergens;
    private String description;

    public Allergen(long allergenId, String allergens, String description) {
        this.allergenId = allergenId;
        this.allergens = allergens;
        this.description = description;
    }

    public long getAllergenId() {
        return allergenId;
    }

    public void setAllergenId(long allergenId) {
        this.allergenId = allergenId;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
