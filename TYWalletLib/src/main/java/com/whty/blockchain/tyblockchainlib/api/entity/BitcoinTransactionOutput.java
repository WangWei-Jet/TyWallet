package com.whty.blockchain.tyblockchainlib.api.entity;

import org.jetbrains.annotations.NotNull;

public class BitcoinTransactionOutput {

    //address与address_n 2选1,目前均使用address
    @NotNull
    String address;

    String address_n;

    //required(单位:satoshi)
    @NotNull
    String amountSatoshi;

    //单位btc
    double amount;

    //required
    String script_type;

    String multisig;

    String op_return_data;

    String decred_script_version;

    @NotNull
    public String getAddress() {
        return address;
    }

    @NotNull
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_n() {
        return address_n;
    }

    public void setAddress_n(String address_n) {
        this.address_n = address_n;
    }

    @NotNull
    public String getAmountSatoshi() {
        return amountSatoshi;
    }

    @NotNull
    public void setAmountSatoshi(String amountSatoshi) {
        this.amountSatoshi = amountSatoshi;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getScript_type() {
        return script_type;
    }

    public void setScript_type(String script_type) {
        this.script_type = script_type;
    }

    public String getMultisig() {
        return multisig;
    }

    public void setMultisig(String multisig) {
        this.multisig = multisig;
    }

    public String getOp_return_data() {
        return op_return_data;
    }

    public void setOp_return_data(String op_return_data) {
        this.op_return_data = op_return_data;
    }

    public String getDecred_script_version() {
        return decred_script_version;
    }

    public void setDecred_script_version(String decred_script_version) {
        this.decred_script_version = decred_script_version;
    }

    @Override
    public String toString() {
        return "BitcoinTransactionOutput{" +
                "address='" + address + '\'' +
                ", address_n='" + address_n + '\'' +
                ", amountSatoshi=" + amountSatoshi +
                ", amount=" + amount +
                ", script_type='" + script_type + '\'' +
                ", multisig='" + multisig + '\'' +
                ", op_return_data='" + op_return_data + '\'' +
                ", decred_script_version='" + decred_script_version + '\'' +
                '}';
    }
}
