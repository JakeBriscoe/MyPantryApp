package com.info301.mypantryapp.domain;

public class User {
    private boolean isAdmin;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String country;
    private String userAuthId;

    private boolean df;
    private boolean gf;
    private boolean celiac;
    private boolean eggs;
    private boolean lactose;
    private boolean nuts;
    private boolean peanuts;
    private boolean shellfish;
    private boolean soy;
    private boolean vegan;
    private boolean vegetarian;
    private boolean wheat;

    public User(String userAuthId) {

        this.userAuthId = userAuthId;

        this.isAdmin = false;
        this.df = false;
        this.gf = false;
        this.eggs = false;
        this.celiac = false;
        this.lactose = false;
        this.nuts = false;
        this.peanuts = false;
        this.shellfish = false;
        this.soy = false;
        this.vegan = false;
        this.vegetarian = false;
        this.wheat = false;
    }

    public User(boolean isAdmin, String username, String email, String firstName, String lastName, String country, String userAuthId,
                boolean df, boolean gf, boolean celiac, boolean eggs, boolean lactose, boolean nuts, boolean peanuts,
                boolean shellfish, boolean soy, boolean vegan, boolean vegetarian, boolean wheat) {
        this.isAdmin = isAdmin;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.userAuthId = userAuthId;

        this.df = df;
        this.gf = gf;
        this.eggs = eggs;
        this.celiac = celiac;
        this.lactose = lactose;
        this.nuts = nuts;
        this.peanuts = peanuts;
        this.shellfish = shellfish;
        this.soy = soy;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
        this.wheat = wheat;
    }

    public boolean getAdmin() {
        return isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUserAuthId() {return userAuthId;}

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUserAuthId(String userAuthId) {this.userAuthId = userAuthId; }

    public boolean getDf() {
        return df;
    }

    public boolean getGf() {
        return gf;
    }

    public boolean getCeliac() {
        return celiac;
    }

    public boolean getEggs() {
        return eggs;
    }

    public boolean getLactose() {
        return lactose;
    }

    public boolean getNuts() {
        return nuts;
    }

    public boolean getPeanuts() {
        return peanuts;
    }

    public boolean getShellfish() {
        return shellfish;
    }

    public boolean getSoy() {
        return soy;
    }

    public boolean getVegan() {
        return vegan;
    }

    public boolean getVegetarian() {
        return vegetarian;
    }

    public boolean getWheat() {
        return wheat;
    }

    public void setDf(boolean df) {
        this.df = df;
    }

    public void setGf(boolean gf) {
        this.gf = gf;
    }

    public void setCeliac(boolean celiac) {
        this.celiac = celiac;
    }

    public void setEggs(boolean eggs) {
        this.eggs = eggs;
    }

    public void setLactose(boolean lactose) {
        this.lactose = lactose;
    }

    public void setNuts(boolean nuts) {
        this.nuts = nuts;
    }

    public void setPeanuts(boolean peanuts) {
        this.peanuts = peanuts;
    }

    public void setShellfish(boolean shellfish) {
        this.shellfish = shellfish;
    }

    public void setSoy(boolean soy) {
        this.soy = soy;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public void setWheat(boolean wheat) {
        this.wheat = wheat;
    }


    @Override
    public String toString() {
        return "User{" +
                "isAdmin=" + isAdmin +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", userAuthId='" + userAuthId + '\'' +
                '}';
    }
}
