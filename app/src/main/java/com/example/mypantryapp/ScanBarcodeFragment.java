package com.example.mypantryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;

import com.google.android.material.bottomnavigation.BottomNavigationView;



import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanBarcodeFragment extends Fragment {
    private CameraSource mCameraSource;
    private SurfaceView mCameraView;
    private TextView mTextView;
    public static final String TAG = "PLACEHOLDER";
    public static final int requestPermissionID = 100;// . or any other value



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        // Show bottom navigation
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation_drawer);
        navBar.setVisibility(View.VISIBLE);

        Toolbar mActionBarToolbar = getActivity().findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("[Pantry 1]");

//        mCameraView = getActivity().findViewById(R.id.surfaceView);
//        mTextView = getActivity().findViewById(R.id.text_view);

        return inflater.inflate(R.layout.fragment_scan_barcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        mCameraView = getActivity().findViewById(R.id.scanBarSufview);
        mTextView = getActivity().findViewById(R.id.text_view2);


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
                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                mCameraSource.stop();
                                //Update barcode value to TextView
                                mTextView.setText(String.valueOf((barcodes.valueAt(0).displayValue)));

                            }

                        });


                    }


                }
            });

        }
    }
}