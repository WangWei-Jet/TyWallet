package com.whty.blockchain.tyblockchainlib.api.pojo;

public class CreateWalletResponse extends BaseResponse{


    boolean operationSuccess;

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    public void setOperationSuccess(boolean operationSuccess) {
        this.operationSuccess = operationSuccess;
    }

    @Override
    public String toString() {
        return "CreateWalletResponse{" +
                "operationSuccess=" + operationSuccess +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", descriptionCode='" + descriptionCode + '\'' +
                '}';
    }
}
