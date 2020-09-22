package com.example.mypantryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * For clarification:
 * navigation drawer = the menu that pulls out from the left side of the screen,
 * bottom navigation = the menu that remains on the bottom of the screen.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                ScanIngredientsFragment.SendMessage,
                                                                AddItemFragment.SendDetails,
                                                                SettingsFragment.UpdateUser {
    private DrawerLayout drawer;
    BottomNavigationView bottomNav; // This needs to be here so it can be accessed in multiple methods

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        if (user == null){
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }
        else {
            currentUser = mAuth.getCurrentUser();
            boolean emailVerified = user.isEmailVerified();

            setContentView(R.layout.activity_main);

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, new AddItemManually(), "addManuallyTag")
//                    .commit();

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

            Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("isFirstRun", true);

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
                                    TextView headerUser = findViewById(R.id.headerUser);
                                    TextView headerEmail = findViewById(R.id.headerEmail);
                                    headerUser.setText((String) document.get("name"));
                                    headerEmail.setText((String) document.get("email"));
                                }
                            }
                        }
                    });

            // Make sure that it opens to the home fragment, but rotating screen doesn't restart state
            if (savedInstanceState == null) {
                Fragment fragment;

                if (isFirstRun) {
                    // Navigate to settings if it is the first time user has logged in
                    fragment = new SettingsFragment();
                    navigationView.setCheckedItem(R.id.nav_settings);
                } else {
                    // Navigate to home fragment
                    fragment = new HomeFragment();
                    navigationView.setCheckedItem(R.id.nav_pantry1);
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).addToBackStack(null).commit();

            }

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
                        new SettingsFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_add_pantry:
                // Just a placeholder
                Toast.makeText(this, "Add Pantry", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent);
            default:
                Log.e("TAG", "Unrecognized section: " + item.getItemId());

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
        } else if (currentFragment instanceof ScanBarcodeFragment ||
                        currentFragment instanceof AddItemManually ||
                        currentFragment instanceof ScanIngredientsFragment) {
            getFragmentManager().popBackStack();
            super.onBackPressed();
        } else if (currentFragment instanceof SettingsFragment) {
            // Navigate to home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).addToBackStack(null).commit();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_pantry1);
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


    /**
     * Replace the automatic toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public void sendData(String message) {
        AddItemManually f = (AddItemManually) getSupportFragmentManager().findFragmentByTag("addManuallyTag");
        assert f != null;
        f.displayReceivedData(message);
    }

    @Override
    public void sendDetails(ExampleItem item) {
        BottomSheetDialog f = new BottomSheetDialog();
        f.show(getSupportFragmentManager(), "bottomSheetTag");
        assert f != null;
        f.displayReceivedData(item);
    }

    @Override
    public void setUser(String name, String email) {
        TextView headerUser = findViewById(R.id.headerUser);
        TextView headerEmail = findViewById(R.id.headerEmail);
        headerUser.setText(name);
        headerEmail.setText(email);

        currentUser = mAuth.getCurrentUser();
        currentUser.verifyBeforeUpdateEmail(email.trim());
    }

    /**
     * Dictate what happens when each toolbar option is selected
     * @param item the item selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbarScan:
                // Ensure bottom nav corresponds to add item
                bottomNav.setSelectedItemId(R.id.nav_add_item);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScanBarcodeFragment()).addToBackStack(null).commit();
                return true;
            case R.id.toolbarShare:
                Toast.makeText(this, "Share Pantry", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.toolbarDelete:
                Toast.makeText(this, "Delete Pantry", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
