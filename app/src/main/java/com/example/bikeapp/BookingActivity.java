package com.example.bikeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView pickup_dis,bike_dis,est_price;
    private EditText hrs_dis,min_dis;
    private Button continue_btn;
    private Dialog loading;
    private Toolbar toolbar;
    private Spinner spinner,spinner2,spinner3;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    public int est_cost;
    private String userID;
    public String pickup_time,rent_duration,payment_mode;
    public Map<String, Object> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        toolbar = findViewById(R.id.tbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pickup_dis = (TextView)findViewById(R.id.pickup_display);
        bike_dis = (TextView)findViewById(R.id.bike_display);
        est_price = (TextView)findViewById(R.id.est_price);        //figure this out later
        hrs_dis = (EditText)findViewById(R.id.hrs_dis);
        min_dis = (EditText)findViewById(R.id.min_dis);
        continue_btn = findViewById(R.id.continue_btn);

        Bundle bundle = getIntent().getExtras();
        String pickup = "Pickup : " + bundle.get("Gate").toString();
        pickup_dis.setText(pickup);
        String bike = "Bike : " + bundle.get("Bicycle").toString();          //this has to be changed later => replace with bike name
        bike_dis.setText(bike);

        hrs_dis.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "11")});
        min_dis.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(BookingActivity.this,
                R.layout.spinner_item_am, getResources().getStringArray(R.array.renting_hours));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
//        spinner.getSelectedItem().toString();

        spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(BookingActivity.this,
                R.layout.spinner_item_am, getResources().getStringArray(R.array.am_pm));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(myAdapter2);

        spinner3 = findViewById(R.id.spinner3);
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(BookingActivity.this,
                R.layout.spinner_item_am, getResources().getStringArray(R.array.payment_modes));
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(myAdapter3);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        loading = new Dialog(BookingActivity.this);
        loading.setContentView(R.layout.waiting_progressbar);
        loading.setCancelable(false);
        loading.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loading.show();
                rent_duration =  spinner.getSelectedItem().toString();
                est_cost = 40;    //////////////////////////////////////////////////////incomplete
                pickup_time = hrs_dis.getText().toString() + ":" + min_dis.getText().toString() + " " + spinner2.getSelectedItem().toString();
                payment_mode = spinner3.getSelectedItem().toString();
                Intent intent = new Intent(BookingActivity.this,PaymentActivity.class);
                startActivity(intent);
            }
        });

    }
}