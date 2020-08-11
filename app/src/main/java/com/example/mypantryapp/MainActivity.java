package com.example.mypantryapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * For clarification:
 * navigation drawer = the menu that pulls out from the left side of the screen,
 * bottom navigation = the menu that remains on the bottom of the screen.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    BottomNavigationView bottomNav; // This needs to be here so it can be accessed in multiple methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_pantry1);
        }

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
                        new SettingsFragment()).commit();
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
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
                            selectedFragment).commit();

                    return true;
                }
            };

}