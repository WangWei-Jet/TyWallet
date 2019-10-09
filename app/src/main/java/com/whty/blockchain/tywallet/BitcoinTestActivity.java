package com.whty.blockchain.tywallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.whty.blockchain.tybitcoinlib.api.BlockExplorerAPI;
import com.whty.blockchain.tywallet.blockchain.AdminClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BitcoinTestActivity extends AppCompatActivity {

//    private final String TAG = this.getClass().getName();

    //    BlockExplorerAPI blockExplorerAPI = null;
    @BindView(R.id.bt_get_balance)
    Button btGetBalance;
    @BindView(R.id.resultView)
    TextView resultView;
    @BindView(R.id.bt_push_transaction)
    Button btPushTransaction;
    @BindView(R.id.tv_env)
    TextView tvEnv;
    @BindView(R.id.bt_env)
    Button btEnv;

    private Context context;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_test);
        ButterKnife.bind(this);

        context = this;

        tvEnv.setText("当前环境:比特币测试链");
//        blockExplorerAPI = new BlockExplorerAPI(BlockExplorerAPI.BlockExplorer.Insight_Testnet);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick(R.id.bt_env)
    void switchEnv() {
        logger.debug("switchEnv: on click");

        final String[] envs = new String[]{"比特币测试链", "比特币主链"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("选择区块链")
                .setItems(envs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logger.debug("env:" + envs[which]);
                        tvEnv.setText("当前环境:" + envs[which]);
                        if (which == 0) {
                            //测试链
                            AdminClient.switchBitcoinEnv(BlockExplorerAPI.BlockExplorer
                                    .Insight_Testnet);
                        } else if (which == 1) {
                            //主链
                            AdminClient.switchBitcoinEnv(BlockExplorerAPI.BlockExplorer.Insight);
                        }
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @OnClick(R.id.bt_get_balance)
    void getBalance() {
        logger.debug("getBalance: on click");

        startActivity(new Intent(this, SelectInputActivity.class));

    }


    @OnClick(R.id.bt_push_transaction)
    void sendBTCTransaction() {
        logger.debug("sendBTCTransaction: on click");

        startActivity(new Intent(this,BitcoinTransferActivity.class));

//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    //第一步:获取UTXO信息
//
//                    logger.debug("sendBTCTransaction: step1:");
//                    List<String> addressList = new ArrayList<>();
//                    addressList.add("2MvmXPJ6JPMzAgqEiY12F54fPfYZWHt4phn");
//                    final JSONObject jsonObject = AdminClient.getBitcoinExplorerAPI()
//                            .getUnspentOutputs(addressList);
//                    if (jsonObject == null) {
//                        logger.debug("sendBTCTransaction: getUnspentOutputs null");
//                        return;
//                    }
//                    logger.debug("getUTXOResponse: " + jsonObject.toString());
//                    BlockchainUTXOResponse blockchainUTXOResponse = new Gson().fromJson
//                            (jsonObject.toString(),
//                                    BlockchainUTXOResponse.class);
//                    if (blockchainUTXOResponse == null) {
//                        logger.debug("sendBTCTransaction: block chain utxo response null");
//                        return;
//                    }
//                    List<BlockchainUTXO> blockchainUTXOList = blockchainUTXOResponse
//                            .getUnspent_outputs();
//                    if (blockchainUTXOList == null || blockchainUTXOList.isEmpty()) {
//                        logger.debug("sendBTCTransaction: block chain utxo list null");
//                        return;
//                    }
//
//                    logger.debug("sendBTCTransaction: find " + blockchainUTXOList.size() + " utxo");
//
//                    //第二步:拼装交易inputs
//                    List<BitcoinTransactionInput> inputList = new ArrayList<>();
//                    for (BlockchainUTXO blockchainUTXO : blockchainUTXOList) {
//                        logger.debug("sendBTCTransaction: utxo:\n\t" + blockchainUTXO.toString());
//
//                        BitcoinTransactionInput bitcoinTransactionInput = new
//                                BitcoinTransactionInput();
//
//                        //TODO
//                        bitcoinTransactionInput.setAddress_n("");
//                        bitcoinTransactionInput.setPrev_hash(blockchainUTXO.getTx_hash_big_endian
//                                ());
//                        bitcoinTransactionInput.setPrev_index(blockchainUTXO.getTx_output_n());
//                        bitcoinTransactionInput.setAmount(blockchainUTXO.getValue());
//
//                        inputList.add(bitcoinTransactionInput);
//                    }
//
//                    //第三步:拼装交易outputs
//                    List<BitcoinTransactionOutput> outputList = new ArrayList<>();
//
//                    BitcoinTransactionOutput bitcoinTransactionOutput = new
//                            BitcoinTransactionOutput();
//                    bitcoinTransactionOutput.setAddress("2Mxi5TCnNoh63gDAiD5fvN6Z42DAByRr8FR");
//                    bitcoinTransactionOutput.setAmount(new BigInteger("110"));
//                    bitcoinTransactionOutput.setScript_type("00");
//
//                    BitcoinTransactionOutput bitcoinTransactionOutput1 = new
//                            BitcoinTransactionOutput();
//                    bitcoinTransactionOutput1.setAddress("mvmxhaQbdQhGcXzsJmsvh92wBcwaSDnhKx");
//                    bitcoinTransactionOutput1.setAmount(new BigInteger("140"));
//                    bitcoinTransactionOutput1.setScript_type("00");
//
//                    outputList.add(bitcoinTransactionOutput);
//                    outputList.add(bitcoinTransactionOutput1);
//
//                    //第四步:拼装指令请求，从设备获取签名信息
//                    BitcoinTransactionRequest bitcoinTransactionRequest = new
//                            BitcoinTransactionRequest();
//                    bitcoinTransactionRequest.setInputList(inputList);
//                    bitcoinTransactionRequest.setOutputList(outputList);
//
//                    String jsonRequestStr = new Gson().toJson
//                            (bitcoinTransactionRequest);
//                    logger.debug("sendBTCTransaction: request info:" + jsonRequestStr);
//
//                    //TODO 通过jni发送请求给设备进行交易信息序列化
//                    BtcTransactionInfoReq btcTransactionInfoReq = new BtcTransactionInfoReq();
//
//                    btcTransactionInfoReq.setRequestJsonStr(jsonRequestStr);
//
//                    String response = TyWalletFactory.getTyWalletInstance(context).signTx
//                            (btcTransactionInfoReq);
//
//                    logger.debug("response: "+response);
//
//                    String responseData =
//                            "02483045022100ab94a4b9cf986403d24d06f5b5feed963b446055c29f376bdba0bd3c90b8b43e02207661b83612707c5038e2a631cd06d3b1f3262a2b5440e9e37958924a894b73d1012102b2e17724e09f2ff3c097df7d7ae32a1bec7e068195ee486f476a9ee20bc7c7ad";
//
//                    //通过浏览器api发送交易
//                    JSONObject transactionResponse = AdminClient.getBitcoinExplorerAPI().pushTx
//                            (responseData, null);
//                    logger.debug("sendBTCTransaction: transaction response:" + transactionResponse
//                            .toString
//                                    ());
//
//
//                } catch (Exception e) {
//                    Log.e(TAG, "异常", e);
//                }
//            }
//        }.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
