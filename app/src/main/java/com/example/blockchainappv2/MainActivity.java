package com.example.blockchainappv2;
//users can log in with their own private keys and access the smart contract
//use a single smart contract to manage all transactions
//can transfer using contract but contract doesn't know the token's in the other account

// firefox address: 0x2412F42C68dDe2Ee49514975d3bEA066B1320723
// firefox key: 664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd

// chrome address: 0xa7D7dF54C33E6579C9dE2AFF3dF86DD2F0723c28 owner of feaa
// chrome key: 35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9

//firefox_dev address: 0x28321A3929E33A0c8300ccBF2C825F6683C0F9d8
//firefox_dev key: 40b0f86c631705c1e0754d1df04b4bc96ef593e499438036ba8e31cd757d09aa

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pinata.Pinata;
import pinata.PinataException;
import pinata.PinataResponse;

// Main menu

public class MainActivity extends AppCompatActivity {

//    private final String PRIVATE_KEY_CHROME = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    private String PRIVATE_KEY_CHROME = "35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9";
    //use _transfer() method and
//    private String PRIVATE_KEY_CHROME = "40b0f86c631705c1e0754d1df04b4bc96ef593e499438036ba8e31cd757d09aa";
    //perhaps we can keep this key the same for every account
    //private final String PRIVATE_KEY = "ddfc78e76722eacbd5f9c4401fae889c7106b21abafa5cbe459a6048fa75c976";
    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final String RECIPIENT = "0x647067E5140f1Befe80d695b129a12F22f772675";
    //    private final String CONTRACT_ADDRESS = "0x22E279B66Bb08a61DF776e765B9519F9FA56673C";
//    private final String CONTRACT_ADDRESS = "0x1b208c90e60EDb5c53f7580dDf23861cA08EBd00";
//    private final String CONTRACT_ADDRESS = "0xb7b849e4b790906c9a2e2b1f6933e5403bad97c5";
    private final String CONTRACT_ADDRESS = "0x2100448fd5c91d5d28024561b23143f865d0f4a4";
//    private final String CONTRACT_ADDRESS_CHROME = "0x0bb2df51764ebcbf844498e81aba2bd1445a81c0"; //Sc_test
    private final String CONTRACT_ADDRESS_CHROME = "0xfeaa5fe401400505cfa259d780bd2062a854b13f"; //GetAllTokId

    private String pubAddr = "";

    private final String TAG = "MainActivity";
    private ImageView mImageView;
    final int[] bal = {0};

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

    String PUBLIC_KEY = "";
    boolean passwordEntered = false;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setupBouncyCastle();

        // create the get Intent object. Whichever activity starts this one will have its data
        // transferred to this activity, which we use
        Intent pwdEntry = getIntent();

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
//        pubAddr = pwdEntry.getStringExtra("publicAddr");

        try{
            // data from starting activity
            PRIVATE_KEY_CHROME = pwdEntry.getStringExtra("privkey");
            pubAddr = getCredentialsFromPrivateKey().getAddress();
            passwordEntered = pwdEntry.getBooleanExtra("enteredPassword", false);
            PUBLIC_KEY = pwdEntry.getStringExtra("pubKey");
            System.out.println(pubAddr);
        }catch (Exception e){

        }

        //check if a wallet information directory exists
        // if it's a first time user, wallet file screen is displayed, then app is used.
        // if not, login screen is displayed, then app is used.
        File file = new File(getApplicationContext().getFilesDir(),"walletDir");
        if(!file.isDirectory()){
            Intent intent = new Intent(this, create_wallet.class);
            startActivity(intent);
        } else if (!passwordEntered) {

            Intent intent = new Intent(this, passwordEntry.class);
            startActivity(intent);
        }



    }

    // a method is executed when a specific button is pressed
    public void newActivity(View view) {

        Intent intent = new Intent(this, ItemMenu.class);
        intent.putExtra("nftBal", bal[0]);
        intent.putExtra("pubAddr", pubAddr);
        intent.putExtra("PRIVATE_KEY", PRIVATE_KEY_CHROME);
        startActivity(intent);
    }
    public void startItemUpload(View view) {

        // When clicked, button starts item upload activity and pushes data to it
        Intent intent = new Intent(this, item_upload.class);
        intent.putExtra("privK", PRIVATE_KEY_CHROME);
        intent.putExtra("pubAddr", pubAddr);
        startActivity(intent);
    }

    public void startItemReceive(View view) {
        Intent intent = new Intent(this, receiveItem.class);
        intent.putExtra("pubAddr", pubAddr);
        intent.putExtra("PRIVATE_KEY", PRIVATE_KEY_CHROME);
        startActivity(intent);
    }

    public MainActivity(){

    }


    @SuppressLint("CheckResult")
    public void onClick(View view) {
        Intent intent = new Intent(this, accountInfo.class);
        intent.putExtra("pubAddr", pubAddr);
        intent.putExtra("PRIVATE_KEY", PRIVATE_KEY_CHROME);
        intent.putExtra("PUBLIC_KEY", PUBLIC_KEY);
        startActivity(intent);

    }


    private Credentials getCredentialsFromPrivateKey(){
        Credentials cred = Credentials.create(PRIVATE_KEY_CHROME);
        System.out.println(cred.getEcKeyPair().getPublicKey().toString());
        return Credentials.create(PRIVATE_KEY_CHROME);

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }



    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
//        return Erc721.deploy(
//                web3j,
//                credentials,
//                GAS_PRICE,
//                GAS_LIMIT).send().getContractAddress();
        GetAllTokId vruh = GetAllTokId.deploy(web3j, credentials, new DefaultGasProvider()).send();
        return GetAllTokId.deploy(web3j, credentials, new DefaultGasProvider()).send().getContractAddress();
    }

    private void printWeb3Version(Web3j web3j){
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert web3ClientVersion != null;
        String web3ClientVersionString = web3ClientVersion.getWeb3ClientVersion();
        System.out.println("Web3 client version: " + web3ClientVersionString);
    }

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials(
                "passphrase",
                "wallet/path"
        );
    }

    private void transferEth(Web3j web3j, Credentials credentials) throws Exception {

        TransactionManager tm = new RawTransactionManager(
                web3j,
                credentials
        );

        Transfer tf = new Transfer(
                web3j,
                tm
        );

        TransactionReceipt tr = tf.sendFunds(
                RECIPIENT,
                BigDecimal.ONE,
                Convert.Unit.ETHER,
                GAS_PRICE,
                GAS_LIMIT
        ).send();
    }
}