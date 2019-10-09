package com.whty.blockchain.tywallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXO;
import com.whty.blockchain.tybitcoinlib.model.Coin;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionInput;
import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionOutput;
import com.whty.blockchain.tyblockchainlib.api.entity.BitcoinTransactionRequest;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.SignTxResponse;
import com.whty.blockchain.tywallet.blockchain.AdminClient;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BitcoinTransferActivity extends AppCompatActivity {

    @BindView(R.id.tv_input)
    TextView tvInput;
    @BindView(R.id.ib_arrow_right)
    ImageButton ibArrowRight;
    @BindView(R.id.transfer_button)
    Button transferButton;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_output)
    TextView tvOutput;
    @BindView(R.id.ib_add_output)
    ImageButton ibAddOutput;

    private Context context;

//    private final String TAG = this.getClass().getName();

    //选择交易输入请求码
    private final int REQUEST_CODE_CHOOSE_INPUT = 11;
    List<BlockchainUTXO> queredBlockchainUTXOList = null;
    List<BitcoinTransactionOutput> expectedOutputList = new ArrayList<>();
    private BitcoinTransactionRequest targetBitcoinTransactionRequest = null;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_transfer);
        ButterKnife.bind(this);

        context = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyBoard();
    }

    @OnClick(R.id.ib_arrow_right)
    void choosenInput() {
        Intent intent = new Intent(this, SelectInputActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("from", this.getClass().getName());
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_INPUT);
    }

    @OnClick(R.id.tv_input)
    void showInputList() {

        logger.debug("showInputList: on clcik");

        if (targetBitcoinTransactionRequest == null) {
            return;
        }

        if (targetBitcoinTransactionRequest.getInputList() == null ||
                targetBitcoinTransactionRequest.getInputList().isEmpty()) {
            return;
        }

        //弹出input list
        View popupwindowView = LayoutInflater.from(context).inflate(R.layout
                .popupwindow_recyclerview, null);
        RecyclerView recyclerView = popupwindowView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        PopupWindow popupWindow = new PopupWindow(tvInput.getWidth(), ViewGroup.LayoutParams
                .WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xF4C0C0C0));
        popupWindow.setContentView(popupwindowView);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(tvInput);


    }

    @OnClick(R.id.tv_output)
    void showOutputList() {

        logger.debug("showOutputList: on clcik");

        if (expectedOutputList.isEmpty()) {
            return;
        }

        //弹出output list
        View popupwindowView = LayoutInflater.from(context).inflate(R.layout
                .popupwindow_recyclerview, null);
        RecyclerView recyclerView = popupwindowView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(outputListRecyclerAdapter);

        PopupWindow popupWindow = new PopupWindow(tvInput.getWidth(), ViewGroup.LayoutParams
                .WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xF4C0C0C0));
        popupWindow.setContentView(popupwindowView);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(tvOutput);


    }

    @OnClick(R.id.ib_add_output)
    void addOutput() {
        showOutputDialog();
    }

    RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_transaction_input_layout, parent, false);
            RecyclerView.ViewHolder viewHolder = new OutputViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (queredBlockchainUTXOList == null ||
                    queredBlockchainUTXOList.isEmpty()) {
                return;
            }
            BlockchainUTXO blockchainUTXO = queredBlockchainUTXOList.get(position);
            LinearLayout linearLayout = ((OutputViewHolder) holder)
                    .getLl_remove();
            linearLayout.setVisibility(View.VISIBLE);
            ((OutputViewHolder) holder).getTv_address().setText
                    ("Address:\n" +
                            blockchainUTXO.getAddress());
            ((OutputViewHolder) holder).getTv_address_n().setText
                    ("AddressN:" + blockchainUTXO.getAddressN());
            ((OutputViewHolder) holder).getTv_transaction_id().setText
                    ("Transaction Id:\n" + blockchainUTXO.getTxHashBigEndian());
            ((OutputViewHolder) holder).getTv_output_n().setText("Output " +
                    "Index:\n" + blockchainUTXO.getTxOutputN());
//            BigInteger amount = blockchainUTXO.getValue();
//            Coin coin = new Coin(amount.longValue());
            ((OutputViewHolder) holder).getTv_amount().setText("Amount" +
                    "(satoshis):\n" + blockchainUTXO.getValue() + "=" + blockchainUTXO
                    .getAmountBtc() + " BTC");

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logger.debug("onClick: ll_remove on click");
                    queredBlockchainUTXOList.remove(blockchainUTXO);
                    if (queredBlockchainUTXOList.isEmpty()) {
                        tvInput.setText("");
                    }
                    notifyDataSetChanged();

                    //更新选中的交易输入
                    List<BitcoinTransactionInput> bitcoinTransactionInputList = new ArrayList<>();
                    for (BlockchainUTXO blockchainUTXO : queredBlockchainUTXOList) {
                        BitcoinTransactionInput bitcoinTransactionInput = new
                                BitcoinTransactionInput();
                        bitcoinTransactionInput.setAddress_n(blockchainUTXO.getAddressN());
                        bitcoinTransactionInput.setAmountSatoshi(blockchainUTXO.getValue()
                                .toString());
                        bitcoinTransactionInput.setAmount(blockchainUTXO.getAmountBtc());
                        bitcoinTransactionInput.setPrev_hash(blockchainUTXO.getTxHashBigEndian
                                ());
                        bitcoinTransactionInput.setPrev_index(blockchainUTXO.getTxOutputN());
                        bitcoinTransactionInputList.add(bitcoinTransactionInput);
                    }
                    BitcoinTransactionRequest bitcoinTransactionRequest = new
                            BitcoinTransactionRequest();
                    bitcoinTransactionRequest.setInputList(bitcoinTransactionInputList);
                    targetBitcoinTransactionRequest = new BitcoinTransactionRequest();
                    targetBitcoinTransactionRequest.setInputList(bitcoinTransactionRequest
                            .getInputList());
                }
            });

        }

        @Override
        public int getItemCount() {
            if (queredBlockchainUTXOList == null) {
                return 0;
            }
            return queredBlockchainUTXOList.size();
        }
    };


    RecyclerView.Adapter outputListRecyclerAdapter = new RecyclerView.Adapter() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_transaction_input_layout, parent, false);
            RecyclerView.ViewHolder viewHolder = new OutputViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (expectedOutputList.isEmpty()) {
                return;
            }
            BitcoinTransactionOutput bitcoinTransactionOutput = expectedOutputList.get(position);
            LinearLayout linearLayout = ((OutputViewHolder) holder)
                    .getLl_remove();
            linearLayout.setVisibility(View.VISIBLE);
            ((OutputViewHolder) holder).getTv_address().setText
                    ("Address:\n" +
                            bitcoinTransactionOutput.getAddress());
            ((OutputViewHolder) holder).getTv_address_n().setText
                    ("AddressN:" + bitcoinTransactionOutput.getAddress_n());
            ((OutputViewHolder) holder).getTv_transaction_id()
                    .setVisibility(View.GONE);
            ((OutputViewHolder) holder).getTv_output_n().setVisibility
                    (View.GONE);
            ((OutputViewHolder) holder).getTv_amount().setText("Amount" +
                    "(satoshis):\n" + bitcoinTransactionOutput.getAmountSatoshi() + "=" +
                    bitcoinTransactionOutput
                            .getAmount() + " BTC");

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logger.debug("onClick: ll_remove on click");
                    expectedOutputList.remove(bitcoinTransactionOutput);
                    if (expectedOutputList.isEmpty()) {
                        tvOutput.setText("");
                    }
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return expectedOutputList.size();
        }
    };

    class OutputViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_remove;
        TextView tv_address;
        TextView tv_address_n;
        TextView tv_transaction_id;
        TextView tv_output_n;
        TextView tv_amount;

        public OutputViewHolder(View itemView) {
            super(itemView);
            this.ll_remove = itemView.findViewById(R.id.ll_remove);
            this.tv_address = itemView.findViewById(R.id.tv_input_address);
            this.tv_address_n = itemView.findViewById(R.id.tv_address_n);
            this.tv_transaction_id = itemView.findViewById(R.id.tv_transaction_id);
            this.tv_output_n = itemView.findViewById(R.id.tv_output_n);
            this.tv_amount = itemView.findViewById(R.id.tv_amount);
        }

        public LinearLayout getLl_remove() {
            return ll_remove;
        }

        public TextView getTv_address_n() {
            return tv_address_n;
        }

        public TextView getTv_address() {
            return tv_address;
        }

        public TextView getTv_transaction_id() {
            return tv_transaction_id;
        }

        public TextView getTv_output_n() {
            return tv_output_n;
        }

        public TextView getTv_amount() {
            return tv_amount;
        }
    }


    void showOutputDialog() {

        View view = LayoutInflater.from(context).inflate(R
                .layout.output_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false);


        final AlertDialog outputAlertDialog = builder.create();


        EditText etAddressN = view.findViewById(R.id.et_address_n);
        Button btGetAddress = view.findViewById(R.id.bt_address);
        EditText etToAddress = view.findViewById(R.id.et_to_address);
        EditText etTransferAmount = view.findViewById(R.id.transfer_edit);
        Button btCancel = view.findViewById(R.id.bt_cancel);
        Button btConfirm = view.findViewById(R.id.bt_confirm);


        //为地址索引输入框赋初始值
        switch (AdminClient.getCurrentBitcoinEnv()) {
            case Blockchain:
            case Insight:
                etAddressN.setText("m/49'/0'/0'/0/0");
                break;

            case Blockchain_Testnet:
            case Insight_Testnet:
                etAddressN.setText("m/49'/1'/0'/0/0");
                break;
        }

        outputAlertDialog.show();

        btGetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get address
                if (!TyWalletFactory.getTyWalletInstance
                        (context).isWalletConnected()) {
                    Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
                    outputAlertDialog.dismiss();
                    return;
                }

                String addressN = etAddressN.getText().toString();

                if (addressN == null || addressN.trim().length() == 0) {
                    Toast.makeText(context, "获取地址失败,地址索引为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                logger.debug("getAddress: addressN:" + addressN);
                GetAddressResponse getAddressResponse = null;
                switch (AdminClient.getCurrentBitcoinEnv()) {
                    case Insight:
                    case Blockchain:
                        getAddressResponse = TyWalletFactory.getTyWalletInstance
                                (context).getAddress(addressN, CoinType.BTC);
                        break;

                    case Insight_Testnet:
                    case Blockchain_Testnet:
                        getAddressResponse = TyWalletFactory.getTyWalletInstance
                                (context).getAddress(addressN, CoinType.TESTNET);
                        break;
                }

                if (getAddressResponse == null) {
                    Toast.makeText(context, "获取地址失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getAddressResponse.getAddress() == null) {
                    Toast.makeText(context, "获取地址失败:" + getAddressResponse.getDescription(), Toast
                            .LENGTH_SHORT).show();
                    return;
                }

                if (getAddressResponse.getAddress().startsWith("0x")) {
                    getAddressResponse.setAddress(getAddressResponse.getAddress().substring(2));
                }
                etToAddress.setText(getAddressResponse.getAddress());
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputAlertDialog.dismiss();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outputAddress = etToAddress.getText().toString();
                if (outputAddress == null || outputAddress.trim().length() == 0) {
                    Toast.makeText(context, "收款方地址为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (outputAddress.startsWith("0x")) {
                    outputAddress = outputAddress.substring(2);
                }

                String value = etTransferAmount.getText().toString();
                BigDecimal valueDecimal;
                try {
                    valueDecimal = new BigDecimal(value);
                } catch (Exception e) {
                    logger.error("transfer: 数字转换异常", e);
                    Toast.makeText(context, "输入的金额有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (valueDecimal.compareTo(new BigDecimal("0")) <= 0) {
                    Toast.makeText(context, "输入的金额需大于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                logger.debug("addOutputInfo: address:" + outputAddress + "\tamount:" + value);
                BitcoinTransactionOutput bitcoinTransactionOutput = new BitcoinTransactionOutput();
                //单位satoshi
                Coin coin = Coin.fromString(value, Coin.BitcoinDenomination.BTC);
                long satoshiBTC = coin.toNumber();
                logger.debug("transferBTC: satoshi:" + satoshiBTC);
                bitcoinTransactionOutput.setAmountSatoshi(satoshiBTC + "");
                bitcoinTransactionOutput.setAmount(Double.valueOf(value));
                bitcoinTransactionOutput.setAddress(outputAddress);
                expectedOutputList.add(bitcoinTransactionOutput);
                tvOutput.setText("已添加收款方输出,点击查看详情");
                outputAlertDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.transfer_button)
    void transferBTC() {

        logger.debug("transferBTC: on click");
        if (targetBitcoinTransactionRequest == null) {
            Toast.makeText(context, "请求为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //检查input list
        List<BitcoinTransactionInput> bitcoinTransactionInputList =
                targetBitcoinTransactionRequest
                        .getInputList();
        if (bitcoinTransactionInputList == null ||
                bitcoinTransactionInputList.isEmpty()) {
            Toast.makeText(context, "请求付款方输入为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //检查output list
        if (expectedOutputList.isEmpty()) {
            Toast.makeText(context, "请求收款方输出为空", Toast.LENGTH_SHORT).show();
            return;
        }
        targetBitcoinTransactionRequest.setOutputList(expectedOutputList);
        switch (AdminClient.getCurrentBitcoinEnv()) {
            case Blockchain:
            case Insight:
                targetBitcoinTransactionRequest.setCoinType(0);
                break;

            case Blockchain_Testnet:
            case Insight_Testnet:
            case BlockCypher_Testnet:
                targetBitcoinTransactionRequest.setCoinType(1);
                break;
        }

        final LoadingDailog.Builder builder = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("正在授权交易\n请在设备上确认");
        final LoadingDailog loadingDailog = builder.create();
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    String jsonRequestStr = gson.toJson
                            (targetBitcoinTransactionRequest);
                    logger.debug("sendBTCTransaction: request info:" + jsonRequestStr);

                    final SignTxResponse signTxResponse = TyWalletFactory.getTyWalletInstance
                            (context).signTx(targetBitcoinTransactionRequest);

                    logger.debug("sign btc transaction response: " + signTxResponse);

                    if (signTxResponse == null) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDailog.dismiss();
                                tvResult.setText("交易签名失败");
                            }
                        });
                        return;
                    }

                    if (signTxResponse.getSerialized() == null) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDailog.dismiss();
                                tvResult.setText("获取交易签名失败\ndescription:" + signTxResponse
                                        .getDescription() + "\ndescription code:" +
                                        signTxResponse.getDescriptionCode());
                            }
                        });
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDailog.dismiss();
                            showInfoDialogBeforePushTx(signTxResponse.getSerialized());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDailog.dismiss();
                            tvResult.setText("异常:" + e.getMessage());
                        }
                    });
                    return;
                }
            }
        }.start();


    }

    void showInfoDialogBeforePushTx(String txData) {

        logger.debug("showInfoDialogBeforePushTx: tx data:" + txData);

        View view = LayoutInflater.from(this).inflate(R.layout.common_dialog,
                null);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view).create();
        alertDialog.show();

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("钱包设备签名信息");

        TextView tvContent = view.findViewById(R.id.tv_content_0);
        tvContent.setVisibility(View.VISIBLE);
        tvContent.setTextSize(16);
        tvContent.setTextColor(Color.RED);
        tvContent.setText(txData);

        Button cancelButton = view.findViewById(R.id.bt_oper_0);
        cancelButton.setVisibility(View.VISIBLE);
        cancelButton.setText("取消");


        Button confirmButton = view.findViewById(R.id.bt_oper_1);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setText("发送");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();


                final LoadingDailog.Builder builder = new LoadingDailog.Builder(context)
                        .setCancelable(false)
                        .setCancelOutside(false)
                        .setMessage("正在请求交易\n请稍候...");
                final LoadingDailog loadingDailog = builder.create();
                loadingDailog.show();

                //TODO 发送请求数据到浏览器api
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            final JSONObject jsonObject = AdminClient.getBitcoinExplorerAPI().pushTx
                                    (txData, txData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(jsonObject.toString());
                                    loadingDailog.dismiss();
                                }
                            });
                        } catch (Exception e) {
                            logger.error("run: 异常", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "异常", Toast.LENGTH_SHORT).show();
                                    loadingDailog.dismiss();
                                }
                            });
                        }
                    }
                }.start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            //成功返回
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                Toast.makeText(context, "未返回input信息", Toast.LENGTH_SHORT).show();
                return;
            }
            String responseJson = bundle.getString("input");
            if (responseJson == null) {
                Toast.makeText(context, "返回input信息为空", Toast.LENGTH_SHORT).show();
                return;
            }
            Type type = new TypeToken<List<BlockchainUTXO>>() {
            }.getType();
            queredBlockchainUTXOList = new Gson().fromJson
                    (responseJson, type);

            if (queredBlockchainUTXOList == null ||
                    queredBlockchainUTXOList.isEmpty()) {
                Toast.makeText(context, "返回input信息解析为空", Toast.LENGTH_SHORT).show();
                return;
            }

            List<BitcoinTransactionInput> bitcoinTransactionInputList = new ArrayList<>();
            for (BlockchainUTXO blockchainUTXO : queredBlockchainUTXOList) {
                BitcoinTransactionInput bitcoinTransactionInput = new BitcoinTransactionInput();
                bitcoinTransactionInput.setAddress_n(blockchainUTXO.getAddressN());
                bitcoinTransactionInput.setAmountSatoshi(blockchainUTXO.getValue().toString());
                bitcoinTransactionInput.setAmount(blockchainUTXO.getAmountBtc());
                bitcoinTransactionInput.setPrev_hash(blockchainUTXO.getTxHashBigEndian());
                bitcoinTransactionInput.setPrev_index(blockchainUTXO.getTxOutputN());
                bitcoinTransactionInputList.add(bitcoinTransactionInput);
            }

            BitcoinTransactionRequest bitcoinTransactionRequest = new BitcoinTransactionRequest();
            bitcoinTransactionRequest.setInputList(bitcoinTransactionInputList);

            if (bitcoinTransactionRequest == null) {
                Toast.makeText(context, "返回input信息解析为空", Toast.LENGTH_SHORT).show();
                return;
            }
            targetBitcoinTransactionRequest = new BitcoinTransactionRequest();
            logger.debug("onActivityResult: bitcoinTransactionRequest:" + bitcoinTransactionRequest
                    .toString());
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_INPUT:
                    tvInput.setText("已选取付款方输入,点击查看详情");
                    targetBitcoinTransactionRequest.setInputList(bitcoinTransactionRequest
                            .getInputList());
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        hideKeyBoard();
    }

    void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
