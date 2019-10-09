/**
 * 
 */
package com.whty.blockchain.wallet.model;

import com.whty.blockchain.wallet.entity.Env;

import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * <p>
 * Title:EthereumWeb3jService
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Date:2018年7月27日 下午2:03:02
 * </p>
 * <p>
 * 
 * @author wangwei01
 *         </p>
 */
public interface EthereumWeb3jService {

	//设置环境
	void setEnv(Env env);

	//设置环境url
	void setEnvUrl(String envUrl);

	String getAddress(String walletfile, String password);

	BigInteger getBalance(String address);

	// 以太币转账，由geth自身签名
	String ethSendTransaction(Transaction transaction);

	// 以太币转账,由秘钥文件签名
	EthSendTransaction ethSendRawTransaction(String walletfile, String password, String toAddress,
                                             BigDecimal coinNum);

	void ethTransferToken(String walletfile, String password, String tokenAddress, String
            toAddress, BigInteger amount);

	// 创建只能合约(发行代币)
	EthSendTransaction ethSendRawContractTransaction(String walletfile, String password,
                                                     BigDecimal coinNum,
                                                     String data);

	// 获取代币余额
	void ethGetTokenBalance(String address, String tokenAddress);

	BigInteger getGasPrice();

	// 通过创建合约事务的hash查询对应的合约信息
	void ethGetTransactionReceipt(String transactionHash);

	BigInteger ethGetTransactionCount(String address);

}
