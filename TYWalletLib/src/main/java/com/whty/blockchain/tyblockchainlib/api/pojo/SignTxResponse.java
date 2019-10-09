package com.whty.blockchain.tyblockchainlib.api.pojo;

public class SignTxResponse extends BaseResponse{

    String serialized;

    public String getSerialized() {
        return serialized;
    }

    public void setSerialized(String serialized) {
        this.serialized = serialized;
    }

    @Override
    public String toString() {
        return "SignTxResponse{" +
                "serialized='" + serialized + '\'' +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", descriptionCode='" + descriptionCode + '\'' +
                '}';
    }
}
