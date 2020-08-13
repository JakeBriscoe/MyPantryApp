package com.example.mypantryapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getProducts();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void getProducts() {
        DocumentReference docRef = db.collection("products").document("test");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("exists", "DocumentSnapshot data: " + document.getData());
                        name = (TextView) getView().findViewById(R.id.test_name);
                        name.setText(document.getString("name"));

                    } else {
                        Log.d("doesn't exist", "No such document");
                    }
                } else {
                    Log.d("exists", "get failed with ", task.getException());
                }
            }
        });
    }
}