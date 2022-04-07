package com.example.blockchainappv2;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

//import android.content.Context;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import pinata.Pinata;
import pinata.PinataException;
import pinata.PinataResponse;

class ImageData {
    String path;
    String filename;
}

public class item_upload extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String PRIVATE_KEY = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
//    private String PRIVATE_KEY_CHROME = "35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9";
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
    ImageView mImageView;
    String imgName;

    String itemName = "";
    String itemDesc = "";
    String returnDate = "";

    public item_upload() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_upload);

        Intent mainActivity = getIntent();
        PRIVATE_KEY = mainActivity.getStringExtra("privk");



    }





    public void onUploadClick(View view) {
        editItemName = findViewById(R.id.editDeclareEthAddr);
        editItemDesc = findViewById(R.id.editUploadTxtDesc);
        editDateToReturnBy = findViewById(R.id.dateToReturnBy);
//        mImageView = new ImageView();


        itemName = editItemName.getText().toString();
        itemDesc = editItemDesc.getText().toString();
        returnDate = editDateToReturnBy.getText().toString();


        if (!itemDesc.equals("") && !itemName.equals("")) { //add date validation here
            System.out.println("here-1");
            dispatchTakePictureIntent();
            System.out.println("after function");

            Thread thr = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }); thr.start();
            try {
                thr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials) {
        return GetAllTokId.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    private void writeToFile(String data, String fileName) {
        try {
            FileOutputStream fileout = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(data);
            outputWriter.close();
            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
//                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        System.out.println("here0");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
            System.out.println("here2");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("Entered thread");
                    Pinata pinata = new Pinata("6296046404a96424609a",
                            "a7d827beac22e26015680273dd8622af45f4733a" +
                                    "51df75bf9f3dcd000affbf34");
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


                    //                    mImageView.invalidate();
//            BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
//            ImageData imd = saveToInternalStorage(drawable.getBitmap());
                    ImageData imd = saveToInternalStorage(imageBitmap);

                    // upload pic first

                    PinataResponse pinResponsePicture = new PinataResponse();
                    try {
//                        pinResponse = pinata.pinFileToIpfs(new File("config.json"));
                        System.out.println("pinning picture...");
                        pinResponsePicture = pinata.pinFileToIpfs(new File(imd.path, imd.filename));
                        System.out.println("picture pinned.");

                    } catch (PinataException | IOException e) {
                        e.printStackTrace();
                    }

                    Gson gson = new Gson();
                    PinataResponseClass respPicture = gson.fromJson(pinResponsePicture.getBody(), PinataResponseClass.class);
                    Log.d(TAG, respPicture.IpfsHash);

                    pupload.imgLink = "https://gateway.pinata.cloud/ipfs/" + respPicture.IpfsHash;


                    gson = new Gson();
                    String json = gson.toJson(pupload);
                    System.out.println(json);


                    // get current unix time for filename uniqueness
                    long unixTime = System.currentTimeMillis();
                    writeToFile(json, "config" + unixTime + ".json");

                    PinataResponse pinResponse = new PinataResponse();

                    try {
//                        pinResponse = pinata.pinFileToIpfs(new File("config.json"));
                        System.out.println("pinning file...");
                        pinResponse = pinata.pinFileToIpfs(getApplicationContext().openFileInput("config" + unixTime + ".json"), "config" + unixTime + ".json");
                        System.out.println("file pinned.");

                    } catch (PinataException | IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println(pinResponse.getStatus());
                    System.out.println(pinResponse.getBody());

                    gson = new Gson();
                    PinataResponseClass resp = gson.fromJson(pinResponse.getBody(), PinataResponseClass.class);
                    Log.d(TAG, resp.IpfsHash);

                    String interwebAddress = "https://gateway.pinata.cloud/ipfs/" + resp.IpfsHash;
                    System.out.println("interwebAddress" + interwebAddress);
                    try {
                        System.out.println("minting token...");
                        nft.mintUniqueToken("0x2412F42C68dDe2Ee49514975d3bEA066B1320723", interwebAddress).send();
//                        Toast.makeText(item_upload.this, "item minted", Toast.LENGTH_LONG).show(); //cannot toast on this thread
                        System.out.println("token minted...");
//                        System.out.println("uri of nft with pinata data:"+ nft.tokenURI(BigInteger.valueOf(1)).send());
//                        System.out.println("owner of nft with pinata data:"+ nft.ownerOf(BigInteger.valueOf(1)).send());
//                        nft.transferFrom("0x6fe1f0c587cb02ae23c443d5bf899099eb341762", "0x2100448fd5c91d5d28024561b23143f865d0f4a4",BigInteger.valueOf(1)).send();


//                        System.out.println("owner of nft with pinata data:"+nft.ownerOf(BigInteger.valueOf(1)).encodeFunctionCall());
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

        }
    }


    private ImageData saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        ImageData imgdat = new ImageData();
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        imgdat.path = directory.getAbsolutePath();
        // Create imageDir

        long imgTime = System.currentTimeMillis();
        File mypath = new File(directory, "imgpic" + imgTime + ".jpg");
        imgdat.filename = "imgpic" + imgTime + ".jpg";

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgdat;
    }

    private String loadImageFromStorage(String path, String fileName) {
        try {
            File f = new File(path, fileName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            mImageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return path + fileName;
    }
}
































