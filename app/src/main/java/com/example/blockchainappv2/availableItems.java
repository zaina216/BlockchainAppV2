package com.example.blockchainappv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import io.reactivex.functions.Consumer;

import androidx.appcompat.widget.AppCompatButton;

import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;

import io.reactivex.disposables.Disposable;

public class availableItems extends AppCompatActivity {

    private String privK = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
//    private final String PRIVATE_KEY = "ddfc78e76722eacbd5f9c4401fae889c7106b21abafa5cbe459a6048fa75c976";
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
    private String pubAddr = "";


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
    View itemLayout;
    List allTokIds = new ArrayList<>();
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
    List tokens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_items);

        Intent intent = getIntent();
        pubAddr = intent.getStringExtra("pubAddr");
        privK = intent.getStringExtra("PRIVATE_KEY");


        Toast.makeText(getApplicationContext(), "Fetching available items...", Toast.LENGTH_LONG).show();


//        getting item inventory of current user

        Thread thread  = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                try {
                    nftBala = Integer.parseInt(nft.balanceOf(pubAddr).send().toString());
                    nftIDs = nft.getTokenIds(pubAddr).send();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {

                    Disposable subscribe = nft.transferEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                            .subscribe(new Consumer<GetAllTokId.TransferEventResponse>() {
                                @SuppressLint("CheckResult")
                                @Override
                                public void accept(GetAllTokId.TransferEventResponse event) throws Exception {
                                    allTokIds.add(event.tokenId);
                                }
                            });

                    System.out.println("all token ids - " + allTokIds);
                    System.out.println("MINTING...");
                    System.out.println("MINT COMPLETE");
                    tokens = nft.getTokenIds(CONTRACT_ADDRESS_CHROME).send();
                    System.out.println("my token ids - " + tokens);

                    for (int i = 0; i < tokens.size(); i++) {
                        allTokIds.remove(tokens.get(i));
                    }
                    LinkedHashSet set = new LinkedHashSet<>(allTokIds);
                    allTokIds.clear();
                    allTokIds.addAll(set);
                    System.out.println("other token ids - " + allTokIds);
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
    }

    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(privK);
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        return com.example.blockchainappv2.Sc_test.deploy(web3j, credentials, new DefaultGasProvider()).send().getContractAddress();
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }


    PinataUploadClass puc;
    PinataUploadClass[] apuc;
    Gson gson = new Gson();
    TextView itemTitleTxt = null;
    ImageView itemImg = null;


    @SuppressLint("CheckResult")
    public void displayInventory() throws IOException {
        URL url = null;
        Toast.makeText(getApplicationContext(), "Fetching available items...", Toast.LENGTH_LONG).show();
        itemNames = new ArrayList<>();
        System.out.println("pressed button");
        System.out.println("nft bal for loop - "+nftBala);

        for(i = 0; i < tokens.size(); i++) {
            try {

                System.out.println("here");
                // can use this to set data to the item layout dynamically
                LinearLayout ll = (LinearLayout) findViewById(R.id.bottom_part);
                LayoutInflater inflater = getLayoutInflater();
                itemLayout = inflater.inflate(R.layout.item, ll, false);

                itemTitleTxt = itemLayout.findViewById(R.id.itemTitle);
                itemImg = itemLayout.findViewById(R.id.itemImage);
                //use strings when setting text
                assert nftIDs != null;

                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            nftMetaData = nft.tokenURI((BigInteger) tokens.get(i)).send();

                            String json = getImageFromURL(nftMetaData);
                            System.out.println(nftMetaData);
                            Gson gson = new Gson();
                            if (!nftMetaData.contains("typicode")) {
                                System.out.println("continuing in thread");
                                puc = gson.fromJson(json, PinataUploadClass.class);
                            }


                            itemNames.add(puc.itemDesc);
                            if (puc.imgLink == null || puc.imgLink.equals("")) {
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
                System.out.println("outside thread-" + itemNames);
                if (nftMetaData.contains("typicode")) {
                    System.out.println("continuing");
                    continue;
                }
                itemTitleTxt.setText(new StringBuilder().append("Item ID - ").append(tokens.get(i)).append(", Item name - ").append(itemNames.get(itemNames.size() - 1)).toString());
                Picasso.get().load(puc.imgLink).into(itemImg);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                if (nftBala != 0) {
                    params.addRule(RelativeLayout.BELOW, itemLayout.getId());
                }
                ll.addView(itemLayout, params);
                addlayoutClicked = true;
            }catch(Exception e){
                continue;
            }

        }
        i = 0;
        System.out.println("fin");
    }



    private static String getImageFromURL(String imgURL) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(imgURL);
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
        startActivity(intent);
    }
}
