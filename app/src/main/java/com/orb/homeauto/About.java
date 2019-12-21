package com.orb.homeauto;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Intent i = getIntent();

    }


    public void makeCall(View view) {
        // intent to make a phone call
        Intent call = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("tel:01749817193"));
        startActivity(call);
    }


    public void gotoWebPage(View view) {
        // intent to go to a Webpage
        Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/bappyrahsan"));
        startActivity(i);


    }
}
