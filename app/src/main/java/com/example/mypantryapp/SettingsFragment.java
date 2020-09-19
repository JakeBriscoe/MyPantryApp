package com.example.mypantryapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    final String userId = currentUser.getUid(); //get unique user id

    EditText settingsName;
    EditText settingsEmail;
    CheckBox checkBoxVegan;
    CheckBox checkBoxVegetarian;
    CheckBox checkBoxDairyFree;
    CheckBox checkBoxGlutenFree;
    CheckBox checkBoxCeliac;
    CheckBox checkBoxWheat;
    CheckBox checkBoxLactose;
    CheckBox checkBoxEgg;
    CheckBox checkBoxNut;
    CheckBox checkBoxPeanut;
    CheckBox checkBoxShellFish;
    CheckBox checkBoxSoy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.GONE);

        settingsName = v.findViewById(R.id.settingsName);
        settingsEmail = v.findViewById(R.id.settingsEmail);
        checkBoxVegan = v.findViewById(R.id.checkBoxVegan);
        checkBoxVegetarian = v.findViewById(R.id.checkBoxVegetarian);
        checkBoxDairyFree = v.findViewById(R.id.checkBoxDairyFree);
        checkBoxGlutenFree = v.findViewById(R.id.checkBoxGlutenFree);
        checkBoxCeliac = v.findViewById(R.id.checkBoxCeliac);
        checkBoxWheat = v.findViewById(R.id.checkBoxWheat);
        checkBoxLactose = v.findViewById(R.id.checkBoxLactose);
        checkBoxEgg = v.findViewById(R.id.checkBoxEgg);
        checkBoxNut = v.findViewById(R.id.checkBoxNut);
        checkBoxPeanut = v.findViewById(R.id.checkBoxPeanut);
        checkBoxShellFish = v.findViewById(R.id.checkBoxShellFish);
        checkBoxSoy = v.findViewById(R.id.checkBoxSoy);

        // Set the fields based on what has previously been saved for the user
        db.collection("users")
                .whereEqualTo("userAuthId", userId)//looks for the corresponding value with the field
                // in the database
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.get("name") != null) {
                                    settingsName.setText((CharSequence) document.get("name"));
                                }
                                settingsEmail.setText((CharSequence) document.get("email"));
                                if (document.get("vegan") != null) {
                                    checkBoxVegan.setChecked((boolean) document.get("vegan"));
                                }
                                if (document.get("vegetarian") != null) {
                                    checkBoxVegetarian.setChecked((boolean) document.get("vegetarian"));
                                }
                                if (document.get("df") != null) {
                                    checkBoxDairyFree.setChecked((boolean) document.get("df"));
                                }
                                if (document.get("gf") != null) {
                                    checkBoxGlutenFree.setChecked((boolean) document.get("gf"));
                                }
                                if (document.get("celiac") != null) {
                                    checkBoxCeliac.setChecked((boolean) document.get("celiac"));
                                }
                                if (document.get("wheat") != null) {
                                    checkBoxWheat.setChecked((boolean) document.get("wheat"));
                                }
                                if (document.get("lactose") != null) {
                                    checkBoxLactose.setChecked((boolean) document.get("lactose"));
                                }
                                if (document.get("eggs") != null) {
                                    checkBoxEgg.setChecked((boolean) document.get("eggs"));
                                }
                                if (document.get("nuts") != null) {
                                    checkBoxNut.setChecked((boolean) document.get("nuts"));
                                }
                                if (document.get("peanuts") != null) {
                                    checkBoxPeanut.setChecked((boolean) document.get("peanuts"));
                                }
                                if (document.get("shellFish") != null) {
                                    checkBoxShellFish.setChecked((boolean) document.get("shellFish"));
                                }
                                if (document.get("soy") != null) {
                                    checkBoxSoy.setChecked((boolean) document.get("soy"));
                                }
                            }
                        }
                    }
                });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Boolean isFirstRun = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        if (isFirstRun) {
            // If it is the first run, hide the nav header and drawer.
            navigationView.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);

            Button save = view.findViewById(R.id.saveDetails);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                    setItem(view);

                    navigationView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.nav_pantry1);
                }
            });

            this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();

        } else {
            // Otherwise, show the nav header and drawer.
            navigationView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            Button save = view.findViewById(R.id.saveDetails);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItem(view);
                }
            });
        }

        Toolbar mActionBarToolbar = getActivity().findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Settings");
    }

    private void setItem(View v) {
        String name = settingsName.getText().toString();
        String email = settingsEmail.getText().toString();
        boolean df = checkBoxDairyFree.isChecked();
        boolean gf = checkBoxGlutenFree.isChecked();
        boolean vegan = checkBoxVegan.isChecked();
        boolean vegetarian = checkBoxVegetarian.isChecked();
        boolean shellfish = checkBoxShellFish.isChecked();
        boolean wheat = checkBoxWheat.isChecked();
        boolean peanuts = checkBoxPeanut.isChecked();
        boolean nuts = checkBoxNut.isChecked();
        boolean lactose = checkBoxLactose.isChecked();
        boolean eggs = checkBoxEgg.isChecked();
        boolean soy = checkBoxSoy.isChecked();
        boolean celiac = checkBoxCeliac.isChecked();

        Map<String, Object> item = new HashMap<>();
        item.put("userAuthId", userId);
        item.put("name", name);
        item.put("email", email);
        item.put("df", df);
        item.put("gf", gf);
        item.put("vegan", vegan);
        item.put("vegetarian", vegetarian);
        item.put("shellfish", shellfish);
        item.put("wheat", wheat);
        item.put("peanuts", peanuts);
        item.put("nuts", nuts);
        item.put("lactose", lactose);
        item.put("eggs", eggs);
        item.put("soy", soy);
        item.put("celiac", celiac);

        db.collection("users").document(userId).set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Your details are saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });


    }
}