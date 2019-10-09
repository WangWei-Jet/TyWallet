package com.whty.blockchain.tyblockchainlib.api.impl;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.whty.blockchain.tyblockchainlib.api.TyWalletJni;
import com.whty.blockchain.tyblockchainlib.api.core.TyWallet;
import com.whty.blockchain.tyblockchainlib.api.core.TyWalletBtDriver;
import com.whty.blockchain.tyblockchainlib.api.core.TyWalletDriver;
import com.whty.blockchain.tyblockchainlib.api.core.WalletObserver;
import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionInput;
import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionOutput;
import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionRequest;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.entity.CreateWalletConfig;
import com.whty.blockchain.tyblockchainlib.api.entity.EthTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.entity.RecoverWalletConfig;
import com.whty.blockchain.tyblockchainlib.api.entity.ResponseCode;
import com.whty.blockchain.tyblockchainlib.api.entity.SignedDataRes;
import com.whty.blockchain.tyblockchainlib.api.entity.TokenTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.pojo.BackupWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.CreateWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.DeviceFeature;
import com.whty.blockchain.tyblockchainlib.api.pojo.DeviceUpgradtionResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.ErrorCode;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetDeviceFeatureResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.RecoverWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignEthTxResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignTxResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.WipeDeviceResponse;
import com.whty.blockchain.tyblockchainlib.api.util.GPMethods;
import com.whty.blockchain.tyblockchainlib.api.util.TLV;
import com.whty.blockchain.tyblockchainlib.api.util.TLVParser;
import com.whty.blockchain.tyblockchainlib.api.util.UpgradeListener;
import com.whty.blockchain.tyblockchainlib.api.util.UpgradeUtil;
import com.whty.bluetoothsdk.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class TyWalletImpl implements TyWallet {

    private TyWalletBtDriver tyWalletBtDriver;

    private Context context;

    private WalletObserver walletObserver;

    private final long COMMAND_TIMEOUT = 3000L;

    private final int BTC = 0;
    private final int TESTNET = 1;
    private final int ETH = 60;

    private TyWalletDriver tyWalletDriver;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    TyWalletImpl(@NonNull Context context) {

        this.context = context.getApplicationContext();

        tyWalletBtDriver = new TyWalletBtDriver(this.context);

        tyWalletBtDriver.init();
    }

    public TyWalletImpl(@NonNull Context context, @NonNull WalletObserver walletObserver) {

        this(context);

        this.walletObserver = walletObserver;

        tyWalletDriver = new TyWalletDriver(tyWalletBtDriver, walletObserver);

        //日志设置
//        new TYLoggerConfig.Builder()
//                .tag("TYWalletLib")
//                .enableLog(true)
//                .fileLogger(true)
//                .fileDirectory(Environment.getExternalStorageDirectory().getPath() +
//                        "/TYWalletLogs")
//                .fileFormatter(new SimpleFormatter())
//                .build();

    }


    public TyWalletDriver getTyWalletDriver() {
        return tyWalletDriver;
    }

    @Override
    public boolean connectWallet(final BluetoothDevice device) {
        logger.debug("connectWallet ");
        boolean result = connectWalletSync(device);
        logger.debug("connectWallet return:" + result);
        return result;
    }

    @Override
    public boolean isWalletConnected() {
        return tyWalletBtDriver.isConnected();
    }

    private synchronized boolean connectWalletSync(final BluetoothDevice device) {
        logger.debug("connectWalletSync ");
        try {
            if (tyWalletBtDriver.isConnected()) {
                logger.debug("connectWalletSync: wallet already in connected state");
                return true;
            }
            boolean connectResult = tyWalletBtDriver.connectWallet(device);
            logger.debug("connectWalletSync return:" + connectResult);
            return connectResult;
        } catch (Exception e) {
            logger.error("connectWalletSync: 异常", e);
        }
        return false;
    }

    private void connectWalletAsyn(final BluetoothDevice device) {
        logger.debug("connectWalletAsyn: ");

        try {
            //子线程处理业务
            //主线程通知代理类结果
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    //连接设备
                    boolean connectResult = connectWalletSync(device);
                    emitter.onNext(connectResult);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            //正常返回
                            logger.debug("accept: 设备连接结果:" + aBoolean);
                            if (walletObserver != null) {
                                walletObserver.onWalletConnect(device, aBoolean, ResponseCode
                                        .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //异常
                            logger.error("accept: 设备连接接口异常", throwable);
                            if (walletObserver != null) {
                                walletObserver.onWalletConnect(device, false, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("connectWalletAsyn: 异常", e);
            if (walletObserver != null) {
                walletObserver.onWalletConnect(device, false, ResponseCode
                        .SYSTEM_ERR);
            }
        }
    }

    @Override
    public boolean disconnectWallet() {
        logger.debug("disconnectWallet: ");
        boolean result = disconnectWalletSync();
        logger.debug("disconnectWallet return:" + result);
        return result;
    }

    private synchronized boolean disconnectWalletSync() {
        logger.debug("disconnectWalletSync: ");
        try {
            boolean disconnectResult = tyWalletBtDriver.disconnectWallet();
            logger.debug("disconnectWalletSync: " + disconnectResult);
            return disconnectResult;
        } catch (Exception e) {
            logger.error("disconnectWalletSync: 异常", e);
        }
        return false;
    }

    private void disconnectWalletAsyn() {
        logger.debug("disconnectWalletAsyn: ");

        try {
            //子线程处理业务
            //主线程通知代理类结果
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    //断开设备连接
                    boolean disconnectResult = disconnectWalletSync();

                    emitter.onNext(disconnectResult);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            //正常返回
                            logger.debug("accept: 断开设备连接结果:" + aBoolean);
                            if (walletObserver != null) {
                                walletObserver.onWalletDisconnect(aBoolean, ResponseCode
                                        .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //异常
                            logger.error("accept: 断开设备连接接口异常", throwable);
                            if (walletObserver != null) {
                                walletObserver.onWalletDisconnect(false, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("disconnectWalletAsyn: 异常", e);
            if (walletObserver != null) {
                walletObserver.onWalletDisconnect(false, ResponseCode
                        .SYSTEM_ERR);
            }
        }
    }

    @Override
    public GetAddressResponse getAddress(String path, CoinType coinType) {
        logger.debug("getAddress: ");
        GetAddressResponse getAddressResponse = getAddressSync(path, coinType);
        logger.debug("getAddress return:" + getAddressResponse);
        return getAddressResponse;
    }

    public synchronized GetAddressResponse getAddressSync(String path, CoinType coinType) {
        logger.debug("getAddressSync: ");
        logger.debug("path:" + path + "\tcoin type:" + coinType);
        GetAddressResponse getAddressResponse;
        String errorDescription;
        try {
            getAddressResponse = new GetAddressResponse();
            getAddressResponse.setCode(ErrorCode.EXCEPTION);
            getAddressResponse.setDescriptionCode(ErrorCode.PARAM_ERROR + "");
            if(path == null){
                errorDescription = "path null";
                logger.debug(errorDescription);
                getAddressResponse.setDescription(errorDescription);
                return getAddressResponse;
            }
            String addressJson;
            switch (coinType) {
                case BTC:
                    addressJson = TyWalletJni.getAddress(path, BTC, getTyWalletDriver());
                    break;

                case ETH:
                    addressJson = TyWalletJni.getAddress(path, ETH, getTyWalletDriver());
                    break;

                case TESTNET:
                    addressJson = TyWalletJni.getAddress(path, TESTNET, getTyWalletDriver());
                    break;

                default:
                    errorDescription = "coin type not supported";
                    logger.debug(errorDescription);
                    getAddressResponse.setDescription(errorDescription);
                    return getAddressResponse;
            }
            logger.debug("address json:" + addressJson);
            getAddressResponse = new Gson().fromJson(addressJson, GetAddressResponse.class);
            logger.debug("getAddressSync: get address response:" + getAddressResponse);
            if (getAddressResponse == null) {
                errorDescription = "get address return null";
                getAddressResponse = new GetAddressResponse();
                getAddressResponse.setCode(ErrorCode.EXCEPTION);
                getAddressResponse.setDescription(errorDescription);
                getAddressResponse.setDescriptionCode(ErrorCode.RESULT_NULL + "");
                return getAddressResponse;
            }
            getAddressResponse.setDescription(getAddressResponse.getDescription());
            if (getAddressResponse.getAddress() !=
                    null && !getAddressResponse.getAddress().startsWith("0x")) {
                getAddressResponse.setAddress("0x" + getAddressResponse.getAddress());
            }
            logger.debug("getAddressSync: get address response:" + getAddressResponse);
        } catch (Exception e) {
            logger.error("getAddressSync: 异常", e);
            getAddressResponse = new GetAddressResponse();
            getAddressResponse.setCode(ErrorCode.EXCEPTION);
            getAddressResponse.setDescription("exception");
            getAddressResponse.setDescriptionCode(ErrorCode.EXCEPTION+"");
        }

        return getAddressResponse;
    }


    public void getAddressAsyn(final String path, final CoinType coinType) {
        logger.debug("getAddressAsyn: ");
        logger.debug("path:" + path + "\tcoin type:" + coinType);

        try {
            //子线程处理业务
            //主线程通知代理类结果
            Observable.create(new ObservableOnSubscribe<GetAddressResponse>() {
                @Override
                public void subscribe(ObservableEmitter<GetAddressResponse> emitter) {
                    GetAddressResponse walletAddress = getAddressSync(path, coinType);

                    emitter.onNext(walletAddress);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GetAddressResponse>() {
                        @Override
                        public void accept(GetAddressResponse getAddressResponse) throws Exception {
                            //正常返回
                            logger.debug("accept: get address 结果:" + getAddressResponse.toString());
                            if (walletObserver != null) {
                                walletObserver.onGetAddress(path, coinType, getAddressResponse,
                                        ResponseCode
                                                .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //异常
                            logger.error("accept: get address 接口异常", throwable);
                            if (walletObserver != null) {
                                GetAddressResponse getAddressResponse = new GetAddressResponse();
                                getAddressResponse.setAddress(null);
                                getAddressResponse.setCode(ErrorCode.EXCEPTION);
                                getAddressResponse.setDescription("exception");
                                walletObserver.onGetAddress(path, coinType, getAddressResponse,
                                        ResponseCode
                                                .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("getAddressAsyn: 异常", e);
            if (walletObserver != null) {
                GetAddressResponse getAddressResponse = new GetAddressResponse();
                getAddressResponse.setAddress(null);
                getAddressResponse.setCode(ErrorCode.EXCEPTION);
                getAddressResponse.setDescription("exception");
                walletObserver.onGetAddress(path, coinType, getAddressResponse, ResponseCode
                        .SYSTEM_ERR);
            }
        }
    }

    @Override
    public String exportPublicKey(String path) {
        logger.debug("exportPublicKey: ");
        String result = exportPublicKeySync(path);
        logger.debug("export public key return:" + result);
        return result;
    }

    public String exportPublicKeySync(String path) {
        logger.debug("exportPublicKeySync: ");
        if (path == null) {
            return null;
        }
        return null;
    }

    public void exportPublicKeyAsyn(final String path) {
        logger.debug("exportPublicKeyAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) {
                    String publicKey = exportPublicKeySync(path);

                    emitter.onNext(publicKey);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            logger.debug("accept: public key=>" + s);
                            if (walletObserver != null) {
                                walletObserver.onExportPublicKey(path, s, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                walletObserver.onExportPublicKey(path, null, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("exportPublicKeyAsyn: 异常", e);
        }

    }

    @Override
    public SignTxResponse signTx(BitcoinTransactionRequest bitcoinTransactionRequest) {
        logger.debug("signTx:");
        logger.debug("bitcoinTransactionRequest:" + bitcoinTransactionRequest);
        SignTxResponse signTxResponse = signTxSync(bitcoinTransactionRequest);
        logger.debug("signTxResponse:" + signTxResponse);
        return signTxResponse;
    }


    public synchronized SignTxResponse signTxSync(BitcoinTransactionRequest
                                                          bitcoinTransactionRequest) {
        logger.debug("signTxSync: ");
        logger.debug("bitcoinTransactionRequest:" + bitcoinTransactionRequest);

        SignTxResponse signTxResponse;

        String errorMsg;
        try {
            signTxResponse = new SignTxResponse();
            signTxResponse.setCode(ErrorCode.INPUT_PARAMETERS_ERROR);
            signTxResponse.setDescriptionCode(ErrorCode.PARAM_ERROR + "");
            if (bitcoinTransactionRequest == null) {
                errorMsg = "bitcoinTransactionRequest null";
                logger.debug(errorMsg);
                signTxResponse.setDescription(errorMsg);
                return signTxResponse;
            }
            int coinType = bitcoinTransactionRequest.getCoinType();
            List<BitcoinTransactionInput> bitcoinTransactionInputList = bitcoinTransactionRequest
                    .getInputList();
            List<BitcoinTransactionOutput> bitcoinTransactionOutputList =
                    bitcoinTransactionRequest.getOutputList();
            switch (coinType) {
                case 0:
                    logger.debug("coin type 0:bitcoin");
                    break;

                case 1:
                    logger.debug("coin type 1:testnet");
                    break;

                default:
                    errorMsg = "unknown coin type";
                    logger.debug(errorMsg);
                    signTxResponse = new SignTxResponse();
                    signTxResponse.setCode(ErrorCode.NOT_SUPPORT);
                    signTxResponse.setDescription(errorMsg);
                    return signTxResponse;
            }

            if (bitcoinTransactionInputList == null || bitcoinTransactionInputList.isEmpty()) {
                errorMsg = "bitcoinTransactionRequest input list null";
                logger.debug(errorMsg);
                signTxResponse.setDescription(errorMsg);
                return signTxResponse;
            }

            if (bitcoinTransactionOutputList == null || bitcoinTransactionOutputList.isEmpty()) {
                errorMsg = "bitcoinTransactionRequest output list null";
                logger.debug(errorMsg);
                signTxResponse.setDescription(errorMsg);
                return signTxResponse;
            }
            for (BitcoinTransactionInput bitcoinTransactionInput : bitcoinTransactionInputList) {
                String addressN = bitcoinTransactionInput.getAddress_n();
                String previousTransactionHash = bitcoinTransactionInput.getPrev_hash();
                String previousIndex = bitcoinTransactionInput.getPrev_index();
                String amountSatoshi = bitcoinTransactionInput.getAmountSatoshi();
                if (addressN == null || addressN.trim().length() == 0
                        || previousTransactionHash == null || previousTransactionHash.trim()
                        .length() == 0 || previousIndex == null || previousIndex.trim().length()
                        == 0 || amountSatoshi == null || amountSatoshi.trim().length() == 0) {
                    errorMsg = "bitcoinTransactionRequest input should contain at less addressN," +
                            "previousHash,previousIndex and amountSatoshi";
                    logger.debug(errorMsg);
                    signTxResponse.setDescription(errorMsg);
                    return signTxResponse;
                }
            }

            for (BitcoinTransactionOutput bitcoinTransactionOutput : bitcoinTransactionOutputList) {
                String address = bitcoinTransactionOutput.getAddress();
                String amountSatoshi = bitcoinTransactionOutput.getAmountSatoshi();
                if (address == null || address.trim().length() == 0
                        || amountSatoshi == null || amountSatoshi.trim().length() == 0) {
                    errorMsg = "bitcoinTransactionRequest output should contain at less address" +
                            " and amountSatoshi";
                    logger.debug(errorMsg);
                    signTxResponse.setDescription(errorMsg);
                    return signTxResponse;
                }
            }
            logger.debug("param correct.reset response");

            String requestJsonStr = new Gson().toJson(bitcoinTransactionRequest);

            String response = TyWalletJni.signTx(requestJsonStr, getTyWalletDriver());

            logger.debug("response:" + response);

            signTxResponse = new Gson().fromJson(response, SignTxResponse.class);
            if (signTxResponse == null) {
                errorMsg = "response null";
                signTxResponse = new SignTxResponse();
                signTxResponse.setCode(ErrorCode.EXCEPTION);
                signTxResponse.setDescription(errorMsg);
                signTxResponse.setDescriptionCode(ErrorCode.RESULT_NULL + "");
                return signTxResponse;
            }
            signTxResponse.setDescription(signTxResponse.getDescription());

        } catch (Exception e) {
            logger.error("异常", e);
            signTxResponse = new SignTxResponse();
            signTxResponse.setCode(ErrorCode.EXCEPTION);
            signTxResponse.setDescription("exception");
            signTxResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
        }
        return signTxResponse;

    }

    public void signTxAsyn(final BitcoinTransactionRequest bitcoinTransactionRequest) {
        logger.debug("signTxAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<SignTxResponse>() {
                @Override
                public void subscribe(ObservableEmitter<SignTxResponse> emitter) {
                    SignTxResponse txSignedData = signTxSync(bitcoinTransactionRequest);

                    emitter.onNext(txSignedData);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<SignTxResponse>() {
                        @Override
                        public void accept(SignTxResponse s) throws Exception {
                            logger.debug("accept: tx signed data=>" + s);
                            if (walletObserver != null) {
                                walletObserver.onSignTx(bitcoinTransactionRequest, s, ResponseCode
                                        .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                SignTxResponse signTxResponse = new SignTxResponse();
                                signTxResponse.setSerialized(null);
                                signTxResponse.setCode(ErrorCode.EXCEPTION);
                                signTxResponse.setDescription("exception");
                                walletObserver.onSignTx(bitcoinTransactionRequest, signTxResponse,
                                        ResponseCode
                                                .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("signTxAsyn: 异常", e);
            if (walletObserver != null) {
                SignTxResponse signTxResponse = new SignTxResponse();
                signTxResponse.setSerialized(null);
                signTxResponse.setCode(ErrorCode.EXCEPTION);
                signTxResponse.setDescription("exception");
                walletObserver.onSignTx(bitcoinTransactionRequest, signTxResponse, ResponseCode
                        .SYSTEM_ERR);
            }
        }

    }


    @Override
    public SignEthTxResponse signEthTx(EthTransactionInfoReq ethTransactionInfoReq) {
        logger.debug("signEthTx: ");
        logger.debug("ethTransactionInfoReq:" + ethTransactionInfoReq);
        SignEthTxResponse signEthTxResponse = signEthTxSync(ethTransactionInfoReq);
        logger.debug("sign eth tx return:" + signEthTxResponse);
        return signEthTxResponse;
    }


    public synchronized SignEthTxResponse signEthTxSync(EthTransactionInfoReq
                                                                ethTransactionInfoReq) {
        logger.debug("signEthTxSync: ");
        logger.debug("ethTransactionInfoReq:" + ethTransactionInfoReq);

        SignEthTxResponse signEthTxResponse;
        String errorDescription;
        try {
            signEthTxResponse = new SignEthTxResponse();
            signEthTxResponse.setCode(ErrorCode.EXCEPTION);
            signEthTxResponse.setDescriptionCode(ErrorCode.PARAM_ERROR + "");
            if (ethTransactionInfoReq == null) {
                errorDescription = "transactionInfo is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            String addressN = ethTransactionInfoReq.getAddressN();
            if (addressN == null) {
                errorDescription = "ethTransactionInfo addressN is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            String[] eles = addressN.split("/");
            if (eles.length != 6) {
                errorDescription = "ethTransactionInfo addressN format error";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getChainId() == null) {
                String predictedChainId = eles[2];
                //在未指定chainId的情况下，通过地址索引判断对应的链
                if (predictedChainId.startsWith("0")) {
                    //比特币
                    ethTransactionInfoReq.setChainId(0);
                } else if (predictedChainId.startsWith("1")) {
                    //测试链(所有测试币的)
                    ethTransactionInfoReq.setChainId(4);
                } else if (predictedChainId.startsWith("60")) {
                    //以太坊
                    ethTransactionInfoReq.setChainId(1);
                } else {
                    errorDescription = "not supported addressN format";
                    logger.debug(errorDescription);
                    signEthTxResponse.setDescription(errorDescription);
                    return signEthTxResponse;
                }
            }
            logger.debug("signEthTxSync: final chainId:" + ethTransactionInfoReq.getChainId());
            if (ethTransactionInfoReq.getChainId() == null) {
                errorDescription = "chain id is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getGasLimit() == null) {
                errorDescription = "ethTransactionInfo gas limit is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getGasLimit().compareTo(BigInteger.valueOf(0)) <= 0) {
                errorDescription = "ethTransaction gas limit not bigger than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getGasPrice() == null) {
                errorDescription = "ethTransactionInfo gas price is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getGasPrice().compareTo(BigInteger.valueOf(0)) <= 0) {
                errorDescription = "ethTransactionInfo gas price not bigger than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getNonce() == null) {
                errorDescription = "ethTransactionInfo nonce is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getNonce().compareTo(BigInteger.valueOf(0)) < 0) {
                errorDescription = "ethTransactionInfo nonce less than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getToAddress() == null) {
                errorDescription = "ethTransactionInfo to address is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getToAddress().startsWith("0x")) {
                ethTransactionInfoReq.setToAddress(ethTransactionInfoReq.getToAddress().substring
                        (2));
            }
            if (ethTransactionInfoReq.getValue() == null) {
                errorDescription = "ethTransactionInfo transfer value is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (ethTransactionInfoReq.getValue().compareTo(BigDecimal.valueOf(0)) <= 0) {
                errorDescription = "ethTransactionInfo transfer value less than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            //参数判断完毕
            logger.debug("param correct.reset response");

            signEthTxResponse = new SignEthTxResponse();

            //设备返回的数据包含 r s v
            String signedEthTxData = signEthTransaction(ethTransactionInfoReq);
            logger.debug("signedEthTxData:" + signedEthTxData);
            if (signedEthTxData == null || signedEthTxData.trim().length() == 0) {
                errorDescription = "get signed data fail";
                logger.debug(errorDescription);
                signEthTxResponse.setCode(ErrorCode.EXCEPTION);
                signEthTxResponse.setDescription(errorDescription);
                signEthTxResponse.setDescriptionCode(ErrorCode.RESULT_NULL + "");
                return signEthTxResponse;
            }
            BigDecimal weiValue = Convert.toWei(ethTransactionInfoReq.getValue(), Convert.Unit
                    .ETHER);
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction
                    (ethTransactionInfoReq.getNonce(),
                            ethTransactionInfoReq
                                    .getGasPrice(), ethTransactionInfoReq.getGasLimit(),
                            ethTransactionInfoReq
                                    .getToAddress(),
                            weiValue.toBigInteger());

            SignedDataRes signedData = new Gson().fromJson(signedEthTxData, SignedDataRes.class);
            if (signedData == null) {
                errorDescription = "get signed data fail";
                logger.debug(errorDescription);
                signEthTxResponse.setCode(ErrorCode.EXCEPTION);
                signEthTxResponse.setDescription(errorDescription);
                signEthTxResponse.setDescriptionCode(ErrorCode.RESULT_NULL + "");
                return signEthTxResponse;
            }
            signedData.setDescription(signedData.getDescription());
            String r = signedData.getR();
            logger.debug("signEthTx: r:" + r);
            String s = signedData.getS();
            logger.debug("signEthTx: s:" + s);

            if (r == null || s == null || r.trim().length() == 0 || s.trim().length() == 0) {
                logger.debug("r or s null");
                signEthTxResponse.setCode(signedData.getCode());
                signEthTxResponse.setDescription(signedData.getDescription());
                signEthTxResponse.setDescriptionCode(signedData.getDescriptionCode());
                return signEthTxResponse;
            }

            byte[] rb = Utils.hexString2Bytes(r);
            byte[] sb = Utils.hexString2Bytes(s);
            byte v = (byte) signedData.getV();

            Sign.SignatureData signatureData = new Sign.SignatureData(v, rb, sb);
            byte[] finalDataBytes = encode(rawTransaction, signatureData);
            String hexValue = Numeric.toHexString(finalDataBytes);

            signEthTxResponse.setSignedData(hexValue);
            signEthTxResponse.setCode(signedData.getCode());
            signEthTxResponse.setDescription(signedData.getDescription());
            signEthTxResponse.setDescriptionCode(signedData.getDescriptionCode());

            logger.debug("signEthTxSync: signedEthTxData=>" + hexValue);
        } catch (Exception e) {
            logger.error("signEthTxSync: 异常", e);
            signEthTxResponse = new SignEthTxResponse();
            signEthTxResponse.setCode(ErrorCode.EXCEPTION);
            signEthTxResponse.setDescription("exception");
            signEthTxResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
        }
        return signEthTxResponse;

    }

    private byte[] encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private List<RlpType> asRlpValues(
            RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList<>();

        result.add(RlpString.create(rawTransaction.getNonce()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        if (signatureData != null) {
            result.add(RlpString.create(signatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        return result;
    }

    public void signEthTxAsyn(final EthTransactionInfoReq ethTransactionInfoReq) {
        logger.debug("signEthTxAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<SignEthTxResponse>() {
                @Override
                public void subscribe(ObservableEmitter<SignEthTxResponse> emitter) {
                    SignEthTxResponse signEthTxResponse = signEthTxSync(ethTransactionInfoReq);

                    emitter.onNext(signEthTxResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<SignEthTxResponse>() {
                        @Override
                        public void accept(SignEthTxResponse signEthTxResponse) throws Exception {
                            logger.debug("accept: eth tx signed data=>" + signEthTxResponse
                                    .toString());
                            if (walletObserver != null) {
                                walletObserver.onSignEthTx(ethTransactionInfoReq,
                                        signEthTxResponse, ResponseCode
                                                .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                SignEthTxResponse signEthTxResponse = new SignEthTxResponse();
                                signEthTxResponse.setSignedData(null);
                                signEthTxResponse.setCode(ErrorCode.EXCEPTION);
                                signEthTxResponse.setDescription("exception");
                                walletObserver.onSignEthTx(ethTransactionInfoReq,
                                        signEthTxResponse, ResponseCode
                                                .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("signEthTxAsyn: 异常", e);
            if (walletObserver != null) {
                SignEthTxResponse signEthTxResponse = new SignEthTxResponse();
                signEthTxResponse.setSignedData(null);
                signEthTxResponse.setCode(ErrorCode.EXCEPTION);
                signEthTxResponse.setDescription("exception");
                walletObserver.onSignEthTx(ethTransactionInfoReq, signEthTxResponse, ResponseCode
                        .SYSTEM_ERR);
            }
        }

    }

    @Override
    public SignEthTxResponse signEthTokenTx(TokenTransactionInfoReq tokenTransactionInfoReq) {
        logger.debug("signEthTokenTx:");
        logger.debug("tokenTransactionInfoReq:" + tokenTransactionInfoReq);
        SignEthTxResponse signEthTxResponse = signEthTokenTxSync(tokenTransactionInfoReq);
        logger.debug("signEthTokenTx return:" + signEthTxResponse);
        return signEthTxResponse;
    }

    public synchronized SignEthTxResponse signEthTokenTxSync(TokenTransactionInfoReq
                                                                     tokenTransactionInfoReq) {
        logger.debug("signEthTokenTxSync: ");
        logger.debug("tokenTransactionInfoReq:" + tokenTransactionInfoReq);

        SignEthTxResponse signEthTxResponse;
        String errorDescription;
        try {
            signEthTxResponse = new SignEthTxResponse();
            signEthTxResponse.setCode(ErrorCode.EXCEPTION);
            signEthTxResponse.setDescriptionCode(ErrorCode.PARAM_ERROR + "");
            if (tokenTransactionInfoReq == null) {
                errorDescription = "token transactionInfo is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            String addressN = tokenTransactionInfoReq.getAddressN();
            if (addressN == null) {
                errorDescription = "ethTokenTransactionInfo addressN is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            String[] eles = addressN.split("/");
            if (eles.length != 6) {
                errorDescription = "ethTokenTransactionInfo addressN format error";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getChainId() == null) {
                String predictedChainId = eles[2];
                //在未指定chainId的情况下，通过地址索引判断对应的链
                if (predictedChainId.startsWith("0")) {
                    //比特币
                    tokenTransactionInfoReq.setChainId(0);
                } else if (predictedChainId.startsWith("1")) {
                    //测试链(所有测试币的)
                    tokenTransactionInfoReq.setChainId(4);
                } else if (predictedChainId.startsWith("60")) {
                    //以太坊
                    tokenTransactionInfoReq.setChainId(1);
                } else {
                    errorDescription = "not supported addressN format";
                    logger.debug(errorDescription);
                    signEthTxResponse.setDescription(errorDescription);
                    return signEthTxResponse;
                }
            }
            logger.debug("signEthTokenTxSync: final chainId:" + tokenTransactionInfoReq
                    .getChainId());
            if (tokenTransactionInfoReq.getChainId() == null) {
                errorDescription = "chain id is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getGasLimit() == null) {
                errorDescription = "ethTokenTransactionInfo gas limit is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getGasLimit().compareTo(BigInteger.valueOf(0)) <= 0) {
                errorDescription = "ethTokenTransaction gas limit not bigger than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getGasPrice() == null) {
                errorDescription = "ethTokenTransactionInfo gas price is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getGasPrice().compareTo(BigInteger.valueOf(0)) <= 0) {
                errorDescription = "ethTransactionInfo gas price not bigger than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getNonce() == null) {
                errorDescription = "ethTokenTransactionInfo nonce is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getNonce().compareTo(BigInteger.valueOf(0)) < 0) {
                errorDescription = "ethTokenTransactionInfo nonce less than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getTokenAddress() == null) {
                errorDescription = "ethTokenTransactionInfo token address is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getTokenAddress().startsWith("0x")) {
                tokenTransactionInfoReq.setTokenAddress(tokenTransactionInfoReq.getTokenAddress()
                        .substring
                                (2));
            }
            if (tokenTransactionInfoReq.getToAddress() == null) {
                errorDescription = "ethTokenTransactionInfo to address is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getToAddress().startsWith("0x")) {
                tokenTransactionInfoReq.setToAddress(tokenTransactionInfoReq.getToAddress()
                        .substring
                                (2));
            }
            if (tokenTransactionInfoReq.getValue() == null) {
                errorDescription = "ethTokenTransactionInfo transfer value is null";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            if (tokenTransactionInfoReq.getValue().compareTo(BigDecimal.valueOf(0)) <= 0) {
                errorDescription = "ethTokenTransactionInfo transfer value less than zero";
                logger.debug(errorDescription);
                signEthTxResponse.setDescription(errorDescription);
                return signEthTxResponse;
            }
            //参数判断完毕
            logger.debug("param correct.reset response");

            signEthTxResponse = signEthTokenTransaction(tokenTransactionInfoReq);
            logger.debug("signEthTokenTxSync: signedEthTokenTxData=>" + signEthTxResponse
                    .getSignedData());

        } catch (Exception e) {
            logger.error("signEthTokenTxSync: 异常", e);
            signEthTxResponse = new SignEthTxResponse();
            signEthTxResponse.setCode(ErrorCode.EXCEPTION);
            signEthTxResponse.setDescription("exception");
            signEthTxResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
        }

        return signEthTxResponse;
    }

    private void signEthTokenTxAsyn(final TokenTransactionInfoReq
                                            tokenTransactionInfoReq) {
        logger.debug("signEthTokenTxAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<SignEthTxResponse>() {
                @Override
                public void subscribe(ObservableEmitter<SignEthTxResponse> emitter) {
                    SignEthTxResponse signEthTxResponse = signEthTokenTxSync
                            (tokenTransactionInfoReq);

                    emitter.onNext(signEthTxResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<SignEthTxResponse>() {
                        @Override
                        public void accept(SignEthTxResponse signEthTxResponse) {
                            logger.debug("accept: eth tx signed data=>" + signEthTxResponse
                                    .toString());
                            if (walletObserver != null) {
                                walletObserver.onSignEthTokenTx(tokenTransactionInfoReq,
                                        signEthTxResponse, ResponseCode
                                                .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                SignEthTxResponse signEthTxResponse = new SignEthTxResponse();
                                signEthTxResponse.setSignedData(null);
                                signEthTxResponse.setCode(ErrorCode.EXCEPTION);
                                signEthTxResponse.setDescription("exception");
                                walletObserver.onSignEthTokenTx(tokenTransactionInfoReq,
                                        signEthTxResponse, ResponseCode
                                                .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("signEthTokenTxAsyn: 异常", e);
            if (walletObserver != null) {
                SignEthTxResponse signEthTxResponse = new SignEthTxResponse();
                signEthTxResponse.setSignedData(null);
                signEthTxResponse.setCode(ErrorCode.EXCEPTION);
                signEthTxResponse.setDescription("exception");
                walletObserver.onSignEthTokenTx(tokenTransactionInfoReq, signEthTxResponse,
                        ResponseCode
                                .SYSTEM_ERR);
            }
        }

    }

    @Override
    public GetDeviceFeatureResponse getDeviceFeature() {
        logger.debug("getDeviceFeature: ");
        GetDeviceFeatureResponse getDeviceFeatureResponse = getDeviceFeatureSync();
        logger.debug("getDeviceFeature return:" + getDeviceFeatureResponse);
        return getDeviceFeatureResponse;
    }

    public synchronized GetDeviceFeatureResponse getDeviceFeatureSync() {
        //TODO 检查description code是否已返回
        logger.debug("getDeviceFeatureSync: ");
        GetDeviceFeatureResponse getDeviceFeatureResponse;
        try {
            String result = TyWalletJni.getFeatures(getTyWalletDriver());
            logger.debug("getDeviceFeatureSync: " + result);
            DeviceFeature deviceFeature = new Gson().fromJson(result, DeviceFeature.class);
            getDeviceFeatureResponse = new GetDeviceFeatureResponse();
            getDeviceFeatureResponse.setDeviceFeature(deviceFeature);
            HashMap<String,String > info = getDevicePNAndVer();
            if (info != null) {
                getDeviceFeatureResponse.setDevicePN(GPMethods.hexStr2Str(info.get("devicePN")));
                getDeviceFeatureResponse.setDeviceVer(GPMethods.hexStr2Str(info.get("deviceVer")));
            }
            getDeviceFeatureResponse.setDeviceSN(getDeviceSN());
            getDeviceFeatureResponse.setDeviceBattery(getDeviceBattery());

        } catch (Exception e) {
            logger.error("getDeviceFeatureSync: 异常", e);
            getDeviceFeatureResponse = new GetDeviceFeatureResponse();
            getDeviceFeatureResponse.setDeviceFeature(null);
            getDeviceFeatureResponse.setCode(ErrorCode.EXCEPTION);
            getDeviceFeatureResponse.setDescription("exception");

        }
        return getDeviceFeatureResponse;
    }

    public void getDeviceFeatureAsyn() {
        logger.debug("getDeviceFeatureAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<GetDeviceFeatureResponse>() {
                @Override
                public void subscribe(ObservableEmitter<GetDeviceFeatureResponse> emitter) {
                    GetDeviceFeatureResponse getDeviceFeatureResponse = getDeviceFeatureSync();

                    emitter.onNext(getDeviceFeatureResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GetDeviceFeatureResponse>() {
                        @Override
                        public void accept(GetDeviceFeatureResponse s) throws Exception {
                            logger.debug("accept: get account info=>" + s.toString());
                            if (walletObserver != null) {
                                walletObserver.onGetDeviceFeature(s, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                GetDeviceFeatureResponse getDeviceFeatureResponse = new
                                        GetDeviceFeatureResponse();
                                getDeviceFeatureResponse.setDeviceFeature(null);
                                getDeviceFeatureResponse.setCode(ErrorCode.EXCEPTION);
                                getDeviceFeatureResponse.setDescription("exception");
                                walletObserver.onGetDeviceFeature(getDeviceFeatureResponse,
                                        ResponseCode
                                                .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("getAccountInfoAsyn: 异常", e);
            if (walletObserver != null) {
                GetDeviceFeatureResponse getDeviceFeatureResponse = new
                        GetDeviceFeatureResponse();
                getDeviceFeatureResponse.setDeviceFeature(null);
                getDeviceFeatureResponse.setCode(ErrorCode.EXCEPTION);
                getDeviceFeatureResponse.setDescription("exception");
                walletObserver.onGetDeviceFeature(getDeviceFeatureResponse, ResponseCode
                        .SYSTEM_ERR);
            }
        }
    }


    public synchronized String getDeviceSN() {
        logger.debug("getDeviceSNSync: ");

        try {
            byte[] cmd_send = GPMethods.str2bytes("FE01010600");
            byte[] response = new byte[300];
            logger.debug("prepare to get device SN");
            int ret = tyWalletBtDriver.transCommandAPDU(cmd_send, cmd_send.length,
                    response, COMMAND_TIMEOUT);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(response, 0, res, 0, ret);
                if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                    // 返回的数据时TLV格式的
                    String deviceSN = Utils.bytesToHexString(res, ret - 2);
                    TLVParser tlvParser = new TLVParser(new String[]{"06"});
                    TLV[] tlvs = tlvParser.getTLVs(GPMethods
                            .str2bytes(deviceSN));
                    deviceSN = GPMethods.bytesToHexString(tlvs[0].getData());
                    return deviceSN;
                } else {
                    //
                }
            } else {
                logger.debug("command interaction timeout!");
            }
        } catch (Exception e) {
            logger.debug("error occurs trying to get device SN",e);
        }
        return null;
    }

    public synchronized HashMap<String,String > getDevicePNAndVer() {
        logger.debug("getDevicePNSync: ");
        try {
            byte[] cmd_send = GPMethods.str2bytes("FE01000000");
            byte[] response = new byte[300];
            logger.debug("prepare to get device PN");
            int ret = tyWalletBtDriver.transCommandAPDU(cmd_send, cmd_send.length,
                    response, COMMAND_TIMEOUT);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(response, 0, res, 0, ret);
                if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                    // 返回的数据时TLV格式的
                    String devicePN = Utils.bytesToHexString(res, ret - 2);
                    TLVParser tlvParser = new TLVParser(new String[]{"01","02","03","04"});
                    TLV[] tlvs = tlvParser.getTLVs(GPMethods
                            .str2bytes(devicePN));
                    devicePN = GPMethods.bytesToHexString(tlvs[2].getData());
                    String deviceVer = GPMethods.bytesToHexString(tlvs[3].getData());
                    HashMap<String,String > deviceInfo = new HashMap();
                    deviceInfo.put("devicePN",devicePN);
                    deviceInfo.put("deviceVer",deviceVer);
                    return deviceInfo;
                } else {
                    //
                }
            } else {
                logger.debug("command interaction timeout!");
            }
        } catch (Exception e) {
            logger.debug("error occurs trying to get device PN",e);
        }
        return null;
    }

    public synchronized int getDeviceBattery() {
        logger.debug("getDeviceBatterySync: ");
        try {
            byte[] cmd_send = GPMethods.str2bytes("F0A0000000");
            byte[] response = new byte[300];
            logger.debug("prepare to get device Battery");
            int ret = tyWalletBtDriver.transCommandAPDU(cmd_send, cmd_send.length,
                    response, COMMAND_TIMEOUT);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(response, 0, res, 0, ret);
                if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                    logger.debug("power is " + res[0]);
                    return res[0];
                } else {
                    //
                }
            } else {
                logger.debug("command interaction timeout!");
            }
        } catch (Exception e) {
            logger.debug("error occurs trying to get device Battery",e);
        }
        return 0;
    }

    @Override
    public CreateWalletResponse createWallet(CreateWalletConfig createWalletConfig) {
        logger.debug("createWallet: ");
        logger.debug("createWalletConfig:" + createWalletConfig);
        CreateWalletResponse createWalletResponse = createWalletSync(createWalletConfig);
        logger.debug("CreateWalletResponse return:" + createWalletResponse);
        return createWalletResponse;
    }

    synchronized CreateWalletResponse createWalletSync(CreateWalletConfig createWalletConfig) {
        logger.debug("createWalletSync: ");
        logger.debug("createWalletConfig:" + createWalletConfig);
        CreateWalletResponse createWalletResponse;
        try {
            if (createWalletConfig == null) {
                logger.debug("createWalletSync: create wallet config null");
                return null;
            }
            if (createWalletConfig.getName() == null) {
                logger.debug("createWalletSync: wallet name null");
                return null;
            }
            String configJsonStr = new Gson().toJson(createWalletConfig);
            logger.debug("createWalletSync: config:" + configJsonStr);
            String result = TyWalletJni.createWallet(configJsonStr,
                    getTyWalletDriver());
            logger.debug("createWalletSync: result:" + result);
            createWalletResponse = new Gson().fromJson(result,
                    CreateWalletResponse.class);
            if (createWalletResponse != null) {
                createWalletResponse.setDescription(createWalletResponse.getDescription());
            }
            logger.debug("createWalletSync: createWalletResponse:" + createWalletResponse);
            if (createWalletResponse != null && createWalletResponse.getCode() == 0) {
                createWalletResponse.setOperationSuccess(true);
            }
            return createWalletResponse;
        } catch (Exception e) {
            logger.error("wipeWalletSync: 异常", e);
            createWalletResponse = new CreateWalletResponse();
            createWalletResponse.setOperationSuccess(false);
            createWalletResponse.setCode(ErrorCode.EXCEPTION);
            createWalletResponse.setDescription("exception");
        }
        return createWalletResponse;
    }

    void createWalletAsync(final CreateWalletConfig createWalletConfig) {
        logger.debug("createWalletAsync: ");
        logger.debug("createWalletConfig:" + createWalletConfig);
        try {
            Observable.create(new ObservableOnSubscribe<CreateWalletResponse>() {
                @Override
                public void subscribe(ObservableEmitter<CreateWalletResponse> emitter) {

                    CreateWalletResponse createWalletResponse = createWallet(createWalletConfig);

                    emitter.onNext(createWalletResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CreateWalletResponse>() {
                        @Override
                        public void accept(CreateWalletResponse createWalletResponse) throws
                                Exception {
                            logger.debug("accept: create Wallet result=>" + createWalletResponse
                                    == null ? null : createWalletResponse.toString());
                            if (walletObserver != null) {
                                walletObserver.onCreateWallet(createWalletResponse, ResponseCode
                                        .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                CreateWalletResponse createWalletResponse = new
                                        CreateWalletResponse();
                                createWalletResponse.setOperationSuccess(false);
                                createWalletResponse.setCode(ErrorCode.EXCEPTION);
                                createWalletResponse.setDescription("exception");
                                walletObserver.onCreateWallet(createWalletResponse, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("createWalletAsync: 异常", e);
            if (walletObserver != null) {
                CreateWalletResponse createWalletResponse = new CreateWalletResponse();
                createWalletResponse.setOperationSuccess(false);
                createWalletResponse.setCode(ErrorCode.EXCEPTION);
                createWalletResponse.setDescription("exception");
                walletObserver.onCreateWallet(createWalletResponse, ResponseCode.SYSTEM_ERR);
            }
        }
    }

    @Override
    public WipeDeviceResponse wipeDevice() {
        logger.debug("wipeDevice: ");
        WipeDeviceResponse wipeDeviceResponse = wipeDeviceSync();
        logger.debug("wipeDeviceResponse:" + wipeDeviceResponse);
        return wipeDeviceResponse;
    }

    synchronized WipeDeviceResponse wipeDeviceSync() {
        logger.debug("wipeDeviceSync: ");
        WipeDeviceResponse wipeDeviceResponse = null;
        try {
            String result = TyWalletJni.wipeWallet(getTyWalletDriver());
            logger.debug("wipeDeviceSync: result:" + result);
            wipeDeviceResponse = new Gson().fromJson(result,
                    WipeDeviceResponse.class);
            if (wipeDeviceResponse != null) {
                wipeDeviceResponse.setDescription(wipeDeviceResponse.getDescription());
            }
            logger.debug("wipeWalletSync: wipe device result:" + wipeDeviceResponse);
            if (wipeDeviceResponse != null && wipeDeviceResponse.getCode() == 0) {
                wipeDeviceResponse.setOperationSuccess(true);
                return wipeDeviceResponse;
            }
        } catch (Exception e) {
            logger.error("wipeDeviceSync: 异常", e);
            wipeDeviceResponse = new WipeDeviceResponse();
            wipeDeviceResponse.setOperationSuccess(false);
            wipeDeviceResponse.setCode(ErrorCode.EXCEPTION);
            wipeDeviceResponse.setDescription("exception");
        }
        return wipeDeviceResponse;
    }

    void wipeDeviceAsync() {
        logger.debug("wipeDeviceAsync: ");
        try {
            Observable.create(new ObservableOnSubscribe<WipeDeviceResponse>() {
                @Override
                public void subscribe(ObservableEmitter<WipeDeviceResponse> emitter) {

                    WipeDeviceResponse wipeDeviceResponse = wipeDeviceSync();

                    emitter.onNext(wipeDeviceResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<WipeDeviceResponse>() {
                        @Override
                        public void accept(WipeDeviceResponse b) throws Exception {
                            logger.debug("accept: wipe Wallet result=>" + b);
                            if (walletObserver != null) {
                                walletObserver.onWipeDevice(b, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                WipeDeviceResponse wipeDeviceResponse = new WipeDeviceResponse();
                                wipeDeviceResponse.setOperationSuccess(false);
                                wipeDeviceResponse.setCode(ErrorCode.EXCEPTION);
                                wipeDeviceResponse.setDescription("exception");
                                walletObserver.onWipeDevice(wipeDeviceResponse, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("wipeDeviceAsync: 异常", e);
            if (walletObserver != null) {
                WipeDeviceResponse wipeDeviceResponse = new WipeDeviceResponse();
                wipeDeviceResponse.setOperationSuccess(false);
                wipeDeviceResponse.setCode(ErrorCode.EXCEPTION);
                wipeDeviceResponse.setDescription("exception");
                walletObserver.onWipeDevice(wipeDeviceResponse, ResponseCode.SYSTEM_ERR);
            }

        }
    }

    @Override
    public boolean initialize() {
        logger.debug("initialize: ");
        boolean result = initializeSync();
        logger.debug("initialize return:" + result);
        return result;
    }

    public synchronized boolean initializeSync() {
        logger.debug("initializeSync: ");

        try {
            //TODO
            byte[] req = new byte[]{(byte) 0xf0, (byte) 0xc3, 0x00, 0x00, 0x00};
            int reqLen = req.length;
            byte[] res = new byte[512];
            int resLen = tyWalletBtDriver.transCommandAPDU(req, reqLen, res, COMMAND_TIMEOUT);
            logger.debug("initializeSync: 指令回复数据长度=>" + resLen);
            return false;
        } catch (Exception e) {
            logger.error("initializeSync: 异常", e);
        }

        return false;
    }

    public void initializeAsyn() {
        logger.debug("initializeAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {

                    boolean result = initializeSync();

                    emitter.onNext(result);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean b) throws Exception {
                            logger.debug("accept: initialize result=>" + b);
                            if (walletObserver != null) {
                                walletObserver.onInitialize(b, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                walletObserver.onInitialize(false, ResponseCode.SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("initializeAsyn: 异常", e);
        }
    }

    @Override
    public RecoverWalletResponse recoverWallet(RecoverWalletConfig recoverWalletConfig) {
        logger.debug("recoverWallet: ");
        logger.debug("recoverWalletConfig:" + recoverWalletConfig);
        RecoverWalletResponse recoverWalletResponse = recoverWalletSync(recoverWalletConfig);
        return recoverWalletResponse;
    }

    public synchronized RecoverWalletResponse recoverWalletSync(RecoverWalletConfig
                                                                        recoverWalletConfig) {
        logger.debug("recoverWalletSync: ");
        logger.debug("recoverWalletConfig:" + recoverWalletConfig);
        RecoverWalletResponse recoverWalletResponse;
        try {
            if (recoverWalletConfig == null) {
                logger.debug("recoverWallet: recover wallet config null");
                return null;
            }
            if (recoverWalletConfig.getWord_count() <= 0) {
                logger.debug("recoverWallet: memonic words count error");
                return null;
            }
            String configJsonStr = new Gson().toJson(recoverWalletConfig);
            logger.debug("recoverWalletSync: config" + configJsonStr);
            String result = TyWalletJni.recoverWallet(configJsonStr, getTyWalletDriver());
            logger.debug("recoverWalletSync: " + result);
            recoverWalletResponse = new Gson().fromJson(result,
                    RecoverWalletResponse.class);
            if (recoverWalletResponse != null) {
                recoverWalletResponse.setDescription(recoverWalletResponse.getDescription());
            }
            logger.debug("recoverWalletSync: response:" + recoverWalletResponse.toString());
            if (recoverWalletResponse != null && recoverWalletResponse.getCode() == 0) {
                recoverWalletResponse.setOperationSuccess(true);
            }
        } catch (Exception e) {
            logger.error("recoverWalletSync: 异常", e);
            recoverWalletResponse = new RecoverWalletResponse();
            recoverWalletResponse.setOperationSuccess(false);
            recoverWalletResponse.setCode(ErrorCode.EXCEPTION);
            recoverWalletResponse.setDescription("exception");
        }

        return recoverWalletResponse;
    }

    public void recoverWalletAsyn(final RecoverWalletConfig recoverWalletConfig) {
        logger.debug("recoverWalletAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<RecoverWalletResponse>() {
                @Override
                public void subscribe(ObservableEmitter<RecoverWalletResponse> emitter) {

                    RecoverWalletResponse recoverWalletResponse = recoverWalletSync
                            (recoverWalletConfig);

                    emitter.onNext(recoverWalletResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<RecoverWalletResponse>() {
                        @Override
                        public void accept(RecoverWalletResponse b) throws Exception {
                            logger.debug("accept: recover result=>" + b);
                            if (walletObserver != null) {
                                walletObserver.onRecoverWallet(b, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                RecoverWalletResponse recoverWalletResponse = new
                                        RecoverWalletResponse();
                                recoverWalletResponse.setOperationSuccess(false);
                                recoverWalletResponse.setCode(ErrorCode.EXCEPTION);
                                recoverWalletResponse.setDescription("exception");
                                walletObserver.onRecoverWallet(recoverWalletResponse,
                                        ResponseCode.SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("recoverWalletAsyn: 异常", e);
            if (walletObserver != null) {
                RecoverWalletResponse recoverWalletResponse = new RecoverWalletResponse();
                recoverWalletResponse.setOperationSuccess(false);
                recoverWalletResponse.setCode(ErrorCode.EXCEPTION);
                recoverWalletResponse.setDescription("exception");
                walletObserver.onRecoverWallet(recoverWalletResponse, ResponseCode.SYSTEM_ERR);
            }
        }
    }

    @Override
    public BackupWalletResponse backupWallet() {
        logger.debug("backupWallet: ");
        BackupWalletResponse backupWalletResponse = backupWalletSync();
        logger.debug("backupWallet return:" + backupWalletResponse);
        return backupWalletResponse;
    }

    public synchronized BackupWalletResponse backupWalletSync() {
        logger.debug("backupWalletSync: ");
        BackupWalletResponse backupWalletResponse;
        try {
            String result = TyWalletJni.backupWallet(getTyWalletDriver());
            logger.debug("backupWalletSync: " + result);
            backupWalletResponse = new Gson().fromJson(result,
                    BackupWalletResponse.class);
            if (backupWalletResponse != null) {
                backupWalletResponse.setDescription(backupWalletResponse.getDescription());
            }
            logger.debug("backupWalletSync: result:" + backupWalletResponse);
            if (backupWalletResponse != null && backupWalletResponse.getCode() == 0) {
                backupWalletResponse.setOperationSuccess(true);
            }
        } catch (Exception e) {
            logger.error("backupWalletSync: 异常", e);
            backupWalletResponse = new BackupWalletResponse();
            backupWalletResponse.setOperationSuccess(false);
            backupWalletResponse.setCode(ErrorCode.EXCEPTION);
            backupWalletResponse.setDescription("exception");
        }

        return backupWalletResponse;
    }

    public void backupWalletAsyn() {
        logger.debug("backupWalletAsyn: ");
        try {
            Observable.create(new ObservableOnSubscribe<BackupWalletResponse>() {
                @Override
                public void subscribe(ObservableEmitter<BackupWalletResponse> emitter) {

                    BackupWalletResponse result = backupWalletSync();

                    emitter.onNext(result);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BackupWalletResponse>() {
                        @Override
                        public void accept(BackupWalletResponse b) throws Exception {
                            logger.debug("accept: back up result=>" + b);
                            if (walletObserver != null) {
                                walletObserver.onBackupWallet(b, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                BackupWalletResponse backupWalletResponse = new
                                        BackupWalletResponse();
                                backupWalletResponse.setOperationSuccess(false);
                                backupWalletResponse.setCode(ErrorCode.EXCEPTION);
                                backupWalletResponse.setDescription("exception");
                                walletObserver.onBackupWallet(backupWalletResponse, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("backupWalletAsyn: 异常", e);
            if (walletObserver != null) {
                BackupWalletResponse backupWalletResponse = new
                        BackupWalletResponse();
                backupWalletResponse.setOperationSuccess(false);
                backupWalletResponse.setCode(ErrorCode.EXCEPTION);
                backupWalletResponse.setDescription("exception");
                walletObserver.onBackupWallet(backupWalletResponse, ResponseCode
                        .SYSTEM_ERR);
            }
        }
    }

    @Override
    public boolean setDeviceName(String deviceName) {
        logger.debug("setDeviceName: ");
        logger.debug("deviceName:" + deviceName);
        boolean result = setDeviceNameSync(deviceName);
        logger.debug("setDeviceName return:" + result);
        return result;
    }

    public synchronized boolean setDeviceNameSync(String deviceName) {
        logger.debug("setDeviceNameSync: ");
        logger.debug("deviceName:" + deviceName);
        try {
            //TODO
            byte[] req = new byte[]{(byte) 0xf0, (byte) 0xc3, 0x00, 0x00, 0x00};
            int reqLen = req.length;
            byte[] res = new byte[512];
            int resLen = tyWalletBtDriver.transCommandAPDU(req, reqLen, res, COMMAND_TIMEOUT);
            logger.debug("setDeviceNameSync: 指令回复数据长度=>" + resLen);
            return false;
        } catch (Exception e) {
            logger.error("setDeviceNameSync: 异常", e);
        }

        return false;
    }

    public void setDeviceNameAsyn(final String deviceName) {
        logger.debug("setDeviceNameAsyn: ");
        logger.debug("deviceName:" + deviceName);
        try {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {

                    boolean result = setDeviceNameSync(deviceName);

                    emitter.onNext(result);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean b) throws Exception {
                            logger.debug("accept: set device name result=>" + b);
                            if (walletObserver != null) {
                                walletObserver.onSetDeviceName(deviceName, b, ResponseCode.SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                walletObserver.onSetDeviceName(deviceName, false, ResponseCode
                                        .SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("setDeviceNameAsyn: 异常", e);
        }
    }

    @Override
    public DeviceUpgradtionResponse upgradeDevice(String filePath, UpgradeListener
            upgradeListener) {
        logger.debug("upgradeDevice: ");
        logger.debug("filePath:" + filePath + "\tupgradeListener:" + upgradeListener);
        DeviceUpgradtionResponse deviceUpgradtionResponse = upgradeDeviceSync(filePath,
                upgradeListener);
        logger.debug("upgradeDevice return:" + deviceUpgradtionResponse);
        return deviceUpgradtionResponse;

    }


    public DeviceUpgradtionResponse upgradeDeviceSync(String filePath, UpgradeListener
            upgradeListener) {

        logger.debug("upgradeDeviceSync: ");
        logger.debug("filePath:" + filePath + "\tupgradeListener:" + upgradeListener);

        String errorDescription;
        DeviceUpgradtionResponse deviceUpgradtionResponse;
        deviceUpgradtionResponse = new DeviceUpgradtionResponse();
        deviceUpgradtionResponse.setOperationSuccess(false);
        deviceUpgradtionResponse.setDescriptionCode(ErrorCode.PARAM_ERROR + "");
        deviceUpgradtionResponse.setCode(ErrorCode.EXCEPTION);

        if (filePath == null) {
            errorDescription = "file path null";
            logger.debug(errorDescription);
            if (upgradeListener != null) {
                upgradeListener.upgradeFail(UpgradeListener.UPGRADE_FILE_NOT_FOUND);
            }
            deviceUpgradtionResponse.setDescription(errorDescription);
            return deviceUpgradtionResponse;
        }
        if (upgradeListener == null) {
            logger.debug("upgrade listener null");
        }

        File file = new File(filePath);

        if (!file.exists()) {
            errorDescription = "file not found,path:" + filePath;
            logger.debug(errorDescription);
            //升级文件不存在
            if (upgradeListener != null) {
                upgradeListener.upgradeFail(UpgradeListener.UPGRADE_FILE_NOT_FOUND);
            }
            deviceUpgradtionResponse.setDescription(errorDescription);
            return deviceUpgradtionResponse;
        }
        logger.debug("param correct.reset response");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int fileLength = fileInputStream.available();
            byte[] fileContent = new byte[fileLength];
            fileInputStream.read(fileContent, 0, fileLength);

            UpgradeUtil upgradeUtil = new UpgradeUtil(tyWalletBtDriver);
            boolean operationResult = upgradeUtil.upgradeDevice(fileContent, upgradeListener);
            deviceUpgradtionResponse = new DeviceUpgradtionResponse();
            deviceUpgradtionResponse.setOperationSuccess(operationResult);
        } catch (Exception e) {
            logger.error("upgradeDeviceSync: 异常", e);
            deviceUpgradtionResponse = new DeviceUpgradtionResponse();
            deviceUpgradtionResponse.setOperationSuccess(false);
            deviceUpgradtionResponse.setCode(ErrorCode.EXCEPTION);
            deviceUpgradtionResponse.setDescription("exception");
            deviceUpgradtionResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
        }
        return deviceUpgradtionResponse;

    }

    public void upgradeDeviceAsyn(final String filePath, final UpgradeListener
            upgradeListener) {
        logger.debug("upgradeDeviceAsyn: ");
        logger.debug("filePath:" + filePath + "\tupgradeListener:" + upgradeListener);
        try {
            Observable.create(new ObservableOnSubscribe<DeviceUpgradtionResponse>() {
                @Override
                public void subscribe(ObservableEmitter<DeviceUpgradtionResponse> emitter) {

                    DeviceUpgradtionResponse deviceUpgradtionResponse = upgradeDeviceSync
                            (filePath, upgradeListener);

                    emitter.onNext(deviceUpgradtionResponse);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<DeviceUpgradtionResponse>() {
                        @Override
                        public void accept(DeviceUpgradtionResponse b) throws Exception {
                            logger.debug("accept: firmware upgrade result=>" + b);
                            if (walletObserver != null) {
                                walletObserver.onDeviceUpgradtion(filePath, b, ResponseCode
                                        .SUCCESS);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            logger.error("accept: 异常", throwable);
                            if (walletObserver != null) {
                                DeviceUpgradtionResponse deviceUpgradtionResponse = new
                                        DeviceUpgradtionResponse();
                                deviceUpgradtionResponse.setOperationSuccess(false);
                                deviceUpgradtionResponse.setCode(ErrorCode.EXCEPTION);
                                deviceUpgradtionResponse.setDescription("exception");
                                walletObserver.onDeviceUpgradtion(filePath,
                                        deviceUpgradtionResponse,
                                        ResponseCode.SYSTEM_ERR);
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("upgradeDeviceAsyn: 异常", e);
            if (walletObserver != null) {
                DeviceUpgradtionResponse deviceUpgradtionResponse = new
                        DeviceUpgradtionResponse();
                deviceUpgradtionResponse.setOperationSuccess(false);
                deviceUpgradtionResponse.setCode(ErrorCode.EXCEPTION);
                deviceUpgradtionResponse.setDescription("exception");
                walletObserver.onDeviceUpgradtion(filePath,
                        deviceUpgradtionResponse,
                        ResponseCode.SYSTEM_ERR);
            }
        }
    }


    private String signEthTransaction(EthTransactionInfoReq ethTransactionInfo) {
        try {
            //转账金额,从 ETH 转换成 WEI
            BigDecimal weiValue = Convert.toWei(ethTransactionInfo.getValue(), Convert.Unit.ETHER);
            logger.debug("ethTransactionInfo:" + ethTransactionInfo);
            return signEthTx(ethTransactionInfo.getChainId(), ethTransactionInfo.getAddressN(),
                    ethTransactionInfo.getNonce(),
                    ethTransactionInfo
                            .getGasPrice(), ethTransactionInfo.getGasLimit(), ethTransactionInfo
                            .getToAddress(), null, weiValue
                            .toBigInteger());
        } catch (Exception e) {
            logger.error("signEthTx: ETH转账签名异常", e);
        }
        return null;
    }


    private SignEthTxResponse signEthTokenTransaction(TokenTransactionInfoReq
                                                              tokenTransactionInfo) {
        SignEthTxResponse signEthTokenTxResponse;
        String errorDescription;
        try {
            // 调用合约的transfer进行转账
            Address toAddress = new Address
                    (tokenTransactionInfo.getToAddress());
            logger.debug("to address:" + toAddress.toString() + "\nvalue:" + Numeric.toBigInt
                    (tokenTransactionInfo.getToAddress()));
            Function function = new Function("transfer", Arrays.<Type>asList(toAddress, new
                    Uint256(tokenTransactionInfo
                    .getValue().toBigInteger())),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                    }, new TypeReference<Uint256>() {
                    }));
            String encodedFunction = FunctionEncoder.encode(function);
            while (encodedFunction != null && encodedFunction.startsWith("0x")) {
                encodedFunction = encodedFunction.substring(2);
            }
            logger.debug("encodedFunction:" + encodedFunction);
            if (encodedFunction == null) {
                errorDescription = "function data null";
                logger.debug(errorDescription);
                signEthTokenTxResponse = new SignEthTxResponse();
                signEthTokenTxResponse.setCode(ErrorCode.EXCEPTION);
                signEthTokenTxResponse.setDescription(errorDescription);
                signEthTokenTxResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
                return signEthTokenTxResponse;
            }
            logger.debug("tokenTransactionInfo:" + tokenTransactionInfo.toString());
            String signedRsv = signEthTx(tokenTransactionInfo.getChainId(), tokenTransactionInfo
                            .getAddressN(),
                    tokenTransactionInfo.getNonce(),
                    tokenTransactionInfo
                            .getGasPrice(), tokenTransactionInfo.getGasLimit(), tokenTransactionInfo
                            .getTokenAddress(), encodedFunction, new BigInteger("0"));

            RawTransaction rawTransaction = RawTransaction.createTransaction
                    (tokenTransactionInfo.getNonce(), tokenTransactionInfo.getGasPrice(),
                            tokenTransactionInfo.getGasLimit(), tokenTransactionInfo
                                    .getTokenAddress(), encodedFunction);


            SignedDataRes signedData = new Gson().fromJson(signedRsv, SignedDataRes.class);

            if (signedData == null) {
                errorDescription = "signed data null";
                logger.debug(errorDescription);
                signEthTokenTxResponse = new SignEthTxResponse();
                signEthTokenTxResponse.setCode(ErrorCode.EXCEPTION);
                signEthTokenTxResponse.setDescription(errorDescription);
                signEthTokenTxResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
                return signEthTokenTxResponse;
            }

            signedData.setDescription(signedData.getDescription());

            String r = signedData.getR();
            logger.debug("signEthTokenTransaction: r:" + r);
            String s = signedData.getS();
            logger.debug("signEthTokenTransaction: s:" + s);


            if (r == null || s == null || r.trim().length() == 0 || s.trim().length() == 0) {
                logger.debug("r or s null");
                signEthTokenTxResponse = new SignEthTxResponse();
                signEthTokenTxResponse.setCode(signedData.getCode());
                signEthTokenTxResponse.setDescription(signedData.getDescription());
                signEthTokenTxResponse.setDescriptionCode(signedData.getDescriptionCode());
                return signEthTokenTxResponse;
            }

            byte[] rb = Utils.hexString2Bytes(r);
            byte[] sb = Utils.hexString2Bytes(s);
            byte v = (byte) signedData.getV();

            Sign.SignatureData signatureData = new Sign.SignatureData(v, rb, sb);
            byte[] finalDataBytes = encode(rawTransaction, signatureData);
            String hexValue = Numeric.toHexString(finalDataBytes);

            signEthTokenTxResponse = new SignEthTxResponse();
            signEthTokenTxResponse.setSignedData(hexValue);
            signEthTokenTxResponse.setCode(signedData.getCode());
            signEthTokenTxResponse.setDescription(signedData.getDescription());
            signEthTokenTxResponse.setDescriptionCode(signedData.getDescriptionCode());

        } catch (Exception e) {
            logger.error("signEthTokenTransaction: 以太坊代币转账签名异常", e);
            signEthTokenTxResponse = new SignEthTxResponse();
            signEthTokenTxResponse.setSignedData(null);
            signEthTokenTxResponse.setCode(ErrorCode.EXCEPTION);
            signEthTokenTxResponse.setDescription("exception");
            signEthTokenTxResponse.setDescriptionCode(ErrorCode.EXCEPTION + "");
        }
        return signEthTokenTxResponse;
    }

    private String signEthTx(int chainId, String addressN, BigInteger nonce, BigInteger gasPrice,
                             BigInteger gasLimit, String toAddress, String data, BigInteger value) {
        try {
            //发送指令，与设备交互，设备对交易信息进行签名
            String signedEthTxJsonData = TyWalletJni.signEthTx(chainId, addressN, nonce
                    .toByteArray(), gasPrice.toByteArray(), gasLimit.toByteArray(), toAddress ==
                    null ? null : Utils.hexString2Bytes(toAddress), data == null ? null : Utils
                    .hexString2Bytes(data), value.toByteArray(), getTyWalletDriver());
            logger.debug("signEthTx: " + signedEthTxJsonData);

            return signedEthTxJsonData;
        } catch (Exception e) {
            logger.error("signEthTx: 异常", e);
        }
        return null;
    }
}
