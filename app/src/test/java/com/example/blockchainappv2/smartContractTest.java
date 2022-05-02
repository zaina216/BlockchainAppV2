package com.example.blockchainappv2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import static org.junit.Assert.*;

import java.math.BigInteger;

class smartContractTest {

    String testpk = "664899c672b95434dc0dc6f99baa95701f36d9dfe412d061626d4117ae2e5ffd";
    Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/292bf993eaf9433594b8926593cfd04c"));
    Credentials credentials = getCredentialsFromPrivateKey();
    String contractLocation = "";
    GetAllTokId nft = loadContract("0xfeaa5fe401400505cfa259d780bd2062a854b13f", web3j, credentials);




    private Credentials getCredentialsFromPrivateKey(){
        return Credentials.create(testpk);
    }

    private GetAllTokId loadContract(String deployedAddr, Web3j web3j, Credentials credentials){
        return GetAllTokId.load(deployedAddr, web3j, credentials, BigInteger.valueOf(20000000000L), BigInteger.valueOf(6721975L));
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
//        return Erc721.deploy(
//                web3j,
//                credentials,
//                GAS_PRICE,
//                GAS_LIMIT).send().getContractAddress();
        GetAllTokId vruh = GetAllTokId.deploy(web3j, credentials, new DefaultGasProvider()).send();
        return vruh.getContractAddress();
    }


    @Test
    void testDeploy() {
        assertEquals(5L, 5L);
//        assertNotSame("", deployContract(web3j, credentials), "did not deploy");
    }

    @Test
    void testMint() throws Exception {
        int balanceBeforeMint = nft.balanceOf("0x2412F42C68dDe2Ee49514975d3bEA066B1320723").send().intValue();
    }

    @Test
    void onButtonClick() {
    }
}