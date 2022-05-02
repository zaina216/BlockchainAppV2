package com.example.blockchainappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class passwordEntry extends AppCompatActivity {
    EditText passwordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_entry);

        passwordEntry = (EditText) findViewById(R.id.editEnterPwd);

        /*
        When the wallet was created, we created another file that stores the name of the wallet
        Here, we load the file using BufferedReader, and read the wallet name. Using Web3j's
        wallet utilities, we decrypt the wallet with the user-supplied password
        */
        BufferedReader reader;
        String walletName;
        try {
            //file reading
            reader = new BufferedReader(new FileReader(
                    getApplicationContext().getFilesDir() + "/walletDir/userInfo.txt"));
            walletName = reader.readLine();

            reader.close();

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("walletDir", Context.MODE_PRIVATE);

            //decryption. if wrong password supplied, CipherException thrown
            Credentials walletCreds = WalletUtils.loadCredentials(passwordEntry.getText().toString(), directory + "/" + walletName);
            System.out.println("wallet credentials output - " + walletCreds.getEcKeyPair().getPublicKey());
            System.out.println("Public address - " + walletCreds.getAddress());

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("publicAddr", walletCreds.getAddress());
            intent.putExtra("enteredPassword", true);
            intent.putExtra("privkey", walletCreds.getEcKeyPair().getPrivateKey().toString());
            startActivity(intent);

        } catch (IOException e) {
            Toast.makeText(this, "Could find file", Toast.LENGTH_SHORT).show();
        } catch (CipherException e){
            Toast.makeText(this, "Could not decrypt wallet", Toast.LENGTH_SHORT).show();
        }
    }



    public void onSavePassword(View view) {



        BufferedReader reader;
        String walletName;
        try {
            reader = new BufferedReader(new FileReader(
                    getApplicationContext().getFilesDir() + "/walletDir/userInfo.txt"));
            walletName = reader.readLine();

            reader.close();

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("walletDir", Context.MODE_PRIVATE);

            Credentials walletCreds = WalletUtils.loadCredentials(passwordEntry.getText().toString(), directory + "/" + walletName);
            System.out.println("wallet credentials output - " + walletCreds.getEcKeyPair().getPublicKey());
            System.out.println("Public address - " + walletCreds.getAddress());

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("publicAddr", walletCreds.getAddress());
            intent.putExtra("enteredPassword", true);
            intent.putExtra("privkey", walletCreds.getEcKeyPair().getPrivateKey().toString());
            intent.putExtra("pubKey", walletCreds.getEcKeyPair().getPublicKey().toString());
            startActivity(intent);

        } catch (IOException e) {
            Toast.makeText(this, "Could find file", Toast.LENGTH_SHORT).show();
        } catch (CipherException e){
            Toast.makeText(this, "Could not decrypt wallet", Toast.LENGTH_SHORT).show();
        }

    }
}
