package com.whty.blockchain.tybitcoinlib.util;

import org.bitcoinj.core.Sha256Hash;

import static org.bitcoinj.core.Utils.HEX;

public class Crypto {
    private static Crypto instance = null;

    private Crypto() {}

    public static Crypto getInstance() {
        if (instance == null) {
            instance = new Crypto();
        }
        return instance;
    }

    public String encrypt(String plainText, String password, byte[] iv) {
        try {
            AESCrypt crypt = new AESCrypt(password, iv);
            return crypt.encrypt(plainText);
        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String cipherText, String password, byte[] iv) {
        try {
            AESCrypt crypt = new AESCrypt(password, iv);
            return crypt.decrypt(cipherText);
        } catch (Exception e) {
            return null;
        }
    }

    public String SHA256HashFor(String input) {
        return HEX.encode(Sha256Hash.hash(input.getBytes()));
    }

    public String doubleSHA256HashFor(String input) {
        return HEX.encode(Sha256Hash.hashTwice(input.getBytes()));
    }
}
