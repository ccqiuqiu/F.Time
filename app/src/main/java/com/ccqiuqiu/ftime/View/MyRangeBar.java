package com.ccqiuqiu.ftime.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by cc on 2016/3/11.
 * 主要是为RangeBar添加控制是否能拖动的功能。
 */
public class MyRangeBar extends com.appyvet.rangebar.RangeBar {

    private boolean isDrag = true;

    public MyRangeBar(Context context) {
        super(context);
    }

    public MyRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrag) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }
    public void setIsDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }
}
