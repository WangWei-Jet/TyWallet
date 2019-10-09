package com.whty.blockchain.wallet.utils.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ScrollView;

/**
 * 自定义scrollview，最大高度为屏幕的一半高度
 */
public class MyScrollView extends ScrollView {
    private Context mContext;

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            DisplayMetrics d = new DisplayMetrics();
            display.getMetrics(d);
            // 设置控件最大高度不能超过屏幕高度的一半
            heightMeasureSpec = MeasureSpec
                    .makeMeasureSpec(d.heightPixels / 2, MeasureSpec.AT_MOST);
        } catch (Exception e) {
            e.printStackTrace();
        } // 重新计算控件的宽高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
