package com.whty.blockchain.wallet.view;

import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;

import java.math.BigInteger;

public interface IBaseView {

    void showLoadingDialog();

    void showLoadingDialogWithContentMsg(String contentMsg);

    void dismissLoadingDialog();

    void onError(String errorMsg);


    void showEthereumEnvironmentBalanceResult(BigInteger balance);

    void showBitcoinEnvironmentBalanceResult(BlockchainUTXOResponse blockchainUTXOResponse);

}
