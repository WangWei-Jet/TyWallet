package com.whty.blockchain.tybitcoinlib.api;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockchainAPI {
    private String baseURL;
    private Networking networking;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public BlockchainAPI(String baseURL) {
        this.baseURL = baseURL;
        this.networking = new Networking();
    }

    public Object getBlockHeight() throws Exception {
        logger.info("getBlockHeight");
        return this.networking.getURLNotJSON(this.baseURL+"q/getblockcount");
    }

    public JSONObject getAddressData(String address) throws Exception {
        logger.info("getAddressData");
        String responseData = this.networking.getURL(this.baseURL+"address/"+address);
        logger.debug("browser api response:"+responseData);

        return responseData == null ? null : (new JSONObject(responseData));
    }

    public JSONObject getTx(String txHash) throws Exception {
        logger.debug("getTx");
        logger.debug("tx hash:"+txHash);
        String responseData = this.networking.getURL(this.baseURL + "tx/" + txHash+"?format=json");
        logger.debug("browser api response:"+responseData);
        return responseData == null ? null : (new JSONObject(responseData));
    }

    public JSONObject pushTx(String txHex, String txHash) throws Exception {
        logger.debug("pushTx");
        logger.debug("tx hex:"+txHex+"\ttx hash:"+txHash);
        JSONObject obj = this.networking.postURLNotJSON(this.baseURL + "pushtx", "tx=" + txHex);
        logger.debug("pushTx browser api return:"+obj.toString());
        if (obj.has(Networking.HTTP_ERROR_CODE)) {
            return obj;
        } else {
            JSONObject txidObj = new JSONObject();
            txidObj.put("response","Transaction Submitted");
            //TODO calculate transaction id
//            txidObj.put("txid", BitcoinjWrapper.reverseHexString(txHash));
            return txidObj;
        }
    }

    public JSONObject getUnspentOutputs(List<String> addressArray) throws Exception {
        logger.debug("getUnspentOutputs");
        String params = "?active="+StringUtils.join(addressArray, "|");
        String responseData = this.networking.getURL(this.baseURL+"unspent"+params);
        logger.debug("getUnspentOutputs browser api return:"+responseData);
        return responseData == null ? null : (new JSONObject(responseData));
    }

    public JSONObject getAddressesInfo(List<String> addressArray) throws Exception {
        logger.debug("getAddressesInfo");
        String params = "?no_button=true&active="+StringUtils.join(addressArray, "|");
        String responseData = this.networking.getURL(this.baseURL+"multiaddr"+params);
        logger.debug("getAddressesInfo browser api return:"+responseData);
        return responseData == null ? null : (new JSONObject(responseData));
    }
}
