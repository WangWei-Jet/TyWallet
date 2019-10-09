package com.whty.blockchain.tyblockchainlib.api.entity;

import org.jetbrains.annotations.NotNull;

public class BitcoinTransactionInput {

    //设备用于获取地址以及秘钥信息
    @NotNull
    String address_n;

    //required(UTXO对应的交易记录的txid)
    @NotNull
    String prev_hash;

    //required(UTXO对应的交易记录中outputs中的索引)
    @NotNull
    String prev_index;

    //当前未使用
    String script_sig;

    String sequence;

    String script_type;

    //TODO 当前未使用
    String mutisig;

    //(单位:satoshi)
    @NotNull
    String amountSatoshi;

    //单位btc
    float amount;

    //TODO 当前未使用
    String decred_tree;

    //TODO 当前未使用
    String decred_script_version;

    @NotNull
    public String getAddress_n() {
        return address_n;
    }

    @NotNull
    public void setAddress_n(String address_n) {
        this.address_n = address_n;
    }

    @NotNull
    public String getPrev_hash() {
        return prev_hash;
    }

    @NotNull
    public void setPrev_hash(String prev_hash) {
        this.prev_hash = prev_hash;
    }

    @NotNull
    public String getPrev_index() {
        return prev_index;
    }

    @NotNull
    public void setPrev_index(String prev_index) {
        this.prev_index = prev_index;
    }

    public String getScript_sig() {
        return script_sig;
    }

    public void setScript_sig(String script_sig) {
        this.script_sig = script_sig;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getScript_type() {
        return script_type;
    }

    public void setScript_type(String script_type) {
        this.script_type = script_type;
    }

    public String getMutisig() {
        return mutisig;
    }

    public void setMutisig(String mutisig) {
        this.mutisig = mutisig;
    }

    public String getDecred_tree() {
        return decred_tree;
    }

    public void setDecred_tree(String decred_tree) {
        this.decred_tree = decred_tree;
    }

    public String getDecred_script_version() {
        return decred_script_version;
    }

    public void setDecred_script_version(String decred_script_version) {
        this.decred_script_version = decred_script_version;
    }

    @NotNull
    public String getAmountSatoshi() {
        return amountSatoshi;
    }

    @NotNull
    public void setAmountSatoshi(String amountSatoshi) {
        this.amountSatoshi = amountSatoshi;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BitcoinTransactionInput{" +
                "address_n='" + address_n + '\'' +
                ", prev_hash='" + prev_hash + '\'' +
                ", prev_index='" + prev_index + '\'' +
                ", script_sig='" + script_sig + '\'' +
                ", sequence='" + sequence + '\'' +
                ", script_type='" + script_type + '\'' +
                ", mutisig='" + mutisig + '\'' +
                ", amountSatoshi=" + amountSatoshi +
                ", amount=" + amount +
                ", decred_tree='" + decred_tree + '\'' +
                ", decred_script_version='" + decred_script_version + '\'' +
                '}';
    }
}
