package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ItemMenu extends AppCompatActivity {

    private ImageView mImageView;

    //This is a test to see if changes work.

    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_menu);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mImageView = (ImageView) findViewById(R.id.itemImage);
                mImageView.setImageResource(R.drawable.pop_cat);


            }
        });
    }

    public void addlayoutBtn(View view) {

        rl = findViewById(R.id.bottom_part);


    }
}