package com.whty.blockchain.tyblockchainlib.api.pojo;

public class GetDeviceFeatureResponse extends BaseResponse{

    DeviceFeature deviceFeature;

    String devicePN;

    String deviceSN;

    String deviceVer;

    int deviceBattery;

    @Override
    public String toString() {
        return "GetDeviceFeatureResponse{" +
                "deviceFeature=" + deviceFeature +
                ", devicePN='" + devicePN + '\'' +
                ", deviceSN='" + deviceSN + '\'' +
                ", deviceVer='" + deviceVer + '\'' +
                ", deviceBattery=" + deviceBattery +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", descriptionCode='" + descriptionCode + '\'' +
                '}';
    }

    public String getDevicePN() {
        return devicePN;
    }

    public void setDevicePN(String devicePN) {
        this.devicePN = devicePN;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceVer() {
        return deviceVer;
    }

    public void setDeviceVer(String deviceVer) {
        this.deviceVer = deviceVer;
    }

    public int getDeviceBattery() {
        return deviceBattery;
    }

    public void setDeviceBattery(int deviceBattery) {
        this.deviceBattery = deviceBattery;
    }


    public DeviceFeature getDeviceFeature() {
        return deviceFeature;
    }

    public void setDeviceFeature(DeviceFeature deviceFeature) {
        this.deviceFeature = deviceFeature;
    }

}
