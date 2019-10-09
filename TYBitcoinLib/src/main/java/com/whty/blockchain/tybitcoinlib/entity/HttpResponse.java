package com.whty.blockchain.tybitcoinlib.entity;

public class HttpResponse {

    int responseCode;

    String responseMsg;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "responseCode=" + responseCode +
                ", responseMsg='" + responseMsg + '\'' +
                '}';
    }
}
