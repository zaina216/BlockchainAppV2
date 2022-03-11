package com.example.blockchainappv2;

import static org.web3j.tx.Transfer.GAS_LIMIT;
import static org.web3j.tx.gas.DefaultGasProvider.GAS_PRICE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemMenu extends AppCompatActivity {

    private final String PRIVATE_KEY = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    //private final String PRIVATE_KEY = "ddfc78e76722eacbd5f9c4401fae889c7106b21abafa5cbe459a6048fa75c976";
//    private final String PRIVATE_KEY = "35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9";

    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final String RECIPIENT = "0x647067E5140f1Befe80d695b129a12F22f772675";
    //    private final String CONTRACT_ADDRESS = "0x22E279B66Bb08a61DF776e765B9519F9FA56673C";
//    private final String CONTRACT_ADDRESS = "0x1b208c90e60EDb5c53f7580dDf23861cA08EBd00";
//    private final String CONTRACT_ADDRESS = "0xb7b849e4b790906c9a2e2b1f6933e5403bad97c5";
    private final String CONTRACT_ADDRESS = "0x2100448fd5c91d5d28024561b23143f865d0f4a4";
    private final String CONTRACT_ADDRESS_CHROME = "0xfeaa5fe401400505cfa259d780bd2062a854b13f"; //GetAllTokId

    boolean addlayoutClicked = false;


    private final String TAG = "ItemMenu";
    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    //                Web3j web3j = Web3j.build(new HttpService("http://192.168.1.108:8545"));
    Credentials credentials = getCredentialsFromPrivateKey();
//    Sc_test nft = loadContract(CONTRACT_ADDRESS, web3j, credentials);

    GetAllTokId nft = loadContract(CONTRACT_ADDRESS_CHROME, web3j, credentials);

//    View mImageView = findViewById();
    int numOfItems=0;

    final int[] nftBal = new int[1];

    int nftBala = 0;
    List nftIDs = null;
    int i = 0;
    String nftMetaData = "";
    ArrayList<String> itemNames = new ArrayList<>();

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

//        getting item inventory of current user
        Intent intent = getIntent();
//        numOfItems = intent.getIntExtra("nftBala", 0);
//        int[] nftBala = new int[1];

        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    nftBal[0] = Integer.parseInt(nft.balanceOf("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send().toString());
                    nftBala = Integer.parseInt(nft.balanceOf(CONTRACT_ADDRESS_CHROME).send().toString());
                    nftIDs = nft.getTokenIds(CONTRACT_ADDRESS_CHROME).send();
//                    System.out.println(nftBal[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                AppCompatButton btn = findViewById(R.id.startItemlist);
//                btn.performClick();



//                for(int i = 0; i < nftBala; i++)
//                {
//
//                    LinearLayout rl = (LinearLayout) findViewById(R.id.bottom_part);
//                    LayoutInflater inflater = getLayoutInflater();
//                    View itemLayout = inflater.inflate(R.layout.item, rl, false);
//
//                    // can set different values to the text fields in the item layout and then load them into the inventory layout.
//                    ImageView imgv = itemLayout.findViewById(R.id.itemImage);
//
//
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//                    if (nftBala != 0) {
//                        params.addRule(RelativeLayout.BELOW, itemLayout.getId());
//                    }
//
//                    rl.addView(itemLayout, params);
//                }

//                try {
//                    nftBala = Integer.parseInt(nft.balanceOf(CONTRACT_ADDRESS_CHROME).send().toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "Balance inside thread" + nftBala);
//
//                try {
//                    nftIDs = nft.getTokenIds(CONTRACT_ADDRESS_CHROME).send();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Log.d(TAG, "ID list inside thread" + nftIDs.toString());

            }
        });
        thread.start();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.itemInInventory);
//        itemNames.add("a");

//        rl.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(ItemMenu.this, "Long click!", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//        });
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

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    URL url = null;
    PinataUploadClass puc;
    PinataUploadClass[] apuc;
    Gson gson = new Gson();
    public void onClickAddLayout(View view) throws IOException {
//        Thread thread  = new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
////                int nftBala = 0;
//                try {
//                    nftBala = Integer.parseInt(nft.balanceOf(CONTRACT_ADDRESS_CHROME).send().toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "Balance inside thread" + nftBala);
//
//                try {
//                    nftIDs = nft.getTokenIds(CONTRACT_ADDRESS_CHROME).send();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Log.d(TAG, "ID list inside thread" + nftIDs.toString());
//
//
//            }
//        });
//        thread.start();
//        if(addlayoutClicked)
//        {
//            recreate();
//        }

        for(i = 0; i < nftBala; i++)
        {
//            System.out.println("here");
            // can use this to set data to the item layout dynamically
            LinearLayout rl = (LinearLayout) findViewById(R.id.bottom_part);
            LayoutInflater inflater = getLayoutInflater();
            View itemLayout = inflater.inflate(R.layout.item , rl, false);
            Random rnd = new Random();
//            int colour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            itemLayout.setBackgroundColor(colour);

            TextView itemTitleTxt = itemLayout.findViewById(R.id.itemTitle);
            //use strings when setting text
            assert nftIDs != null;

            Thread thread2  = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nftMetaData = nft.tokenURI( (BigInteger)nftIDs.get(i) ).send();

//                        String json = readUrl(nftMetaData);
//
//                        Gson gson = new Gson();
//                        puc = gson.fromJson(json, PinataUploadClass.class);
//                        itemNames.add(puc.itemName);
//
//                        System.out.println(puc.itemName);

//                        url = new URL(nftMetaData);
//                        System.out.println("in thread - " + url);
//                        InputStreamReader reader = new InputStreamReader(url.openStream());
//                        puc = gson.fromJson(reader, PinataUploadClass.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread2.start();
            try {
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            URL


            System.out.println(nftMetaData);

            itemTitleTxt.setText( nftMetaData);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (nftBala != 0)
            {
                params.addRule(RelativeLayout.BELOW, itemLayout.getId());
            }
            rl.addView(itemLayout, params);
            addlayoutClicked = true;
        }
        i = 0;
//        barcodeLauncher.launch(new ScanOptions());
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
    // Create barcode scanning event and initialise camera


    // Launch
    public void onButtonClick(View view) {

    }

    public void bruh(View view) {

    }
    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            Log.d("", "Longpress detected");
        }
    });

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}