package com.whty.blockchain.wallettestapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.whty.blockchain.tyblockchainlib.api.TyWalletFactory;
import com.whty.blockchain.tyblockchainlib.api.entity.CoinType;
import com.whty.blockchain.tyblockchainlib.api.entity.CreateWalletConfig;
import com.whty.blockchain.tyblockchainlib.api.pojo.BackupWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.CreateWalletResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetAddressResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.GetDeviceFeatureResponse;
import com.whty.blockchain.tyblockchainlib.api.pojo.WipeDeviceResponse;
import com.whty.blockchain.wallettestapplication.entity.TestConfig;
import com.whty.blockchain.wallettestapplication.entity.TestDetail;
import com.whty.blockchain.wallettestapplication.entity.TestType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestTaskActivity extends AppCompatActivity {

    @BindView(R.id.bt_popupwindow)
    Button btPopupwindow;
    @BindView(R.id.et_target_api)
    EditText etTargetApi;
    @BindView(R.id.bt_target_api)
    Button btTargetApi;
    @BindView(R.id.et_test_duration)
    EditText etTestDuration;
    @BindView(R.id.bt_reset_config)
    Button btResetConfig;
    @BindView(R.id.bt_confirm_config)
    Button btConfirmConfig;
    @BindView(R.id.bt_more_config)
    Button btMoreConfig;
    @BindView(R.id.tv_config_info)
    TextView tvConfigInfo;
    @BindView(R.id.tv_test_info)
    TextView tvTestInfo;
    @BindView(R.id.bt_launch_test)
    Button btLaunchTest;

    private Context context;

    private TestConfig testConfig;

    private final List<TestDetail> testApiList = new ArrayList<>();

    private TestDetail selectedTestDetail;

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_task);
        ButterKnife.bind(this);

        context = this;

        initData();
    }

    private void initData() {

        testApiList.clear();

        TestDetail getAddressTestDetail = new TestDetail(TestType.GET_ADDRESS, "获取地址");
        TestDetail getFeatureTestDetail = new TestDetail(TestType.GET_FEATURE, "获取设备信息");
        TestDetail createWalletTestDetail = new TestDetail(TestType.CREATE_WALLET, "创建钱包");
        TestDetail wipeWalletTestDetail = new TestDetail(TestType.WIPE_WALLET, "擦除钱包");
        TestDetail backupWalletTestDetail = new TestDetail(TestType.BACKUP_WALLET, "备份钱包");
        TestDetail recoverWalletTestDetail = new TestDetail(TestType.RECOVER_WALLET, "恢复钱包");

        testApiList.add(getAddressTestDetail);
        testApiList.add(getFeatureTestDetail);
        testApiList.add(createWalletTestDetail);
        testApiList.add(wipeWalletTestDetail);
        testApiList.add(backupWalletTestDetail);
        testApiList.add(recoverWalletTestDetail);
    }

    @OnClick(R.id.bt_popupwindow)
    void popupWindow() {
        new ConfirmPopupWindow(context).showAtBottom(btPopupwindow);
    }

    @OnClick(R.id.bt_target_api)
    void chooseTargetApi() {
        Log.d(TAG, "chooseTargetApi: on click");


        ListAdapter listAdapter = new ArrayAdapter<TestDetail>(context, android.R.layout
                .select_dialog_singlechoice, testApiList);

        AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true)
                .setTitle("选择待测试接口")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TestDetail target = testApiList.get(which);
                        selectedTestDetail = target;
                        etTargetApi.setText(selectedTestDetail.getDescription());
                        Log.d(TAG, "onClick: target test api:" + selectedTestDetail
                                .getDescription());
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.show();
    }

    @OnClick(R.id.bt_reset_config)
    void resetConfig() {
        Log.d(TAG, "resetConfig: on click");

        etTargetApi.setText("");
        etTestDuration.setText("");
    }

    @OnClick(R.id.bt_confirm_config)
    void confirmConfig() {
        Log.d(TAG, "confirmConfig: on click");
        float testDuration = 0;
        String targetApiStr = etTargetApi.getText().toString();
        if (targetApiStr == null || targetApiStr.trim().length() == 0) {
            Toast.makeText(context, "目标测试接口为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTestDetail == null || selectedTestDetail.getDescription() == null) {
            Toast.makeText(context, "获取测试接口失败", Toast.LENGTH_SHORT).show();
            return;
        }
        String testDurationStr = etTestDuration.getText().toString();
        if (testDurationStr == null || testDurationStr.trim().length() == 0) {
            Toast.makeText(context, "测试时间为空", Toast.LENGTH_SHORT).show();
            return;
        }
        testDuration = Float.valueOf(testDurationStr);
        if (testDuration <= 0) {
            Toast.makeText(context, "测试时间小于等于0", Toast.LENGTH_SHORT).show();
            return;
        }

        testConfig = new TestConfig();
        testConfig.setTestDetail(selectedTestDetail);
        testConfig.setTestDuration(testDuration);

        String configInfo = "目标方法:" + testConfig.getTestDetail().getDescription()
                + "\n计划测试时间:" + testConfig.getTestDuration() + "分钟";
        tvConfigInfo.setText(configInfo);

    }

    @OnClick({R.id.bt_more_config})
    void moreConfig() {
        Log.d(TAG, "moreConfig: on click");
    }

    @OnClick(R.id.bt_launch_test)
    void launchTest() {
        Log.d(TAG, "launchTest: on click");

        if (testConfig == null) {
            Toast.makeText(context, "未发现测试计划", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "launchTest: \n测试计划:" + "\n测试接口:" + testConfig.getTestDetail().getDescription
                () + "\n计划测试时间:" + testConfig.getTestDuration() + "分钟");

        LoadingDailog.Builder builder = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setCancelOutside(false)
                .setMessage("正在测试");
        final LoadingDailog loadingDailog = builder.create();
        loadingDailog.show();

        switch (testConfig.getTestDetail().getTestType()) {
            case GET_ADDRESS:

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            long startTime = System.currentTimeMillis();
                            long currentTime = startTime;
                            int addressStartIndex = 0;
                            while (currentTime - startTime < testConfig.getTestDuration() * 60 *
                                    1000) {
                                //input
                                final String addressN = "m/44'/60'/0'/0/" + addressStartIndex;
                                //output
                                final GetAddressResponse getAddressResponse = TyWalletFactory.getTyWalletInstance
                                        (context).getAddress(addressN, CoinType.ETH);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String testInfo = "输入地址索引:" + addressN + "\n获取到的地址:" +
                                                getAddressResponse;
                                        String exitedInfo = tvTestInfo.getText().toString();
                                        tvTestInfo.setText(exitedInfo == null ? testInfo :
                                                (exitedInfo + "\n" + testInfo));
                                    }
                                });
                                currentTime = System.currentTimeMillis();
                            }
                            Log.d(TAG, "run: test over");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "测试完成";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "run: 异常", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "异常，测试终止";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } finally {
                            loadingDailog.dismiss();
                        }
                    }
                }.start();
                break;
            case GET_FEATURE:

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            long startTime = System.currentTimeMillis();
                            long currentTime = startTime;
                            while (currentTime - startTime < testConfig.getTestDuration() * 60 *
                                    1000) {
                                //output
                                final GetDeviceFeatureResponse getDeviceFeatureResponse = TyWalletFactory.getTyWalletInstance
                                        (context).getDeviceFeature();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String testInfo = "设备信息:"+getDeviceFeatureResponse;
                                        String exitedInfo = tvTestInfo.getText().toString();
                                        tvTestInfo.setText(exitedInfo == null ? testInfo :
                                                (exitedInfo + "\n" + testInfo));
                                    }
                                });
                                currentTime = System.currentTimeMillis();
                            }
                            Log.d(TAG, "run: test over");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "测试完成";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "run: 异常", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "异常，测试终止";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } finally {
                            loadingDailog.dismiss();
                        }
                    }
                }.start();
                break;
            case CREATE_WALLET:

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            long startTime = System.currentTimeMillis();
                            long currentTime = startTime;
                            while (currentTime - startTime < testConfig.getTestDuration() * 60 *
                                    1000) {
                                final String walletName = "TYWallet";
                                //input
                                CreateWalletConfig createWalletConfig = new CreateWalletConfig();
                                createWalletConfig.setName(walletName);
                                //output
                                final CreateWalletResponse createWalletResponse = TyWalletFactory.getTyWalletInstance
                                        (context).createWallet(createWalletConfig);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String testInfo = "钱包名称:"+walletName+"\n创建钱包结果:"+createWalletResponse;
                                        String exitedInfo = tvTestInfo.getText().toString();
                                        tvTestInfo.setText(exitedInfo == null ? testInfo :
                                                (exitedInfo + "\n" + testInfo));
                                    }
                                });
                                currentTime = System.currentTimeMillis();
                            }
                            Log.d(TAG, "run: test over");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "测试完成";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "run: 异常", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "异常，测试终止";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } finally {
                            loadingDailog.dismiss();
                        }
                    }
                }.start();
                break;
            case WIPE_WALLET:

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            long startTime = System.currentTimeMillis();
                            long currentTime = startTime;
                            while (currentTime - startTime < testConfig.getTestDuration() * 60 *
                                    1000) {
                                //output
                                final WipeDeviceResponse wipeDeviceResponse = TyWalletFactory.getTyWalletInstance
                                        (context).wipeDevice();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String testInfo = "创擦除钱包结果:"+wipeDeviceResponse;
                                        String exitedInfo = tvTestInfo.getText().toString();
                                        tvTestInfo.setText(exitedInfo == null ? testInfo :
                                                (exitedInfo + "\n" + testInfo));
                                    }
                                });
                                currentTime = System.currentTimeMillis();
                            }
                            Log.d(TAG, "run: test over");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "测试完成";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "run: 异常", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "异常，测试终止";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } finally {
                            loadingDailog.dismiss();
                        }
                    }
                }.start();
                break;
            case BACKUP_WALLET:

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            long startTime = System.currentTimeMillis();
                            long currentTime = startTime;
                            while (currentTime - startTime < testConfig.getTestDuration() * 60 *
                                    1000) {
                                //output
                                final BackupWalletResponse backupWalletResponse = TyWalletFactory.getTyWalletInstance
                                        (context).backupWallet();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String testInfo = "备份钱包结果:"+backupWalletResponse;
                                        String exitedInfo = tvTestInfo.getText().toString();
                                        tvTestInfo.setText(exitedInfo == null ? testInfo :
                                                (exitedInfo + "\n" + testInfo));
                                    }
                                });
                                currentTime = System.currentTimeMillis();
                            }
                            Log.d(TAG, "run: test over");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "测试完成";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "run: 异常", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String testInfo = "异常，测试终止";
                                    String exitedInfo = tvTestInfo.getText().toString();
                                    tvTestInfo.setText(exitedInfo == null ? testInfo :
                                            (exitedInfo + "\n" + testInfo));
                                }
                            });
                        } finally {
                            loadingDailog.dismiss();
                        }
                    }
                }.start();
                break;
            case RECOVER_WALLET:
                Toast.makeText(context,"暂不支持该功能测试",Toast.LENGTH_SHORT).show();
                loadingDailog.dismiss();
                break;
            default:
                Log.d(TAG, "launchTest: 未知测试");
                loadingDailog.dismiss();
                break;
        }
    }


}
