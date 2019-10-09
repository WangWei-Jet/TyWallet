package com.whty.blockchain.tywallet;

import android.os.Handler;

import com.whty.blockchain.tyblockchainlib.api.util.UpgradeListener;

public class UpgradeListenerImpl implements UpgradeListener {

    public static final int UPGRADE_SUCCESS = 10;

    public static final int UPGRADE_FAIL = 11;

    public static final int UPGRADE_PROCESS_UPDATE = 12;

    private Handler handler;

    public UpgradeListenerImpl(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void upgradeDeviceSuccess() {
        handler.obtainMessage(UPGRADE_SUCCESS).sendToTarget();
    }

    @Override
    public void showProgress(int paramInt) {
        handler.obtainMessage(UPGRADE_PROCESS_UPDATE, new Integer(paramInt)).sendToTarget();
    }

    @Override
    public void upgradeFail(int paramInt) {
        handler.obtainMessage(UPGRADE_FAIL).sendToTarget();
    }

}
