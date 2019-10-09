package com.whty.blockchain.tyblockchainlib.api.core;

import com.whty.blockchain.tyblockchainlib.api.util.TYLog;

public class TyWalletDriver {

    //蓝牙driver
    private TyWalletBtDriver tyWalletBtDriver;

    private WalletObserver walletObserver;

    private final String TAG = this.getClass().getName();

    public TyWalletDriver(TyWalletBtDriver tyWalletBtDriver, WalletObserver walletObserver) {
        this.tyWalletBtDriver = tyWalletBtDriver;
        this.walletObserver = walletObserver;
    }

    public int transCommand(byte[] req,byte[] res,long timeout){

        TYLog.d(TAG, "transCommand invoked by jni => timeout:"+timeout);
        int resLen = tyWalletBtDriver.transCommand(req,req.length,res,timeout);
        return resLen;
    }

    public String requestWord(){
        TYLog.d(TAG,"jni request word");
        return walletObserver.requestWord();
    }



}
