package com.info301.mypantryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;

public class ScanBarcodeFragment extends Fragment {
    private CameraSource mCameraSource;
    private SurfaceView mCameraView;
    public static final String TAG = "PLACEHOLDER";
    public static final int requestPermissionID = 100;// . or any other value

    String message = "";
    Button btnConfirm;
    Button btnCancel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CheckIngredients checkIngredients = new CheckIngredients();

    /**
     * Set up any static views
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // POSSIBLY NOT NEEDED. Show bottom navigation.
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);


        // Get the camera view
        mCameraView = getActivity().findViewById(R.id.scanBarSufview);

        return inflater.inflate(R.layout.fragment_scan_barcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        mCameraView = getActivity().findViewById(R.id.scanBarSufview);
        btnConfirm = getActivity().findViewById(R.id.scanBarcode);
        btnCancel = getActivity().findViewById(R.id.scanBarcodeCancel);

        //Create the Barcode Detector
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getActivity().getApplicationContext())
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        if (!detector.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
            return;
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getActivity().getApplicationContext(), detector)
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

            detector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() > 0) {
                        message = String.valueOf((barcodes.valueAt(0).displayValue));
                    }


                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });

            // Set the onclick listener for taking a pic of the barcode.
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    try {
                        if (message.equals("") || message == null) {
                            // If no barcode has been identified, then prompt the user to try again.
                            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
                        } else {
                            // If a barcode has been identified:
                            // Determine if it exists in our 'products' collection in Firestore.
                            db.collection("products")
                                    .whereEqualTo("barcodeNum", Long.parseLong(message))//looks for the corresponding value with the field in the database
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if(task.getResult().isEmpty()){
                                                    // Navigate to AddItemManually
                                                    // Pop the stack if previous fragment was AddItemManually, make a new instance otherwise
                                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                                    String previousFragment = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 2).getName();
                                                    if (previousFragment == null) {
                                                        getActivity().getSupportFragmentManager().popBackStack();
                                                    } else {
                                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddItemManuallyFragment(), "AddItemManuallyFragment").addToBackStack(null).commit();
                                                    }
                                                    // Send the barcode to AddItemManually so it can be pre-populated.
                                                    Bundle result = new Bundle();
                                                    result.putString("bundleKey", message);
                                                    getParentFragmentManager().setFragmentResult("requestBarcode", result);
                                                }
                                                else {
                                                    // If the barcode does exist, BottomSheetDialog should pop up
                                                    // and display the product details.
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        if (document.exists()) {
                                                            String dietWarnings = checkIngredients.checkIngredients((String) document.get("ingredients"));

                                                            BottomSheetDialog f = new BottomSheetDialog();
                                                            f.show(getFragmentManager(), "bottomSheetTag");
                                                            // Send the barcode information to BottomSheetDialog
                                                            Bundle results = new Bundle();
                                                            results.putString("bundleName", (String) document.get("name"));
                                                            results.putString("bundleBrand", (String) document.get("brand"));
                                                            results.putString("bundleId", document.getId());
                                                            results.putLong("bundleShelfLife", (Long) document.get("shelfLife"));
                                                            getParentFragmentManager().setFragmentResult("requestProductDetails", results);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}