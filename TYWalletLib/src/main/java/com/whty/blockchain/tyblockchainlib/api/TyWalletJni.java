package com.whty.blockchain.tyblockchainlib.api;


import com.whty.blockchain.tyblockchainlib.api.core.TyWalletDriver;

public class TyWalletJni {

    static {
        System.loadLibrary("wallet-jni");
    }

    public static native String createWallet(String configJsonStr, TyWalletDriver tyWalletDriver);

    public static native String wipeWallet(TyWalletDriver tyWalletDriver);

    public static native String backupWallet(TyWalletDriver tyWalletDriver);

    public static native String recoverWallet(String configJsonStr, TyWalletDriver tyWalletDriver);

    public static native String getFeatures(TyWalletDriver tyWalletDriver);

    public static native String getPubKey(String requestJsonStr,TyWalletDriver tyWalletDriver);

    public static native String signTx(String requestJsonStr,TyWalletDriver tyWalletDriver);

    public static native String signEthTx(int chainId, String addressN, byte[] nonce, byte[]
            gasPrice, byte[] gasLimit, byte[] toAddress, byte[] data, byte[] value, TyWalletDriver
            tyWalletDriver);

    public static native String getAddress(String addressN, int coinType, TyWalletDriver
            tyWalletDriver);

//    public static native int transCommand(byte[] req,byte[] res,long timeout);

}
