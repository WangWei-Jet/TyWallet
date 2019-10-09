package com.whty.blockchain.tyblockchainlib.api.pojo;

public class DeviceFeature{
    //{"vendor":"www.whty.com.cn","device_id":"D508DDAA5B76A57C37E532C7","model":"1","major":1,"minor":6,"patch":2}

    String vendor;

    String device_id;

    String model;

    int major;

    int minor;

    int patch;

    String label;

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String device_id) {
        this.device_id = device_id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "DeviceFeature{" +
                "vendor='" + vendor + '\'' +
                ", device_id='" + device_id + '\'' +
                ", model='" + model + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                ", label='" + label + '\'' +
                '}';
    }
}
