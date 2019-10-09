package com.whty.blockchain.tywallet;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.whty.blockchain.tybitcoinlib.model.Coin;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignTxResponse;
import com.whty.blockchain.tyblockchainlib.api.util.GPMethods;

import org.bitcoinj.core.Sha256Hash;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {


        Coin coin = Coin.fromString("0.00990373",Coin.BitcoinDenomination.BTC);
        long satoshiBTC = coin.toNumber();
        System.out.println("transferBTC: satoshi:"+satoshiBTC);

        String txHex = "010000000001018F143A22464F3C9D7B18EF7A677473CA5F2B79E537FC68D6B4AB00B11FF18FEA0000000017160014665D59A5289C8798476A18B8D70FCDCA9A28DE76FFFFFFFF01701A3E000000000017A9143BECBC08BFA44512EBB9C71D7F6D817AAF2D74FB8702473044022022355C324079C8963A8A48782B2DEE24B096B94A373152E8612B86DF4BD5108902203E770198D2EEEC99918FF75510FC0CB3EADBC811505EE8E26D774D894DC3EB190121031EA891C3D7CACADE3798709DF95A929D5DD6392041E0592B553AB59D8E1727DF00000000";


        byte[] txHexBytes = BaseEncoding.base16().upperCase().decode(txHex);

        txHex = BaseEncoding.base16().lowerCase().encode(txHexBytes);

        System.out.println("txHex:"+txHex);

        byte[] hashTwice = Sha256Hash.hashTwice(txHexBytes);

        System.out.println("hashTwice:"+GPMethods.bytesToHexString(hashTwice));

        String txHash = Sha256Hash.wrapReversed(hashTwice).toString();

        System.out.println("txHash:"+txHash);

        String address = "43FE224C8963724B4E4BDD60B68C9F5A71DC10DC";

        BigInteger bigInteger = new BigInteger(address,16);

        System.out.println("address number:"+bigInteger);

        String response = "";
        System.out.println("response:"+response);
        try {
            SignTxResponse signTxResponse = new Gson().fromJson(response, SignTxResponse.class);

            System.out.println("signTxResponse:"+signTxResponse);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        String str = "800d:Device is already initialized. Use Wipe first.";
        String[] strs = str.split(":");
        System.out.println("str size:"+strs.length);
        for(String tempStr : strs){
            System.out.println("str:"+tempStr);
        }

        assertEquals(4, 2 + 2);
    }
}