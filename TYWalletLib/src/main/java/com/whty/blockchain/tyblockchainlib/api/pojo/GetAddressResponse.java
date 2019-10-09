package com.whty.blockchain.tyblockchainlib.api.pojo;

public class GetAddressResponse extends BaseResponse{

    String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "GetAddressResponse{" +
                "address='" + address + '\'' +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", descriptionCode='" + descriptionCode + '\'' +
                '}';
    }
}
