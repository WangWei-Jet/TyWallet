package com.whty.blockchain.tywallet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.whty.blockchain.tybitcoinlib.api.BlockExplorerAPI;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.core.TyWallet;
import com.whty.blockchain.tyblockchainlib.api.core.WalletObserver;
import com.whty.blockchain.tyblockchainlib.api.entity.CreateWalletConfig;
import com.whty.blockchain.tyblockchainlib.api.entity.RecoverWalletConfig;
import com.whty.blockchain.tyblockchainlib.api.pojo.BackupWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.CreateWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetDeviceFeatureResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.RecoverWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.WipeDeviceResponse;
import com.whty.blockchain.tywallet.blockchain.AdminClient;
import com.whty.blockchain.tywallet.util.BlueToothDeviceReceiver;
import com.whty.blockchain.tywallet.util.DeviceDialogUtil;
import com.whty.blockchain.tywallet.util.ENV;
import com.whty.blockchain.tywallet.util.SharedMSG;
import com.whty.blockchain.tywallet.util.UIMessage;
import com.whty.bluetooth.manage.util.BlueToothUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final int EX_FILE_PICKER_RESULT = 99;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static DialogHandler dialogHandler;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE", Manifest.permission
            .ACCESS_COARSE_LOCATION};
    @BindView(R.id.scanDevice)
    Button scanDevice;
    @BindView(R.id.connect)
    Button connect;
    @BindView(R.id.disconnect)
    Button disconnect;
    @BindView(R.id.bt_deviceInfo)
    Button btDeviceInfo;
    @BindView(R.id.getBalance)
    Button getBalance;
    @BindView(R.id.transfer)
    Button transfer;
    @BindView(R.id.resultView)
    TextView showResult;
    Context context;
    @BindView(R.id.bt_create_wallet)
    Button btCreateWallet;
    @BindView(R.id.bt_backup_wallet)
    Button btBackupWallet;
    @BindView(R.id.bt_recover_wallet)
    Button btRecoverWallet;
    @BindView(R.id.bt_wipe_wallet)
    Button btWipeWallet;
    @BindView(R.id.tv_env)
    TextView tvEnv;
    @BindView(R.id.bt_env)
    Button btEnv;
    @BindView(R.id.bt_select_file)
    Button btSelectFile;
    @BindView(R.id.bt_bitcoin_test)
    Button btBitcoinTest;
    @BindView(R.id.tv_env_bitcoin)
    TextView tvEnvBitcoin;
    @BindView(R.id.bt_env_bitcoin)
    Button btEnvBitcoin;
    @BindView(R.id.bt_bitcoin_query_utxo)
    Button btBitcoinQueryUtxo;
    @BindView(R.id.bt_bitcoin_push_tx)
    Button btBitcoinPushTx;
    TyWallet tyWallet;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private BroadcastReceiver receiver = null;
    private DeviceDialogUtil devicedialog = null;
    private BluetoothDevice currentDevice;
    private String mDeviceAddress;
    private String mDeviceName;
    private boolean deviceConnected = false;
    private int languageID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger.debug("onCreate: ");
        context = this;
        ButterKnife.bind(this);
        registerBtReceiver();
        UIMessage.setMessage(languageID);

        mayReqPermission();

        ENV env = AdminClient.getCurrentEnv();
        switch (env) {
            case ETHEREUM:
                tvEnv.setText("当前环境:以太坊主链");
                break;
            case TESTNET:
                tvEnv.setText("当前环境:以太坊测试链");
                break;
        }

        tvEnvBitcoin.setText("当前环境:比特币测试链");


        WalletObserver walletObserver = new WalletObserverImpl(new WeakReference<Activity>(this));
        tyWallet = TyWalletFactory.getTyWalletInstance(context, walletObserver);


    }

    private void mayReqPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission
                        .ACCESS_COARSE_LOCATION);
        List<String> permissionList = new ArrayList<>();

        if (permissionState != PackageManager.PERMISSION_GRANTED) {

            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        //检测是否有写的权限
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionList.add("android.permission.READ_EXTERNAL_STORAGE");
            permissionList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (!permissionList.isEmpty()) {
            String[] reqPermissions = permissionList.toArray(new String[permissionList.size()]);
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, reqPermissions, 0);
        }
    }

    @Override
    protected void onResume() {
        logger.debug("onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        logger.debug("onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        logger.debug("onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        logger.debug("onDestroy: ");
        super.onDestroy();
        try {
            tyWallet.disconnectWallet();
        } catch (Exception e) {
            logger.error("onDestroy: 异常", e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.common_dialog,
                    null);

            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(view).create();
            alertDialog.show();

            TextView tvTitle = view.findViewById(R.id.tv_title);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText("退出");

            TextView tvContent = view.findViewById(R.id.tv_content_0);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setTextSize(16);
            tvContent.setTextColor(Color.RED);
            tvContent.setText("确定退出应用");

            Button cancelButton = view.findViewById(R.id.bt_oper_0);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("取消");


            Button confirmButton = view.findViewById(R.id.bt_oper_1);
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setText("确定");

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
                    finish();
                    System.exit(0);
                }
            });

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    synchronized void registerBtReceiver() {

        // handler用于跟UI的交互
        dialogHandler = new DialogHandler();
        devicedialog = new DeviceDialogUtil(dialogHandler);

        // 广播接收者接收监听蓝牙状态，然后将需要的信息由Hanlder放到队列以便更新UI使用
        receiver = new BlueToothDeviceReceiver(dialogHandler);
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intent.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

        context.registerReceiver(receiver, intent);
    }

    @OnClick(R.id.bt_env)
    void switchEnv() {
        logger.debug("switchEnv: on click");

        final String[] envs = new String[]{"以太坊测试链", "以太坊主链"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("选择区块链")
                .setItems(envs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logger.debug("env:" + envs[which]);
                        tvEnv.setText("当前环境:" + envs[which]);
                        if (which == 0) {
                            //测试链
                            AdminClient.switchEnv(ENV.TESTNET);
                        } else if (which == 1) {
                            //主链
                            AdminClient.switchEnv(ENV.ETHEREUM);
                        }
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @OnClick(R.id.bt_env_bitcoin)
    void switchBitcoinEnv() {
        logger.debug("switchBitcoinEnv: on click");

        final String[] envs = new String[]{"比特币测试链", "比特币主链"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("选择区块链")
                .setItems(envs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logger.debug("env:" + envs[which]);
                        tvEnvBitcoin.setText("当前环境:" + envs[which]);
                        if (which == 0) {
                            //测试链
                            AdminClient.switchBitcoinEnv(BlockExplorerAPI.BlockExplorer
                                    .Blockchain_Testnet);
                        } else if (which == 1) {
                            //主链
                            AdminClient.switchBitcoinEnv(BlockExplorerAPI.BlockExplorer.Blockchain);
                        }
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @OnClick(R.id.scanDevice)
    void scanDevice() {
        logger.debug("scanDevice: on click");
        try {
            if (!deviceConnected) {
                mDeviceAddress = null;
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
                if (BlueToothUtil.mDialog != null) {
                    BlueToothUtil.mDialog = null;
                }
                BlueToothDeviceReceiver.items.clear();
            } else {
                Toast.makeText(MainActivity.this,
                        UIMessage.connected_device, Toast.LENGTH_SHORT)
                        .show();
            }

        } catch (Exception e) {
            logger.error("scanDevice: 异常", e);
        }
    }

    @OnClick(R.id.connect)
    void connectDevice() {
        logger.debug("connectDevice: on click");
        try {
            if (showResult.getText().toString()
                    .equals(UIMessage.connecting_device)) {
                Toast.makeText(MainActivity.this,
                        UIMessage.connecting_device, Toast.LENGTH_SHORT)
                        .show();
            } else {
                if (deviceConnected) {
                    Toast.makeText(MainActivity.this,
                            UIMessage.connected_device + " " + mDeviceName,
                            Toast.LENGTH_SHORT).show();
                } else {
                    showResult.setText(UIMessage.connecting_device);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    if (mDeviceAddress != null
                            && mDeviceAddress.length() > 0) {
//                        tyWallet.connectWallet(currentDevice);
                        new Thread() {
                            public void run() {
                                final boolean result = tyWallet.connectWallet(currentDevice);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showResult.setText("设备" + currentDevice.getName() +
                                                "连接结果:" + result);
                                        logger.debug("设备连接成功!!!!!!");
                                    }
                                });
                            }
                        }.start();
                    } else {
                        showResult.setText(UIMessage.donot_select_device);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("connectDevice: 异常", e);
        }
    }

    @OnClick(R.id.disconnect)
    void disconnectDevice() {
        logger.debug("disconnectDevice: on click");
        try {
//            tyWallet.disconnectWallet(null);

            new Thread() {
                public void run() {
                    final boolean result = tyWallet.disconnectWallet();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            showResult.setText("断开连接结果:" + result);
                            showResult.setText("设备" + currentDevice.getName() + "断开连接结果:" + result);
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            logger.error("disconnectDevice: 异常", e);
        }
    }

    @OnClick(R.id.bt_create_wallet)
    void createWallet() {
        logger.debug("createWallet: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        showCreateWalletDialog();
    }

    @OnClick(R.id.bt_wipe_wallet)
    void wipeWallet() {
        logger.debug("wipeWallet: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("正在擦除钱包");
        final LoadingDailog loadingDailog = builder1.create();
        final TextView msgView = loadingDailog.findViewById(R.id.tipTextView);
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgView.setText("请在设备上确认");
                    }
                });
                final WipeDeviceResponse wipeDeviceResponse = tyWallet.wipeDevice();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String tipInfo;
                        if (wipeDeviceResponse == null) {
                            tipInfo = "擦除设备失败";
                        } else {
                            if (wipeDeviceResponse.isOperationSuccess()) {
                                tipInfo = "擦除设备成功";
                            } else {
                                tipInfo = "擦除设备失败\ndescription code:" + wipeDeviceResponse
                                        .getDescriptionCode() + "\ndescription:" +
                                        wipeDeviceResponse
                                                .getDescription();
                            }
                        }
                        showResult.setText(tipInfo);

                        loadingDailog.dismiss();
                    }
                });
            }
        }.start();
    }

    @OnClick(R.id.bt_backup_wallet)
    void backupWallet() {
        logger.debug("backupWallet: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("备份钱包!请在设备上操作");
        final LoadingDailog loadingDailog = builder1.create();
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                final BackupWalletResponse backupWalletResponse = tyWallet.backupWallet();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String tipInfo;
                        if (backupWalletResponse == null) {
                            tipInfo = "备份钱包失败";
                        } else {
                            if (backupWalletResponse.isOperationSuccess()) {
                                tipInfo = "备份钱包成功";
                            } else {
                                tipInfo = "备份钱包失败\ndescription code:" + backupWalletResponse
                                        .getDescriptionCode() + "\ndescription:" +
                                        backupWalletResponse
                                                .getDescription();
                            }
                        }
                        showResult.setText(tipInfo);
                        loadingDailog.dismiss();
                    }
                });
            }
        }.start();
    }

    void showCreateWalletDialog() {

        View view = LayoutInflater.from(context).inflate(R.layout.common_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("输入钱包名称");

        final EditText etContent0 = view.findViewById(R.id.et_content_0);
        etContent0.setVisibility(View.VISIBLE);

        Button cancelButton = view.findViewById(R.id.bt_oper_0);
        cancelButton.setVisibility(View.VISIBLE);
        cancelButton.setText("取消");
        Button confirmButton = view.findViewById(R.id.bt_oper_1);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setText("确定");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String walletName = etContent0.getText().toString();
                if (walletName == null) {
                    Toast.makeText(context, "钱包名称为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadingDailog.Builder builder1 = new LoadingDailog.Builder(context)
                        .setCancelable(false)
                        .setCancelOutside(false)
                        .setMessage("正在创建钱包");
                final LoadingDailog loadingDailog = builder1.create();
                loadingDailog.show();

                try {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
//                            WalletProperties walletProperties = new WalletProperties();
                            CreateWalletConfig createWalletConfig = new CreateWalletConfig();
                            createWalletConfig.setName(walletName);
//                            walletProperties.setWalletName(walletName);
                            final CreateWalletResponse createWalletResponse = tyWallet
                                    .createWallet(createWalletConfig);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String tipInfo;
                                    if (createWalletResponse == null) {
                                        tipInfo = "创建钱包失败";
                                    } else {
                                        if (createWalletResponse.isOperationSuccess()) {
                                            tipInfo = "创建钱包成功";
                                        } else {
                                            tipInfo = "创建钱包失败\ndescription code:" +
                                                    createWalletResponse.getDescriptionCode() +
                                                    "\ndescription:" +
                                                    createWalletResponse.getDescription();
                                        }
                                    }
//                                    Toast.makeText(context, tipInfo, Toast.LENGTH_SHORT).show();
                                    showResult.setText(tipInfo);
                                    loadingDailog.dismiss();
                                }
                            });
                        }
                    }.start();
                } catch (Exception e) {
                    logger.error("getAccountInfo: 异常", e);
                }
                alertDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.bt_recover_wallet)
    void recoverWallet() {
        logger.debug("recoverWallet: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] wordCountList = new String[]{"12", "18", "24"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("选择助记词个数")
                .setItems(wordCountList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String targetWordCount = wordCountList[which];
                        logger.debug("word count:" + targetWordCount);
                        dialog.dismiss();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                RecoverWalletConfig recoverWalletConfig = new RecoverWalletConfig();
                                recoverWalletConfig.setWord_count(Integer.valueOf(targetWordCount));
                                logger.debug("recoverWalletConfig:" + recoverWalletConfig);
                                final RecoverWalletResponse recoverWalletResponse = tyWallet.recoverWallet
                                        (recoverWalletConfig);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tipInfo;
                                        if (recoverWalletResponse == null) {
                                            tipInfo = "恢复钱包失败";
                                        } else {
                                            if (recoverWalletResponse.isOperationSuccess()) {
                                                tipInfo = "恢复钱包成功";
                                            } else {
                                                tipInfo = "恢复钱包失败\ndescription code:" + recoverWalletResponse
                                                        .getDescriptionCode() + "\ndescription:" +
                                                        recoverWalletResponse
                                                                .getDescription();
                                            }
                                        }
                                        showResult.setText(tipInfo);
                                    }
                                });
                            }
                        }.start();
                    }
                });

        builder.create().show();

    }

    @OnClick(R.id.bt_deviceInfo)
    void getAccountInfo() {
        logger.debug("getAccountInfo: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    final GetDeviceFeatureResponse getDeviceFeatureResponse = tyWallet
                            .getDeviceFeature();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getDeviceFeatureResponse == null) {
//                                Toast.makeText(context, "获取设备信息失败", Toast.LENGTH_SHORT).show();
                                showResult.setText("获取设备信息失败");
                                return;
                            }
                            if (getDeviceFeatureResponse.getDeviceFeature() == null) {
                                showResult.setText("获取设备信息失败\ndescription code:" +
                                        getDeviceFeatureResponse.getDescriptionCode() +
                                        "\ndescription:" +
                                        getDeviceFeatureResponse.getDescription());
                                return;
                            }
//                            Toast.makeText(context, accountInfo, Toast.LENGTH_SHORT).show();
                            showResult.setText("设备信息:" + getDeviceFeatureResponse
                                    .getDeviceFeature() + "\n SN:" + getDeviceFeatureResponse.getDeviceSN()
                                    + "\n PN:" + getDeviceFeatureResponse.getDevicePN()
                                    + "\n 电量:" + getDeviceFeatureResponse.getDeviceBattery()
                                    + "\n 版本号:" + getDeviceFeatureResponse.getDeviceVer());
                        }

                    });
                }
            }.start();
        } catch (Exception e) {
            logger.error("getAccountInfo: 异常", e);
        }
    }

    @OnClick(R.id.getBalance)
    void getBalance() {
        logger.debug("getBalance: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //界面跳转
            startActivity(new Intent(context, GetBalanceActivity.class));
        } catch (Exception e) {
            logger.error("getBalance: 异常", e);
        }
    }

    @OnClick(R.id.transfer)
    void transfer() {
        logger.debug("transfer: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        //跳转到转账界面
        startActivity(new Intent(context, TransferActivity.class));
    }

    @OnClick(R.id.bt_bitcoin_query_utxo)
    void getBitcoinUTXO() {
        logger.debug("getBitcoinUTXO: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, SelectInputActivity.class));
    }

    @OnClick(R.id.bt_bitcoin_push_tx)
    void pushBitcoinTx() {
        logger.debug("pushBitcoinTx: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, BitcoinTransferActivity.class));
    }

    @OnClick(R.id.bt_select_file)
    void selectFile() {
        logger.debug("selectFile: on click");
        if (!tyWallet.isWalletConnected()) {
            Toast.makeText(context, "当前无连接", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(this, WalletUpgradeActivity.class));

    }


    class DialogHandler extends Handler {

        @Override
        public void dispatchMessage(Message msg) {

            super.dispatchMessage(msg);

            switch (msg.what) {
                // 收到系统发现设备的广播，传给handler处理，弹出dialog
                case SharedMSG.No_Device_Selected:
                    Toast.makeText(MainActivity.this,
                            UIMessage.donot_select_device, Toast.LENGTH_SHORT)
                            .show();
                    break;

                // 搜索到蓝牙设备
                case SharedMSG.Device_Found:
                    if (mDeviceAddress == null || mDeviceAddress.length() <= 0) {
                        devicedialog.listDevice(MainActivity.this);
                    }
                    break;

                case SharedMSG.No_Device:
                    Toast.makeText(MainActivity.this,
                            UIMessage.donot_connect_device, Toast.LENGTH_SHORT)
                            .show();
                    break;

                // 选中蓝牙设备
                case SharedMSG.Device_Ensured:
                    currentDevice = (BluetoothDevice) msg.obj;
                    mDeviceName = currentDevice.getName();
                    mDeviceAddress = currentDevice.getAddress();
                    showResult.setText(UIMessage.selected_device + " "
                            + mDeviceName);
                    break;

                // 蓝牙断开连接
                case SharedMSG.Device_Disconnected:
                    deviceConnected = false;
                    break;
            }
        }
    }
}
