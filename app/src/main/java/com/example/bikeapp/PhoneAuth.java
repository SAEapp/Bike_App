package com.example.bikeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {
    private Button nextBtn;
    private EditText codeEnter,mPhone;
    private TextView mLoginBtn,state;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    ProgressBar progressBar;
    CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        codeEnter = findViewById(R.id.codeEnter);
        mPhone = findViewById(R.id.phone);
        state = findViewById(R.id.state);
        nextBtn = findViewById(R.id.nextBtn);
        codePicker = findViewById(R.id.ccp);
        progressBar = findViewById(R.id.progressBar);



        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationInProgress){
                    if(!mPhone.getText().toString().isEmpty() && mPhone.getText().toString().length() == 10){

                        String phoneNo = "+"+codePicker.getSelectedCountryCode()+mPhone.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP");
                        state.setVisibility(View.VISIBLE);
                        requestOTP(phoneNo);

                    }else
                    {
                        mPhone.setError("Phone number is not Valid");
                    }
                }else {
                    String userOTP = codeEnter.getText().toString();

                    if(!userOTP.isEmpty() && userOTP.length() == 6){

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userOTP);
                        verifyAuth(credential);

                    }else {
                        codeEnter.setError("Valis OTP is required");
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(fAuth.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Checking..");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }


    private void verifyAuth(PhoneAuthCredential credential) {

        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PhoneAuth.this, "Phone Verified."+fAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    checkUserProfile();
                }else {
                    progressBar.setVisibility(View.GONE);
                    state.setVisibility(View.GONE);
                    Toast.makeText(PhoneAuth.this, "Can not Verify phone and Create Account.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void requestOTP(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);
                verificationId =  s;
                token = forceResendingToken;
                nextBtn.setText("Verify");
                verificationInProgress = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(PhoneAuth.this, "Cannot create acount" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUserProfile() {
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else {
                    //Toast.makeText(Register.this, "Profile Do not Exists.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),Details.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PhoneAuth.this, "Profile Do Not Exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}