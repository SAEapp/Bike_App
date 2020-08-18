package com.example.bikeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText mFullName, mPassword,mEmail,codeEnter;
    private Button mRegisterBtn,phBtn;
    private TextView mLoginBtn,state;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private Dialog waiting;
    ProgressBar progressBar;
    CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        //mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerBtn);
        phBtn = findViewById(R.id.phBtn);
        mLoginBtn = findViewById(R.id.createText);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        codeEnter = findViewById(R.id.codeEnter);
        state = findViewById(R.id.state);
        codePicker = findViewById(R.id.ccp);
        progressBar = findViewById(R.id.progressBar);

        waiting = new Dialog(Register.this);
        waiting.setContentView(R.layout.waiting_progressbar);
        waiting.setCancelable(true);
        waiting.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        waiting.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        if (fAuth.getCurrentUser() != null ) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //creating user and adding data in firestore



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                //final String phone = mPhone.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required!");
                }
                if (password.length() < 6) {
                    mPassword.setError("Password too short!");
                    return;
                }
//                if (!phone.matches("[0-9]+")) {
//                    mPhone.setError("Must be a number");
//                    return;
//                } else {
//                    if (phone.length() < 10 || phone.length() > 11) {
//                        mPhone.setError("Invalid Number!");
//                        return;
//                    }
//                }
                if (fullName.length() > 13) {
                    mFullName.setError("Must be less than 13 characters.");
                    return;
                }
                waiting.show();
                //register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {



                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("email", email);
                            //user.put("phone", phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, R.anim.zoom);
                            finish();

                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            waiting.cancel();
                        }
                    }
                });
            }
        });

        phBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PhoneAuth.class));
                finish();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }



}