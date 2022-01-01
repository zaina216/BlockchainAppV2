package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ItemMenu extends AppCompatActivity {

    private ImageView mImageView;

    //This is a test to see if changes work.

    private LinearLayout rl;

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
    int numOfItems=0;
    public void addlayoutBtn(View view) {


        System.out.println(numOfItems);


        rl = (LinearLayout) findViewById(R.id.bottom_part);
        LayoutInflater inflater = getLayoutInflater();
        View itemLayout = inflater.inflate(R.layout.item, rl, false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (numOfItems != 0) {
            params.addRule(RelativeLayout.BELOW, itemLayout.getId());
        }

        rl.addView(itemLayout, params);
        numOfItems++;
    }
}