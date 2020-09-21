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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateProfile extends AppCompatActivity {

    EditText et_name,et_age,et_phno,et_email;
    Button button;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE=1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        imageView = findViewById(R.id.imageview_cp);
        et_name = findViewById(R.id.name_et_cp);
        et_age = findViewById(R.id.age_et_cp);
        et_email = findViewById(R.id.email_et_cp);
        et_phno = findViewById(R.id.phno_et_cp);
        button = findViewById(R.id.save_profile_btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);

        documentReference = db.collection("user").document("profile");
        storageReference = FirebaseStorage.getInstance().getReference("profile images");

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

        assert data != null;
        imageUri = data.getData();

        Picasso.get().load(imageUri).into(imageView);

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadData()
    {
        final String name = et_name.getText().toString();
        final String age = et_age.getText().toString();
        final String email = et_email.getText().toString();
        final String phno = et_phno.getText().toString();

        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(age) ||
                !TextUtils.isEmpty(email) || !TextUtils.isEmpty(phno) || imageUri!=null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));

            uploadTask = reference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
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
                                Map<String,String> profile = new HashMap<>();
                                profile.put("name",name);
                                profile.put("age",age);
                                profile.put("phno",phno);
                                profile.put("email",email);
                                assert downloadUri != null;
                                profile.put("url",downloadUri.toString());

                                documentReference.set(profile)
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
}