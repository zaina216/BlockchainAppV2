package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class accountInfo extends AppCompatActivity {

    //Display account info to user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        TextView seePubAddr = findViewById(R.id.pubAddr);
        TextView seePubKey = findViewById(R.id.pubKey);
        TextView seePrivKey = findViewById(R.id.privKey);

        Intent mainIntent = getIntent();
        seePubAddr.append(mainIntent.getStringExtra("pubAddr"));
        seePubKey.append(mainIntent.getStringExtra("PUBLIC_KEY"));
        seePrivKey.append(mainIntent.getStringExtra("PRIVATE_KEY"));
    }
}