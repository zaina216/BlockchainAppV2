package com.example.blockchainappv2;
//users can log in with their own private keys and access the smart contract
//use a single smart contract to manage all transactions
//can transfer using contract but contract doesn't know the token's in the other account
import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Text;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

import pinata.Pinata;
import pinata.PinataException;
import pinata.PinataResponse;

public class MainActivity extends AppCompatActivity {

    private final String PRIVATE_KEY = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    private final String PRIVATE_KEY_CHROME = "35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9";
    //perhaps we can keep this key the same for every account
    //private final String PRIVATE_KEY = "ddfc78e76722eacbd5f9c4401fae889c7106b21abafa5cbe459a6048fa75c976";
    private final BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final String RECIPIENT = "0x647067E5140f1Befe80d695b129a12F22f772675";
    //    private final String CONTRACT_ADDRESS = "0x22E279B66Bb08a61DF776e765B9519F9FA56673C";
//    private final String CONTRACT_ADDRESS = "0x1b208c90e60EDb5c53f7580dDf23861cA08EBd00";
//    private final String CONTRACT_ADDRESS = "0xb7b849e4b790906c9a2e2b1f6933e5403bad97c5";
    private final String CONTRACT_ADDRESS = "0x2100448fd5c91d5d28024561b23143f865d0f4a4";
    private final String CONTRACT_ADDRESS_CHROME = "0x0bb2df51764ebcbf844498e81aba2bd1445a81c0";


    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    //                Web3j web3j = Web3j.build(new HttpService("http://192.168.1.108:8545"));


//    Sc_test nft = loadContract(CONTRACT_ADDRESS, web3j, credentials);

    private final String TAG = "MainActivity";
    private ImageView mImageView;
    final int[] bal = {0};

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

    public Credentials createCredentialsUsingEcPair(){
        setupBouncyCastle();

        ECKeyPair keys = null;

        {
            try {
                keys = Keys.createEcKeyPair();
                System.out.println("keys created");
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
        }



        Credentials dummyCredentials = Credentials.create(keys);

        {
            try {
                dummyCredentials = Credentials.create(Keys.createEcKeyPair());
            } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
                e.printStackTrace();
            }
        }

        return dummyCredentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        Credentials dummyCredentials = createCredentialsUsingEcPair();
//        Sc_test nft = loadContract(CONTRACT_ADDRESS, web3j, dummyCredentials);
//
//        Thread thread  = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
////                    bal[0] = Integer.parseInt(nft.balanceOf("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send().toString());
////                    System.out.println(bal[0]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        thread.start();
    }

    public void newActivity(View view) {

        Intent intent = new Intent(this, ItemMenu.class);
        System.out.println(bal[0]);

//        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
//        String message = editText.getText().toString();
        intent.putExtra("nftBal", bal[0]);
        //Reload the contract many in each activity. Hacky, I know, but we cant implement serialisable in Sc_test.
//        intent.putExtra("contractObj")
        startActivity(intent);
    }
    public void startItemUpload(View view) {

        // When clicked, button starts item upload activity
        Intent intent = new Intent(this, item_upload.class);
        startActivity(intent);
    }

    public MainActivity(){

    }

    public void onClick(View view) {
        Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
//                Web3j web3j = Web3j.build(new HttpService("http://192.168.1.108:8545"));
//        Credentials credentials = createCredentialsUsingEcPair();
        Credentials credentials = getCredentialsFromPrivateKey();
//        Sc_test nft = loadContract(CONTRACT_ADDRESS, web3j, credentials);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    System.out.println("deploying");
//                    String deployedAddr =  deployContract(web3j,credentials);//deployed once; don't deploy again
//                    System.out.println("_________________new contract address:" + deployedAddr + "_________________");
//                            Log.d(TAG, deployedAddr); //Erc721: 0x279635563b42541A3372bCf0c75898211A6AE1Bd, ERC721deployable: 0xf9ec635edbc2c21f1e0ca1d68af8849bf38f6571
                    //ERC721DeployableAndMintable: 0x1b208c90e60EDb5c53f7580dDf23861cA08EBd00
                  Sc_test nft = loadContract(CONTRACT_ADDRESS_CHROME, web3j, credentials);

//                  String n = nft.name().sendAsync().toString();

//                  String n = nft.name().send();
//                  Log.d(TAG, "name of NFT: " + n);
                    System.out.println("MINTING...");
//                    String id = String.valueOf(nft.mintUniqueToken("0x2412F42C68dDe2Ee49514975d3bEA066B1320723", "https://my-json-server.typicode.com/abcoathup/samplenft/tokens/").send());


//                            Type result = (Type) nft.mintUniqueToken("0x2412F42C68dDe2Ee49514975d3bEA066B1320723","https://my-json-server.typicode.com/abcoathup/samplenft/tokens/").send();
                    System.out.println("MINT COMPLETE");
//
//                            System.out.println(result.getTypeAsString());
//                    System.out.println("id: " + id);
//                            nft.setName("ZUniqueToken").send();
//                            nft.setSym("ZEXT").send();
                    Log.d(TAG, nft.balanceOf("0xa7D7dF54C33E6579C9dE2AFF3dF86DD2F0723c28").send().toString());
//                    Log.d(TAG, "name:"+nft.name().send());
//                    Log.d(TAG, "owner: "+ nft.ownerOf(BigInteger.valueOf(1)).send());
//                    nft.transferFrom("0x2412F42C68dDe2Ee49514975d3bEA066B1320723","0xa7D7dF54C33E6579C9dE2AFF3dF86DD2F0723c28",BigInteger.valueOf(4)).send();
//                    nft.transferFrom("0xa7D7dF54C33E6579C9dE2AFF3dF86DD2F0723c28","0x2412F42C68dDe2Ee49514975d3bEA066B1320723",BigInteger.valueOf(4)).send();

//                            Log.d(TAG, "transferred?");
//                            Log.d(TAG, "owner: "+ nft.ownerOf(BigInteger.valueOf(4)).send());

                    Pinata pinata = new Pinata("6296046404a96424609a", "a7d827beac22e26015680273dd8622af45f4733a51df75bf9f3dcd000affbf34");

                    // If you created a Pinata instance with keys
                    try {
                        PinataResponse authResponse = pinata.testAuthentication();
                        // If a PinataException hasn't been been thrown, it means that the status is 200
                        //System.out.println(authResponse.getStatus()); // 200
                        Log.d(TAG, String.valueOf(authResponse.getStatus()));
                    } catch (PinataException e) {
                        // The status returned is not 200
                    } catch (IOException e) {
                        // Unable to send request
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                        mImageView = (ImageView) findViewById(R.id.imageViewId);
                        mImageView.setImageResource(R.drawable.pop_cat);

                        }
                    });



//                            ImageView img = new ImageView();
//                            Bitmap bitmap = getBitmapFromAsset("drawable/pop_cat.png");
//                            imageView.setImageBitmap(bitmap);

                            Log.d(TAG, "=========================END======================");
//                            AssetManager assetManager = getAssets();
//
//                            InputStream istr = assetManager.open("pop_cat.png");
//                            Bitmap bitmap = BitmapFactory.decodeStream(istr);
//                            istr.close();
//
//
//
//                            PinataResponse pinResponse = pinata.pinFileToIpfs(new File("pop_cat.png"));
//                            System.out.println(pinResponse.getStatus());
//                            System.out.println(pinResponse.getBody());
//
//                            Gson gson = new Gson();
//                            PinataResponseClass resp = gson.fromJson(pinResponse.getBody(), PinataResponseClass.class);
//
//                            System.out.println(resp.IpfsHash);
//                            Log.d(TAG, resp.IpfsHash);
//
//                            String interwebAddress = "https://gateway.pinata.cloud/ipfs/" + resp.IpfsHash;
                    {
//                            // Pull all the events for this contract
//                            final Uint256[] arg3 = new Uint256[1];
//                            web3j.ethLogFlowable(filter).subscribe(log -> {
//                                String eventHash = log.getTopics().get(0); // Index 0 is the event definition hash
//
//                                if(eventHash.equals(MY_EVENT_HASH)) { // Only MyEvent. You can also use filter.addSingleTopic(MY_EVENT_HASH)
//                                    // address indexed _arg1
//                                    Address arg1 = (Address) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(1), new TypeReference<Address>() {});
//                                    // bytes32 indexed _arg2
//                                    Type arg2 = (Type) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(2), new TypeReference<Type>() {});
//                                    // uint8 _arg3
//                                    arg3[0] = (Uint256) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(3), new TypeReference<Uint256>() {});
//                                }
//
//                            });

//                            Log.d(TAG, "===================================================================");
//                            Log.d(TAG, "UINT256 VALUE:" + arg3[0]);
//                            Log.d(TAG, "===================================================================");

                        //token doesn't exist as of yet after minting, so stuff here will error
                        // can keep creating nfts to represent items, will be using differet URIs to get images and descriptions.
//                            Log.d(TAG, nft.getDeployedAddress("4"));
//                            Log.d(TAG, "Owner of NFT id 0: "+nft.ownerOf(BigInteger.valueOf(0)).sendAsync().get().toString());
//                            Log.d(TAG, "name: " + n);
//                            AddressBook getAddr = loadContract(CONTRACT_ADDRESS, web3j, credentials);
//
//                            Snackbar.make(view, "lontract loaded!!!1111", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//
//                            addAddresses(getAddr);
//                            printAddresses(getAddr);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    {
//    private void addAddresses(AddressBook addressBook) throws Exception {
//        addressBook
//                .addAddress("0x35fEF2216D1426Eb69b3a5BC3F76c62Cb770F360", "joooaaaaoo")
//                .send();
//
//    }
//
//    private void printAddresses(AddressBook addressBook) throws Exception {
//        for(Object address : addressBook.getAddresses().send()){ //might need sendAsync if waiting for transactions to complete. Ganache is instant tho
//            String addressString = address.toString();
//            String alias = addressBook.getAlias(addressString).send();
//            Log.d(TAG, "address: "+ addressString + " alias: " + alias + "\n");
////            System.out.println("");
//        }
//    }
    }



    private Credentials getCredentialsFromPrivateKey(){
        TextView key = (TextView) findViewById(R.id.editUploadTxtName);
        if(key.getText().toString().equals("")){
            return Credentials.create(PRIVATE_KEY_CHROME);
        } else {
            return Credentials.create(key.getText().toString());
        }


    }

    private com.example.blockchainappv2.Sc_test loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return com.example.blockchainappv2.Sc_test.load(deployedAddr, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
//        return Erc721.deploy(
//                web3j,
//                credentials,
//                GAS_PRICE,
//                GAS_LIMIT).send().getContractAddress();
        return com.example.blockchainappv2.Sc_test.deploy(web3j, credentials, new DefaultGasProvider()).send().getContractAddress();
    }

    private Bitmap getBitmapFromAsset(String strName)
    {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
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