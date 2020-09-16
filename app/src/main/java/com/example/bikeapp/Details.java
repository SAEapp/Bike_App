package com.example.bikeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {

    EditText fName,  email;
    Button save;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fName = findViewById(R.id.fname);
        email = findViewById(R.id.email);
        save = findViewById(R.id.saveBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        final DocumentReference docRef = fStore.collection("users").document(userID);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fName.getText().toString().isEmpty() && !email.getText().toString().isEmpty()){

                    String fullName = fName.getText().toString();
                    String eMail = email.getText().toString();

                    Map<String,Object> user = new HashMap<>();
                    user.put("fName",fullName);
                    user.put("email",eMail);
                    user.put("phone",0);
                    user.put("bike_rented",false);
                    user.put("rent_duration",0);
                    user.put("payment_status","N/A");

                    docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("tag", "onSuccess: User Profile Created." + userID);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Failed to Create User " + e.toString());
                        }
                    });
                }
            }
        });

    }
}