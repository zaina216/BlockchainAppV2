package com.example.blockchainappv2;

//import org.testng.annotations.Test;
import static android.content.ContentValues.TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import android.util.Log;

import com.google.gson.Gson;

import org.bouncycastle.crypto.generators.KDFDoublePipelineIterationBytesGenerator;
import org.junit.Test;
import org.junit.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import pinata.Pinata;
import pinata.PinataException;
import pinata.PinataResponse;

//import static org.testng.AssertJUnit.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {



//    String testpk = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    String testpk = "35a49d01c8211b3f968371d429d32606bafe38dae4835aa93dfe4ea5dd17c8c9";
    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    Credentials credentials = getCredentialsFromPrivateKey();
    String contractLocation = null;
    GetAllTokId nft = loadContract("0xfeaa5fe401400505cfa259d780bd2062a854b13f", web3j, credentials);

    @Test
    public void testDeploy() throws Exception {
        contractLocation = deployContract(web3j, credentials);
        assertNotNull(contractLocation);
        System.out.println(contractLocation);
        assertNotSame("", deployContract(web3j, credentials), "did not deploy");
    }

    @Test
    public void testMint() throws Exception {

        List allTokIds = nft.getTokenIds("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send();
        System.out.println(nft.owner().send());
        System.out.println(allTokIds);
        int beforeBalance = nft.balanceOf("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send().intValue();
        nft.mintUniqueToken("0x2412F42C68dDe2Ee49514975d3bEA066B1320723", "1.1.1.1").send();
        int afterBalance = nft.balanceOf("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send().intValue();
        allTokIds = nft.getTokenIds("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send();
        System.out.println(allTokIds);
        assertEquals(afterBalance, beforeBalance + 1);

    }


    @Test
    public void testTr() throws Exception { //this works

        List allTokIds = nft.getTokenIds("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send();
        BigInteger tokenToTransfer = (BigInteger)allTokIds.get(allTokIds.size()-1);
        System.out.println("will transfer - "+tokenToTransfer.toString());

        try{
            nft.setApprovalForAll ("0x2412F42C68dDe2Ee49514975d3bEA066B1320723", true);

            nft.transferFrom("0xa7d7df54c33e6579c9de2aff3df86dd2f0723c28",
                    "0x28321a3929e33a0c8300ccbf2c825f6683c0f9d8",
                    BigInteger.valueOf(105)).send();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("transferred");
        System.out.println(nft.ownerOf(tokenToTransfer).send());
        System.out.println(allTokIds);
        assertEquals("0x28321a3929e33a0c8300ccbf2c825f6683c0f9d8".toLowerCase(),
                nft.ownerOf(BigInteger.valueOf(105)).send());
    }



    @Test
    public void testPin() throws Exception {
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

        PinataUploadClass pupload = new PinataUploadClass();
        pupload.itemDesc = "unitTest";
        pupload.itemName = "UnitTest name";
        pupload.returnDate = "unitTest return date";

        Gson gson = new Gson();
        String json = gson.toJson(pupload);
        System.out.println(json);

        // write json to file
        try{

            File file = new File("F:\\Documents\\pinatatest\\unitTestPinata.json");
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(json);
            output.close();
            System.out.println("File written");

        }catch(Exception e){
            System.out.println("Could not create the file");
        }

        PinataResponse pinResponse = new PinataResponse();

        //pin test file
        try {
            System.out.println("pinning file...");
            pinResponse = pinata.pinFileToIpfs(new File("F:\\Documents\\pinatatest\\unitTestPinata.json"));
            System.out.println("file pinned.");

        } catch (PinataException | IOException e) {
            e.printStackTrace();
        }

        // now we try to read the file from pinata
        gson = new Gson();
        PinataResponseClass resp = gson.fromJson(pinResponse.getBody(), PinataResponseClass.class);
//        Log.d(TAG, resp.IpfsHash);

        String interwebAddress = "https://gateway.pinata.cloud/ipfs/" + resp.IpfsHash;
        String internetJson = readUrl(interwebAddress);
        System.out.println("local url - " + json);
        System.out.println("internet url - " + internetJson);
        assertEquals(internetJson, json);




    }
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }


    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(testpk);
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, BigInteger.valueOf(20000000000L), BigInteger.valueOf(6721975L));
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {

        GetAllTokId vruh = GetAllTokId.deploy(web3j, credentials, new DefaultGasProvider()).send();
        return vruh.getContractAddress();

    }

}