package com.whty.blockchain.tyblockchainlib.api.entity;


import java.math.BigDecimal;
import java.math.BigInteger;

public class TokenTransactionInfoReq {

    //链id（比特币，以太坊，测试链等）
    Integer chainId;

    //交易计数器
    BigInteger nonce;

    //gas价格
    BigInteger gasPrice;

    //gas最大消耗量
    BigInteger gasLimit;

    //钱包地址索引
    String addressN;

    //代币合约地址
    String tokenAddress;

    //收款地址
    String toAddress;

    //转账金额
    BigDecimal value;

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getAddressN() {
        return addressN;
    }

    public void setAddressN(String addressN) {
        this.addressN = addressN;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TokenTransactionInfoReq{" +
                "chainId=" + chainId +
                ", nonce=" + nonce +
                ", gasPrice=" + gasPrice +
                ", gasLimit=" + gasLimit +
                ", addressN='" + addressN + '\'' +
                ", tokenAddress='" + tokenAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", value=" + value +
                '}';
    }
}
