package com.example.mypantryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanIngredientsFragment extends Fragment {
    private CameraSource mCameraSource;
    private SurfaceView mCameraView;
    private TextView mTextView;
    public static final String TAG = "PLACEHOLDER";
    public static final int requestPermissionID = 100;// . or any other value
    private StringBuilder stringBuilder = new StringBuilder();
    private Button btnConfirm;
    public String ingredients;
    public ArrayList<String> dietWarnings;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View view;

    SendMessage SM;
    String message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Show bottom navigation
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        return inflater.inflate(R.layout.fragment_scan_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        mCameraView = getActivity().findViewById(R.id.surfaceView);
        final Button takeSnapshot = getActivity().findViewById(R.id.btnTakePicture);

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getActivity().getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                /**
                 * Release resources for cameraSource
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {

                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");
                        }
                        //might need to change where this is
                        transformMessage(stringBuilder.toString().trim());

                    }
                }
                //maybe will need else set colour blank
            });

            btnConfirm = getActivity().findViewById(R.id.btnConfirm);

            // When the barcode icon is selected, the user should be navigated to the barcode fragment.
            takeSnapshot.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {

                    if (takeSnapshot.getText().equals("Take Picture")) {
                        mCameraSource.stop();
                        takeSnapshot.setText("Try Again");
                        // Show button for user to confirm
                        btnConfirm.setVisibility(View.VISIBLE);
                    } else if (takeSnapshot.getText().equals("Try Again")) {
                        try {

                            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA},
                                        requestPermissionID);
                                return;
                            }
                            mCameraSource.start(mCameraView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        takeSnapshot.setText("Take Picture");
                        btnConfirm.setVisibility(View.INVISIBLE);
                    }
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {

                    try {

                        message = transformMessage(stringBuilder.toString().trim());
                        SM.sendData(message);
                        assert getFragmentManager() != null;
                        getFragmentManager().popBackStack();

                    } catch (ArrayIndexOutOfBoundsException exception) {
                        Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                        btnConfirm.setVisibility(View.VISIBLE);
                        takeSnapshot.setText("Take Picture");
                    }
                }
            });

        }

    }

    interface SendMessage {
        void sendData(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    /**
     * Modify the message passed so that unnecessary words are ignored
     *
     * ASSUMPTIONS
     * ------------------------
     * The format will include "Ingredients ...", "May contain ..." and "Contains ..."
     * There is only one occurrence of 'Ingredients' in the snapshot taken
     * The camera picks up the full stops
     *
     * TODO: consider "May be present: ..."
     *
     * @param data the result from scan ingredients
     * @return the modified string
     */
    protected String transformMessage(String data) {

        // Start string after "Ingredients"
        // Get two substrings: "Contains ..." and "May contain ..." based on occurrence and consequent full stop.
        // If "Contains ..." and "May contain ..." doesn't exist, return without altering
        // Otherwise, get rid of everything after the first occurrence, and concatenate the two substrings

        int iIngredients; // This should be at the last occurrence of 'Ingredients'

        // Need to consider the case where the word ingredients is in capitals
        int iLower = data.lastIndexOf("Ingredients");
        int iUpper = data.lastIndexOf("INGREDIENTS");
        if (iLower > iUpper) {
            iIngredients = iLower;
        } else {
            iIngredients = iUpper;
        }

        // If there is no occurrence of 'Ingredients', then there are no ingredients to identify
        if (iIngredients == -1) {
            Toast.makeText(getActivity(), "No ingredients identified", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Fix formatting issues
        String ingredients = data.substring(iIngredients + 11).replace("\n", " ");

        // Start our result at the first occurrence of a letter
        // To account for cases such as "Ingredients: ..." as well as "Ingredients ..."
        ingredients = ingredients.substring(findFirstLetterPosition(ingredients));

        // Get indexes of "May contain ..." and "Contains ...". Account for capitals.
        int iMayContain = ingredients.indexOf("MAY CONTAIN");
        int iContains = ingredients.indexOf("CONTAINS");
        if (iMayContain == -1) {
            iMayContain = ingredients.indexOf("May contain");
        }
        if (iContains == -1) {
            iContains = ingredients.indexOf("Contains");
        }

        // Get substrings which contain the "May contain ..." and "Contains ..." information.
        // This is done by taking a substring of ingredients from the trigger word until the next full stop.
        // Need to make sure that the indices are viable.
        String mayContain;
        String contains;

        if (iMayContain == -1) {
            mayContain = "";
        } else {
            mayContain = ingredients.substring(iMayContain, ingredients.indexOf(".", iMayContain) + 1);
            //may contain ingredients (ingredients type, may need to add in firebase)
        }

        if (iContains == -1) {
            contains = "";
        } else {
            contains = ingredients.substring(iContains, ingredients.indexOf(".", iContains) + 1);
        }

        String result;

        // Now decide where to finish the ingredients string based on "Contains ..." and "May contain ..."
        if (iMayContain < iContains && iMayContain != -1) { // Both exist, and "May contain ..." comes first
            ingredients = ingredients.substring(0, iMayContain);
            result = ingredients + "\n\n" + contains + "\n\n" + mayContain;
        } else if (iContains < iMayContain && iContains != -1) { // Both exist, and "Contains ..." comes first
            ingredients = ingredients.substring(0, iContains);
            result = ingredients + "\n\n" + contains + "\n\n" + mayContain;
        } else if (iMayContain == -1 && iContains == -1) { // Neither exist
            int stop = ingredients.indexOf(".");
            // While the full stop is due to a decimal, continue looking for the final full stop.
            while (ingredients.substring(stop-1, stop).matches("\\d+") && ingredients.substring(stop+1, stop+2).matches("\\d+")) {
                stop = ingredients.indexOf(".", stop+1);
            }
            ingredients = ingredients.substring(0, ingredients.indexOf(".") + 1);
            result = ingredients;
        } else if (iMayContain != -1) { // Only "May contain ..." exists
            ingredients = ingredients.substring(0, iMayContain);
            result = ingredients + "\n\n" + mayContain;
        } else { // Only "Contain  s ..." exists
            ingredients = ingredients.substring(0, iContains);
            result = ingredients + "\n\n" + contains;
        }

        return result;
    }

    /**
     * Helper function
     * @param input the message
     * @return Occurrence of first alphabetical character
     */
    public int findFirstLetterPosition(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isLetter(input.charAt(i))) {
                return i;
            }
        }
        return -1; // not found
    }

    /**
     * Checks if a products ingredients do not match a users diet and updates the background color
     * accordingly
     * @param ingrs ingredients list
     */
    public void checkIngredients(final String[] ingrs, final boolean mayContain, final boolean finalPic) {
        dietWarnings = new ArrayList<>();
        db.collection("dietary")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            docLoop:
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = document.getString("name");
                                List<String> blacklist = (List<String>) document.get("blacklist");
                                for(String ingr: ingrs) {
                                    if(blacklist.contains(ingr)) {
                                        if(mayContain) {
                                            view.setBackgroundColor(Color.YELLOW);
                                        } else {
                                            view.setBackgroundColor(Color.RED);
                                        }
                                        // add formating here, mb \n name:
                                        if (!dietWarnings.contains(name)) {
                                            dietWarnings.add(name);
                                        }
                                        dietWarnings.add(ingr);
                                    }
                            }
                            if(dietWarnings.size() == 0) {
                                view.setBackgroundColor(Color.GREEN);
                            }
                            if(finalPic) {
                                SM.sendData(dietWarnings);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
