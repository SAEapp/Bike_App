package com.example.bikeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateProfile extends AppCompatActivity {

    EditText fName,et_age,phone,et_bio,email;
    Button button;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE=1;
    UploadTask uploadTask;
    private FirebaseAuth fAuth;
    private String userID;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db;
    DocumentReference documentReference;
    ImageView imageView;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        imageView = findViewById(R.id.imageview_cp);
        fName = findViewById(R.id.name_et_cp);
        email = findViewById(R.id.email_et_cp);
        et_age = findViewById(R.id.age_et_cp);
        et_bio = findViewById(R.id.bio_et_cp);
        phone = findViewById(R.id.phno_et_cp);
        button = findViewById(R.id.save_profile_btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        documentReference = db.collection("users").document(userID);
        storageReference = firebaseStorage.getInstance().getReference("profile images");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });

    }

    public void ChooseImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null) {

            imageUri = data.getData();

            Picasso.get().load(imageUri).into(imageView);
        }

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadData()
    {
         final String name = fName.getText().toString();
         final String age = et_age.getText().toString();
         final String bio = et_bio.getText().toString();
         final String phno = phone.getText().toString();
         final String em = email.getText().toString();

        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(age) || !TextUtils.isEmpty(bio) || !TextUtils.isEmpty(phno) || !TextUtils.isEmpty(em) || imageUri!=null) {


            progressBar.setVisibility(View.VISIBLE);

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));

            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return reference.getDownloadUrl();
                }
            })

                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                Map<String ,String > user = new HashMap<>();
                                user.put("fName",name);
                                user.put("age",age);
                                user.put("phone",phno);
                                user.put("email",em);
                                user.put("bio",bio);
                                assert downloadUri != null;
                                user.put("url",downloadUri.toString());

                                documentReference.set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CreateProfile.this,Showprofile.class);
                                                startActivity(intent);



                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CreateProfile.this, "Failed!", Toast.LENGTH_SHORT).show();

                                            }
                                        });



                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });
        }else{
            Toast.makeText(this, "All fields required!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String name_result = task.getResult().getString("fName");
                            String age_result = task.getResult().getString("age");
                            String bio_result = task.getResult().getString("bio");
                            String phno_result = task.getResult().getString("phone");
                            String em_result = task.getResult().getString("email");
                            String Url = task.getResult().getString("url");

                            if (Url.isEmpty()) {
                                imageView.setImageResource(R.drawable.ic_icon_person);
                            } else{
                                Picasso.get().load(Url).into(imageView);
                            }

                            //Picasso.get().load(Url).into(imageView);

                            fName.setText(name_result);
                            et_age.setText(age_result);
                            et_bio.setText(bio_result);
                            phone.setText(phno_result);
                            email.setText(em_result);



                        }else {
                            Toast.makeText(CreateProfile.this, "No Profile exists!", Toast.LENGTH_SHORT).show();
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