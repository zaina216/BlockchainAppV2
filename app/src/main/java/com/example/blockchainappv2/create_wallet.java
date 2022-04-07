package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.FileWriter;
import java.security.Provider;
import java.security.Security;

public class create_wallet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        setupBouncyCastle();
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;

        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        File dir = new File(mcoContext.getFilesDir(), "walletDir");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    String wallet;
    public void onSavePassword(View view) {
        try {

            //Create wallet

            TextView pwd = findViewById(R.id.editDeclareEthAddr);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("walletDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath());

            System.out.println(f.getAbsolutePath());
            wallet = WalletUtils.generateLightNewWalletFile(pwd.getText().toString(), directory);
            System.out.println("wallet output - " + wallet);

            // Write to file. First line is the wallet filename, second is the password

            File dir = new File(getApplicationContext().getFilesDir(), "walletDir");

            if(!dir.exists()){
                dir.mkdir();
            }

            try {
                File gpxfile = new File(dir, "userInfo.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(wallet);
                writer.append(System.lineSeparator());
                writer.append(pwd.getText().toString());

                writer.flush();
                writer.close();
            } catch (Exception e){
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}