package com.whty.blockchain.tywallet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.pojo.DeviceUpgradtionResponse;
import com.whty.blockchain.tyblockchainlib.api.util.UpgradeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class WalletUpgradeActivity extends AppCompatActivity {

    private static final int EX_FILE_PICKER_RESULT = 99;
    @BindView(R.id.et_upgrade_file_path)
    EditText etUpgradeFilePath;
    @BindView(R.id.bt_select_file)
    Button btSelectFile;
    @BindView(R.id.bt_wallet_upgrade)
    Button btWalletUpgrade;
    @BindView(R.id.tv_result)
    TextView tvResult;

    private LoadingDailog loadingDailog;

    private MyHandler handler = new MyHandler();

    private UpgradeListener upgradeListener;

    private Context context;

//    private final String TAG = this.getClass().getName();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_upgrade);
        ButterKnife.bind(this);

        context = this;

        upgradeListener = new UpgradeListenerImpl(handler);
    }

    @OnClick(R.id.bt_select_file)
    void selectFile() {
        logger.debug("selectFile: on click");

        ExFilePicker exFilePicker = new ExFilePicker();
        exFilePicker.setCanChooseOnlyOneItem(true);// 单选
        exFilePicker.setQuitButtonEnabled(true);

        exFilePicker.setStartDirectory(Environment.getExternalStorageDirectory().getPath());

        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
        exFilePicker.start(this, EX_FILE_PICKER_RESULT);
    }

    @OnClick(R.id.bt_wallet_upgrade)
    void upgradeWallet() {
        logger.debug("upgradeWallet: on click");
        if(!TyWalletFactory.getTyWalletInstance
                (context).isWalletConnected()){
            Toast.makeText(context,"当前无连接",Toast.LENGTH_SHORT).show();
            return;
        }
        final String filePath = etUpgradeFilePath.getText().toString();
        if (filePath == null || filePath.trim().length() == 0) {
            logger.warn("upgradeWallet: no wallet upgrade file found");
            return;
        }
        if (loadingDailog == null) {
            LoadingDailog.Builder builder = new LoadingDailog.Builder(context)
                    .setCancelable(false)
                    .setCancelOutside(false)
                    .setMessage("正在升级钱包");
            loadingDailog = builder.create();
        }
        loadingDailog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                final long start = System.currentTimeMillis();
                final DeviceUpgradtionResponse deviceUpgradtionResponse = TyWalletFactory
                        .getTyWalletInstance(context)
                        .upgradeDevice(filePath, upgradeListener);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String tipInfo;
                        if (deviceUpgradtionResponse == null) {
                            tipInfo = "设备升级失败";
                        } else {
                            if (deviceUpgradtionResponse.isOperationSuccess()) {
                                tipInfo = "设备升级成功";
                            } else {
                                tipInfo = "设备升级失败:" + deviceUpgradtionResponse
                                        .getDescription();
                            }
                        }
                        long end = System.currentTimeMillis();
                        tvResult.setText(tipInfo + "\n耗时:" + ((end - start) / 1000) + "秒");
                    }
                });
            }
        }.start();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            TextView msgView = loadingDailog.findViewById(R.id.tipTextView);

            switch (msg.what) {
                case UpgradeListenerImpl.UPGRADE_SUCCESS:
                    //更新成功
                    loadingDailog.dismiss();
                    tvResult.setText("钱包更新成功");
                    break;

                case UpgradeListenerImpl.UPGRADE_FAIL:
                    //更新失败
                    loadingDailog.dismiss();
                    tvResult.setText("钱包更新失败");
                    break;

                case UpgradeListenerImpl.UPGRADE_PROCESS_UPDATE:
                    //进度更新
                    int process = ((Integer) msg.obj).intValue();
                    msgView.setText("进度:" + process + "%");
                    break;
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EX_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                String path = result.getPath();

                List<String> names = result.getNames();
                for (int i = 0; i < names.size(); i++) {
                    File f = new File(path, names.get(i));
                    try {
                        Uri uri = Uri.fromFile(f); //这里获取了真实可用的文件资源
                        Toast.makeText(context, "选择文件:" + uri.getPath(), Toast.LENGTH_SHORT)
                                .show();
                        tvResult.setText("选择了文件:" + uri.getPath());
                        etUpgradeFilePath.setText(uri.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
