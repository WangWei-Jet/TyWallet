package com.whty.blockchain.tyblockchainlib.api.entity;

import com.whty.blockchain.tyblockchainlib.api.pojo.BaseResponse;

public class SignedDataRes extends BaseResponse{

    String r;

    String s;

    int v;

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "SignedDataRes{" +
                "r='" + r + '\'' +
                ", s='" + s + '\'' +
                ", v=" + v +
                ", code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
