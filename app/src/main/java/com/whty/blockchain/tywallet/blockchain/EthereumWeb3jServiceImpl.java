/**
 * 
 */
package com.whty.blockchain.tywallet.blockchain;

import android.util.Log;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;


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
public class EthereumWeb3jServiceImpl implements EthereumWeb3jService {


	private final String TAG = this.getClass().getName();

	@Override
	public String getAddress(String walletfile, String password) {

		try {
			Credentials credentials = WalletUtils.loadCredentials(password, walletfile);
			return credentials.getAddress();
		} catch (Exception e) {
			Log.e(TAG, "获取用户帐号异常:"+ e.getMessage(), e);
		}
		return null;
	}

	@Override
	public BigInteger getBalance(String address){
		Log.i(TAG, "getBalance invoked");
		try {
			Admin admin = AdminClient.newInstance();
			if (admin == null) {
				Log.i(TAG,"获取admin为空");
				return null;
			}
			EthGetBalance ethGetBalance = admin.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
			// 单位wei
			BigInteger balance = ethGetBalance.getBalance();

			Log.d(TAG,"account:" + address + "\tbalance:" + balance);

			return balance;
		} catch (IOException e) {
			Log.e(TAG, "异常", e);
		}

		return null;
	}

	@Override
	public String ethSendTransaction(Transaction transaction) {
		Log.i(TAG,"ethSendTransaction invoked");
		try {
			Admin admin = AdminClient.newInstance();
			if (admin == null) {
				Log.i(TAG,"获取admin为空");
				return null;
			}
			// 解锁账户
			admin.personalUnlockAccount(transaction.getFrom(), "123456").send();

			// 发送交易
			EthSendTransaction ethSendTransaction = admin.ethSendTransaction(transaction).send();

			Log.d(TAG,"json rpc:" + ethSendTransaction.getJsonrpc() + "\tid:" + ethSendTransaction.getId()
					+ "\traw response:" + ethSendTransaction.getRawResponse() + "\tresult:"
					+ ethSendTransaction.getResult() + "\ttransaction hash:" + ethSendTransaction.getTransactionHash());
		} catch (Exception e) {
			Log.e(TAG,"异常", e);
		}

		return null;
	}

	@Override
	public EthSendTransaction ethSendRawTransaction(String walletfile, String password, String toAddress,
			BigDecimal coinNum) {

		String fromAddress = "";

		try {
			// 从钱包文件中读取账户地址和秘钥信息
			Credentials credentials = WalletUtils.loadCredentials(password, walletfile);
			fromAddress = credentials.getAddress();

			Admin admin = AdminClient.newInstance();
			EthGetTransactionCount ethGetTransactionCount = admin
					.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync()
					.get();

			BigInteger nonce = ethGetTransactionCount.getTransactionCount();

			BigDecimal weiValue = Convert.toWei(coinNum, Convert.Unit.ETHER);

			BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);

			BigInteger gasLimit = BigInteger.valueOf(90000);

			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddress,
					weiValue.toBigInteger());

			// 使用秘钥信息对交易进行签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = admin.ethSendRawTransaction(hexValue).send();
			Log.d(TAG,"json rpc:" + ethSendTransaction.getJsonrpc() + "\tid:" + ethSendTransaction.getId()
					+ "\traw response:" + ethSendTransaction.getRawResponse() + "\tresult:"
					+ ethSendTransaction.getResult() + "\ttransaction hash:" + ethSendTransaction.getTransactionHash());
			return ethSendTransaction;
		} catch (Exception e) {
			Log.e(TAG,fromAddress+" 转账给"+toAddress+" "+coinNum+"个ETH异常:"+e.toString(), e);
		}
		return null;
	}

	@Override
	public void ethTransferToken(String walletfile, String password, String tokenAddress, String toAddress,
			BigInteger amount) {
		String fromAddress = "";
		try {
			// 调用合约的transfer进行转账
			Function function = new Function("transfer", Arrays.<Type>asList(new Address(toAddress), new Uint256(amount)),
					Arrays.<TypeReference<?>>asList(new TypeReference<Address>(){} , new TypeReference<Uint256>(){}));
			String encodedFunction = FunctionEncoder.encode(function);
			Log.d(TAG,"encodeFunction:" + encodedFunction);

			// 智能合约事物
			// RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,
			// Constants.GAS_PRICE, Constants.GAS_LIMIT,"代币地址",encodedFunction);

			// 从钱包文件中读取账户地址和秘钥信息
			Credentials credentials = WalletUtils.loadCredentials(password, walletfile);
			fromAddress = credentials.getAddress();
			Admin admin = AdminClient.newInstance();

			EthGetTransactionCount ethGetTransactionCount = admin
					.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync()
					.get();

			BigInteger nonce = ethGetTransactionCount.getTransactionCount();

			BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);

			BigInteger gasLimit = BigInteger.valueOf(5000000);

			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, tokenAddress,
					encodedFunction);

			// 使用秘钥信息对交易进行签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = admin.ethSendRawTransaction(hexValue).send();
			Log.d(TAG,"json rpc:" + ethSendTransaction.getJsonrpc() + "\tid:" + ethSendTransaction.getId()
					+ "\traw response:" + ethSendTransaction.getRawResponse() + "\tresult:"
					+ ethSendTransaction.getResult() + "\ttransaction hash:" + ethSendTransaction.getTransactionHash());

		} catch (Exception e) {
			Log.e(TAG,fromAddress+" 转账给"+toAddress+" "+amount+"个代币异常:"+e.toString(), e);
		}
	}

	@Override
	public EthSendTransaction ethSendRawContractTransaction(String walletfile, String password, BigDecimal coinNum,
			String data) {

		String fromAddress = "";

		try {
			// 从钱包文件中读取账户地址和秘钥信息
			Credentials credentials = WalletUtils.loadCredentials(password, walletfile);
			fromAddress = credentials.getAddress();

			Admin admin = AdminClient.newInstance();
			EthGetTransactionCount ethGetTransactionCount = admin
					.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync()
					.get();

			BigInteger nonce = ethGetTransactionCount.getTransactionCount();

			BigDecimal weiValue = Convert.toWei(coinNum, Convert.Unit.ETHER);

			BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);

			BigInteger gasLimit = BigInteger.valueOf(3000000);

			RawTransaction rawTransaction = RawTransaction.createContractTransaction(nonce, gasPrice, gasLimit,
					weiValue.toBigInteger(), data);

			// 使用秘钥信息对交易进行签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = admin.ethSendRawTransaction(hexValue).send();
			Log.d(TAG,"json rpc:" + ethSendTransaction.getJsonrpc() + "\tid:" + ethSendTransaction.getId()
					+ "\traw response:" + ethSendTransaction.getRawResponse() + "\tresult:"
					+ ethSendTransaction.getResult() + "\ttransaction hash:" + ethSendTransaction.getTransactionHash());
			return ethSendTransaction;
		} catch (Exception e) {
			Log.e(TAG,fromAddress+" 创建合约异常:"+e.toString(), e);
		}
		return null;
	}

	@Override
	public void ethGetTokenBalance(String address, String tokenAddress) {
		try {
			Admin admin = AdminClient.newInstance();
			// 调用合约的查询余额方法
			//此处要特别注意Arrays.asList中间的那个类型，不能省略，省略报错
			Function function = new Function("balanceOf", Arrays.<Type>asList(new Address(address)),
					Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
					}));
			String encodedFunction = FunctionEncoder.encode(function);
			Log.d(TAG,"encodeFunction:" + encodedFunction);
			// "0x70a08231000000000000000000000000819F07d5e4b7a0A26fF992173FB0B7Dd016A5273"
			// 第三个参数是data
			EthCall ethCall = admin
					.ethCall(Transaction.createEthCallTransaction(address, tokenAddress, encodedFunction),
							DefaultBlockParameterName.LATEST)
					.send();
			BigInteger tokenBalance = Numeric.decodeQuantity(ethCall.getResult());
			Log.d(TAG,"token balance:" + tokenBalance);
			Log.d(TAG,"id:" + ethCall.getId() + "\tjson rpc:" + ethCall.getJsonrpc() + "\traw response:"
					+ ethCall.getRawResponse() + "\tresult:" + ethCall.getResult() + "\tresult:" + ethCall.getValue());
		} catch (Exception e) {
			Log.e(TAG,"异常", e);
		}
	}

	@Override
	public void ethGetTransactionReceipt(String transactionHash) {
		try {
			Admin admin = AdminClient.newInstance();
			EthGetTransactionReceipt ethGetTransactionReceipt = admin.ethGetTransactionReceipt(transactionHash).send();
			Log.d(TAG,"id:" + ethGetTransactionReceipt.getId() + "\tjsonrpc:" + ethGetTransactionReceipt.getJsonrpc()
					+ "\traw response:" + ethGetTransactionReceipt.getRawResponse() + "\tcontract address:"
					+ ethGetTransactionReceipt.getResult().getContractAddress());
		} catch (Exception e) {
			Log.e(TAG,"异常", e);
		}

	}

	@Override
	public BigInteger getGasPrice() {
		Log.i(TAG,"getGasPrice invoked");
		try {
			Admin admin = AdminClient.newInstance();
			if (admin == null) {
				Log.i(TAG,"获取admin为空");
				return null;
			}
			EthGasPrice ethGasPrice = admin.ethGasPrice().send();
			BigInteger gasPrice = ethGasPrice.getGasPrice();
			Log.d(TAG,"gas price:" + gasPrice);
			return gasPrice;
		} catch (IOException e) {
			Log.e(TAG,"异常", e);
		}

		return null;
	}

	@Override
	public BigInteger ethGetTransactionCount(String address) {
		Log.i(TAG,"ethGetTransactionCount invoked");
		try {
			Admin admin = AdminClient.newInstance();
			if (admin == null) {
				Log.i(TAG,"获取admin为空");
				return null;
			}

			EthGetTransactionCount ethGetTransactionCount = admin
					.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();

			Log.d(TAG, "json rpc:" + ethGetTransactionCount.getJsonrpc() + "\tid:" + ethGetTransactionCount.getId()
					+ "\traw response:" + ethGetTransactionCount.getRawResponse() + "\tresult:"
					+ ethGetTransactionCount.getResult() + "\ttransaction count:"
					+ ethGetTransactionCount.getTransactionCount());

			return ethGetTransactionCount.getTransactionCount();

		} catch (IOException e) {
			Log.e(TAG,"异常", e);
		}

		return null;
	}

}
