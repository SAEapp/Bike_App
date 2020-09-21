package com.example.bikeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    Button logOut;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOut = findViewById(R.id.logout_button);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout(v);
                //FirebaseAuth.getInstance().signOut();
            }
        });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();      //logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public void ShowProfile(View view) {
        Intent intent = new Intent(MainActivity.this,CreateProfile.class);
        startActivity(intent);

    }
}