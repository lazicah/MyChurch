package com.disciplesbay.latterhousehq.mychurch;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.app.ConnectivityReceiver;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;

public class GivingActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    RotatingTextWrapper rotatingTextWrapper;
    Rotatable rotatable;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giving);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        CardView cardView = findViewById(R.id.card_view1);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GivingActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });



        ImageView imageView = findViewById(R.id.givingImage);
        imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.incorrect_shake));





        rotatingTextWrapper = (RotatingTextWrapper) findViewById(R.id.givingText);
        rotatingTextWrapper.setSize(48);
        rotatable = new Rotatable(Color.parseColor("#FFFFFF"), 3000, "Tithe", "Offering", "Donations");
        rotatable.setSize(48);
        rotatable.setAnimationDuration(500);
        rotatingTextWrapper.setContent("Pay ", rotatable);


    }
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
            snackbar.dismiss();


        } else {
            message = "Not connected to internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();


        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);


    }
}
