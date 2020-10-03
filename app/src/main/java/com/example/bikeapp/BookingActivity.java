package com.example.bikeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView pickup_dis,bike_dis,est_price,gate_name,bike_name;
    private ImageView gate_icon,bike_icon;
    private EditText hrs_dis,min_dis;
    private Button continue_btn;
    private Dialog loading;
    private Toolbar toolbar;
    private Spinner spinner,spinner2,spinner3;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    public int est_cost,hourly_rent=40;
    private String userID;
    public String pickup_time,rent_duration,payment_mode;
    public Map<String, Object> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        toolbar = findViewById(R.id.tbar);
        toolbar.setTitle("Confirm Booking");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pickup_dis = (TextView)findViewById(R.id.pickup_display);
        bike_dis = (TextView)findViewById(R.id.bike_display);
        est_price = (TextView)findViewById(R.id.est_price);
        gate_name =(TextView)findViewById(R.id.gate_name) ;
        gate_icon =(ImageView)findViewById(R.id.gate_icon);
        bike_name =(TextView)findViewById(R.id.bike_name) ;
        bike_icon=(ImageView)findViewById(R.id.bike_icon);
        hrs_dis = (EditText)findViewById(R.id.hrs_dis);
        min_dis = (EditText)findViewById(R.id.min_dis);
        continue_btn = findViewById(R.id.continue_btn);

        Bundle bundle = getIntent().getExtras();
        String pickup = bundle.get("Gate").toString();
//        info.put("current_pickup",bundle.get("Gate").toString());
        gate_name.setText(pickup);
        String bike = bundle.get("Bicycle").toString();          //this has to be changed later => replace with bike name
        bike_name.setText(bike);

       if(pickup.equals("Ganga"))
       {
           gate_icon.setImageResource(R.drawable.ic_icon_gate_ganga);
       }
        if(pickup.equals("Yamuna"))
        {
            gate_icon.setImageResource(R.drawable.ic_icon_gate_yamuna);
        }
        if(pickup.equals("Saraswati"))
        {
            gate_icon.setImageResource(R.drawable.ic_icon_gate_saraswati);
        }
        if(bike.equals("available_bike1"))
        {
            gate_icon.setImageResource(R.drawable.ic_icon_bicycle1);
        }
        if(pickup.equals("available_bike2"))
        {
            gate_icon.setImageResource(R.drawable.ic_icon_bicycle3);
        }

        hrs_dis.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "11")});
        min_dis.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});

        AdapterView.OnItemSelectedListener itemSelect = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = (String) parent.getItemAtPosition(position);
                est_cost = rentDurationStringToInt(a)*hourly_rent;
                est_price.setText(String.valueOf(est_cost));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner = findViewById(R.id.spinner);  //rent duration
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(BookingActivity.this,
                R.layout.spinner_item_am, getResources().getStringArray(R.array.renting_hours));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(itemSelect);
//        spinner.getSelectedItem().toString();

        spinner2 = findViewById(R.id.spinner2);    //AM/PM
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(BookingActivity.this,
                R.layout.spinner_item_am, getResources().getStringArray(R.array.am_pm));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(myAdapter2);

        spinner3 = findViewById(R.id.spinner3);    //
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
                pickup_time = hrs_dis.getText().toString() + ":" + min_dis.getText().toString() + " " + spinner2.getSelectedItem().toString();
                payment_mode = spinner3.getSelectedItem().toString();

//                Bundle bundle1 = getIntent().getExtras();
//                info.put("bike_selected",bundle1.get("Bicycle").toString());
//                info.put("current_pickup",bundle1.get("Gate").toString());
//                info.put("payment_mode",payment_mode);
//                info.put("pickup_time",pickup_time);
//                info.put("rent_duration",rent_duration);
//                info.put("est_price",est_cost);

                Intent intent = new Intent(BookingActivity.this,PaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public int rentDurationStringToInt(String duration) {   //helper method
       int time = 0;
       if(duration.equals("1 hour")) {
           time = 1;
       } else if(duration.equals("2 hours")) {
           time = 2;
       } else if(duration.equals("3 hours")) {
           time = 3;
       } else if(duration.equals("4 hours")) {
           time = 4;
       } else if(duration.equals("5 hours")) {
           time = 5;
       } else if(duration.equals("6 hours")) {
           time = 6;
       }
       return time;
    }


}