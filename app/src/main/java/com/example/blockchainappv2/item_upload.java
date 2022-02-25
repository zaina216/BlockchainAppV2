package com.example.blockchainappv2;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

//import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import pinata.Pinata;
import pinata.PinataException;
import pinata.PinataResponse;

public class item_upload extends AppCompatActivity {

    private final String PRIVATE_KEY = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
//    private final String CONTRACT_ADDRESS = "0x2100448fd5c91d5d28024561b23143f865d0f4a4";
//    private final String CONTRACT_ADDRESS = "0x6fe1f0c587cb02ae23c443d5bf899099eb341762";
    private final String CONTRACT_ADDRESS = "0xfeaa5fe401400505cfa259d780bd2062a854b13f";
    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    Credentials credentials = getCredentialsFromPrivateKey();
    GetAllTokId nft = loadContract(CONTRACT_ADDRESS, web3j, credentials);

    EditText editItemName;
    EditText editItemDesc;
    EditText editDateToReturnBy;

    public item_upload() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_upload);
        editItemName = findViewById(R.id.editUploadTxtName);
        editItemDesc = findViewById(R.id.editUploadTxtDesc);
        editDateToReturnBy = findViewById(R.id.dateToReturnBy);

    }

    public void onUploadClick(View view) {
        String itemName = editItemName.getText().toString();
        String itemDesc = editItemDesc.getText().toString();
        String returnDate = editDateToReturnBy.getText().toString();
        if(!itemDesc.equals("") && !itemName.equals("")){ //add date validation here

            // If you created a Pinata instance with keys


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Pinata pinata = new Pinata("6296046404a96424609a", "a7d827beac22e26015680273dd8622af45f4733a51df75bf9f3dcd000affbf34");
                    PinataResponse authResponse = null;
                    try {
                        authResponse = pinata.testAuthentication();
                    } catch (PinataException | IOException e) {
                        e.printStackTrace();
                    }

                    assert authResponse != null;
                    System.out.println(authResponse.getStatus());

                    PinataUploadClass pupload = new PinataUploadClass();
                    pupload.itemDesc = itemDesc;
                    pupload.itemName = itemName;
                    pupload.returnDate = returnDate;

                    Gson gson = new Gson();
                    String json = gson.toJson(pupload);
                    System.out.println(json);


                    // get current unix time for filename uniqueness
                    long unixTime = System.currentTimeMillis();
                    writeToFile(json, "config.json");

                    PinataResponse pinResponse = new PinataResponse();

                    try {
//                        pinResponse = pinata.pinFileToIpfs(new File("config.json"));
                        System.out.println("pinning file...");
                        pinResponse = pinata.pinFileToIpfs(getApplicationContext().openFileInput("config.json"), "config.json");
                        System.out.println("file pinned.");

                    } catch (PinataException | IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println(pinResponse.getStatus());
                    System.out.println(pinResponse.getBody());

                    gson = new Gson();
                    PinataResponseClass resp = gson.fromJson(pinResponse.getBody(), PinataResponseClass.class);

                    System.out.println(resp.IpfsHash);
                    Log.d(TAG, resp.IpfsHash);

                    String interwebAddress = "https://gateway.pinata.cloud/ipfs/" + resp.IpfsHash;
                    System.out.println("interwebAddress" + interwebAddress);
                    try {
                        System.out.println("minting token...");
                        nft.mintUniqueToken(CONTRACT_ADDRESS, interwebAddress).send();
                        Toast.makeText(item_upload.this, "item minted", Toast.LENGTH_LONG).show();
                        System.out.println("token minted...");
//                        System.out.println("uri of nft with pinata data:"+ nft.tokenURI(BigInteger.valueOf(1)).send());
//                        System.out.println("owner of nft with pinata data:"+ nft.ownerOf(BigInteger.valueOf(1)).send());
//                        nft.transferFrom("0x6fe1f0c587cb02ae23c443d5bf899099eb341762", "0x2100448fd5c91d5d28024561b23143f865d0f4a4",BigInteger.valueOf(1)).send();


//                        System.out.println("owner of nft with pinata data:"+nft.ownerOf(BigInteger.valueOf(1)).encodeFunctionCall());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });thread.start();

        }

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("0x2100448fd5c91d5d28024561b23143f865d0f4a4", BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrCode);
            imageViewQrCode.setImageBitmap(bitmap);
        } catch(Exception e) {

        }
    }

    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(PRIVATE_KEY);
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }



    private void writeToFile(String data, String fileName) {
        try {
            FileOutputStream fileout=openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(data);
            outputWriter.close();
            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
//                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}