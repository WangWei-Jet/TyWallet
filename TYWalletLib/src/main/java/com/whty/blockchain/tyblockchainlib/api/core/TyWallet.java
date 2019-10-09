package com.whty.blockchain.tyblockchainlib.api.core;

import android.bluetooth.BluetoothDevice;

import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionRequest;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.entity.CreateWalletConfig;
import com.whty.blockchain.tyblockchainlib.api.entity.EthTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.entity.RecoverWalletConfig;
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
import com.whty.blockchain.tyblockchainlib.api.util.UpgradeListener;

public interface TyWallet {

    /**
     * 连接钱包设备
     * @param device    蓝牙钱包设备
     * @return  连接蓝牙钱包设备结果
     */
    boolean connectWallet(BluetoothDevice device);

    /**
     * 钱包是否已连接
     * @return  钱包是否已连接
     */
    boolean isWalletConnected();

    /**
     * 断开蓝牙钱包设备连接
     * @return  断开连接操作结果
     */
    boolean disconnectWallet();

    /**
     * 获取账户地址
     * @param path  账户地址索引
     * @param coinType  代币类型（目前支持以太币/比特币/测试币）
     * @return  获取账户地址响应类(包含账户地址/操作结果/响应码/结果描述)
     */
    GetAddressResponse getAddress(String path, CoinType coinType);

    String exportPublicKey(String path);

    /**
     * 签名比特币交易
     * @param bitcoinTransactionRequest 比特币交易请求封装类(包含输入列表/输出列表/正式链还是测试链)
     * @return  签名比特币交易信息响应类(包含签名后的数据/操作结果/响应码/结果描述)
     */
    SignTxResponse signTx(BitcoinTransactionRequest bitcoinTransactionRequest);

    /**
     * 签名以太坊交易
     * @param ethTransactionInfoReq 以太坊交易信息类
     * @return  签名以太坊交易信息响应类(包含签名后的数据/操作结果/响应码/结果描述)
     */
    SignEthTxResponse signEthTx(EthTransactionInfoReq ethTransactionInfoReq);

    /**
     * 签名以太坊代币交易
     * @param tokenTransactionInfoReq 以太坊代币交易信息类
     * @return  签名以太坊交易信息响应类(包含签名后的数据/操作结果/响应码/结果描述)
     */
    SignEthTxResponse signEthTokenTx(TokenTransactionInfoReq tokenTransactionInfoReq);

    /**
     * 获取设备信息
     * @return  设备信息响应类(包含设备信息类/操作结果/响应码/结果描述)
     */
    GetDeviceFeatureResponse getDeviceFeature();

    //管理接口
    //初始化设备
    boolean initialize();

    /**
     * 创建钱包(创建的时候可指定钱包的一些属性，例如:钱包名称)
     * @param createWalletConfig  创建钱包参数类（支持钱包名称设置,是否包含pin保护,是否跳过钱包备份）
     * @return  创建钱包响应类(包含操作结果/响应码/结果描述)
     */
    CreateWalletResponse createWallet(CreateWalletConfig createWalletConfig);

    /**
     * 擦除设备,擦除设备上所有的钱包信息
     * @return  返回擦除设备响应类(包含操作结果/响应码/结果描述)
     */
    WipeDeviceResponse wipeDevice();

    /**
     * 恢复钱包，通过助记词恢复钱包
     * @param recoverWalletConfig    恢复钱包参数类(助记词个数，是否包含pin保护)
     * @return  恢复钱包响应类(包含操作结果/响应码/结果描述)
     */
    RecoverWalletResponse recoverWallet(RecoverWalletConfig recoverWalletConfig);

    /**
     * 备份钱包
     * @return  备份钱包响应类(包含操作结果/响应码/结果描述)
     */
    BackupWalletResponse backupWallet();

    boolean setDeviceName(String deviceName);

    /**
     * 设备升级
     * @param filePath  升级文件路径
     * @param upgradeListener   升级流程监听器(可监听升级进度,升级结果)
     * @return  设备升级响应类(包含操作结果/响应码/结果描述)
     */
    DeviceUpgradtionResponse upgradeDevice(String filePath, UpgradeListener upgradeListener);

}
