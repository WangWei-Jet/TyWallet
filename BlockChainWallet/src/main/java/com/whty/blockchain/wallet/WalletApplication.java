package com.whty.blockchain.wallet;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.whty.blockchain.wallet.entity.Env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletApplication extends Application {

    //默认以太坊
    private Env env = Env.ETHEREUM;

    public static int SCREEN_H;
    public static int SCREEN_W;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onCreate() {
        super.onCreate();
        logger.info("wallet application on create");

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        SCREEN_H = outMetrics.heightPixels;
        SCREEN_W = outMetrics.widthPixels;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logger.info("wallet application on terminate");
    }

    public Env getEnv() {
        return env;
    }

    public void setEnv(Env env) {
        this.env = env;
    }
}
