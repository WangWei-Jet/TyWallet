package com.whty.blockchain.tybitcoinlib.api;

import com.google.gson.Gson;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXO;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;
import com.whty.blockchain.tybitcoinlib.model.BitcoinjWrapper;
import com.whty.blockchain.tybitcoinlib.model.Coin;
import com.whty.blockchain.tybitcoinlib.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InsightAPI {
    //    private static final String TAG = InsightAPI.class.getName();
    private static Logger logger = LoggerFactory.getLogger(InsightAPI.class);
    private String baseURL;
    private Networking networking;

    public InsightAPI(String baseURL) {
        this.baseURL = baseURL;
        this.networking = new Networking();
    }

    public Object getBlockHeight() throws Exception {
        logger.debug("getBlockHeight");
        Object result = this.networking.getURLNotJSON(this.baseURL +
                "api/status?q=getTxOutSetInfo");
        logger.debug("getBlockHeight browser api return:" + result);
        return result;
    }

    public JSONObject getUnspentOutputs(List<String> addressArray) throws Exception {
        logger.debug("getUnspentOutputs");
        String params = StringUtils.join(addressArray, ",");
        Object obj = this.networking.getURLNotJSON(this.baseURL + "api/addrs/" + params + "/utxo");
        logger.debug("getUnspentOutputs browser api return:" + obj.toString());
        if (obj instanceof String) {
            String response = (String) obj;
            JSONArray array = new JSONArray(response);
            JSONObject transformedJsonData = InsightAPI
                    .insightUnspentOutputsToBlockchainUnspentOutputs(array);

            BlockchainUTXOResponse blockchainUTXOResponse = new Gson().fromJson
                    (transformedJsonData.toString(),
                    BlockchainUTXOResponse.class);
            if (blockchainUTXOResponse != null) {
                List<BlockchainUTXO> utxoList = blockchainUTXOResponse.getUnspentOutputs();
                if (utxoList != null && !utxoList.isEmpty()) {
                    logger.debug("utxoList size:" + utxoList.size());
                }
            }

            return transformedJsonData;
        } else {
            return (JSONObject) obj;
        }
    }

    private JSONObject getAddressesInfo(List<String> addressArray, long txCountFrom, JSONArray
            allTxs) throws Exception {
        logger.debug("getAddressesInfo");
        String params = "?from=" + txCountFrom + "&to=" + (txCountFrom + 50);
        String addresses = StringUtils.join(addressArray, ",");
        String responseData = this.networking.getURL(this.baseURL + "api/addrs/" + addresses +
                "/txs" + params);
        if (responseData == null) {
            return null;
        }
        JSONObject jsonData = new JSONObject(responseData);

        JSONArray txs = jsonData.getJSONArray("items");

        long to = jsonData.getLong("to");
        long totalItems = jsonData.getLong("totalItems");

        if (to >= totalItems) {
            if (allTxs.length() == 0) {
                JSONObject transformedJsonData = InsightAPI
                        .insightAddressesTxsToBlockchainMultiaddr(addressArray, txs);
                return transformedJsonData;
            } else {
                allTxs = Utils.concatArray(allTxs, txs);
                JSONObject transformedJsonData = InsightAPI
                        .insightAddressesTxsToBlockchainMultiaddr(addressArray, allTxs);
                return transformedJsonData;
            }
        } else {
            allTxs = Utils.concatArray(allTxs, txs);
            return this.getAddressesInfo(addressArray, to, allTxs);
        }
    }

    public JSONObject getAddressesInfo(List<String> addressArray) throws Exception {
        logger.debug("getAddressesInfo");
        JSONObject jsonObject = this.getAddressesInfo(addressArray, 0, new JSONArray());
        logger.debug("getAddressesInfo browser api return:" + jsonObject);
        return jsonObject;
    }

    public JSONObject getAddressData(String address) throws Exception {
        logger.debug("getAddressData");
        logger.debug("address:" + address);
        String responseData = this.networking.getURL(this.baseURL + "api/txs/?address=" + address);
        logger.debug("getAddressData browser api return:" + responseData);
        if (responseData == null) {
            return null;
        }
        JSONObject jsonData = new JSONObject(responseData);
        JSONArray txs = jsonData.getJSONArray("txs");
        JSONArray transformedTxs = new JSONArray();

        for (int i = 0; i < txs.length(); i++) {
            transformedTxs.put(InsightAPI.insightTxToBlockchainTx(txs.getJSONObject(i)));
        }
        JSONObject transformedJsonData = new JSONObject();
        transformedJsonData.put("txs", transformedTxs);
        return transformedJsonData;
    }

    public JSONObject getTx(String txHash) throws Exception {
        logger.debug("getTx");
        logger.debug("txHash:" + txHash);
        String responseData = this.networking.getURL(this.baseURL + "api/tx/" + txHash);
        logger.debug("getTx browser api return:" + responseData);
        if (responseData == null) {
            return null;
        }
        JSONObject jsonData = new JSONObject(responseData);
        return InsightAPI.insightTxToBlockchainTx(jsonData);
    }

    public JSONObject pushTx(String txHex, String txHash) throws Exception {
        logger.debug("pushTx");
        logger.debug("txHex:" + txHex + "\ttxHash:" + txHash);
        JSONObject jsonObject = this.networking.postURL(this.baseURL + "api/tx/send", "rawtx=" +
                txHex);
        logger.debug("pushTx browser api return:"+jsonObject);
        return jsonObject;
    }

    public static JSONObject insightToBlockchainUnspentOutput(JSONObject unspentOutputDict) {
        try {
            JSONObject blockchainUnspentOutputDict = new JSONObject();
            String txid = unspentOutputDict.getString("txid");
            blockchainUnspentOutputDict.put("tx_hash", BitcoinjWrapper.reverseHexString(txid));
            blockchainUnspentOutputDict.put("tx_hash_big_endian", txid);
            blockchainUnspentOutputDict.put("tx_output_n", unspentOutputDict.getInt("vout"));
            blockchainUnspentOutputDict.put("script", unspentOutputDict.getString("scriptPubKey"));
            blockchainUnspentOutputDict.put("value", unspentOutputDict.getLong("satoshis"));
            if (unspentOutputDict.has("confirmations")) {
                blockchainUnspentOutputDict.put("confirmations", unspentOutputDict.getLong
                        ("confirmations"));
            } else {
                blockchainUnspentOutputDict.put("confirmations", 0);
            }
            return blockchainUnspentOutputDict;
        } catch (JSONException e) {
            logger.debug("insightToBlockchainUnspentOutput JSONException " + e
                    .getLocalizedMessage());
            return null;
        }
    }

    public static JSONObject insightUnspentOutputsToBlockchainUnspentOutputs(JSONArray
                                                                                     unspentOutputs) {
        JSONArray transformedUnspentOutputs = new JSONArray();
        for (int i = 0; i < unspentOutputs.length(); i++) {
            try {
                JSONObject unspentOutput = unspentOutputs.getJSONObject(i);
                JSONObject dict = InsightAPI.insightToBlockchainUnspentOutput(unspentOutput);
                transformedUnspentOutputs.put(dict);
            } catch (JSONException e) {
            }
        }
        try {
            JSONObject transformedJsonData = new JSONObject();
            transformedJsonData.put("unspent_outputs", transformedUnspentOutputs);
            return transformedJsonData;
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject insightAddressesTxsToBlockchainMultiaddr(List<String> addressArray,
                                                                      JSONArray txs) {
        Set<String> addressExistDict = new HashSet<String>();
        JSONObject transformedAddressesDict = new JSONObject();
        try {

            for (String address : addressArray) {
                addressExistDict.add(address);
                JSONObject transformedAddress = new JSONObject();
                transformedAddress.put("n_tx", 0);
                transformedAddress.put("address", address);
                transformedAddress.put("final_balance", 0);
                transformedAddressesDict.put(address, transformedAddress);
            }
            List<JSONObject> transformedTxs = new ArrayList<JSONObject>();
            JSONArray transformedAddresses = new JSONArray();
            for (int i = txs.length() - 1; i >= 0; i--) {
                JSONObject tx = txs.getJSONObject(i);
                if (tx == null) {
                    continue;
                }

                JSONObject transformedTx = InsightAPI.insightTxToBlockchainTx(tx);
                if (transformedTx == null) {
                    continue;
                }
                transformedTxs.add(transformedTx);


                JSONArray inputsArray = transformedTx.getJSONArray("inputs");
                if (inputsArray != null) {
                    for (int j = 0; j < inputsArray.length(); j++) {
                        JSONObject input = inputsArray.getJSONObject(j);
                        if (input.has("prev_out")) {
                            JSONObject prevOut = input.getJSONObject("prev_out");
                            if (prevOut.has("addr")) {
                                String addr = prevOut.getString("addr");
                                if (addressExistDict.contains(addr)) {
                                    JSONObject transformedAddress = transformedAddressesDict
                                            .getJSONObject(addr);
                                    long addressBalance = transformedAddress.getLong
                                            ("final_balance");

                                    long value = prevOut.getLong("value");
                                    addressBalance = addressBalance - value;
                                    transformedAddress.put("final_balance", addressBalance);
                                    long nTxs = transformedAddress.getLong("n_tx");
                                    transformedAddress.put("n_tx", nTxs + 1);
                                }
                            }
                        }
                    }
                }

                JSONArray outsArray = transformedTx.getJSONArray("out");
                if (outsArray != null) {
                    for (int j = 0; j < outsArray.length(); j++) {
                        JSONObject output = outsArray.getJSONObject(j);
                        if (output.has("addr")) {
                            String addr = output.getString("addr");
                            if (addressExistDict.contains(addr)) {
                                JSONObject transformedAddress = transformedAddressesDict
                                        .getJSONObject(addr);
                                long addressBalance = transformedAddress.getLong("final_balance");

                                long value = output.getLong("value");
                                addressBalance = addressBalance + value;
                                transformedAddress.put("final_balance", addressBalance);
                                long nTxs = transformedAddress.getLong("n_tx");
                                transformedAddress.put("n_tx", nTxs + 1);
                            }
                        }
                    }
                }
            }
            JSONArray sortedtransformedTxs = new JSONArray();
            Collections.sort(transformedTxs, (a, b) -> {
                try {
                    long first = a.getLong("time");
                    if (first == 0) {
                        return 1;
                    }
                    long second = b.getLong("time");
                    if (second == 0) {
                        return -1;
                    }
                    if (second > first) {
                        return -1;
                    } else if (second == first) {
                        return 0;
                    }
                    return 1;
                } catch (JSONException e) {
                    return 0;
                }
            });

            for (int i = 0; i < transformedTxs.size(); i++) {
                sortedtransformedTxs.put(transformedTxs.get(i));
            }

            Iterator<?> keys = transformedAddressesDict.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject transformedAddress = transformedAddressesDict.getJSONObject(key);
                transformedAddress.put("final_balance", transformedAddress.getLong
                        ("final_balance"));
                transformedAddresses.put(transformedAddress);
            }

            JSONObject transformedJsonData = new JSONObject();
            transformedJsonData.put("txs", sortedtransformedTxs);
            transformedJsonData.put("addresses", transformedAddresses);
            return transformedJsonData;
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject insightTxToBlockchainTx(JSONObject txDict) {
        JSONObject blockchainTxDict = new JSONObject();
        try {

            JSONArray vins = txDict.getJSONArray("vin");
            JSONArray vouts = txDict.getJSONArray("vout");
            //if (vins == null && vouts == null && txDict.getString("possibleDoubleSpend") !=
            // null) {
            if (vins == null && vouts == null) {
                return null;
            }
            if (txDict.has("txid")) {
                blockchainTxDict.put("hash", txDict.getString("txid"));
            }
            if (txDict.has("version")) {
                blockchainTxDict.put("ver", txDict.getInt("version"));
            }
            if (txDict.has("size")) {
                blockchainTxDict.put("size", txDict.getInt("size"));
            }
            //WARNING: time dont match on different blockexplorers, and field does not exist if
            // unconfirmed
            if (txDict.has("time")) {
                blockchainTxDict.put("time", txDict.getInt("time"));
            }
            if (txDict.has("confirmations")) {
                blockchainTxDict.put("confirmations", txDict.getLong("confirmations"));
                blockchainTxDict.put("block_height", txDict.getLong("confirmations"));
            } else {
                blockchainTxDict.put("confirmations", 0);
                blockchainTxDict.put("block_height", 0);
            }
            if (vins != null) {
                JSONArray inputs = new JSONArray();
                for (int i = 0; i < vins.length(); i++) {
                    JSONObject vin = vins.getJSONObject(i);
                    JSONObject input = new JSONObject();

                    if (vin.has("sequence")) {
                        input.put("sequence", vin.getInt("sequence"));
                    }
                    JSONObject prevOut = new JSONObject();
                    if (vin.has("addr")) {
                        prevOut.put("addr", vin.getString("addr"));
                    } else {
                        //can be nil, for example, mined coins on tx
                        // 32ee55597c590bb104c524298b14fd1c0ac96a230810bd1e68d109df532a46a0
                    }
                    if (vin.has("valueSat")) {
                        prevOut.put("value", vin.getLong("valueSat"));
                    }
                    if (vin.has("n")) {
                        prevOut.put("n", vin.getInt("n"));
                    }
                    input.put("prev_out", prevOut);
                    inputs.put(input);
                }
                blockchainTxDict.put("inputs", inputs);
            }

            if (vouts != null) {
                JSONArray outs = new JSONArray();
                for (int i = 0; i < vouts.length(); i++) {
                    JSONObject vout = vouts.getJSONObject(i);
                    JSONObject aOut = new JSONObject();
                    if (vout.has("n")) {
                        aOut.put("n", vout.getInt("n"));
                    }
                    if (vout.has("scriptPubKey")) {
                        JSONObject scriptPubKey = vout.getJSONObject("scriptPubKey");
                        if (scriptPubKey.has("addresses")) {
                            JSONArray addresses = scriptPubKey.getJSONArray("addresses");
                            if (addresses.length() == 1) {
                                aOut.put("addr", addresses.getString(0));
                            }
                        }
                        if (scriptPubKey.has("hex")) {
                            aOut.put("script", scriptPubKey.getString("hex"));
                        }
                    }
                    if (vout.has("value")) {
                        Coin coinValue = Coin.fromString(vout.getString("value"), Coin
                                .BitcoinDenomination.BTC);
                        aOut.put("value", coinValue.toNumber());
                    }
                    outs.put(aOut);
                }
                blockchainTxDict.put("out", outs);
            }
            return blockchainTxDict;
        } catch (JSONException e) {
            logger.debug("insightTxToBlockchainTx JSONException " + e.getLocalizedMessage());
            return null;
        }
    }
}
