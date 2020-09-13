package com.example.mypantryapp.domain;

import java.util.List;

public class Dietary {
    private long dietaryId;
    private String dietaryType;
    private String description;
    private List<String> blacklist;

    public Dietary(long dietaryId, String dietaryType, String description, List<String> blacklist) {
        this.dietaryId = dietaryId;
        this.dietaryType = dietaryType;
        this.description = description;
        this.blacklist = blacklist;
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

    public List<String> getBlacklist() { return blacklist; }
    public void setBlacklist(List<String> blacklist) { this.blacklist = blacklist; }
}
