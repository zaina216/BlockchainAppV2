package com.example.blockchainappv2;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

public class trWindow extends AppCompatActivity {

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

    private final String TAG = "ItemMenu";
    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    Credentials credentials = getCredentialsFromPrivateKey();
    GetAllTokId nft = loadContract(CONTRACT_ADDRESS_CHROME, web3j, credentials);

    String scanData;

    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(PRIVATE_KEY);
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tr_window);



    }

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            new ActivityResultCallback<ScanIntentResult>() {
                @Override
                public void onActivityResult(ScanIntentResult SiR) {
                    if (SiR.getContents() == null) {
                        Toast.makeText(trWindow.this, "Didn't scan", Toast.LENGTH_LONG).show();
                    } else {
                        scanData = SiR.getContents();
                        Toast.makeText(trWindow.this, "Received - " + SiR.getContents(), Toast.LENGTH_LONG).show();
                        System.out.println("Received - " + SiR.getContents());
                        TextView tv = (TextView) findViewById(R.id.idTrBox);
                        System.out.println("transferring...");
//                        nft.transferFrom()
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    nft.tr("0x2412F42C68dDe2Ee49514975d3bEA066B1320723",
                                            scanData, BigInteger.valueOf(Long.parseLong(tv.getText().toString()))).send();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });



                        try {
                            thread.start();
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("transferred");
                    }
                }
            });

    // Launch
    public void onButtonClick(View view) {
        barcodeLauncher.launch(new ScanOptions());
    }




}







































