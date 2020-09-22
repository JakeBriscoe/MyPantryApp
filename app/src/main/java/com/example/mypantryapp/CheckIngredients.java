package com.example.mypantryapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckIngredients {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String ingredients;
    private Map<String, ArrayList<String>> diets = new HashMap<String, ArrayList<String>>();
    private ArrayList<String> documentNames = new ArrayList<>();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    public CheckIngredients (String ingredients) {
        this.ingredients = ingredients;
        // Gets the diets and blacklisted ingredients from firebase
        db.collection("dietary")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String result = "";
                        ArrayList<String> dietNames = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                 String name = document.getString("name");
                                 ArrayList<String> blacklist = (ArrayList<String>) document.get("blacklist");
                                 diets.put(name, blacklist);
                                 documentNames.add(document.getId());
                            }
                        }
                    }
                });
        String userId = currentUser.getUid(); //get unique user id
        db.collection("users")
                .whereEqualTo("userAuthId", userId) //looks for the corresponding value with the field
                // in the database
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> docData = document.getData();
                                for (Map.Entry<String, Object> entry : docData.entrySet()) {
                                    String field = entry.getKey();
                                    Object value = entry.getValue();
                                    // checks if field is a diet, if value false remove from diet checking
                                    if (documentNames.contains(field) && (Boolean) value == false) {
                                        diets.remove(field);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public String checkIngredients() {
        String[] ingrs = ingredients.split(" ");
        ArrayList<String> dietNames = new ArrayList<>();
        String dietWarnings = "";
        for (Map.Entry<String, ArrayList<String>> entry : diets.entrySet()) {
            String name = entry.getKey();
            ArrayList<String> blacklist = (ArrayList<String>) entry.getValue();
            for (String ingr : ingrs) {
                ingr = ingr.toLowerCase();
                // removes trailing ,
                if (ingr.length() > 0 && (ingr.charAt(ingr.length()-1) == ',' || ingr.charAt(ingr.length()-1) == '.')) {
                    ingr = ingr.substring(0, ingr.length()-1);
                }
                if (blacklist.contains(ingr)) {
                    if (!dietNames.contains(name)) {
                        if (dietWarnings.length() != 0) {
                            // removes unwanted ", " doesn't work for last line
                            if (dietWarnings.charAt(dietWarnings.length()-2) == ',') {
                                dietWarnings = dietWarnings.substring(0, dietWarnings.length() - 2);
                            }
                            dietWarnings += "\n";
                        }
                        dietWarnings += name + ": ";
                        dietNames.add(name);
                    }
                    dietWarnings += ingr + ", ";
                }
            }
            if (dietWarnings.length() != 0 && dietWarnings.charAt(dietWarnings.length()-2) == ',') {
                // removes final trailing ", "
                dietWarnings = dietWarnings.substring(0, dietWarnings.length() - 2);
            }
        }
        if (dietWarnings == "") {
            dietWarnings = "No dietary warnings";
        }
        return dietWarnings;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
