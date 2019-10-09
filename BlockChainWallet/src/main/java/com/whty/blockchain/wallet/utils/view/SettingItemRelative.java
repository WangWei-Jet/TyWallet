package com.whty.blockchain.wallet.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whty.blockchain.wallet.R;

public class SettingItemRelative extends RelativeLayout {

    private ImageView ivLeft;

    private TextView tvKey;

    private TextView tvValue;

    private ImageView ibMore;

    public SettingItemRelative(Context context) {
        super(context);

        initView(context);
    }

    public SettingItemRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_setting_item, this, true);
        ivLeft = findViewById(R.id.iv_image);
        tvKey = findViewById(R.id.tv_key);
        tvValue = findViewById(R.id.tv_value);
        ibMore = findViewById(R.id.ib_more);
    }

    public ImageView getIvLeft() {
        return ivLeft;
    }

    public TextView getTvKey() {
        return tvKey;
    }


    public TextView getTvValue() {
        return tvValue;
    }


    public ImageView getIbMore() {
        return ibMore;
    }

}
