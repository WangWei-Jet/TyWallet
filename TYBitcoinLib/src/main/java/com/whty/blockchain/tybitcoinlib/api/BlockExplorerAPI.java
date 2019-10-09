package com.whty.blockchain.tybitcoinlib.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockExplorerAPI {

    public enum BlockExplorer {
        Blockchain, Blockchain_Testnet, Insight, Insight_Testnet, BlockCypher_Testnet;
    }

    public BlockExplorer blockExplorerAPI = BlockExplorer.Blockchain;
    private String BLOCKEXPLORER_BASE_URL = "https://blockchain.info/";
    private String BLOCKEXPLORER_TEST_BASE_URL = "https://testnet.blockchain.info/";
    private String BLOCKEXPLORER_INSIGHT_BASE_URL = "https://insight.bitpay.com/";
    private String BLOCKEXPLORER_TEST_INSIGHT_BASE_URL = "https://test-insight.bitpay.com/";
    private String BLOCKCYPHER_TEST_BASE_URL = "https://api.blockcypher.com/v1/bcy/test/";

    private BlockchainAPI blockchainAPI = null;
    private BlockCypherAPI blockCypherAPI = null;
    public InsightAPI insightAPI = null;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BlockExplorerAPI(BlockExplorer blockExplorer) {

        blockExplorerAPI = blockExplorer;

        if (blockExplorerAPI == BlockExplorer.Blockchain) {
            this.blockchainAPI = new BlockchainAPI(BLOCKEXPLORER_BASE_URL);
            //needed for push tx api for stealth addresses
            this.insightAPI = new InsightAPI(BLOCKEXPLORER_INSIGHT_BASE_URL);
        } else if (blockExplorerAPI == BlockExplorer.Insight) {
            this.insightAPI = new InsightAPI(BLOCKEXPLORER_INSIGHT_BASE_URL);
        } else if (blockExplorerAPI == BlockExplorer.Blockchain_Testnet) {
            this.blockchainAPI = new BlockchainAPI(BLOCKEXPLORER_TEST_BASE_URL);
            this.insightAPI = new InsightAPI(BLOCKEXPLORER_TEST_INSIGHT_BASE_URL);
        } else if (blockExplorerAPI == BlockExplorer.Insight_Testnet) {
            this.insightAPI = new InsightAPI(BLOCKEXPLORER_TEST_INSIGHT_BASE_URL);
        } else if (blockExplorerAPI == BlockExplorer.BlockCypher_Testnet) {
            this.blockCypherAPI = new BlockCypherAPI(BLOCKCYPHER_TEST_BASE_URL);
        }
    }

    public JSONObject getBlockHeight() throws Exception {
        logger.debug("getBlockHeight");
        if (blockExplorerAPI == BlockExplorer.Insight) {
            return null;
        } else {
            Object obj = this.blockchainAPI.getBlockHeight();
            if (obj instanceof String) {
                JSONObject ret = new JSONObject();
                try {
                    ret.put("height", obj);
                    return ret;
                } catch (JSONException e) {
                    return null;
                }
            } else if (obj instanceof JSONObject) {
                return (JSONObject) obj;
            }
        }
        return null;
    }

    public JSONObject getAddressesInfo(List<String> addressArray) throws Exception {
        logger.debug("getAddressesInfo");
        if (blockExplorerAPI == BlockExplorer.Insight) {
            return this.insightAPI.getAddressesInfo(addressArray);
        } else {
            return this.blockchainAPI.getAddressesInfo(addressArray);
        }
    }

    public JSONObject getUnspentOutputs(List<String> addressArray) throws Exception {
        logger.debug("getUnspentOutputs");
        if (blockExplorerAPI == BlockExplorer.Insight || blockExplorerAPI == BlockExplorer
                .Insight_Testnet) {
            return this.insightAPI.getUnspentOutputs(addressArray);
        } else if(blockExplorerAPI == BlockExplorer.BlockCypher_Testnet){
            return this.blockCypherAPI.getUnspentOutputs(addressArray);
        }else {
            return this.blockchainAPI.getUnspentOutputs(addressArray);
        }
    }

    public JSONObject getAddressData(String address) throws Exception {
        logger.debug("getAddressData");
        if (blockExplorerAPI == BlockExplorer.Insight) {
            return this.insightAPI.getAddressData(address);
        } else {
            return this.blockchainAPI.getAddressData(address);
        }
    }

    public JSONObject getTx(String txHash) throws Exception {
        logger.debug("getTx");
        if (blockExplorerAPI == BlockExplorer.Insight) {
            return this.insightAPI.getTx(txHash);
        } else {
            return this.blockchainAPI.getTx(txHash);
        }
    }

    public JSONObject pushTx(String txHex, String txHash) throws Exception {
        logger.debug("pushTx");
        if (blockExplorerAPI == BlockExplorer.Insight || blockExplorerAPI == BlockExplorer
                .Insight_Testnet) {
            return this.insightAPI.pushTx(txHex, txHash);
        } else if(blockExplorerAPI == BlockExplorer.BlockCypher_Testnet){
            return this.blockCypherAPI.pushTx(txHex,txHash);
        }else {
            return this.blockchainAPI.pushTx(txHex, txHash);
        }
    }

    public String getURLForWebViewAddress(String address) {
        logger.debug("getURLForWebViewAddress");
        return BLOCKEXPLORER_BASE_URL + "address/" + address;
    }

    public String getURLForWebViewTx(String tx) {
        logger.debug("getURLForWebViewTx");
        return BLOCKEXPLORER_BASE_URL + "tx/" + tx;
    }
}
