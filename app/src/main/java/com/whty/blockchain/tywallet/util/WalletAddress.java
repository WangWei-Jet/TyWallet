package com.whty.blockchain.tywallet.util;

public class WalletAddress {

    String path;

    String address;

    public WalletAddress(String path, String address) {
        this.path = path;
        this.address = address;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return getAddress()+"("+getPath()+")";
    }
}
