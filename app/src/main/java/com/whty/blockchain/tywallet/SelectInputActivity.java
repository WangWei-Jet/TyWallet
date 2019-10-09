package com.whty.blockchain.tywallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.Gson;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXO;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;
import com.whty.blockchain.tybitcoinlib.model.Coin;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tywallet.blockchain.AdminClient;
import com.whty.blockchain.tywallet.util.AnimationUtil;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectInputActivity extends AppCompatActivity {

    @BindView(R.id.et_address_n)
    EditText etAddressN;
    @BindView(R.id.bt_address)
    Button btAddress;
    @BindView(R.id.tv_addresses)
    TextView tvAddresses;
    @BindView(R.id.bt_query_utxo)
    Button btQueryUtxo;
    @BindView(R.id.rv_utxo_list)
    RecyclerView rvUtxoList;
    @BindView(R.id.bt_select_utxo_list)
    Button btSelectUtxoList;

    private Context context;

    private String fromActivity = null;
//    private final String TAG = this.getClass().getName();

    private final int GET_UTXO_SUCCESS = 10;
    private final int GET_UTXO_FAIL = 11;
    private final int EXCEPTION = 99;

    Map<String, String> addressInfoMap = new HashMap<>();
    List<BlockchainUTXO> selectedBlockchainUTXOList = new ArrayList<>();
    BlockchainUTXOResponse blockchainUTXOResponse = null;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_input);
        ButterKnife.bind(this);

        logger.debug("onCreate: ");

        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle == null) {
                logger.debug("onNewIntent: bundle null");
            } else {
                logger.debug("onNewIntent: ");

                fromActivity = bundle.getString("from");

                logger.debug("onNewIntent: fromActivity:" + fromActivity);

            }
        }

        context = this;

        rvUtxoList.setLayoutManager(new LinearLayoutManager(context));
        rvUtxoList.setAdapter(adapter);

        //为地址索引输入框赋初始值
        switch (AdminClient.getCurrentBitcoinEnv()) {
            case Blockchain:
            case Insight:
                etAddressN.setText("m/49'/0'/0'/0/0;m/49'/0'/0'/0/1");
                break;

            case Blockchain_Testnet:
            case Insight_Testnet:
                etAddressN.setText("m/49'/1'/0'/0/0;m/49'/1'/0'/0/1");
                break;
        }

        //监听地址变化
        tvAddresses.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btQueryUtxo.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        logger.debug("onNewIntent: ");
    }

    @OnClick(R.id.bt_address)
    void getAddress() {
        logger.debug("getAddress: on click");
        if (!TyWalletFactory.getTyWalletInstance
                (context).isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressNListStr = etAddressN.getText().toString();
        if (addressNListStr == null || addressNListStr.trim().length() == 0) {
            Toast.makeText(context, "地址索引为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] addressNList = addressNListStr.split(";");
        logger.debug("getAddress: address n list size:" + addressNList.length);

        Map<String, String> currentAddressInfoMap = new HashMap<>();
        for (int i = 0; i < addressNList.length; i++) {
            String currentAddressN = addressNList[i];
            if (currentAddressN == null || currentAddressN.trim().length() == 0) {
                continue;
            }
            logger.debug("getAddress: addressN:" + currentAddressN);

            GetAddressResponse getAddressResponse = null;
            switch (AdminClient.getCurrentBitcoinEnv()) {
                case Insight_Testnet:
                case Blockchain_Testnet:
                case BlockCypher_Testnet:
                    getAddressResponse = TyWalletFactory.getTyWalletInstance
                            (context).getAddress(currentAddressN, CoinType.TESTNET);
                    break;

                case Insight:
                case Blockchain:
                    getAddressResponse = TyWalletFactory.getTyWalletInstance
                            (context).getAddress(currentAddressN, CoinType.BTC);
                    break;
            }

            if (getAddressResponse == null) {
                logger.debug("getAddress: 获取地址失败:" + currentAddressN);
                continue;
            }

            if (getAddressResponse.getAddress() == null) {
                logger.debug("getAddress: 获取地址失败:" + currentAddressN + ":" + getAddressResponse
                        .getDescription());
                continue;
            }
            if (getAddressResponse.getAddress().startsWith("0x")) {
                getAddressResponse.setAddress(getAddressResponse.getAddress().substring(2));
            }
            //address作为key，address作为value
            currentAddressInfoMap.put(getAddressResponse.getAddress(), currentAddressN);
        }
        if (currentAddressInfoMap.isEmpty()) {
            Toast.makeText(context, "未获取到地址信息", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> keySet = currentAddressInfoMap.keySet();
        for (String key : keySet) {
            stringBuilder.append("\n" + currentAddressInfoMap.get(key) + ":\n" + key + "\n");
        }
        tvAddresses.setVisibility(View.VISIBLE);
        tvAddresses.setText(stringBuilder.toString());

        addressInfoMap.clear();
        addressInfoMap.putAll(currentAddressInfoMap);

        Set<String> keySet1 = addressInfoMap.keySet();
        for (String key : keySet1) {
            logger.debug("addressN:" + addressInfoMap.get(key) + "\naddress:" + key);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logger.debug("onDestroy: ");
    }

    Handler mainHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            switch (msg.what) {
                case GET_UTXO_SUCCESS:
                    //将内容展示在recycleview中
                    notifyRecycleViewDataChanged();
                    break;

                case GET_UTXO_FAIL:

                    Toast.makeText(context, "未获取到UTXO", Toast.LENGTH_SHORT).show();

                    break;

                case EXCEPTION:
                    Toast.makeText(context, "网络请求异常", Toast.LENGTH_SHORT).show();
                    break;

                default:

                    break;
            }
        }
    };

    @OnClick(R.id.bt_select_utxo_list)
    void setInputList() {
        logger.debug("setInputList: on click");

        if (selectedBlockchainUTXOList.isEmpty()) {
            Toast.makeText(context, "未选中UTXO", Toast.LENGTH_SHORT).show();
            return;
        }

        String resJson = new Gson().toJson(selectedBlockchainUTXOList);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("input", resJson);
        intent.putExtras(bundle);
        setResult(1, intent);

        finish();
    }

    @OnClick(R.id.bt_query_utxo)
    void getUtxoList() {

        logger.debug("getUtxoList: on click");

        final LoadingDailog.Builder builder = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("正在查询UTXO\n请稍候...");
        final LoadingDailog loadingDailog = builder.create();
        loadingDailog.show();

        selectedBlockchainUTXOList.clear();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    List<String> addressList = new ArrayList<>();
                    addressList.addAll(addressInfoMap.keySet());
//                    addressList.add("3JYZpkXQnGa19b75d9AFqhjGdrkGwY2Sox");
//                    addressList.add("14KSfdXLoWMBZ8q2z2ZuAMMr3wcTTCoiCa");

                    if (blockchainUTXOResponse != null && blockchainUTXOResponse
                            .getUnspentOutputs() != null) {
                        blockchainUTXOResponse.getUnspentOutputs().clear();
                    }
                    for (String address : addressList) {
                        List<String> currentAddressList = new ArrayList<>();
                        currentAddressList.add(address);
                        final JSONObject jsonObject = AdminClient.getBitcoinExplorerAPI()
                                .getUnspentOutputs(currentAddressList);
                        if (jsonObject == null) {
                            continue;
                        }
                        logger.debug("getUTXOResponse: " + jsonObject.toString());
                        BlockchainUTXOResponse currentBlockchainUTXOResponse = new Gson().fromJson
                                (jsonObject.toString(),
                                        BlockchainUTXOResponse.class);
                        if (currentBlockchainUTXOResponse == null ||
                                currentBlockchainUTXOResponse.getUnspentOutputs() == null ||
                                currentBlockchainUTXOResponse.getUnspentOutputs().isEmpty()) {
                            continue;
                        }
                        for (int i = 0; i < currentBlockchainUTXOResponse.getUnspentOutputs()
                                .size(); i++) {
                            currentBlockchainUTXOResponse.getUnspentOutputs().get(i).setAddress
                                    (address);
                            currentBlockchainUTXOResponse.getUnspentOutputs().get(i)
                                    .setAddressN(addressInfoMap.get(address));
                        }

                        if (blockchainUTXOResponse == null) {
                            blockchainUTXOResponse = new BlockchainUTXOResponse();
                            blockchainUTXOResponse.setUnspentOutputs(new ArrayList<>());
                        }
                        blockchainUTXOResponse.getUnspentOutputs().addAll
                                (currentBlockchainUTXOResponse.getUnspentOutputs());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDailog.dismiss();
                        }
                    });
                    if (blockchainUTXOResponse == null || blockchainUTXOResponse
                            .getUnspentOutputs().isEmpty()) {
                        mainHandler.obtainMessage(GET_UTXO_FAIL)
                                .sendToTarget();
                        return;
                    }
                    mainHandler.obtainMessage(GET_UTXO_SUCCESS)
                            .sendToTarget();
                } catch (Exception e) {
                    logger.error("run: 异常", e);
                    if (loadingDailog.isShowing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDailog.dismiss();
                            }
                        });
                    }
                    mainHandler.obtainMessage(EXCEPTION).sendToTarget();
                }
            }
        }.start();
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
            if (blockchainUTXOResponse == null) {
                return;
            }
            BlockchainUTXO blockchainUTXO = blockchainUTXOResponse.getUnspentOutputs().get
                    (position);
            if (BitcoinTransferActivity.class.getName().equalsIgnoreCase(fromActivity)) {
                //从转账界面跳转而来
                ((OutputViewHolder) holder).getCb_chosen().setVisibility(View.VISIBLE);
            }
            ((OutputViewHolder) holder).getTv_address().setText("Address:\n" + blockchainUTXO
                    .getAddress());
            ((OutputViewHolder) holder).getTv_address_n().setText("AddressN:" + blockchainUTXO
                    .getAddressN());
            ((OutputViewHolder) holder).getTv_transaction_id().setText("Transaction Id:\n" +
                    blockchainUTXO
                            .getTxHashBigEndian());
            ((OutputViewHolder) holder).getTv_output_n().setText("Output Index:\n" + blockchainUTXO
                    .getTxOutputN());
            BigInteger amount = blockchainUTXO.getValue();
            Coin coin = new Coin(amount.longValue());
            ((OutputViewHolder) holder).getTv_amount().setText("Amount(satoshis):\n" +
                    blockchainUTXO
                            .getValue() + "=" + coin
                    .toPlainString() + " BTC");
            //设置比特币单位金额
            blockchainUTXO.setAmountBtc(Float.valueOf(coin.toPlainString()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((OutputViewHolder) holder).getCb_chosen().getVisibility() == View
                            .VISIBLE) {
                        boolean checkedState = ((OutputViewHolder) holder).getCb_chosen()
                                .isChecked();
                        if (checkedState) {
                            ((OutputViewHolder) holder).getCb_chosen().setChecked(false);
                        } else {
                            ((OutputViewHolder) holder).getCb_chosen().setChecked(true);
                        }
                    }
                }
            });

            ((OutputViewHolder) holder).getCb_chosen().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    logger.debug("onCheckedChanged: \nis button checked?=>" + isChecked +
                            "\nposition:" + position);
                    if (isChecked) {
                        selectedBlockchainUTXOList.add(blockchainUTXO);
                    } else {
                        selectedBlockchainUTXOList.remove(blockchainUTXO);
                    }
                    logger.debug("onCheckedChanged: selectedBlockchainUTXOList size:" +
                            selectedBlockchainUTXOList.size());
                    if (!selectedBlockchainUTXOList.isEmpty()) {
                        if (btSelectUtxoList.getVisibility() != View.VISIBLE) {
//                            btSelectUtxoList.setVisibility(View.VISIBLE);
                            AnimationUtil.with().bottomMoveToViewLocation(btSelectUtxoList, 500);
                        }
                    } else {
                        if (btSelectUtxoList.getVisibility() == View.VISIBLE) {
                            AnimationUtil.with().moveToViewBottom(btSelectUtxoList, 500);
//                            btSelectUtxoList.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (blockchainUTXOResponse == null) {
                return 0;
            }
            return blockchainUTXOResponse.getUnspentOutputs().size();
        }
    };

    private void notifyRecycleViewDataChanged() {

        adapter.notifyDataSetChanged();

    }

    class OutputViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_chosen;
        TextView tv_address;
        TextView tv_address_n;
        TextView tv_transaction_id;
        TextView tv_output_n;
        TextView tv_amount;

        public OutputViewHolder(View itemView) {
            super(itemView);
            this.cb_chosen = itemView.findViewById(R.id.cb_chosen);
            this.tv_address = itemView.findViewById(R.id.tv_input_address);
            this.tv_address_n = itemView.findViewById(R.id.tv_address_n);
            this.tv_transaction_id = itemView.findViewById(R.id.tv_transaction_id);
            this.tv_output_n = itemView.findViewById(R.id.tv_output_n);
            this.tv_amount = itemView.findViewById(R.id.tv_amount);
        }

        public TextView getTv_address_n() {
            return tv_address_n;
        }

        public CheckBox getCb_chosen() {
            return cb_chosen;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            return true;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
