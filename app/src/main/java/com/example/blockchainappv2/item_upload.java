package com.example.blockchainappv2;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

//import android.content.Context;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

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

    private String pubAddr = "";

    String itemName = "";
    String itemDesc = "";
    String returnDate = "";

    public item_upload() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_upload);
        dispatchTakePictureIntent();
        Intent mainActivity = getIntent();
        PRIVATE_KEY = mainActivity.getStringExtra("privk");
        pubAddr = mainActivity.getStringExtra("pubAddr");

    }
    // function is executed when upload item button is pressed
    ImageData imd;
    public void onUploadClick(View view) {
//        dispatchTakePictureIntent();
        editItemName = findViewById(R.id.editDeclareEthAddr);
        editItemDesc = findViewById(R.id.editUploadTxtDesc);
        editDateToReturnBy = findViewById(R.id.dateToReturnBy);
//        mImageView = new ImageView();


        itemName = editItemName.getText().toString();
        itemDesc = editItemDesc.getText().toString();
        returnDate = editDateToReturnBy.getText().toString();


        if (!itemDesc.equals("") && !itemName.equals("") && !returnDate.equals("")) {
            System.out.println("here-1");

            Toast.makeText(this, "Minting item... ", Toast.LENGTH_SHORT).show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("Entered thread");
                    Pinata pinata = new Pinata("6296046404a96424609a",
                            "a7d827beac22e26015680273dd8622af45f4733a" +
                                    "51df75bf9f3dcd000affbf34");
                    PinataResponse pinRespo = null;
                    try {
                        pinRespo = pinata.testAuthentication();
                    } catch (PinataException | IOException e) {
                        e.printStackTrace();
                    }

                    assert pinRespo != null;
                    System.out.println(pinRespo.getStatus());

                    PinataUploadClass pupload = new PinataUploadClass();
                    pupload.itemDesc = itemDesc;
                    pupload.itemName = itemName;
                    pupload.returnDate = returnDate;


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

                    // we put this into the json file as a field
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
                        pinResponse = pinata.pinFileToIpfs(getApplicationContext().
                                openFileInput("config" + unixTime + ".json"),
                                "config" + unixTime + ".json");

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

                        System.out.println(pubAddr);
                        nft.mintUniqueToken(pubAddr, interwebAddress).send();
//                        Toast.makeText(item_upload.this, "item minted", Toast.LENGTH_LONG).show(); //cannot toast on this thread
                        System.out.println("token minted...");
//                        System.out.println("uri of nft with pinata data:"+ nft.tokenURI(BigInteger.valueOf(1)).send());
//                        System.out.println("owner of nft with pinata data:"+ nft.ownerOf(BigInteger.valueOf(1)).send());
//                        nft.transferFrom("0x6fe1f0c587cb02ae23c443d5bf899099eb341762", "0x2100448fd5c91d5d28024561b23143f865d0f4a4",BigInteger.valueOf(1)).send();

//                        System.out.println("owner of nft with pinata data:"+nft.ownerOf(BigInteger.valueOf(1)).encodeFunctionCall());
                    } catch (Exception e) {
                        e.printStackTrace();
                        //only works when transferring from smart contract owner to other address
                    }
                }
            });

            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("after function");
            Toast.makeText(this, "Minted", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // when this function is called, the phone's builtin camera app is opened, and this
    // app is able to use the pic taken by the opened cameras.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Thread.sleep(100);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException | InterruptedException e) {
        }
    }



    // used to mint without camera functionality (used for testing)
    public void mintWithoutCamera(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Entered thread");
                Pinata pinata = new Pinata("6296046404a96424609a",
                        "a7d827beac22e26015680273dd8622af45f4733a" +
                                "51df75bf9f3dcd000affbf34");
                PinataResponse pinRespo = null;
                try {
                    pinRespo = pinata.testAuthentication();
                } catch (PinataException | IOException e) {
                    e.printStackTrace();
                }

                assert pinRespo != null;
                System.out.println(pinRespo.getStatus());

                PinataUploadClass pupload = new PinataUploadClass();
                pupload.itemDesc = itemDesc;
                pupload.itemName = itemName;
                pupload.returnDate = returnDate;



                Gson gson = new Gson();
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
                    nft.mintUniqueToken(pubAddr, interwebAddress).send();
                    System.out.println("token minted...");
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
    //This function is called when the camera activity is closed.  We get the picture taken
    // as a bitmap and is then saved to Android storage
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (!Debug.isDebuggerConnected()){
//            Debug.waitForDebugger();
//            Log.d("debug", "started");
//        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            imd = saveToAndroidStorage(imageBitmap);
            System.out.println("Took picture");
        }
    }


    private ImageData saveToAndroidStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        ImageData imgdat = new ImageData();
        // path to data folder in android app
        // Create imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        imgdat.path = directory.getAbsolutePath();

        long imgTime = System.currentTimeMillis();
        File mypath = new File(directory, "imgpic" + imgTime + ".jpg");
        imgdat.filename = "imgpic" + imgTime + ".jpg";

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(mypath);
            // compress image and write to output stream which writes to file
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgdat;
    }

}