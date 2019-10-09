package com.whty.blockchain.tywallet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tywallet.blockchain.AdminClient;
import com.whty.blockchain.tywallet.util.ENV;
import com.whty.blockchain.tywallet.util.WalletAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetBalanceActivity extends AppCompatActivity {

    @BindView(R.id.bt_get_balance)
    Button btGetBalance;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.bt_address)
    Button btAddress;

    @BindView(R.id.et_address_n)
    EditText etAddressN;
    @BindView(R.id.bt_get_address)
    Button btGetAddress;


//    private final String TAG = this.getClass().getName();
    @BindView(R.id.et_token)
    TextView etToken;
    @BindView(R.id.bt_token)
    Button btToken;

    private Context context;

    private WalletAddress tempWalletAddress;

    String[] tokens = new String[]{"ETH", "ABC"};

    static final int ADDRESS_DIALOG_FINISHED = 0;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArrayList<String>  walletAddressArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_balance);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.debug("onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.debug("onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logger.debug("onDestroy: ");
    }


    @OnClick(R.id.bt_address)
    void showWalletAddress() {
        logger.debug("showWalletAddress: on click");
        //弹框选择
        createAddressDialog(R.id.bt_address);
    }

    @OnClick(R.id.bt_get_address)
    void getAddress() {
        logger.debug("getAddress: on click");
        if (!TyWalletFactory.getTyWalletInstance
                (context).isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        walletAddressArr.clear();
        final String addressN = etAddressN.getText().toString();

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
        walletAddressArr.add(getAddressResponse.getAddress());
        etAddress.setText(getAddressResponse.getAddress());

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
                        etToken.setText(tokens[which]);
//                        btToken.setText(tokens[which]);
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
                            if (viewId == R.id.bt_address) {
                                //address
//                                targetWalletAddress = walletAddress;
                                etAddress.setText(walletAddress.getAddress());
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

        ListAdapter listAdapter = new ArrayAdapter<String>(context, android.R.layout
                .select_dialog_singlechoice, walletAddressArr);

        tempWalletAddress = null;

        AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true)
                .setTitle("选择地址")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tempAddress = walletAddressArr.get(which);
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

    @OnClick(R.id.bt_get_balance)
    void getBalance() {
        logger.debug("getBalance: on click");
        try {

            final String address = etAddress.getText().toString();
            if (address == null) {
                logger.debug("run: 地址为空");
                return;
            }
            String targetToken = etToken.getText().toString();
            if (targetToken == null) {
                Toast.makeText(context, "币种为空", Toast.LENGTH_SHORT).show();
                return;
            } else if (targetToken.equalsIgnoreCase("ETH")) {
                //ETH
                getETHBalance(address);
            } else if (targetToken.equalsIgnoreCase("ABC")) {
                //ABC(Rinkeby)
                getABCBalance(address);
            } else if (targetToken.equalsIgnoreCase("NMB")) {
                //NMB(MainNet)
                getNMBBalance(address);
            } else {
                Toast.makeText(context, "不支持的币种", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (Exception e) {
            logger.error("getBalance: 异常", e);
        }
    }

    private void getETHBalance(final String address) {

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("请稍候");
        final LoadingDailog loadingDailog = builder1.create();
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    EthGetBalance ethGetBalance = AdminClient.newInstance().ethGetBalance
                            (address,
                                    DefaultBlockParameterName.LATEST).send();
                    // 单位wei
                    BigInteger balance = ethGetBalance.getBalance();


                    final BigDecimal balanceDecimal = Convert.fromWei(new BigDecimal(balance)
                            , Convert.Unit.ETHER);
                    logger.debug("run: balance:" + balanceDecimal + "ETH");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("account:\n" + address
                                    + "\n\nbalance:\n"
                                    + balanceDecimal);
                        }
                    });
                } catch (IOException e) {
                    logger.error("run: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("异常");
                        }
                    });
                } catch (Exception e) {
                    logger.error("run: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("异常");
                        }
                    });
                } finally {
                    loadingDailog.dismiss();
                }
            }
        }.start();
    }

    private void getABCBalance(final String address) {

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("请稍候");
        final LoadingDailog loadingDailog = builder1.create();
        loadingDailog.show();

        //测试环境Rinkeby代币:ABC
        final String tokenABCAddress = "0xF1aF94e68C94c752DC4ADA560e307a8658122C13";

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    // 调用合约的查询余额方法
                    Function function = new Function("balanceOf", Arrays.<Type>asList(new Address
                            (address)),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                            }));
                    String encodedFunction = FunctionEncoder.encode(function);
                    logger.debug("encodeFunction:" + encodedFunction);
                    // 第三个参数是data
                    EthCall ethCall = AdminClient.newInstance()
                            .ethCall(Transaction.createEthCallTransaction(address,
                                    tokenABCAddress, encodedFunction),
                                    DefaultBlockParameterName.LATEST)
                            .send();
                    logger.debug("run: eth call result:" + ethCall.getResult());
                    final BigInteger tokenBalance = Numeric.decodeQuantity(ethCall.getResult());
                    //智能合约ABC的decimals为4
                    BigDecimal factor = BigDecimal.TEN.pow(4);
                    final BigInteger tokenABCBalance = tokenBalance.divide(factor.toBigInteger());
                    logger.debug("token ABC balance:" + tokenABCBalance);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("account:\n" + address
                                    + "\n\nbalance:\n"
                                    + tokenABCBalance);
                        }
                    });
                } catch (IOException e) {
                    logger.error("run: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("异常");
                        }
                    });
                } catch (Exception e) {
                    logger.error("run: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("异常");
                        }
                    });
                } finally {
                    loadingDailog.dismiss();
                }
            }
        }.start();
    }

    private void getNMBBalance(final String address) {

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("请稍候");
        final LoadingDailog loadingDailog = builder1.create();
        loadingDailog.show();

        //正式环境MainNet代币:NMB
        final String tokenNMBAddress = "0xDFf61D728151150265Df32042947f404af26D022";

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    // 调用合约的查询余额方法
                    Function function = new Function("balanceOf", Arrays.<Type>asList(new Address
                            (address)),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                            }));
                    String encodedFunction = FunctionEncoder.encode(function);
                    logger.debug("encodeFunction:" + encodedFunction);
                    // 第三个参数是data
                    EthCall ethCall = AdminClient.newInstance()
                            .ethCall(Transaction.createEthCallTransaction(address,
                                    tokenNMBAddress, encodedFunction),
                                    DefaultBlockParameterName.LATEST)
                            .send();
                    logger.debug("run: eth call result:" + ethCall.getResult());
                    final BigInteger tokenBalance = Numeric.decodeQuantity(ethCall.getResult());
                    //智能合约NMB的decimals为8
                    BigDecimal factor = BigDecimal.TEN.pow(8);
                    final BigInteger tokenNMBBalance = tokenBalance.divide(factor.toBigInteger());
                    logger.debug("token ABC balance:" + tokenNMBBalance);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("account:\n" + address
                                    + "\n\nbalance:\n"
                                    + tokenNMBBalance);
                        }
                    });
                } catch (IOException e) {
                    logger.error("run: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("异常");
                        }
                    });
                } catch (Exception e) {
                    logger.error("run: 异常", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("异常");
                        }
                    });
                } finally {
                    loadingDailog.dismiss();
                }
            }
        }.start();
    }
}
