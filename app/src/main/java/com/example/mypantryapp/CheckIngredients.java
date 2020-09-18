package com.example.mypantryapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CheckIngredients {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String dietWarnings;
    private String ingredients;
    // Atomic String is required for updating dietWarnings from inner class
    private AtomicReference<String> resultHolder = new AtomicReference<>();

    public CheckIngredients (String ingredients) {
        this.ingredients = ingredients;
    }

    public void checkIngredients() {
        final String[] ingrs = ingredients.split(" ");
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
                                List<String> blacklist = (List<String>) document.get("blacklist");
                                for (String ingr : ingrs) {
                                    ingr = ingr.toLowerCase();
                                    // removes trailing ,
                                    if (ingr.length() > 0 && (ingr.charAt(ingr.length()-1) == ',' || ingr.charAt(ingr.length()-1) == '.')) {
                                        ingr = ingr.substring(0, ingr.length()-1);
                                    }
                                    if (blacklist.contains(ingr)) {
                                        if (!dietNames.contains(name)) {
                                            if (result.length() != 0) {
                                                // removes unwanted ", " doesn't work for last line
                                                if (result.charAt(result.length()-2) == ',') {
                                                    result = result.substring(0, result.length() - 2);
                                                }
                                                result += "\n";
                                            }
                                            result += name + ": ";
                                            dietNames.add(name);
                                        }
                                        result += ingr + ", ";
                                    }
                                }
                                if (result.length() != 0 && result.charAt(result.length()-2) == ',') {
                                    // removes trailing ", "
                                    result = result.substring(0, result.length() - 2);
                                }
                                resultHolder.set(result);
                            }
                        }
                    }
                });
        dietWarnings = resultHolder.get();
        if (dietWarnings == null || dietWarnings.length() == 0) {
            dietWarnings = "No dietary warnings";
        }
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDietWarnings() {
        return dietWarnings;
    }
}
