package com.whty.blockchain.tyblockchainlib.api.entity;

public class RecoverWalletConfig {

    int word_count;

    boolean pin_protection = true;

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public boolean isPin_protection() {
        return pin_protection;
    }

    public void setPin_protection(boolean pin_protection) {
        this.pin_protection = pin_protection;
    }
}
