package com.example.blockchainappv2;

import static org.web3j.tx.Transfer.GAS_LIMIT;
import static org.web3j.tx.gas.DefaultGasProvider.GAS_PRICE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Random;

public class ItemMenu extends AppCompatActivity {

    private final String PRIVATE_KEY = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    //private final String PRIVATE_KEY = "ddfc78e76722eacbd5f9c4401fae889c7106b21abafa5cbe459a6048fa75c976";
    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final String RECIPIENT = "0x647067E5140f1Befe80d695b129a12F22f772675";
    //    private final String CONTRACT_ADDRESS = "0x22E279B66Bb08a61DF776e765B9519F9FA56673C";
//    private final String CONTRACT_ADDRESS = "0x1b208c90e60EDb5c53f7580dDf23861cA08EBd00";
//    private final String CONTRACT_ADDRESS = "0xb7b849e4b790906c9a2e2b1f6933e5403bad97c5";
    private final String CONTRACT_ADDRESS = "0x2100448fd5c91d5d28024561b23143f865d0f4a4";

    boolean addlayoutClicked = false;



    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    //                Web3j web3j = Web3j.build(new HttpService("http://192.168.1.108:8545"));
    Credentials credentials = getCredentialsFromPrivateKey();
    Sc_test nft = loadContract(CONTRACT_ADDRESS, web3j, credentials);

//    View mImageView = findViewById();
    int numOfItems=0;

    final int[] nftBal = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_menu);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                mImageView = (ImageView) findViewById(R.id.itemImage);
//                mImageView.setImageResource(R.drawable.pop_cat);


            }
        });

        //getting item inventory of current user
        Intent intent = getIntent();
        numOfItems = intent.getIntExtra("nftBal", 0);
//        int[] nftBal = new int[1];

        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    nftBal[0] = Integer.parseInt(nft.balanceOf("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send().toString());
                    System.out.println(nftBal[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                AppCompatButton btn = findViewById(R.id.startItemlist);
//                btn.performClick();



            }
        });
        thread.start();
        System.out.println("bal: "+ nftBal[0]);

        for(int i = 0; i < nftBal[0]; i++)
        {

            LinearLayout rl = (LinearLayout) findViewById(R.id.bottom_part);
            LayoutInflater inflater = getLayoutInflater();
            View itemLayout = inflater.inflate(R.layout.item, rl, false);

            // can set different values to the text fields in the item layout and then load them into the inventory layout.
            ImageView imgv = itemLayout.findViewById(R.id.itemImage);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (nftBal[0] != 0) {
                params.addRule(RelativeLayout.BELOW, itemLayout.getId());
            }

            rl.addView(itemLayout, params);
        }

        nftBal[0]++;

    }

    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(PRIVATE_KEY);
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
//        return Erc721.deploy(
//                web3j,
//                credentials,
//                GAS_PRICE,
//                GAS_LIMIT).send().getContractAddress();
        return com.example.blockchainappv2.Sc_test.deploy(web3j, credentials, new DefaultGasProvider()).send().getContractAddress();
    }

    private com.example.blockchainappv2.Sc_test loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return com.example.blockchainappv2.Sc_test.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    public void onClickAddLayout(View view)
    {

        System.out.println("bal: "+ nftBal[0]);

        if(addlayoutClicked)
        {
            recreate();
        }

        for(int i = 0; i < nftBal[0]; i++)
        {
            // can use this to set data to the item layout dynamically
            LinearLayout rl = (LinearLayout) findViewById(R.id.bottom_part);
            LayoutInflater inflater = getLayoutInflater();
            View itemLayout = inflater.inflate(R.layout.item, rl, false);
            Random rnd = new Random();
            int colour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            itemLayout.setBackgroundColor(colour);

            TextView itemTitleTxt = itemLayout.findViewById(R.id.itemTitle);
            //use strings when setting text
            itemTitleTxt.setText(String.valueOf(i));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (nftBal[0] != 0)
            {
                params.addRule(RelativeLayout.BELOW, itemLayout.getId());
            }

            rl.addView(itemLayout, params);

            addlayoutClicked = true;
        }

        nftBal[0]++;
        barcodeLauncher.launch(new ScanOptions());
    }



    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(ItemMenu.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ItemMenu.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

    // Launch
    public void onButtonClick(View view) {

    }
}













