package com.whty.blockchain.tyblockchainlib.api.pojo;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    public int code;

    @SerializedName("description")
    public String description;

    public String descriptionCode;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if(description != null && description.trim().length() > 0){
            String[] descriptions = description.split(":");
            if(descriptions.length == 1){
                this.description = descriptions[0];
            }else if(descriptions.length >= 2){
                setDescriptionCode(descriptions[0]);
                this.description = descriptions[1];
            }
        }
    }

    public String getDescriptionCode() {
        return descriptionCode;
    }

    public void setDescriptionCode(String descriptionCode) {
        this.descriptionCode = descriptionCode;
    }
}
