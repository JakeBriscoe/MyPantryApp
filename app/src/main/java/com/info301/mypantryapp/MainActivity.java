package com.info301.mypantryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.info301.mypantryapp.domain.ProductItem;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Map;


/**
 * For clarification:
 * navigation drawer = the menu that pulls out from the left side of the screen,
 * bottom navigation = the menu that remains on the bottom of the screen.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                ScanIngredientsFragment.SendMessage,
                                                                AddItemFragment.SendDetails,
                                                                SettingsFragment.UpdateUser,
                                                                ShoppingListFragment.SendDetailsShoppingList {
    // Declarations
    private DrawerLayout drawer;
    private BottomNavigationView bottomNav;

    // Get the information from Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;


    /**
     * Everything in onCreate should happen once only, and on first creation of the activity.
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Config.FLAG, Context.MODE_PRIVATE);
                if(sharedPreferences.getBoolean(Config.FLAG,true)){
                    startActivity(new Intent(MainActivity.this,DefaultIntro.class));
                    SharedPreferences.Editor e=sharedPreferences.edit();
                    e.putBoolean(Config.FLAG,false);
                    e.apply();
                }
            }
        });
        t.start();

        // Check if there is a user already logged in
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            // If no user is logged in, take them to the login activity.
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }
        else {
            // If a user is logged in, continue as normal.

            boolean emailVerified = currentUser.isEmailVerified();

            // Set up content view
            setContentView(R.layout.activity_main);

            // Initialise and set listeners for the navigation options
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

            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.dark));

            // Check whether or not this is the user's first time logging in.
            Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("isFirstRun", true);
            String userId = currentUser.getUid(); // Get unique user id, which corresponds with userAuthId in database



            // Get the user from database by matching userId (from authentication) to
            // userAuthId (from 'user' collection in database).
            db.collection("users")
                    .whereEqualTo("userAuthId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Set the header information to the user's information.
                                    TextView headerUser = findViewById(R.id.headerUser);
                                    TextView headerEmail = findViewById(R.id.headerEmail);

                                    // Don't try to set the TextViews if info hasn't been set yet.
                                    if (document.get("name") != null && headerUser != null) {
                                        headerUser.setText((String) document.get("name"));
                                    }
                                    if (document.get("email") != null && headerEmail != null) {
                                        headerEmail.setText((String) document.get("email"));
                                    }
                                }
                            }
                        }
                    });

            // This if-statement ensures that rotating the screen won't restart the state.
            if (savedInstanceState == null) {
                Fragment fragment;
                String fragmentTag;
                if (isFirstRun) {
                    // Navigate to settings if it is the first time user has logged in
                    fragment = new SettingsFragment();
                    fragmentTag = "SettingsFragment";
                    navigationView.setCheckedItem(R.id.nav_settings);
                } else {
                    // Navigate to home fragment otherwise
                    fragment = new HomeFragment();
                    fragmentTag = "HomeFragment";
                    navigationView.setCheckedItem(R.id.nav_pantry);

                    String docRefPantry = this.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                            .getString("pantryRef", null);
                    DocumentReference docRef = db.collection("pantries").document(docRefPantry);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> data = document.getData();
                                    if (data.get("pantryName") != null) {
                                        String pantryName = (String) data.get("pantryName");
                                        toolbar.setTitle(pantryName);
                                        // Get menu from navigationView
                                        Menu menu = navigationView.getMenu();
                                        MenuItem nav_pantry = menu.findItem(R.id.nav_pantry);
                                        // Set new title to the MenuItem
                                        nav_pantry.setTitle(pantryName);
                                    }

                                }
                            }
                        }
                    });
                    toolbar.setTitle(docRefPantry);
                }

                // Replace fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).addToBackStack(fragmentTag).commit();
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
            case R.id.nav_pantry:
                // Pop back stack if fragment has been created, create it otherwise
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).addToBackStack("HomeFragment").commit();
                bottomNav.setSelectedItemId(R.id.nav_home);
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).addToBackStack("SettingsFragment").commit();
                break;
            case R.id.nav_add_pantry:
                // Just a placeholder
                Toast.makeText(this, "Add Pantry Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent);
            default:
                Log.e("TAG", "Unrecognized section: " + item.getItemId());

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Determines what will happen when an item is selected on the bottom menu.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            if (!(currentFragment instanceof HomeFragment)) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new HomeFragment()).addToBackStack("HomeFragment").commit();
                            }
                            break;

                        case R.id.nav_add_item:
                            if (!(currentFragment instanceof AddItemFragment)) {
                                // Pop back stack if fragment has been created, create it otherwise
                                boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate("AddItemFragment", 0);
                                if (!fragmentPopped){ //fragment not in back stack, create it.
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            new AddItemFragment()).addToBackStack("AddItemFragment").commit();
                                }
                            }
                            break;

                        case R.id.nav_shop_list:
                            if (!(currentFragment instanceof ShoppingListFragment)) {
                                // Pop back stack if fragment has been created, create it otherwise
                                boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate("ShoppingListFragment", 0);
                                if (!fragmentPopped){ //fragment not in back stack, create it.
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            new ShoppingListFragment()).addToBackStack("ShoppingListFragment").commit();
                                }
                            }
                            break;
                    }

                    return true;
                }
            };

    /**
     * Dictate what happens when each toolbar option is selected
     * @param item the item selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbarScan:
                // Navigate to ScanBarcodeFragment
                // Ensure bottom nav corresponds to add item
                bottomNav.setSelectedItemId(R.id.nav_add_item);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScanBarcodeFragment()).addToBackStack("ScanBarcodeFragment").commit();
                return true;
            case R.id.toolbarShare:
                // Placeholder
                Toast.makeText(this, "Share Pantry Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.toolbarDelete:
                // Placeholder
                Toast.makeText(this, "Delete Pantry Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup what happens if the back button is pressed.
     * ADD FRAGMENT TO THE ELSEIF STATEMENT IF YOU WANT THE BACK BUTTON TO WORK FOR IT
     */
    @Override
    public void onBackPressed() {
        // Get the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        // If the side navigation is open and back is pressed, then the navigation should be closed.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment instanceof ScanBarcodeFragment ||
                currentFragment instanceof AddItemManuallyFragment ||
                currentFragment instanceof ScanIngredientsFragment) {
            // For the above fragments, clicking back should pop the stack and show the previous fragment.
            getFragmentManager().popBackStack();
            super.onBackPressed();
        } else if (currentFragment instanceof SettingsFragment) {
            // If back is pressed when settings is open, navigate to home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).addToBackStack("HomeFragment").commit();
            // Ensure that the checked item in the bottom navigation corresponds.
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_pantry);
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            // Exit app entirely
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    /************************************** Implement interfaces **********************************/

    /**
     * Replace the automatic toolbar with our own
     * @param menu the menu
     * @return success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Transfers data from ScanIngredientsFragment to AddItemManually.
     * @param message the ingredients scanned
     */
    @Override
    public void sendData(String message) {
        AddItemManuallyFragment f = (AddItemManuallyFragment) getSupportFragmentManager().findFragmentByTag("AddItemManuallyFragment");
        assert f != null;
        f.displayReceivedData(message);
    }

    /**
     * Transfers data from AddItemFragment to BottomSheetDialog.
     * @param item the product details
     */
    @Override
    public void sendDetails(ProductItem item, boolean isAddItem, CheckIngredients checkIngredients) {
        BottomSheetDialog f = new BottomSheetDialog();
        f.show(getSupportFragmentManager(), "bottomSheetTag");
        f.displayReceivedData(item, true, checkIngredients);
    }

    /**
     * Transfers details from SettingsFragment to the side navigation drawer.
     * @param name the user's name
     * @param email the user's email
     */
    @Override
    public void setUser(String name, String email, String pantryName) {
        TextView headerUser = findViewById(R.id.headerUser);
        TextView headerEmail = findViewById(R.id.headerEmail);
        headerUser.setText(name);
        headerEmail.setText(email);


        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem nav_pantry = menu.findItem(R.id.nav_pantry);
        nav_pantry.setTitle(pantryName);

        // TODO: reflect email change in Firestore authentication
        currentUser = mAuth.getCurrentUser();
        currentUser.verifyBeforeUpdateEmail(email.trim());
    }

    public void setPantry(String id, String name, ArrayList<String> locations) {
        // TODO: add content to use Pantry data
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
    }

    @Override
    public void sendDetailsShoppingList(ProductItem item, CheckIngredients checkIngredients) {
        BottomSheetDialog f = new BottomSheetDialog();
        f.show(getSupportFragmentManager(), "bottomSheetTag");
        f.displayReceivedData(item, false, checkIngredients);
    }
}
