package com.info301.mypantryapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    // Get the user's information from Firestore.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    final String userId = currentUser.getUid();
    private int numberOfLines = 1;
    public String docRefPantry;
    public String docRefShoppingList;

    // Declare the views
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
    EditText settingsPantryName;
    Button addButton;


    UpdateUser UU;

    /**
     * Set the information based on the user's information
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Make sure that the bottom nav is not displayed.
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.GONE);

        // Initialise the view
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
        settingsPantryName = v.findViewById(R.id.settingsPantryName);

        // Set the fields based on what has previously been saved for the user
        db.collection("users")
                .whereEqualTo("userAuthId", userId)// Looks for the corresponding value with the field
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

    /**
     * Set the logic based on whether or not this is the first time the user is logged in.
     * @param view view
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // True if this is user's first time logging in, false otherwise.
        boolean isFirstRun = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        docRefPantry = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("pantryRef", null);
        docRefShoppingList = this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                .getString("shoppinglistRef", null);

        // Set the toolbar title to 'Settings'
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        // Initialise the navigation view
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        if (isFirstRun) {
            // If it is the first run, hide the nav header and drawer.
            navigationView.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            final Button addButton = (Button) view.findViewById(R.id.add_button);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Add_Line(view);
                }
            });
            //Set up pantry doc reference for user
            docRefPantry = db.collection("pantries").document().getId();
            // Save details in shared preference
            this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putString("pantryRef",
                    docRefPantry).apply();

            //Set up shoppinglist doc reference for user
            docRefShoppingList = db.collection("shoppinglists").document().getId();
            // Save details in shared preference
            this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putString("shoppinglistRef",
                    docRefShoppingList).apply();

            // Once details have been saved, navigate to HomeFragment and show the navigation.
            Button save = view.findViewById(R.id.saveDetails);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO check this!!
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("HomeFragment").commit();
                    setItem(view);
                    navigationView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    navigationView.setCheckedItem(R.id.nav_pantry);

                }
            });


            // Update shared preferences to reflect that the user has now been logged in
            this.getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();

        } else {
            // Otherwise, show the nav header and drawer.
            navigationView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            // Set the fields based on what has previously been saved for the user



            DocumentReference docRef = db.collection("pantries").document(docRefPantry);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            if (data.get("pantryName") != null) {
                                settingsPantryName.setText((CharSequence) data.get("pantryName"));
                            }

                            // TODO: set pantry setting fields somehow
                            if (data.get("kitchenLocations")!= null ){
                               // ArrayList<String> locations = data.get("kitchenLocations");
                            }
                        }
                    }
                }
            });
            db.collection("pantries")
                    .whereEqualTo("users", userId)// Looks for the corresponding value with the field
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
                                }
                            }
                        }
                    });


            Button addButton = view.findViewById(R.id.add_button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Add_Line(view);
                }
            });
            // Save details
            Button save = view.findViewById(R.id.saveDetails);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItem(view);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("HomeFragment").commit();
                    navigationView.setCheckedItem(R.id.nav_pantry);
                }
            });
        }
    }

    /**
     * Set the user's details
     * @param v
     */
    private void setItem(View v) {
        // Get what the user has entered
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

        //Get Pantry Details
        String pantryName = settingsPantryName.getText().toString();
        //get locations as array
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.linearLayoutDecisions);
        ArrayList<String> locations = new ArrayList<String>();
        int lLength = 0;
        for (int i=0; i<ll.getChildCount();  i++){
            EditText editText = (EditText)ll.getChildAt(i);
            String kL = editText.getText().toString();
            if (!kL.equals("")){
                locations.add(kL);
                lLength += 1;
            }
        }
        //ArrayList for multiple users, add current user id
        ArrayList<String> users = new ArrayList<>();
        users.add(userId);
        //Put pantry details in a map
        Map<String, Object> pantry = new HashMap<>();
        pantry.put("users", users);
        pantry.put("pantryName", pantryName);
        pantry.put("kitchenLocations", locations);

        // Put these details in a map
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

        //Make a new pantry and set the id, then add the pantry info to new document
        db.collection("pantries").document(docRefPantry).set(pantry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Details saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });

        // Set the user details accordingly
        db.collection("users").document(userId).set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(getContext(), "Your details are saved!", Toast.LENGTH_SHORT).show();
                        UU.setUser(name, email);
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

    /**
     * Here to send the user details to HomeFragment.
     * @param context context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            UU = (UpdateUser) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    /**
     * Send the user's name and email to HomeFragment so the navigation view can be
     * updated accordingly.
     */
    interface UpdateUser {
        void setUser(String name, String email);
    }


    public void Add_Line(View v) {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.linearLayoutDecisions);
        // add edittext when button clicked
        EditText et = new EditText(getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(p);
        et.setHint("Add a Location");
        et.setId(numberOfLines + 1);
        ll.addView(et);
        numberOfLines++;
    }

}