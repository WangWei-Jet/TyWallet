package com.whty.blockchain.wallet.view;

import com.whty.blockchain.wallet.entity.Env;

public interface IMainActivityView extends IBaseView{

    void setEnv(Env env);

    Env getEnv();

}
