package com.whty.blockchain.wallet.presentor;

import com.whty.blockchain.wallet.view.IBaseView;

public abstract class BasePresentor<V extends IBaseView> {

    V view;

    public void attachView(V view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public boolean isViewAttached() {
        return (getView() == null) ? false : true;
    }

    public V getView(){
        return view;
    }

}
