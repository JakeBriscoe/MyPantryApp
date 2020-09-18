package com.example.mypantryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

        public EditText emailId, passwd;
        Button btnSignUp;
        TextView signIn;
        FirebaseAuth firebaseAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.sign_up);
            firebaseAuth = FirebaseAuth.getInstance();
            emailId = findViewById(R.id.ETemail);
            passwd = findViewById(R.id.ETpassword);
            btnSignUp = findViewById(R.id.btnSignUp);
            signIn = findViewById(R.id.TVSignIn);
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String emailID = emailId.getText().toString();
                    String paswd = passwd.getText().toString();
                    if (emailID.isEmpty()) {
                        emailId.setError("Provide your Email first!");
                        emailId.requestFocus();
                    } else if (paswd.isEmpty()) {
                        passwd.setError("Set your password");
                        passwd.requestFocus();
                    } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                        firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this.getApplicationContext(),
                                            "SignUp unsuccessful: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                } else {

                                    // Then this is the first time the user has signed in
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putBoolean("isFirstRun", true).apply();

                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    String userId = currentUser.getUid(); //get unique user id

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("email", emailID);

                                    db.collection("users").document(userId).set(item)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                    Log.d("TAG", e.toString());
                                                }
                                            });
                                    }

                                    finish();
                                }

                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent I = new Intent(SignUpActivity.this, LogInActivity.class);
                    startActivity(I);
                }
            });
        }
    }

