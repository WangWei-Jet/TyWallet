package com.whty.blockchain.tywallet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.Gson;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.entity.EthTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.entity.TokenTransactionInfoReq;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignEthTxResponse;
import com.whty.blockchain.tywallet.blockchain.AdminClient;
import com.whty.blockchain.tywallet.util.ENV;
import com.whty.blockchain.tywallet.util.WalletAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferActivity extends AppCompatActivity {

    @BindView(R.id.transfer_edit)
    EditText transferEdit;
    @BindView(R.id.transfer_button)
    Button transferButton;
    @BindView(R.id.bt_from_address)
    Button btFromAddress;
    @BindView(R.id.tv_from_address)
    TextView tvFromAddress;
    @BindView(R.id.et_to_address)
    EditText etToAddress;
    @BindView(R.id.bt_to_address)
    Button btToAddress;
    @BindView(R.id.toAddress)
    TextView toAddress;
    @BindView(R.id.value_transfer)
    TextView valueTransfer;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.et_address_n)
    EditText etAddressN;
    @BindView(R.id.bt_get_address)
    Button btGetAddress;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.bt_set_from_address)
    Button btSetFromAddress;
    @BindView(R.id.bt_set_to_address)
    Button btSetToAddress;
    @BindView(R.id.ll_address_result)
    LinearLayout llAddressResult;
    @BindView(R.id.bt_token)
    Button btToken;

    private Context context;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> toArrayAdapter;

//    private final String TAG = this.getClass().getName();

    static final int ADDRESS_DIALOG_FINISHED = 0;


    WalletAddress fromWalletAddress;

    String[] tokens = new String[]{"ETH", "ABC"};

    private WalletAddress tempWalletAddress = null;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);

        context = this;

        ENV env = AdminClient.getCurrentEnv();
        switch (env) {
            case TESTNET:
                etAddressN.setText("m/44'/1'/0'/0/0");
                tokens = new String[]{"ETH", "ABC"};
                break;
            case ETHEREUM:
                etAddressN.setText("m/44'/60'/0'/0/0");
                tokens = new String[]{"ETH", "NMB"};
                break;
        }

        final String[] fromAddressArray = getResources().getStringArray(R.array.fromAddressArray);
        final String[] toAddressArray = getResources().getStringArray(R.array.toAddressArray);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                fromAddressArray);
        toArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                toAddressArray);


    }

    @OnClick(R.id.bt_to_address)
    void showToWalletAddress() {
        logger.debug("showToWalletAddress: on click");
        //弹框选择
        createAddressDialog(R.id.bt_to_address);
    }


    @OnClick(R.id.bt_get_address)
    void getAddress() {
        logger.debug("getAddress: on click");
        if (!TyWalletFactory.getTyWalletInstance
                (context).isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressN = etAddressN.getText().toString();

        if (addressN == null || addressN.trim().length() == 0) {
            tvResult.setText("获取地址失败,地址索引为空");
            return;
        }
        String[] eles = addressN.split("/");
        if (eles.length != 6) {
            tvResult.setText("获取地址失败,地址格式错误");
            return;
        }
        if (AdminClient.getCurrentEnv() == ENV.TESTNET) {
            //当前环境是测试链
            if (!addressN.startsWith("m/44'/1'/")) {
                tvResult.setText("获取地址失败,地址索引需要以m/44'/1'/开头");
                return;
            }
        } else if (AdminClient.getCurrentEnv() == ENV.ETHEREUM) {
            //以太坊主链
            if (!addressN.startsWith("m/44'/60'/")) {
                tvResult.setText("获取地址失败,地址索引需要以m/44'/60'/开头");
                return;
            }
        }

        logger.debug("getAddress: addressN:" + addressN);
        GetAddressResponse getAddressResponse = TyWalletFactory.getTyWalletInstance
                (context).getAddress(addressN, CoinType.ETH);

        if (getAddressResponse == null) {
            tvResult.setText("获取地址失败");
            return;
        }

        if (getAddressResponse.getAddress() == null) {
            tvResult.setText("获取地址失败\ndescription code:" + getAddressResponse.getDescriptionCode
                    ()+"\ndescription:" + getAddressResponse.getDescription());
            return;
        }

        if (!getAddressResponse.getAddress().startsWith("0x")) {
            String address = "0x" + getAddressResponse.getAddress();
            getAddressResponse.setAddress(address);
        }
        //获取地址成功

        llAddressResult.setVisibility(View.VISIBLE);

        tvAddress.setText(getAddressResponse.getAddress());

//        etAddress.setText(address);

    }

    @OnClick(R.id.bt_set_from_address)
    void setFromAddress() {
        fromWalletAddress = new WalletAddress(etAddressN.getText().toString(), tvAddress.getText
                ().toString());
        tvFromAddress.setText(fromWalletAddress.getAddress());
    }

    @OnClick(R.id.bt_set_to_address)
    void setToAddress() {
//        toWalletAddress = new WalletAddress(etAddressN.getText().toString(), tvAddress.getText()
//                .toString());
        etToAddress.setText(tvAddress.getText()
                .toString());
    }

    @OnClick(R.id.bt_token)
    void setCoinType() {
        logger.debug("setCoinType: on click");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle("选择币种")
                .setItems(tokens, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logger.debug("token:" + tokens[which]);
                        btToken.setText(tokens[which]);
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            logger.debug("handleMessage: ");
            try {
                switch (msg.what) {
                    case ADDRESS_DIALOG_FINISHED:
                        //地址弹框关闭
                        if (msg.arg2 == 0) {
                            //选中了值返回
                            int viewId = msg.arg1;
                            WalletAddress walletAddress = (WalletAddress) msg.obj;
                            if (viewId == R.id.bt_from_address) {
                                //from address
                                fromWalletAddress = walletAddress;
                                tvFromAddress.setText(fromWalletAddress.getAddress());
                            } else if (viewId == R.id.bt_to_address) {
                                //to address
//                                toWalletAddress = walletAddress;
                                etToAddress.setText(walletAddress.getAddress());
                            }
                        } else {
                            //未选中值返回
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error("handleMessage: 异常", e);
            }
        }
    };

    void createAddressDialog(final int viewId) {

        final ArrayList<String> walletAddressArrayList = new ArrayList<>();
        walletAddressArrayList.add(
                "0xafe47bce6c8afda6c1b174448dd68e417789b23f");
        walletAddressArrayList.add(
                "0x43fe224c8963724b4e4bdd60b68c9f5a71dc10dc");
        walletAddressArrayList.add(
                "0xF96240A522E9951BED2e03f1305da463E3747385");

        ListAdapter listAdapter = new ArrayAdapter<String>(context, android.R.layout
                .select_dialog_singlechoice, walletAddressArrayList);

        AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true)
                .setTitle("选择地址")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tempAddress = walletAddressArrayList.get(which);
                        tempWalletAddress = new WalletAddress(null, null);
                        tempWalletAddress.setAddress(tempAddress);
                        logger.debug("onClick: \npath:" + tempWalletAddress.getPath() + "\naddress:" +
                                tempWalletAddress.getAddress());
                        handler.obtainMessage(ADDRESS_DIALOG_FINISHED, viewId, 0,
                                tempWalletAddress)
                                .sendToTarget();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @OnClick(R.id.transfer_button)
    void transfer() {
        logger.debug("transfer: on click");
        if (fromWalletAddress == null) {
            Toast.makeText(context, "付款地址索引为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final String toAccountAddress = etToAddress.getText().toString();
        if (toAccountAddress == null || toAccountAddress.trim().length() == 0) {
            Toast.makeText(context, "收款地址为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String token = btToken.getText().toString();
        if (token == null) {
            Toast.makeText(context, "请先选择代币", Toast.LENGTH_SHORT).show();
            return;
        }
        String value = transferEdit.getText().toString();
        final BigDecimal valueDecimal;
        try {
            valueDecimal = new BigDecimal(value);
        } catch (Exception e) {
            logger.error("transfer: 数字转换异常", e);
            Toast.makeText(context, "输入的金额有误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (token.equalsIgnoreCase("ETH")) {
            //以太币
            doETHTransfer(fromWalletAddress, toAccountAddress, valueDecimal);
        } else if (token.equalsIgnoreCase("ABC")) {
            //ERC-20代币ABC
            doABCTransfer(fromWalletAddress, toAccountAddress, valueDecimal);
        } else if (token.equalsIgnoreCase("NMB")) {
            //ERC-20代币NMB
            doNMBTransfer(fromWalletAddress, toAccountAddress, valueDecimal);
        } else {
            //其它币种
            Toast.makeText(context, "目前不支持代币token", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void doNMBTransfer(final WalletAddress fromWalletAddress, final String toAccountAddress,
                               final BigDecimal valueDecimal) {
        //代币NMB转账(MainNet环境)
        final String tokenNMBAddress = "0xDFf61D728151150265Df32042947f404af26D022";

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("请稍后");
        final LoadingDailog loadingDailog = builder1.create();
        final TextView msgView = loadingDailog.findViewById(R.id.tipTextView);
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    TokenTransactionInfoReq tokenTransactionInfoReq = new TokenTransactionInfoReq();
                    String fromAccountAddress = fromWalletAddress.getAddress();
                    tokenTransactionInfoReq.setAddressN(fromWalletAddress.getPath());
                    tokenTransactionInfoReq.setTokenAddress(tokenNMBAddress);
                    tokenTransactionInfoReq.setToAddress(toAccountAddress);


                    //智能合约NMB的decimals为8
                    BigDecimal factor = BigDecimal.TEN.pow(8);
                    BigDecimal transferNMBValue = valueDecimal.multiply(factor);
                    tokenTransactionInfoReq.setValue(transferNMBValue);

                    tokenTransactionInfoReq.setGasLimit(BigInteger.valueOf(80000));
                    //gas price
                    tokenTransactionInfoReq.setGasPrice(BigInteger.valueOf(16_000_000_000L));
//                    EthGasPrice ethGasPrice = AdminClient.newInstance().ethGasPrice().send();
//                    if (ethGasPrice != null) {
//                        BigInteger gasPrice = ethGasPrice.getGasPrice();
//                        tokenTransactionInfoReq.setGasPrice(gasPrice);
//                    }
                    //请求获取nonce
                    EthGetTransactionCount ethGetTransactionCount = AdminClient.newInstance()
                            .ethGetTransactionCount(fromAccountAddress,
                                    DefaultBlockParameterName.LATEST)
                            .send();
                    if (ethGetTransactionCount != null) {
                        logger.debug("transfer: transaction count:" + ethGetTransactionCount
                                .getTransactionCount());
                        tokenTransactionInfoReq.setNonce(ethGetTransactionCount
                                .getTransactionCount());
                    }

                    logger.debug("request info:" + tokenTransactionInfoReq);

                    final String transactionInfoStr = new Gson().toJson(tokenTransactionInfoReq);
                    logger.debug("transfer: " + transactionInfoStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("请在设备上操作");
                        }
                    });
                    final SignEthTxResponse signEthTxResponse = TyWalletFactory.getTyWalletInstance
                            (context).signEthTokenTx(tokenTransactionInfoReq);

                    if (signEthTxResponse == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("签名失败");
                            }
                        });
                        return;
                    }
                    if (signEthTxResponse.getSignedData() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("签名失败\ndescription code:" + signEthTxResponse
                                        .getDescriptionCode() + "\ndescription:" +
                                        signEthTxResponse.getDescription());
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("正在发送转账请求,请稍后");
                        }
                    });
                    EthSendTransaction ethSendRawTransaction = AdminClient.newInstance()
                            .ethSendRawTransaction(signEthTxResponse.getSignedData()).send();

                    final String transactionHash = ethSendRawTransaction.getTransactionHash();

                    final Response.Error error = ethSendRawTransaction.getError();
                    logger.debug("json rpc:" + ethSendRawTransaction.getJsonrpc() + "\tid:" +
                            ethSendRawTransaction.getId()
                            + "\terror msg:" + (error == null ? null : error.getMessage())
                            + "\traw response:" + ethSendRawTransaction.getRawResponse() +
                            "\tresult:"
                            + ethSendRawTransaction.getResult() + "\ttransaction hash:" +
                            ethSendRawTransaction.getTransactionHash());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (error != null) {
                                tvResult.setText("transaction failed:" + error.getMessage());
                                return;
                            }
                            tvResult.setText("transaction hash:" + transactionHash);
                        }
                    });
                } catch (Exception e) {
                    logger.error("transfer: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("请求出错，请重试\n" + e.getMessage());
                        }
                    });
                } finally {
                    loadingDailog.dismiss();
                }

            }
        }.start();

    }

    private void doABCTransfer(final WalletAddress fromWalletAddress, final String toAccountAddress,
                               final BigDecimal valueDecimal) {
        //代币ABC转账(rinkeby测试环境)
        final String tokenABCAddress = "0xF1aF94e68C94c752DC4ADA560e307a8658122C13";

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("请稍后");
        final LoadingDailog loadingDailog = builder1.create();
        final TextView msgView = loadingDailog.findViewById(R.id.tipTextView);
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    TokenTransactionInfoReq tokenTransactionInfoReq = new TokenTransactionInfoReq();
                    //eg=>m/44'/60'/0'/0/0
                    String fromAccountAddress = fromWalletAddress.getAddress();
                    tokenTransactionInfoReq.setAddressN(fromWalletAddress.getPath());
                    //eg=>0x15QWp8VrkoT3UN6raM3J5aHYjrRJHh661V
                    tokenTransactionInfoReq.setTokenAddress(tokenABCAddress);
                    tokenTransactionInfoReq.setToAddress(toAccountAddress);


                    //智能合约ABC的decimals为4
                    BigDecimal factor = BigDecimal.TEN.pow(4);
                    BigDecimal transferABCValue = valueDecimal.multiply(factor);
                    tokenTransactionInfoReq.setValue(transferABCValue);

                    tokenTransactionInfoReq.setGasLimit(BigInteger.valueOf(80000));
                    //gas price
                    tokenTransactionInfoReq.setGasPrice(BigInteger.valueOf(6_000_000_000L));
//                    EthGasPrice ethGasPrice = AdminClient.newInstance().ethGasPrice().send();
//                    if (ethGasPrice != null) {
//                        BigInteger gasPrice = ethGasPrice.getGasPrice();
//                        tokenTransactionInfoReq.setGasPrice(gasPrice);
//                    }
                    //请求获取nonce

                    EthGetTransactionCount ethGetTransactionCount = AdminClient.newInstance()
                            .ethGetTransactionCount(fromAccountAddress,
                                    DefaultBlockParameterName.LATEST)
                            .send();
                    if (ethGetTransactionCount != null) {
                        logger.debug("transfer: transaction count:" + ethGetTransactionCount
                                .getTransactionCount());
                        tokenTransactionInfoReq.setNonce(ethGetTransactionCount
                                .getTransactionCount());
                    }

                    logger.debug("request info:" + tokenTransactionInfoReq);

                    final String transactionInfoStr = new Gson().toJson(tokenTransactionInfoReq);
                    logger.debug("transfer: " + transactionInfoStr);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("请在设备上操作");
                        }
                    });
                    final SignEthTxResponse signEthTxResponse = TyWalletFactory.getTyWalletInstance
                            (context).signEthTokenTx(tokenTransactionInfoReq);

                    if (signEthTxResponse == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("签名失败");
                            }
                        });
                        return;
                    }
                    if (signEthTxResponse.getSignedData() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("签名失败\ndescription code:" + signEthTxResponse
                                        .getDescriptionCode() + "\ndescription:" +
                                        signEthTxResponse.getDescription());
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("正在发送转账请求,请稍后");
                        }
                    });
                    EthSendTransaction ethSendRawTransaction = AdminClient.newInstance()
                            .ethSendRawTransaction(signEthTxResponse.getSignedData()).send();

                    final String transactionHash = ethSendRawTransaction.getTransactionHash();

                    final Response.Error error = ethSendRawTransaction.getError();
                    logger.debug("json rpc:" + ethSendRawTransaction.getJsonrpc() + "\tid:" +
                            ethSendRawTransaction.getId()
                            + "\terror msg:" + (error == null ? null : error.getMessage())
                            + "\traw response:" + ethSendRawTransaction.getRawResponse() +
                            "\tresult:"
                            + ethSendRawTransaction.getResult() + "\ttransaction hash:" +
                            ethSendRawTransaction.getTransactionHash());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (error != null) {
                                tvResult.setText("transaction failed:" + error.getMessage());
                                return;
                            }
                            tvResult.setText("transaction hash:" + transactionHash);
                        }
                    });
                } catch (Exception e) {
                    logger.error("transfer: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("请求出错，请重试\n" + e.getMessage());
                        }
                    });
                } finally {
                    loadingDailog.dismiss();
                }

            }
        }.start();

    }

    private void doETHTransfer(final WalletAddress fromWalletAddress, final String
            toAccountAddress, final BigDecimal valueDecimal) {

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("请稍后");
        final LoadingDailog loadingDailog = builder1.create();
        final TextView msgView = loadingDailog.findViewById(R.id.tipTextView);
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    EthTransactionInfoReq ethTransactionInfo = new EthTransactionInfoReq();
                    //eg=>m/44'/60'/0'/0/0
                    String fromAccountAddress = fromWalletAddress.getAddress();
                    ethTransactionInfo.setAddressN(fromWalletAddress.getPath());
                    //eg=>0x15QWp8VrkoT3UN6raM3J5aHYjrRJHh661V
                    ethTransactionInfo.setToAddress(toAccountAddress);
                    ethTransactionInfo.setValue(valueDecimal);
                    ethTransactionInfo.setGasLimit(BigInteger.valueOf(25000));
                    //36GWEI
                    ethTransactionInfo.setGasPrice(BigInteger.valueOf(16_000_000_000L));
//                    EthGasPrice ethGasPrice = AdminClient.newInstance().ethGasPrice().send();
//                    if (ethGasPrice != null) {
//                        BigInteger gasPrice = ethGasPrice.getGasPrice();
//                        ethTransactionInfo.setGasPrice(gasPrice);
//                    }
                    //请求获取nonce

                    EthGetTransactionCount ethGetTransactionCount = AdminClient.newInstance()
                            .ethGetTransactionCount(fromAccountAddress,
                                    DefaultBlockParameterName.LATEST)
                            .send();
                    if (ethGetTransactionCount != null) {
                        logger.debug("transfer: transaction count:" + ethGetTransactionCount
                                .getTransactionCount());
                        ethTransactionInfo.setNonce(ethGetTransactionCount
                                .getTransactionCount());
                    }
//                        ethTransactionInfo.setNonce(new BigInteger("1"));

                    logger.debug("run: transaction info:" + ethTransactionInfo.toString());

                    final String transactionInfoStr = new Gson().toJson(ethTransactionInfo);
                    logger.debug("transfer: " + transactionInfoStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("请在设备上操作");
                        }
                    });
                    final SignEthTxResponse signEthTxResponse = TyWalletFactory.getTyWalletInstance
                            (context).signEthTx
                            (ethTransactionInfo);

                    if (signEthTxResponse == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("签名失败");
                            }
                        });
                        return;
                    }
                    if (signEthTxResponse.getSignedData() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("签名失败\ndescription code:" + signEthTxResponse
                                        .getDescriptionCode() + "\ndescription:" +
                                        signEthTxResponse.getDescription());
                            }
                        });
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("正在发送转账请求,请稍后");
                        }
                    });
                    EthSendTransaction ethSendRawTransaction = AdminClient.newInstance()
                            .ethSendRawTransaction(signEthTxResponse.getSignedData()).send();

                    final String transactionHash = ethSendRawTransaction.getTransactionHash();
                    final Response.Error error = ethSendRawTransaction.getError();
                    logger.debug("json rpc:" + ethSendRawTransaction.getJsonrpc() + "\tid:" +
                            ethSendRawTransaction.getId() + "\terror message:" +
                            (error == null ? null : error.getMessage())
                            + "\traw response:" + ethSendRawTransaction.getRawResponse() +
                            "\tresult:"
                            + ethSendRawTransaction.getResult() + "\ttransaction hash:" +
                            ethSendRawTransaction.getTransactionHash());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (error != null) {
                                tvResult.setText("transaction failed:" + error.getMessage());
                                return;
                            }
                            tvResult.setText("transaction hash:" + transactionHash);
                        }
                    });
                } catch (Exception e) {
                    logger.error("transfer: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("请求出错，请重试\n" + e.getMessage());
                        }
                    });
                } finally {
                    loadingDailog.dismiss();
                }

            }
        }.start();
    }
}
