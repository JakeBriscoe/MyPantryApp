package com.example.mypantryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * For clarification:
 * navigation drawer = the menu that pulls out from the left side of the screen,
 * bottom navigation = the menu that remains on the bottom of the screen.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    BottomNavigationView bottomNav; // This needs to be here so it can be accessed in multiple methods


    private static final String TAG = "AddItemManually";
    private static final String KEY_NAME= "name";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_QUANTITY = "quantity";
    private  static final String KEY_CATEGORY = "category";
    private static final String KEY_DIETARY = "dietary";


    private EditText editTextName;
    private EditText editTextBrand;
    private EditText editTextBarcode;
    private EditText editTextDescription;
    private EditText editTextQuantity;
    private EditText editTextCategory;
    private EditText editTextDietary;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_add_item_manually);
        editTextName =  (EditText) findViewById(R.id.productNameInputMan);
        editTextBrand =  (EditText) findViewById(R.id.brandInputMan);
        editTextBarcode =  (EditText) findViewById(R.id.barcodeInputMan);
        editTextDescription = (EditText) findViewById(R.id.descriptionInputMan);
        editTextQuantity = (EditText) findViewById(R.id.QuantityInputMan);
        //editTextCategory = findViewById(R.id.CategorySpinMan);
        //editTextDietary = findViewById(R.id.DietSpinMan);

        setContentView(R.layout.activity_main);



        // Set listeners for the navigation options
        bottomNav = findViewById(R.id.bottom_navigation_drawer);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        drawer = findViewById(R.id.navigation_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Replace automatic toolbar with our own toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add listener so we can toggle between the menu drawer being open and closed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Make sure that it opens to the home fragment, but rotating screen doesn't restart state
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).addToBackStack(null).commit();
            navigationView.setCheckedItem(R.id.nav_pantry1);
        }




    }


    public void addItemMan(View v) {
        String name = editTextName.getText().toString();
        String brand = editTextBrand.getText().toString();
        String barcode = editTextBarcode.getText().toString();
        String description = editTextDescription.getText().toString();
        String quantity = editTextQuantity.getText().toString();
//        String category = editTextCategory.getText().toString();
//        String dietary = editTextDietary.getText().toString();

        Map<String, Object> item = new HashMap<>();
        item.put(KEY_NAME, name);
        item.put(KEY_BRAND, brand);
        item.put(KEY_BARCODE, barcode);
        item.put(KEY_DESCRIPTION, description);
        item.put(KEY_QUANTITY, quantity);
//        item.put(KEY_CATEGORY, category);
//        item.put(KEY_DIETARY, dietary);

        db.collection("products").document().set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Item Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    /**
     * This method determines what will happen when an item is selected in the navigation menu.
     * @param item the item that was selected
     * @return true if method was successful
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_pantry1:
            case R.id.nav_pantry2:
                // This case is just to demonstrate that we want to be able to create multiple
                // pantry's for one user. No actual functionality atm.
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new HomeFragment()).commit();
                // Need to make sure the bottom navigation corresponds
                bottomNav.setSelectedItemId(R.id.nav_home);
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_add_pantry:
                // Just a placeholder
                Toast.makeText(this, "Add Pantry", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                // Placeholder
                Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Ensures that going back will simply close the navigation drawer if open.
     * ADD FRAGMENT TO THE ELSEIF STATEMENT IF YOU WANT THE BACK BUTTON TO WORK FOR IT
     */
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment instanceof ScanBarcodeFragment) {
            getFragmentManager().popBackStack();
            super.onBackPressed();
        } else {
            // Exit app entirely
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    /**
     * Determines what will happen when an item is selected on the bottom menu.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = new HomeFragment();
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_add_item:
                            selectedFragment = new AddItemFragment();
                            break;
                        case R.id.nav_recipes:
                            selectedFragment = new RecipesFragment();
                            break;
                        case R.id.nav_shop_list:
                            selectedFragment = new ShoppingListFragment();
                            break;
                        case R.id.nav_meal_plan:
                            selectedFragment = new PlanMealsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).addToBackStack(null).commit();

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

}