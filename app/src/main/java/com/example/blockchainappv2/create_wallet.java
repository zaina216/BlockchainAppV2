package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    //Standard code to set up Android cryptographic providers
    private void setupBouncyCastle() {
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

    /*
    Creates an Ethereum wallet file on the Android device. This allows each user to
    have their own public ethereum address to send/receive NFTs and cryptocurrency.
    The contents are encrypted with AES-128.
    */
    public void onSavePassword(View view) {
        String wallet;
        TextView pwd;

        try {
            //Create wallet

            pwd = findViewById(R.id.editDeclareEthAddr);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // Code for accessing data directory in Android
            File directory = cw.getDir("walletDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath());

//            System.out.println(f.getAbsolutePath());
            Toast.makeText(getApplicationContext(), "Creating your wallet...", Toast.LENGTH_LONG).show();
            wallet = WalletUtils.generateLightNewWalletFile(pwd.getText().toString(), directory);
            System.out.println("wallet output - " + wallet);

            // Write to file wallet filename
            File dir = new File(getApplicationContext().getFilesDir(), "walletDir");
            if(!dir.exists()){
                dir.mkdir();
            }

            try {
                File infofile = new File(dir, "userInfo.txt");
                FileWriter writer = new FileWriter(infofile);
                writer.append(wallet);
                writer.append(System.lineSeparator());

                writer.flush();
                writer.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            Credentials walletCreds = WalletUtils.loadCredentials(pwd.getText().toString(), directory + "/" + wallet);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("publicAddr", walletCreds.getAddress());
            intent.putExtra("enteredPassword", true);
            intent.putExtra("privkey", walletCreds.getEcKeyPair().getPrivateKey().toString());
            intent.putExtra("pubKey", walletCreds.getEcKeyPair().getPublicKey().toString());
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}