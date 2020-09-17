package com.example.mypantryapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckIngredients {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String dietWarnings;
    private ArrayList<String> dietNames = new ArrayList<>();
    private boolean dietValid = true;

    public CheckIngredients(final String ingredients) {
        final String[] ingrs = ingredients.split(" ");
        db.collection("dietary")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        String dietWarnings = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                List<String> blacklist = (List<String>) document.get("blacklist");
                                for (String ingr : ingrs) {
                                    ingr = ingr.toLowerCase();
                                    // removes trailing ,
                                    if (ingr.charAt(ingr.length()-1) == ',' || ingr.charAt(ingr.length()-1) == '.') {
                                        ingr = ingr.substring(0, ingr.length()-1);
                                    }
                                    if (blacklist.contains(ingr)) {
                                        dietValid = false;
                                        if (!dietNames.contains(name)) {
                                            if (dietWarnings.length() != 0) {
                                                // removes unwanted ", " doesn't work for last line
                                                dietWarnings = dietWarnings.substring(0, dietWarnings.length() - 1);
                                                dietWarnings += "\n";
                                            }
                                            dietWarnings += name + ": ";
                                            dietNames.add(name);
                                        }
                                        dietWarnings += ingr + ", ";
                                    }
                                }
                                if (dietWarnings.length() != 0) {
                                    // removes last ", "
                                    dietWarnings = dietWarnings.substring(0, dietWarnings.length() - 1);
                                }
                            }
                        }
                    }
                });
    }

    public ArrayList<String> getDietNames() {
        return dietNames;
    }

    public String getDietWarnings() {
        return dietWarnings;
    }

    public boolean isDietValid() {
        return dietValid;
    }
}
