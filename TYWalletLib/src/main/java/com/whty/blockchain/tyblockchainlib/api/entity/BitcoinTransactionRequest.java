package com.whty.blockchain.tyblockchainlib.api.entity;

import java.util.List;

public class BitcoinTransactionRequest {

    List<BitcoinTransactionInput> inputList;

    List<BitcoinTransactionOutput> outputList;

    //0:比特币正式环境Bitcoin 1:比特币测试环境 Testnet
    int coinType;

    public List<BitcoinTransactionInput> getInputList() {
        return inputList;
    }

    public void setInputList(List<BitcoinTransactionInput> inputList) {
        this.inputList = inputList;
    }

    public List<BitcoinTransactionOutput> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<BitcoinTransactionOutput> outputList) {
        this.outputList = outputList;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }
}
