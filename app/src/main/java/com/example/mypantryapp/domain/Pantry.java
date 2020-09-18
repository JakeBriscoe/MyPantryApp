package com.example.mypantryapp.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Pantry {
    private List<PantryItem> items;
    private String name;
    private List<String> userIds;
    private List<String> kitchenLocations;

    /**
     * Create a Pantry with multiple user.
     *
     * @param name the name of the pantry
     * @param items the items to add to the pantry
     * @param userIds the IDs of the users to add to the pantry
     */
    public Pantry(String name, List<PantryItem> items, String... userIds) {
        this.name = name;
        this.userIds.addAll(Arrays.asList(userIds));
        this.items = items;
    }

    /**
     * Create a Pantry with a single user.
     *
     * @param name the name of the pantry
     * @param items the items to add to the pantry
     * @param userId the ID of the user to add to the pantry
     */
    public Pantry(String name, List<PantryItem> items, String userId) {
        this.name = name;
        this.userIds.add(userId);
        this.items = items;
    }

    public List<PantryItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void addItem(PantryItem item) {
        this.items.add(item);
    }
}
