package com.whty.blockchain.wallet.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.whty.blockchain.bottomdialog.widget.AlertDialog;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXO;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;
import com.whty.blockchain.tybitcoinlib.model.Coin;
import com.whty.blockchain.wallet.R;
import com.whty.blockchain.wallet.utils.zxing.activity.CaptureActivity;
import com.whty.blockchain.wallet.view.IQueryFragmentView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class QueryFragment extends Fragment implements IQueryFragmentView {

    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.bt_scan_address)
    Button btScanAddress;
    @BindView(R.id.bt_import_address)
    Button btImportAddress;
    @BindView(R.id.bt_query_balance)
    Button btQueryBalance;
    Unbinder unbinder;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    LoadingDailog loadingDailog;

    private MainActivity parentActivity;

    private final int REQUEST_CODE_QR_SCAN = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        logger.info("onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.info("onCreate");

        parentActivity = (MainActivity) this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        logger.info("onCreateView");
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        logger.info("onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.info("onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logger.info("onDestroyView");
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        logger.info("onDetach");
    }

    @OnClick(R.id.bt_query_balance)
    void getBalance() {
        logger.debug("get balance");
        parentActivity.getBrowserAPIPresentor().getBalance(getAccountAddress());
    }

    String getAccountAddress() {
        return etAddress.getText().toString();
    }

    void setAccountAddress(String accountAddress) {
        etAddress.setText(accountAddress);
    }

    @OnClick(R.id.bt_scan_address)
    void doQRCodeScan() {
        logger.info("doQRCodeScan");

        Intent intent = new Intent(parentActivity, CaptureActivity.class);

        startActivityForResult(intent, REQUEST_CODE_QR_SCAN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        logger.info("onActivityResult");
        if (intent != null) {
            String result = intent.getStringExtra("result");
            logger.debug("result:" + result);
            setAccountAddress(result);
        }
    }

    @Override
    public void showLoadingDialog() {
        logger.debug("showLoadingDialog");
        if (loadingDailog == null || !loadingDailog.isShowing()) {
            LoadingDailog.Builder builder = new LoadingDailog.Builder(parentActivity);
            builder.setCancelable(false).setCancelOutside(false).setMessage("");
            loadingDailog = builder.create();
        }
        final TextView msgView = loadingDailog.findViewById(R.id.tipTextView);
        msgView.setText("请稍后...");
        loadingDailog.show();
    }

    @Override
    public void showLoadingDialogWithContentMsg(String contentMsg) {
        logger.debug("showLoadingDialogWithContentMsg:" + contentMsg);
        if (loadingDailog == null || !loadingDailog.isShowing()) {
            LoadingDailog.Builder builder = new LoadingDailog.Builder(parentActivity);
            builder.setCancelable(false).setCancelOutside(false).setMessage("");
            loadingDailog = builder.create();
        }
        final TextView msgView = loadingDailog.findViewById(R.id.tipTextView);
        msgView.setText(contentMsg);
        loadingDailog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        logger.debug("dismissLoadingDialog");
        if (loadingDailog != null && loadingDailog.isShowing()) {
            loadingDailog.dismiss();
            loadingDailog = null;
        }
    }

    @Override
    public void onError(String errorMsg) {
        //自定义的alertdialog
        new AlertDialog(parentActivity)
                .builder()
                .setTitle("获取余额失败")
                .setMsg(errorMsg)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //填写事件
                    }
                }).show();
    }

    @Override
    public void showEthereumEnvironmentBalanceResult(BigInteger balance) {
        logger.debug("balance:" + balance);
        BigDecimal balanceDecimal = Convert.fromWei(new BigDecimal(balance)
                , Convert.Unit.ETHER);
        logger.debug("balance:" + balanceDecimal + "ETH");
        //自定义的alertdialog
        new AlertDialog(parentActivity)
                .builder()
                .setTitle("账户余额")
                .setMsg(balanceDecimal + " ETH")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //填写事件
                    }
                }).show();
    }

    @Override
    public void showBitcoinEnvironmentBalanceResult(BlockchainUTXOResponse blockchainUTXOResponse) {
        logger.debug("showBitcoinEnvironmentBalanceResult");
        if (blockchainUTXOResponse == null || blockchainUTXOResponse.getUnspentOutputs() == null
                || blockchainUTXOResponse.getUnspentOutputs().isEmpty()) {
            logger.debug("no utxo found");
            //自定义的alertdialog
            new AlertDialog(parentActivity)
                    .builder()
                    .setTitle("UTXO信息")
                    .setMsg("no utxo found")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //填写事件
                        }
                    }).show();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (BlockchainUTXO blockchainUTXO : blockchainUTXOResponse.getUnspentOutputs()) {
            String transactionId = blockchainUTXO.getTxHashBigEndian();
            String outputIndex = blockchainUTXO.getTxOutputN();
            BigInteger value = blockchainUTXO.getValue();
            Coin coin = new Coin(value.longValue());
            String amountBtc = coin.toPlainString();
            long confirmations = blockchainUTXO.getConfirmations();
            stringBuilder.append("\nTransactionId:\n" + transactionId
                    + "\t\nOutputIndex:\t\n" + outputIndex + "\t\nAmount:\t\n" + amountBtc +
                    "\t\nConfirmations:\t\n" + confirmations+"\n");
        }
        //自定义的alertdialog
        AlertDialog alertDialog = new AlertDialog(parentActivity)
                .builder();
        alertDialog.getTxt_msg().setGravity(Gravity.LEFT);
        alertDialog.setTitle("UTXO信息")
                .setMsg(stringBuilder.toString())
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //填写事件
                    }
                }).show();
    }
}
