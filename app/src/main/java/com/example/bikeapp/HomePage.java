package com.example.bikeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<HomeModel> homeModel;
    private ExampleAdapter exampleAdapter;
    private TextView BicycleName, Price, avail;
    private Button rent, ganga, yamuna, saraswati;
    private Integer Gate, Bicycle;
    private FirebaseFirestore firestore;
    private AlphaAnimation fadeout,fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();


        //Default initial stage
        Gate=0;
        Bicycle=0;

        BicycleName=findViewById(R.id.bicycleName);
        Price=findViewById(R.id.price);
        avail=findViewById(R.id.avail);

        rent=findViewById(R.id.rentBtn);
        ganga=findViewById(R.id.gangaBtn);
        yamuna=findViewById(R.id.yamunaBtn);
        saraswati=findViewById(R.id.saraswatiBtn);
        //FireStore
        firestore= FirebaseFirestore.getInstance();
        viewPager=findViewById(R.id.viewPager2);

        //Bicycle Name and Price
        final String name[]={"GreenWalk Bicycle", "StarTrek Bicycle"};
        final String price[]={"₹45","₹60"};




        CheckAvailabilty(Gate,Bicycle);

        //rentOnclick
        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avail.getText().toString().equals("Available")){
                    onClickRent(Gate, Bicycle);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Bike N/A", Toast.LENGTH_LONG).show();
                }
            }
        });


        //gateClickListeners

        ganga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gate=0;
                CheckAvailabilty(Gate,Bicycle);
                ganga.setBackground(getDrawable(R.drawable.selectedrect));
                ganga.setTextColor(getColor(R.color.colorPrimaryGreen));

                yamuna.setBackground(getDrawable(R.drawable.rectangle));
                yamuna.setTextColor(getColor(R.color.white));

                saraswati.setBackground(getDrawable(R.drawable.rectangle));
                saraswati.setTextColor(getColor(R.color.white));

            }
        });

        yamuna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gate=1;
                CheckAvailabilty(Gate,Bicycle);
                yamuna.setBackground(getDrawable(R.drawable.selectedrect));
                yamuna.setTextColor(getColor(R.color.colorPrimaryGreen));

                ganga.setBackground(getDrawable(R.drawable.rectangle));
                ganga.setTextColor(getColor(R.color.white));

                saraswati.setBackground(getDrawable(R.drawable.rectangle));
                saraswati.setTextColor(getColor(R.color.white));

            }
        });

        saraswati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gate=2;
                CheckAvailabilty(Gate,Bicycle);
                saraswati.setBackground(getDrawable(R.drawable.selectedrect));
                saraswati.setTextColor(getColor(R.color.colorPrimaryGreen));

                yamuna.setBackground(getDrawable(R.drawable.rectangle));
                yamuna.setTextColor(getColor(R.color.white));

                ganga.setBackground(getDrawable(R.drawable.rectangle));
                ganga.setTextColor(getColor(R.color.white));

            }
        });

        //Image List
        homeModel=new ArrayList<>();
        homeModel.add(new HomeModel(R.drawable.cycle_1));
        homeModel.add(new HomeModel(R.drawable.cycle_two));

        exampleAdapter= new ExampleAdapter(homeModel);
        viewPager.setAdapter(exampleAdapter);

        //Change text View Animation
        fadeout= new AlphaAnimation(1f,0f);
        fadeout.setFillAfter(true);
        fadeout.setDuration(200);
        fadeout.setStartOffset(0);
        BicycleName.setAnimation(fadeout);
        Price.setAnimation(fadeout);
        avail.setAnimation(fadeout);

        fadeIn= new AlphaAnimation(0f,1f);
        fadeIn.setFillAfter(true);
        fadeIn.setDuration(200);
        fadeIn.setStartOffset(0);
        BicycleName.setAnimation(fadeIn);
        Price.setAnimation(fadeIn);
        avail.setAnimation(fadeIn);

        //OnPageChange
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(final int position) {


                Bicycle=position;
                BicycleName.startAnimation(fadeout);
                Price.startAnimation(fadeout);
                //avail.startAnimation(fadeout);
                CheckAvailabilty(Gate,Bicycle);

                Handler mhandler = new Handler();
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BicycleName.setText(name[position]);
                        Price.setText(price[position]);

                        BicycleName.startAnimation(fadeIn);
                        Price.startAnimation(fadeIn);

                    }
                },500);



                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    public void CheckAvailabilty(int gate, final int bicycle){
        // FireStore required string
        final String f_cycle[]={"available_bike1", "available_bike2","available_bike3"};
        final String f_gate[]={"Ganga", "Yamuna", "Saraswati"};
        final Long[] av = new Long[1];

        firestore.collection("pickup_locations").document(f_gate[gate]).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Long lg = documentSnapshot.getLong(f_cycle[Gate]);
                            String s= lg.toString();
                            Integer n= Integer.parseInt(s);
                            if(n>0){
                                avail.setText("Available");
                                rent.setBackground(getDrawable(R.drawable.rounded_button));
                            }else{
                                avail.setText("N/A");
                                rent.setBackground(getDrawable(R.drawable.roundedbtn_no));
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Oops", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onClickRent(int gate, int bicycle){
        final String f_cycle[]={"available_bike1", "available_bike2","available_bike3"};
        final String f_gate[]={"Ganga", "Yamuna", "Saraswati"};
        Intent i= new Intent(HomePage.this, Pricing.class);
        i.putExtra("Bicycle", f_cycle[bicycle]);
        i.putExtra("Gate",f_gate[gate]);
        startActivity(i);
    }

}