package com.whty.blockchain.tybitcoinlib.entity;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class BlockchainUTXO {

    @SerializedName("address_n")
    String addressN;

    String address;

    @SerializedName("tx_hash")
    String txHash;

    //txid
    @SerializedName("tx_hash_big_endian")
    String txHashBigEndian;

    @SerializedName("tx_output_n")
    String txOutputN;
    String script;
    BigInteger value;

    float amountBtc;

    long confirmations;

    public String getAddressN() {
        return addressN;
    }

    public void setAddressN(String addressN) {
        this.addressN = addressN;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getTxHashBigEndian() {
        return txHashBigEndian;
    }

    public void setTxHashBigEndian(String txHashBigEndian) {
        this.txHashBigEndian = txHashBigEndian;
    }

    public String getTxOutputN() {
        return txOutputN;
    }

    public void setTxOutputN(String txOutputN) {
        this.txOutputN = txOutputN;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public float getAmountBtc() {
        return amountBtc;
    }

    public void setAmountBtc(float amountBtc) {
        this.amountBtc = amountBtc;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    @Override
    public String toString() {
        return "BlockchainUTXO{" +
                "addressN='" + addressN + '\'' +
                ", address='" + address + '\'' +
                ", txHash='" + txHash + '\'' +
                ", txHashBigEndian='" + txHashBigEndian + '\'' +
                ", txOutputN='" + txOutputN + '\'' +
                ", script='" + script + '\'' +
                ", value=" + value +
                ", amountBtc=" + amountBtc +
                ", confirmations=" + confirmations +
                '}';
    }
}
