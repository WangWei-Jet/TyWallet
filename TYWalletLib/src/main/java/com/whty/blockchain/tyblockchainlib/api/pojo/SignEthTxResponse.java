package com.whty.blockchain.tyblockchainlib.api.pojo;

public class SignEthTxResponse extends BaseResponse{

    String signedData;

    public String getSignedData() {
        return signedData;
    }

    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }

    @Override
    public String toString() {
        return "SignEthTxResponse{" +
                "signedData='" + signedData + '\'' +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", descriptionCode='" + descriptionCode + '\'' +
                '}';
    }
}
