package com.example.mypantryapp.domain;

public class User {
    private boolean isAdmin;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String country;
    private String userAuthId;

    public User(boolean isAdmin, String username, String email, String firstName, String lastName, String country, String userAuthId) {
        this.isAdmin = isAdmin;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.userAuthId = userAuthId;
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
