package com.whty.blockchain.tybitcoinlib.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Coin {

    private org.bitcoinj.core.Coin coin;

    public enum BitcoinDenomination {
        BTC, mBTC, bits;
        private static CharSequence[] btcUnits = {"BTC", "mBTC", "bits"};

        public static CharSequence[] getBTCUnits() {
            return btcUnits;
        }

        public static String getBTCUnitString(BitcoinDenomination bitcoinDenomination) {
            if (bitcoinDenomination == BTC) {
                return (String) btcUnits[0];
            }
            if (bitcoinDenomination == mBTC) {
                return (String) btcUnits[1];
            }
            return (String) btcUnits[2];
        }

        public static BitcoinDenomination getBitcoinDenomination(int idx) {
            if (idx == 0) {
                return BTC;
            }
            if (idx == 1) {
                return mBTC;
            }
            return bits;
        }

        public static int getBTCUnitIdx(BitcoinDenomination bitcoinDenomination) {
            if (bitcoinDenomination == BTC) {
                return 0;
            }
            if (bitcoinDenomination == mBTC) {
                return 1;
            }
            return 2;
        }

        public static BitcoinDenomination toMyEnum(String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                return BTC;
            }
        }
    }

    public Coin(long satoshis) {
        this.coin = org.bitcoinj.core.Coin.valueOf(satoshis);
    }

    private Coin(org.bitcoinj.core.Coin coin) {
        this.coin = coin;
    }

    public static Coin fromString(String bitcoinAmount, BitcoinDenomination bitcoinDenomination) {
        if (bitcoinDenomination == Coin.BitcoinDenomination.BTC) {
            return new Coin(new BigDecimal(bitcoinAmount).multiply(BigDecimal.valueOf(100000000)).longValue());
        } else if (bitcoinDenomination == Coin.BitcoinDenomination.mBTC) {
            return new Coin(new BigDecimal(bitcoinAmount).multiply(BigDecimal.valueOf(100000)).longValue());
        } else {
            return new Coin(new BigDecimal(bitcoinAmount).multiply(BigDecimal.valueOf(100)).longValue());
        }
    }

    public static Coin zero() {
        return new Coin(0);
    }

    public static Coin one() {
        return new Coin(1);
    }

    public static Coin negativeOne() {
        return new Coin(-1);
    }

    public org.bitcoinj.core.Coin getBTCNumber() {
        return this.coin;
    }

    public Coin add(Coin coin) {
        return new Coin(this.coin.add(coin.coin));
    }

    public Coin subtract(Coin coin) {
        return new Coin(this.coin.subtract(coin.coin));
    }

    public Coin multiply(Coin coin) {
        return new Coin(this.coin.multiply(coin.coin.value));
    }

    public Coin divide(Coin coin) {
        return new Coin(this.coin.divide(coin.coin));
    }

    public long toNumber() {
        return this.coin.value;
    }

    public String bigIntegerToBitcoinAmountString(BitcoinDenomination bitcoinDenomination) {

        if (bitcoinDenomination == Coin.BitcoinDenomination.BTC) {
            return this.coin.toPlainString();
            //return new DecimalFormat("#.########").format(this.bigIntegerToBitcoin());
        } else if (bitcoinDenomination == Coin.BitcoinDenomination.mBTC) {
            return new DecimalFormat("#.#####").format(this.bigIntegerToMilliBit());
            //return String.valueOf(this.bigIntegerToMilliBit().toString());
        } else {
            return new DecimalFormat("#.00").format(this.bigIntegerToBits());
            // have 0 in most single digit place means always get a 0.XX
            //return new DecimalFormat("0.00").format(this.bigIntegerToBits());
            //return String.valueOf(this.bigIntegerToBits().toString());
        }
    }

    public String toPlainString() {
        return this.coin.toPlainString();
    }

    public Double bigIntegerToBits() {
        return this.coin.value*0.01;
    }

    public Double bigIntegerToMilliBit() {
        return this.coin.value*0.00001;
    }

    public Double bigIntegerToBitcoin() {
        return this.coin.value*0.00000001;
    }

    public Boolean less(Coin coin) {
        return this.coin.isLessThan(coin.coin);
    }

    public Boolean lessOrEqual(Coin coin) {
        return this.coin.isLessThan(coin.coin) || this.coin.compareTo(coin.coin) == 0;
    }

    public Boolean greater(Coin coin) {
        return this.coin.isGreaterThan(coin.coin);
    }

    public Boolean greaterOrEqual(Coin coin) {
        return this.coin.isGreaterThan(coin.coin) || this.coin.compareTo(coin.coin) == 0;
    }

    public Boolean equalTo(Coin coin) {
        return this.coin.compareTo(coin.coin) == 0;
    }
}

