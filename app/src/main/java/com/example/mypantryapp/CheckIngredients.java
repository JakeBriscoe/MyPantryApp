package com.example.mypantryapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    public CheckIngredients () {
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
                            }
                        }
                    }
                });
    }

    public String checkIngredients() {
        Log.d("HERE", diets.toString());
        Log.d("checkIngredients", ingredients);
        String[] ingrs = ingredients.split(" ");
        ArrayList<String> dietNames = new ArrayList<>();
        StringBuilder dietWarnings = new StringBuilder();
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
                                dietWarnings = new StringBuilder(dietWarnings.substring(0, dietWarnings.length() - 2));
                            }
                            dietWarnings.append("\n");
                        }
                        dietWarnings.append(name).append(": ");
                        dietNames.add(name);
                    }
                    dietWarnings.append(ingr).append(", ");
                }
            }
            if (dietWarnings.length() != 0 && dietWarnings.charAt(dietWarnings.length()-2) == ',') {
                // removes final trailing ", "
                dietWarnings = new StringBuilder(dietWarnings.substring(0, dietWarnings.length() - 2));
            }
        }
        if (dietWarnings.toString().equals("")) {
            dietWarnings = new StringBuilder("No dietary warnings");
        }
        return dietWarnings.toString();
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
