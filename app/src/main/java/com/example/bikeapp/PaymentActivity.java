package com.example.bikeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;


public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "Test Payment" ;
    private Toolbar toolbar;
    private EditText enterAmt;
    private Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        //Checkout.preload(getApplicationContext());

        toolbar = findViewById(R.id.tbar1);
        enterAmt = findViewById(R.id.enterAmount);
        payBtn = findViewById(R.id.payBtn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterAmt.getText().toString().equals(""))
                {
                    Toast.makeText(PaymentActivity.this,"Please enter the amount",Toast.LENGTH_LONG).show();
                }
                else
                {
                    startPayment();
                }
            }
        });
        toolbar.setTitle("Payment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void startPayment() {

        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("<YOUR_KEY_ID>");

        /**
         * Set your logo here
         */
        //checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "SAE");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            String payment = enterAmt.getText().toString();
            double totalAmt = Double.parseDouble(payment);
            totalAmt = totalAmt* 100;
            options.put("amount", totalAmt);//pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact","9988776655");
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.e(TAG,"payment successful"+s.toString());
        Toast.makeText(this,"Payment Successful "+s.toString() , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e(TAG,"error code "+String.valueOf(i)+" == Payment failed "+s.toString() );
        try {
            Toast.makeText(this,"Payment error\nPlease try again",Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e("OnPaymentError","Exception is OnPaymentError");
        }

    }
}