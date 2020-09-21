package com.example.bikeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Showprofile extends AppCompatActivity {
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    TextView nameEt,ageEt,emailEt,phnoEt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showprofile);

        imageView = findViewById(R.id.imageView_sp);
        nameEt = findViewById(R.id.name_tv_sp);
        ageEt = findViewById(R.id.age_tv_sp);
        emailEt = findViewById(R.id.email_tv_sp);
        phnoEt = findViewById(R.id.phno_tv_sp);

        documentReference = db.collection("user").document("profile");
        storageReference = FirebaseStorage.getInstance().getReference("profile images");

    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (Objects.requireNonNull(task.getResult()).exists()){
                            String name_result = task.getResult().getString("name");
                            String age_result = task.getResult().getString("age");
                            String email_result = task.getResult().getString("email");
                            String phno_result = task.getResult().getString("phno");
                            String Url = task.getResult().getString("url");

                            Picasso.get().load(Url).into(imageView);

                            nameEt.setText(name_result);
                            ageEt.setText(age_result);
                            emailEt.setText(email_result);
                            phnoEt.setText(phno_result);

                            

                        }else {
                            Toast.makeText(Showprofile.this, "No Profile exists!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}