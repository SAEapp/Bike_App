package com.example.bikeapp;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText mFullName, mPassword,mEmail,codeEnter,mPhone;
    private Button mRegisterBtn,fbBtn;
    //private Button google_signinBtn;
    private GoogleSignInClient mgoogleSignInClient;
    private TextView mLoginBtn,state,alreadyReg,or;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private Dialog waiting;
    private int RC_SIGN_IN = 1;
    ProgressBar progressBar;
    CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress=false;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null){

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phnNo);
        mRegisterBtn = findViewById(R.id.registerBtn);
        fbBtn = findViewById(R.id.fbBtn);
        //phnNo = findViewById(R.id.phnNo);
        mLoginBtn = findViewById(R.id.createText);
        or = findViewById(R.id.or_text);
        alreadyReg = findViewById(R.id.alreadyReg);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        codeEnter = findViewById(R.id.codeEnter);
        state = findViewById(R.id.state);
        codePicker = findViewById(R.id.ccp);
        progressBar = findViewById(R.id.progressBar);

        //google_signinBtn = (SignInButton)findViewById(R.id.google_signin);
        SignInButton signInButton = findViewById(R.id.google_signin);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        waiting = new Dialog(Register.this);
        waiting.setContentView(R.layout.waiting_progressbar);
        waiting.setCancelable(true);
        waiting.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        waiting.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        if (fAuth.getCurrentUser() != null ) {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
            finish();
        }

        //creating user and adding data in firestore



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String phone = mPhone.getText().toString();

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
                if (!phone.matches("[0-9]+")) {
                    mPhone.setError("Must be a number");
                    return;
                } else {
                    if (phone.length() < 10 || phone.length() > 11) {
                        mPhone.setError("Invalid Number!");
                        return;
                    }
                }
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
                            user.put("phone", phone);
                            user.put("bike_rented",false);
                            //user.put("phone",0);
                            user.put("rent_duration"," ");
                            user.put("payment_status","");
                            user.put("payment_mode","");
                            user.put("pickup_time","");
                            user.put("est_price",0);
                            user.put("current_pickup","");
                            user.put("bike_selected","");
                           user.put("age","");
                           // user.put("name","");
                            user.put("bio","");
                           // user.put("phno","");
                            user.put("url","");

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

                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                            overridePendingTransition(android.R.anim.fade_in, R.anim.zoom);
                            finish();

                        } else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            waiting.cancel();
                        }
                    }
                });
            }
        });//register button clicked...

        fbBtn.setOnClickListener(new View.OnClickListener() {
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

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {

        Intent signInIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = fAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



    private void updateUI(FirebaseUser fUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            String fName = account.getDisplayName();
            String email = account.getEmail();
            final String id = account.getId();

            userID = fUser.getUid();

            final DocumentReference docRef = fStore.collection("users").document(userID);

            Map<String,Object> user = new HashMap<>();
            user.put("fName",fName);
            user.put("email",email);

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
}