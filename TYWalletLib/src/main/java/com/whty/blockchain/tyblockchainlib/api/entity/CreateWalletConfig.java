package com.whty.blockchain.tyblockchainlib.api.entity;

public class CreateWalletConfig {

    String name;

    boolean pin_protection = true;

    boolean skip_backup = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPin_protection() {
        return pin_protection;
    }

    public void setPin_protection(boolean pin_protection) {
        this.pin_protection = pin_protection;
    }

    public boolean isSkip_backup() {
        return skip_backup;
    }

    public void setSkip_backup(boolean skip_backup) {
        this.skip_backup = skip_backup;
    }
}
