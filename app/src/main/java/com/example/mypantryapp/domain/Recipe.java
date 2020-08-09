package com.example.mypantryapp.domain;

public class Recipe {
    private long recipeId;
    private String recipe;
    private String description;

    public Recipe(long recipeId, String recipe, String description) {
        this.recipeId = recipeId;
        this.recipe = recipe;
        this.description = description;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
