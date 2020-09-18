package com.example.mypantryapp;

public class ExampleItem {
    private String mName;
    private String mBrand;
    private String mId;

    public ExampleItem(String name, String brand, String id) {
        mName = name;
        mBrand = brand;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public String getBrand() {
        return mBrand;
    }

    public String getId() {
        return mId;
    }

}
