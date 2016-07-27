package com.ccqiuqiu.ftime.Utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by cc on 2016/4/15.
 */
public class SoftKeyboardUtil {
    private static int mOffset;
    private static ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public static void observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
        if(activity == null)return;
        final View decorView = activity.getWindow().getDecorView();
        if (listener == null){
            if(onGlobalLayoutListener != null){
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
            return;
        }
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - displayHeight;
                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    if (hide) {
                        mOffset = keyboardHeight;
                    }
                    listener.onSoftKeyBoardChange(keyboardHeight - mOffset, !hide);
                }

                previousKeyboardHeight = height;

            }
        };
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible);
    }
}
