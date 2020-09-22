package com.example.mypantryapp;

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
