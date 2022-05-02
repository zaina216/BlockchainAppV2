package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
//        getting item inventory of current user
public class ItemMenu extends AppCompatActivity {

    private String privK = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";

    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final String CONTRACT_ADDRESS_CHROME = "0xfeaa5fe401400505cfa259d780bd2062a854b13f"; //GetAllTokId

    boolean addlayoutClicked = false;
    private String pubAddr = "";


    private final String TAG = "ItemMenu";
    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    //                Web3j web3j = Web3j.build(new HttpService("http://192.168.1.108:8545"));
    Credentials credentials = getCredentialsFromPrivateKey();
//    Sc_test nft = loadContract(CONTRACT_ADDRESS, web3j, credentials);

    GetAllTokId nft = loadContract(CONTRACT_ADDRESS_CHROME, web3j, credentials);

    int nftBala = 0;
    List nftIDs = null;
    int i = 0;
    String nftMetaData = "";
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemDescs = new ArrayList<>();
    ArrayList<String> itemDates = new ArrayList<>();

    private Runnable task = new Runnable() {
        public void run() {
            Toast.makeText(getApplicationContext(), "Fetching your inventory...", Toast.LENGTH_LONG).show();
            try {
                displayInventory();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
    View itemLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_menu);
        itemLayout = findViewById(R.id.bottom_part);
        Toast.makeText(getApplicationContext(), "Fetching your inventory...", Toast.LENGTH_LONG).show();



        Intent intent = getIntent();
        pubAddr = intent.getStringExtra("pubAddr");
        privK = intent.getStringExtra("PRIVATE_KEY");
        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println("item pubAddr" + pubAddr);
                    nftBala = Integer.parseInt(nft.balanceOf(pubAddr).send().toString());
                    nftIDs = nft.getTokenIds(pubAddr).send();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        thread.start();
        try{
            thread.join();
        } catch (Exception y){
            y.printStackTrace();
        }


        Handler handler = new Handler();
        handler.postDelayed(task, 2000);

        Toast.makeText(getApplicationContext(), "Fetching your inventory...",
                Toast.LENGTH_LONG).show();
        // run for loop on ui thread for espresso testing
//        try {
//            displayInventory();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(privK);
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        return com.example.blockchainappv2.Sc_test.deploy(web3j, credentials,
                new DefaultGasProvider()).send().getContractAddress();
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    PinataUploadClass puc;
    TextView itemTitleTxt = null;
    TextView itemDescTxt = null;
    ImageView itemImg = null;



    public void displayInventory() throws IOException {

        itemNames = new ArrayList<>();
        System.out.println("pressed button");
        System.out.println("nft bal for loop - "+nftBala);
        long startTime = System.nanoTime();
        Toast.makeText(getApplicationContext(), "Fetching your inventory...", Toast.LENGTH_LONG).show();
        for(i = 0; i < nftBala; i++)
        {
            System.out.println(nftIDs);
            System.out.println("here");
            // can use this to set data to the item layout dynamically
            LinearLayout ll = (LinearLayout) findViewById(R.id.bottom_part);
            LayoutInflater inflater = getLayoutInflater();
            itemLayout = inflater.inflate(R.layout.item , ll, false);


            itemTitleTxt = itemLayout.findViewById(R.id.itemTitle);
            itemDescTxt = itemLayout.findViewById(R.id.itemDescription);
            itemImg = itemLayout.findViewById(R.id.itemImage);
            //use strings when setting text
            assert nftIDs != null;

            Thread thread2  = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nftMetaData = nft.tokenURI( (BigInteger)nftIDs.get(i) ).send();

                        String json = getJSONFromURL(nftMetaData);
                        Gson gson = new Gson();
                        if(!nftMetaData.contains("typicode") && !nftMetaData.contains("1.1.1.1"))
                        {
                            System.out.println("continuing in thread");
                            puc = gson.fromJson(json, PinataUploadClass.class);
                        }


                        itemNames.add(puc.itemName);
                        itemDescs.add(puc.itemDesc);
                        itemDates.add(puc.returnDate);

                        if(puc.imgLink == null || puc.imgLink.equals("")){
                            Uri path = Uri.parse("file:///android_asset/raw/sample/thumb-108917.png");
                            puc.imgLink = path.toString();
                            itemImg.setImageResource(R.drawable.thumb_108917);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                thread2.start();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("outside thread-" +  itemNames);
            if(nftMetaData.contains("typicode"))
            {
                System.out.println("continuing");
                continue;
            }
            itemTitleTxt.setText(new StringBuilder().append("ID - ").append(nftIDs.get(i)).append(", Name - ").append(itemNames.get(itemNames.size() - 1)).toString());
            itemDescTxt.setText(new StringBuilder().append("Desc - ").append(itemDescs.get(i)).append("Ret. by - ").append(itemDates.get(i)).toString());
            Picasso.get().load(puc.imgLink).into(itemImg);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (nftBala != 0) {
                params.addRule(RelativeLayout.BELOW, itemLayout.getId());
            }
            ll.addView(itemLayout, params);
            addlayoutClicked = true;
        }
        i = 0;
        System.out.println("fin");
        System.out.println((System.nanoTime() - startTime)/1000000);
        Toast.makeText(getApplicationContext(), "Items loaded", Toast.LENGTH_LONG).show();
    }



    private static String getJSONFromURL(String jsonURL) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(jsonURL);
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

    public void transferToAccount(View view) {
        Intent intent = new Intent(this, trWindow.class);
        intent.putExtra("id", nftBala);
        intent.putExtra("pubAddrForTransfer", pubAddr);
        intent.putExtra("PRIVATE_KEY", privK);
        startActivity(intent);
    }

    public void viewAvailable(View view) {
        Intent intent = new Intent(this, availableItems.class);
        intent.putExtra("pubAddr", pubAddr);
        intent.putExtra("PRIVATE_KEY", privK);
        startActivity(intent);

    }
}