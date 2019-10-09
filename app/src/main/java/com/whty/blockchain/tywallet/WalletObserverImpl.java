package com.whty.blockchain.tywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.blockchain.tyblockchainlib.api.core.WalletObserver;
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
import com.whty.blockchain.tywallet.util.BusinessIntent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

public class WalletObserverImpl implements WalletObserver {

    private WeakReference<Activity> mainActivity;

    private TextView resultView;

    //助记词
    private String word;

//    private final String TAG = this.getClass().getName();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public WalletObserverImpl(WeakReference<Activity> mainActivity) {
        this.mainActivity = mainActivity;

        if (this.mainActivity != null) {
            resultView = ((MainActivity) this.mainActivity.get()).findViewById(R.id.resultView);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BusinessIntent.WORD_GET_SUCCESS_INTENT);
        //注册广播接收者接收用户输入的助记词
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BusinessIntent.WORD_GET_SUCCESS_INTENT)) {
                    //获取单个助记词成功
                    word = intent.getStringExtra("word");
                }
            }
        };
        mainActivity.get().registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onWalletConnect(BluetoothDevice device, boolean result, ResponseCode responseCode) {
        if (resultView != null) {
            resultView.setText("设备" + device.getName() + "连接结果:" + result);
        }
    }

    @Override
    public void onWalletDisconnect(boolean result, ResponseCode responseCode) {
        if (resultView != null) {
//            String deviceName = (device == null ? "" : device.getName());
//            resultView.setText("设备" + deviceName + "断开连接结果:" + result);
        }
    }

    @Override
    public void onGetAddress(String addressN, CoinType coinType, GetAddressResponse
            getAddressResponse,
                             ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onExportPublicKey(String path, String publicKey, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onSignTx(BitcoinTransactionRequest bitcoinTransactionRequest, SignTxResponse
            signTxResponse, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onSignEthTx(EthTransactionInfoReq ethTransactionInfoReq, SignEthTxResponse
            signEthTxResponse, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onSignEthTokenTx(TokenTransactionInfoReq tokenTransactionInfoReq,
                                 SignEthTxResponse signEthTxResponse, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onGetDeviceFeature(GetDeviceFeatureResponse getDeviceFeatureResponse,
                                   ResponseCode responseCode) {
        if (resultView != null) {
            resultView.setText("device feature:" + getDeviceFeatureResponse);
        }
    }

    @Override
    public void onInputWords(ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onInputPin(int type, int resCode, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onInitialize(boolean result, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onCreateWallet(CreateWalletResponse createWalletResponse, ResponseCode
            responseCode) {

    }

    @Override
    public void onWipeDevice(WipeDeviceResponse wipeDeviceResponse, ResponseCode responseCode) {

    }

    @Override
    public void onRecoverWallet(RecoverWalletResponse recoverWalletResponse, ResponseCode
            responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onReset(boolean result, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onBackupWallet(BackupWalletResponse backupWalletResponse, ResponseCode
            responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onSetDeviceName(String deviceName, boolean result, ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public void onDeviceUpgradtion(String filePath, DeviceUpgradtionResponse
            deviceUpgradtionResponse,
                                   ResponseCode responseCode) {
        if (resultView != null) {

        }
    }

    @Override
    public synchronized String requestWord() {

        logger.debug("requestWord: ");
        word = null;
//        Intent intent = new Intent();
//        intent.setAction(BusinessIntent.REQUEST_WORD_INTENT);
//        mainActivity.get().getApplicationContext().sendBroadcast(intent);


        mainActivity.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    showRecoverWalletDialog();
                } catch (Exception e) {
                    logger.error("run: 异常", e);
                    e.printStackTrace();
                }
            }
        });
//        word = "test";
//        long start = System.currentTimeMillis();
        while (word == null) {
            //等待用户确定输入
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                logger.error("requestWord: ", e);
                break;
            }
//            long end = System.currentTimeMillis();
//            if (end - start >= 2000) {
//                break;
//            }
        }
//        word = "test";
        logger.debug("requestWord: word:" + word);
        return word;
    }


    void showRecoverWalletDialog() {

        View view = LayoutInflater.from(((MainActivity) mainActivity.get()).context).inflate(R
                .layout.common_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(((MainActivity) mainActivity.get())
                .context)
                .setView(view)
                .setCancelable(false);


        final AlertDialog recoverAlertDialog = builder.create();

        recoverAlertDialog.show();

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("根据设备提示输入信息");

        final EditText etContent0 = view.findViewById(R.id.et_content_0);
        etContent0.setVisibility(View.VISIBLE);

        Button confirmButton = view.findViewById(R.id.bt_oper_1);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setText("确定");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputWord = etContent0.getText().toString();
                if (inputWord == null) {
                    Toast.makeText(((MainActivity) mainActivity.get()).context, "输入内容为空", Toast
                            .LENGTH_SHORT).show();
                    return;
                }
                v.setEnabled(false);
                recoverAlertDialog.dismiss();
                word = inputWord;
            }
        });
    }
}
