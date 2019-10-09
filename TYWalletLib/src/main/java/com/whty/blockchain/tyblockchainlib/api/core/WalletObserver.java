package com.whty.blockchain.tyblockchainlib.api.core;

import android.bluetooth.BluetoothDevice;

import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionRequest;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.entity.EthTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.entity.ResponseCode;
import com.whty.blockchain.tyblockchainlib.api.entity.TokenTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.pojo.BackupWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.CreateWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.DeviceUpgradtionResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetDeviceFeatureResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.RecoverWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignEthTxResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignTxResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.WipeDeviceResponse;

public interface WalletObserver {

    /**
     * 连接钱包设备结果回调
     *
     * @param device       蓝牙设备 BluetoothDevice
     * @param result       连接结果
     * @param responseCode 响应码
     */
    void onWalletConnect(BluetoothDevice device, boolean result, ResponseCode responseCode);

    /**
     * 断连钱包连接结果回调
     *
     * @param result       断开连接结果
     * @param responseCode 响应码
     */
    void onWalletDisconnect(boolean result, ResponseCode responseCode);

    /**
     * 获取账户地址结果回调
     *
     * @param addressN           账户地址索引
     * @param coinType           代币类型
     * @param getAddressResponse 获取账户地址响应类
     * @param responseCode       响应码
     */
    void onGetAddress(String addressN, CoinType coinType, GetAddressResponse getAddressResponse,
                      ResponseCode responseCode);

    void onExportPublicKey(String path, String publicKey, ResponseCode responseCode);

    void onSignTx(BitcoinTransactionRequest bitcoinTransactionRequest, SignTxResponse
            signTxResponse, ResponseCode responseCode);

    /**
     * 签名以太坊交易回调
     *
     * @param ethTransactionInfoReq 以太坊交易请求信息类
     * @param signEthTxResponse     签名以太坊交易响应类
     * @param responseCode          响应码
     */
    void onSignEthTx(EthTransactionInfoReq ethTransactionInfoReq, SignEthTxResponse
            signEthTxResponse, ResponseCode responseCode);


    /**
     * 签名以太坊代币交易回调
     *
     * @param tokenTransactionInfoReq 以太坊代币交易请求信息类
     * @param signEthTxResponse       签名以太坊代币交易响应类
     * @param responseCode            响应码
     */
    void onSignEthTokenTx(TokenTransactionInfoReq tokenTransactionInfoReq, SignEthTxResponse
            signEthTxResponse, ResponseCode responseCode);

    /**
     * 获取设备信息回调
     *
     * @param getDeviceFeatureResponse 获取设备信息响应类
     * @param responseCode             响应码
     */
    void onGetDeviceFeature(GetDeviceFeatureResponse getDeviceFeatureResponse,
                            ResponseCode responseCode);

    void onInputWords(ResponseCode responseCode);

    void onInputPin(int type, int resCode, ResponseCode responseCode);

    void onInitialize(boolean result, ResponseCode responseCode);

    /**
     * 创建钱包结果回调
     *
     * @param createWalletResponse 创建钱包响应类
     * @param responseCode         响应码
     */
    void onCreateWallet(CreateWalletResponse createWalletResponse, ResponseCode responseCode);

    /**
     * 擦除设备结果回调
     *
     * @param wipeDeviceResponse 擦除设备响应类
     * @param responseCode       响应码
     */
    void onWipeDevice(WipeDeviceResponse wipeDeviceResponse, ResponseCode responseCode);

    /**
     * 恢复钱包结果回调
     *
     * @param recoverWalletResponse 恢复钱包响应类
     * @param responseCode          响应码
     */
    void onRecoverWallet(RecoverWalletResponse recoverWalletResponse, ResponseCode responseCode);

    void onReset(boolean result, ResponseCode responseCode);

    /**
     * 备份钱包结果回调
     *
     * @param backupWalletResponse 备份钱包响应类
     * @param responseCode         响应码
     */
    void onBackupWallet(BackupWalletResponse backupWalletResponse, ResponseCode responseCode);

    void onSetDeviceName(String deviceName, boolean result, ResponseCode responseCode);

    /**
     * 设备升级结果回调
     *
     * @param filePath                 升级文件路径
     * @param deviceUpgradtionResponse 设备升级响应类
     * @param responseCode             响应码
     */
    void onDeviceUpgradtion(String filePath, DeviceUpgradtionResponse deviceUpgradtionResponse,
                            ResponseCode responseCode);

    String requestWord();
}
