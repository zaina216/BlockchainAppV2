package com.example.blockchainappv2;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;

public class trWindow extends AppCompatActivity {

    private String privK = "35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9";

    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final String RECIPIENT = "0x647067E5140f1Befe80d695b129a12F22f772675";
    private final String CONTRACT_ADDRESS_CHROME = "0xfeaa5fe401400505cfa259d780bd2062a854b13f"; //GetAllTokId

    private String pubAddr = "";


    String scanData;

    // required to correctly configure the cryptographic libraries for Web3j
    private void setupCiphers() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;

        }

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(privK);
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tr_window);

        setupCiphers();

        Intent intent = getIntent();
        pubAddr = intent.getStringExtra("pubAddrForTransfer");
        privK = intent.getStringExtra("PRIVATE_KEY");


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
//                        Toast.makeText(trWindow.this, "Received - " + SiR.getContents(), Toast.LENGTH_LONG).show();
                        System.out.println("Received - " + SiR.getContents());
                        TextView tv = (TextView) findViewById(R.id.idTrBox);
                        Toast.makeText(trWindow.this, "Transferring...", Toast.LENGTH_LONG).show();
                        System.out.println("transferring...");

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
                                Credentials credentials = getCredentialsFromPrivateKey();

                                GetAllTokId nft = loadContract(CONTRACT_ADDRESS_CHROME, web3j, credentials);
                                try{
                                    System.out.println(pubAddr);
                                    nft.setApprovalForAll("0xa7d7df54c33e6579c9de2aff3df86dd2f0723c28", true);
//                                    nft.transferFrom("0xa7d7df54c33e6579c9de2aff3df86dd2f0723c28","0x28321a3929e33a0c8300ccbf2c825f6683c0f9d8", BigInteger.valueOf(Long.parseLong(tv.getText().toString()))).send();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    nft.transferFrom(pubAddr,
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
                        Toast.makeText(trWindow.this, "Transferred", Toast.LENGTH_LONG).show();
                    }
                }
            });

    // Launch
    public void onButtonClick(View view) {
        barcodeLauncher.launch(new ScanOptions());
    }
}