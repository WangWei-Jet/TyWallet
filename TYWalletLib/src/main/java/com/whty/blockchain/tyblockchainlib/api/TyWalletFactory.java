package com.whty.blockchain.tyblockchainlib.api;

import android.content.Context;

import com.whty.blockchain.tyblockchainlib.api.core.TyWallet;
import com.whty.blockchain.tyblockchainlib.api.core.WalletObserver;
import com.whty.blockchain.tyblockchainlib.api.impl.TyWalletImpl;

public class TyWalletFactory {

    private static TyWallet tyWallet;

    public static TyWallet getTyWalletInstance(Context context) {
        if (tyWallet == null) {
            tyWallet = new TyWalletImpl(context, null);
        }
        return tyWallet;
    }

    public static TyWallet getTyWalletInstance(Context context, WalletObserver walletObserver) {
        if (tyWallet == null) {
            tyWallet = new TyWalletImpl(context, walletObserver);
        }
        return tyWallet;
    }
}
