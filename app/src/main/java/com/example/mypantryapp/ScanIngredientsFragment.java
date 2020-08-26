package com.example.mypantryapp;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class ScanIngredientsFragment extends Fragment {
    private CameraSource mCameraSource;
    private SurfaceView mCameraView;
    private TextView mTextView;
    public static final String TAG = "PLACEHOLDER";
    public static final int requestPermissionID = 100;// . or any other value
    private StringBuilder stringBuilder = new StringBuilder();

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

                    }
                }
            });

            // When the barcode icon is selected, the user should be navigated to the barcode fragment.
            takeSnapshot.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {

                    Button btnConfirm = getActivity().findViewById(R.id.btnConfirm);

                    if (takeSnapshot.getText().equals("Take Picture")) {
                        mCameraSource.stop();
                        takeSnapshot.setText("Retry");
                        // Show button for user to confirm
                        btnConfirm.setVisibility(View.VISIBLE);
                    } else if (takeSnapshot.getText().equals("Retry")) {
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

        }

    }


}