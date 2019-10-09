package com.whty.blockchain.tybitcoinlib.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlockchainUTXOResponse {

    @SerializedName("unspent_outputs")
    List<BlockchainUTXO> unspentOutputs;

    public List<BlockchainUTXO> getUnspentOutputs() {
        return unspentOutputs;
    }

    public void setUnspentOutputs(List<BlockchainUTXO> unspentOutputs) {
        this.unspentOutputs = unspentOutputs;
    }
}
