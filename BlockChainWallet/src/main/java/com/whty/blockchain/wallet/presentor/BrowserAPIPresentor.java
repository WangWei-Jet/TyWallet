package com.whty.blockchain.wallet.presentor;

import com.google.gson.Gson;
import com.whty.blockchain.tybitcoinlib.api.BlockExplorerAPI;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;
import com.whty.blockchain.wallet.entity.Env;
import com.whty.blockchain.wallet.model.EthereumWeb3jService;
import com.whty.blockchain.wallet.model.EthereumWeb3jServiceImpl;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BrowserAPIPresentor extends BasePresentor {

    //Env
    private Env env;
    //ethereum
    private EthereumWeb3jService ethereumWeb3jService;

    //bitcoin
    private BlockExplorerAPI blockExplorerAPI;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BrowserAPIPresentor(Env env) {
        setEnv(env);
    }

    public void setEnv(Env env) {
        logger.debug("set env");
        if (env == null) {
            logger.debug("param env null");
            return;
        }
        logger.debug("param env:" + env);
        this.env = env;
        ethereumWeb3jService = new EthereumWeb3jServiceImpl();
        switch (env) {
            case ETHEREUM:
            case ETHEREUM_TESTNET:
                ethereumWeb3jService.setEnv(env);
                break;

            case BITCOIN:
                blockExplorerAPI = new BlockExplorerAPI(BlockExplorerAPI.BlockExplorer.Blockchain);
                break;

            case BITCOIN_TESTNET:
                blockExplorerAPI = new BlockExplorerAPI(BlockExplorerAPI.BlockExplorer
                        .Blockchain_Testnet);
                break;
        }
    }

    public Env getEnv() {
        return env;
    }

    public void getBalance(final String address) {
        logger.debug("env:" + env);
        if (address == null || address.trim().length() == 0) {
            getView().onError("账户地址为空");
            return;
        }
        switch (env) {
            case ETHEREUM:
            case ETHEREUM_TESTNET:
                try {
                    Observable.create(new ObservableOnSubscribe<BigInteger>() {
                        @Override
                        public void subscribe(ObservableEmitter<BigInteger> emitter) {
                            BigInteger balanceInteger = ethereumWeb3jService.getBalance(address);
                            emitter.onNext(balanceInteger);
                            emitter.onComplete();
                        }
                    }).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<BigInteger>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    logger.debug("onSubscribe");
                                    getView().showLoadingDialogWithContentMsg("正在查余额...");
                                }

                                @Override
                                public void onNext(BigInteger bigInteger) {
                                    logger.debug("get balance=>" + bigInteger);
                                    getView().showEthereumEnvironmentBalanceResult(bigInteger);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    logger.error("onError: 异常", e);
                                    getView().dismissLoadingDialog();
                                    getView().onError("异常:" + e.getMessage());
                                }

                                @Override
                                public void onComplete() {
                                    logger.debug("on complete");
                                    getView().dismissLoadingDialog();
                                }
                            });
                } catch (Exception e) {
                    logger.error("get balance: 异常", e);
                }
                break;

            case BITCOIN:
            case BITCOIN_TESTNET:
                List<String> addressList = new ArrayList<>();
                String finalAddress = address;
                if(address.startsWith("0x")){
                    finalAddress = address.substring(2);
                }
                addressList.add(finalAddress);
                try {
                    try {
                        Observable.create(new ObservableOnSubscribe<JSONObject>() {
                            @Override
                            public void subscribe(ObservableEmitter<JSONObject> emitter) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = blockExplorerAPI.getUnspentOutputs(addressList);
                                } catch (Exception e) {
                                    logger.error("获取UTXO异常", e);
                                }
                                if (jsonObject == null) {
                                    Exception exception = new Exception("浏览器API获取UTXO为空");
                                    emitter.onError(exception);
                                } else {
                                    emitter.onNext(jsonObject);
                                }
                                emitter.onComplete();
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<JSONObject>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        logger.debug("onSubscribe");
                                        getView().showLoadingDialogWithContentMsg("正在查UTXO...");
                                    }

                                    @Override
                                    public void onNext(JSONObject jsonObject) {
                                        logger.debug("onNext");
                                        BlockchainUTXOResponse currentBlockchainUTXOResponse = new
                                                Gson().fromJson(jsonObject.toString(),
                                                BlockchainUTXOResponse.class);
                                        if (currentBlockchainUTXOResponse == null ||
                                                currentBlockchainUTXOResponse.getUnspentOutputs() ==
                                                        null || currentBlockchainUTXOResponse
                                                .getUnspentOutputs().isEmpty()) {
                                            logger.debug("查询到的UTXO为空");
                                        }
                                        getView().showBitcoinEnvironmentBalanceResult
                                                (currentBlockchainUTXOResponse);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        logger.error("onError", e);
                                        getView().dismissLoadingDialog();
                                        getView().onError("异常:" + e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {
                                        logger.debug("onComplete");
                                        getView().dismissLoadingDialog();
                                    }
                                });
                    } catch (Exception e) {
                        logger.error("get balance: 异常", e);
                    }
                } catch (Exception e) {
                    logger.error("比特币获取UTXO异常", e);
                }
                break;
            default:
                logger.debug("unsupported env");
                break;
        }
    }
}
