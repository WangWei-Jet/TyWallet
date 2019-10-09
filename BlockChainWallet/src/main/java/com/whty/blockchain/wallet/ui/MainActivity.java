package com.whty.blockchain.wallet.ui;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.whty.blockchain.bottomdialog.widget.AlertDialog;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;
import com.whty.blockchain.wallet.R;
import com.whty.blockchain.wallet.WalletApplication;
import com.whty.blockchain.wallet.entity.Env;
import com.whty.blockchain.wallet.presentor.BrowserAPIPresentor;
import com.whty.blockchain.wallet.view.IMainActivityView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements IMainActivityView{

    @BindView(R.id.bnv_navigator)
    BottomNavigationView bnvNavigator;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    BrowserAPIPresentor browserAPIPresentor;

    QueryFragment queryFragment;
    TransferBTCFragment transferBTCFragment;
    SettingFragment settingFragment;

    FragmentManager fragmentManager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.info("onCreate");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        browserAPIPresentor = new BrowserAPIPresentor(getEnv());
        browserAPIPresentor.attachView(this);

        initView();

        requestPermission();
    }

    void initView() {
        logger.info("initView");
        queryFragment = new QueryFragment();
        transferBTCFragment = new TransferBTCFragment();
        settingFragment = new SettingFragment();

        bnvNavigator.setOnNavigationItemSelectedListener(new BottomNavigationView
                .OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.it_navigation_query:
                        switchToQueryFragment();
                        break;

                    case R.id.it_navigation_trade:
                        switchToTradeFragment();
                        break;

                    case R.id.it_navigation_setting:
                        switchToSettingFragment();
                        break;
                }
                return true;
            }
        });

        setDefaultFragment();
    }

    void requestPermission() {
        //相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    void setDefaultFragment() {
        logger.info("setDefaultFragment");
        bnvNavigator.setSelectedItemId(R.id.it_navigation_query);
    }

    void switchToQueryFragment() {
        logger.info("switchToQueryFragment");
        getBrowserAPIPresentor().attachView(queryFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_content, queryFragment);
        fragmentTransaction.commit();
    }

    void switchToTradeFragment() {
        logger.info("switchToTradeFragment");
        getBrowserAPIPresentor().attachView(transferBTCFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_content, transferBTCFragment);
        fragmentTransaction.commit();
    }

    void switchToSettingFragment() {
        logger.info("switchToSettingFragment");
        getBrowserAPIPresentor().attachView(settingFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_content, settingFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.info("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.info("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logger.info("onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //自定义的alertdialog
            new AlertDialog(this)
                .builder()
                .setTitle("退出应用")
                .setMsg("确定退出钱包应用？")
                .setPositiveButton("确认退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //填写事件
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //填写事件
                    }
                }).show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void setEnv(Env env) {
        ((WalletApplication)getApplication()).setEnv(env);
    }

    @Override
    public Env getEnv() {
        return ((WalletApplication)getApplication()).getEnv();
    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void showLoadingDialogWithContentMsg(String contentMsg) {

    }

    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void showEthereumEnvironmentBalanceResult(BigInteger balance) {

    }

    @Override
    public void showBitcoinEnvironmentBalanceResult(BlockchainUTXOResponse blockchainUTXOResponse) {

    }

    public BrowserAPIPresentor getBrowserAPIPresentor() {
        return browserAPIPresentor;
    }
}
