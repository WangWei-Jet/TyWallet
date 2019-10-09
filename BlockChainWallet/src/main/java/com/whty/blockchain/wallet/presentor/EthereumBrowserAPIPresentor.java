package com.whty.blockchain.wallet.presentor;

import com.whty.blockchain.wallet.entity.Env;
import com.whty.blockchain.wallet.model.EthereumWeb3jService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class EthereumBrowserAPIPresentor extends BasePresentor {
    //ethereum
    private EthereumWeb3jService ethereumWeb3jService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public EthereumBrowserAPIPresentor(EthereumWeb3jService ethereumWeb3jService) {
        this.ethereumWeb3jService = ethereumWeb3jService;
    }

    public boolean setEnv(Env env) {
        logger.debug("set env");
        if(!isViewAttached()){
            logger.debug("view is no longer attached");
            return false;
        }
        if (env == null) {
            logger.debug("param env null");
            return false;
        }
        logger.debug("param env:" + env);
        switch (env) {
            case ETHEREUM:
            case ETHEREUM_TESTNET:
                ethereumWeb3jService.setEnv(env);
                logger.debug("env set success");
                return true;
            default:
                break;
        }
        return false;
    }

    public BigInteger getBalance(String address) {
        logger.debug("get balance");
        if(!isViewAttached()){
            logger.debug("view is no longer attached");
            return null;
        }
        BigInteger balanceInteger = ethereumWeb3jService.getBalance(address);
        logger.debug("get balance result:"+balanceInteger);
        return balanceInteger;
    }
}
