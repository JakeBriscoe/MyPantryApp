package com.example.mypantryapp;

public class ExampleItem {
    private String mName;
    private String mBrand;
    private String mId;
    private String mVolume;

    public ExampleItem(String name, String brand, String id, String volume) {
        mName = name;
        mBrand = brand;
        mId = id;
        mVolume = volume;
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

    public String getVolume() {
        return mVolume;
    }

}
