package com.ccqiuqiu.ftime.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.BroadcastReceiver.AlarmStopBroadcastReceiver;
import com.ccqiuqiu.ftime.BroadcastReceiver.NextAlarmReceiver;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Server.MissAlarmService;
import com.ccqiuqiu.ftime.Server.MusicService;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.AnimatorUtils;

import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AlarmActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private static final int PULSE_DURATION_MILLIS = 1000;
    private static final int ALARM_BOUNCE_DURATION_MILLIS = 500;
    private static final int ALERT_SOURCE_DURATION_MILLIS = 250;
    private static final int ALERT_REVEAL_DURATION_MILLIS = 500;
    private static final int ALERT_FADE_DURATION_MILLIS = 500;
    private static final int ALERT_DISMISS_DELAY_MILLIS = 2000;
    private static final Interpolator PULSE_INTERPOLATOR =
            new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    private static final Interpolator REVEAL_INTERPOLATOR =
            new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);

    private static final float BUTTON_SCALE_DEFAULT = 0.7f;
    private static final int BUTTON_DRAWABLE_ALPHA_DEFAULT = 165;

    private boolean mAlarmHandled;
    private int mCurrentHourColor;

    private ViewGroup mContainerView;

    private ViewGroup mAlertView;
    private TextView mAlertTitleView;
    private TextView mAlertInfoView;

    private ViewGroup mContentView;
    private ImageView mAlarmButton;
    private ImageView mSnoozeButton;
    private ImageView mDismissButton;
    private TextView mHintView;

    private ValueAnimator mAlarmAnimator;
    private ValueAnimator mSnoozeAnimator;
    private ValueAnimator mDismissAnimator;
    private ValueAnimator mPulseAnimator;

    private int flg, id;
    private String title, content;
    private long alarm_time;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            missNotification();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏完全透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            //解锁屏幕
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        setContentView(R.layout.activity_alarm);

        mContainerView = (ViewGroup) findViewById(android.R.id.content);

        mAlertView = (ViewGroup) mContainerView.findViewById(R.id.alert);
        mAlertTitleView = (TextView) mAlertView.findViewById(R.id.alert_title);
        mAlertInfoView = (TextView) mAlertView.findViewById(R.id.alert_info);

        mContentView = (ViewGroup) mContainerView.findViewById(R.id.content);
        mAlarmButton = (ImageView) mContentView.findViewById(R.id.alarm);
        mSnoozeButton = (ImageView) mContentView.findViewById(R.id.snooze);
        mDismissButton = (ImageView) mContentView.findViewById(R.id.dismiss);
        mHintView = (TextView) mContentView.findViewById(R.id.hint);


        TypedArray sBackgroundSpectrum = getResources().obtainTypedArray(R.array.background_color_by_hour);
        int sDefaultBackgroundSpectrumColor = getResources().getColor(R.color.hour_12);
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mCurrentHourColor = sBackgroundSpectrum.getColor(hourOfDay, sDefaultBackgroundSpectrumColor);

        final TextView titleView = (TextView) mContentView.findViewById(R.id.title);
        final TextView alarmDesc = (TextView) mContentView.findViewById(R.id.alarm_desc);
        final TextClock digitalClock = (TextClock) mContentView.findViewById(R.id.digital_clock);
        final View pulseView = mContentView.findViewById(R.id.pulse);

        id = getIntent().getIntExtra("id", 0);
        flg = getIntent().getIntExtra("flg", 0);
        title = getIntent().getStringExtra("title");
        alarm_time = getIntent().getLongExtra("alarm_time", 0);
        content = getIntent().getStringExtra("content");

        titleView.setText(title);
        alarmDesc.setText(content);
        handler.sendEmptyMessageDelayed(0, App.mShichang * 60 * 1000);
        //handler.sendEmptyMessageDelayed(0, 20000);

        mContainerView.setBackgroundColor(mCurrentHourColor);

        mAlarmButton.setOnTouchListener(this);
        mSnoozeButton.setOnClickListener(this);
        mDismissButton.setOnClickListener(this);

        mAlarmAnimator = AnimatorUtils.getScaleAnimator(mAlarmButton, 1.0f, 0.0f);
        mSnoozeAnimator = getButtonAnimator(mSnoozeButton, Color.WHITE);
        mDismissAnimator = getButtonAnimator(mDismissButton, mCurrentHourColor);
        mPulseAnimator = ObjectAnimator.ofPropertyValuesHolder(pulseView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f, 0.0f));
        mPulseAnimator.setDuration(PULSE_DURATION_MILLIS);
        mPulseAnimator.setInterpolator(PULSE_INTERPOLATOR);
        mPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mPulseAnimator.start();

        setAnimatedFractions(0.0f /* snoozeFraction */, 0.0f /* dismissFraction */);
    }

    private void missNotification() {
        //停止音乐
        NotificationManager notificationManager = (NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent musicIntent = new Intent(getBaseContext(), MusicService.class);
        getBaseContext().stopService(musicIntent);

        Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
        mBuilder.setContentTitle(title)
                .setContentText(content + " - " + getBaseContext().getString(R.string.miss))
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_alarm);
        notificationManager.notify(id, mBuilder.build());

        //发送广播，设置下一次闹钟
        Intent dismissIntent = new Intent(getBaseContext(), NextAlarmReceiver.class);
        dismissIntent.putExtra("id", id);
        dismissIntent.putExtra("flg", flg);
        getBaseContext().sendBroadcast(dismissIntent);

        //停止自己
        stop();
        finish();
    }

    private void stop() {
        handler.removeMessages(0);
        //停止音乐
        Intent musicIntent = new Intent(AlarmActivity.this, MusicService.class);
        stopService(musicIntent);
        //将音量调回响铃前
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, App.mSysMusicVol, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, App.mSysAlarmVol, 0);
    }

    @Override
    public void onBackPressed() {
    }

    //按主页键回桌面或到应用程序切换页，则发送消息，停止activity
    @Override
    protected void onUserLeaveHint() {
        AlarmUtils.setNotification(AlarmActivity.this,id,flg,alarm_time,title,content);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (mAlarmHandled) {
            return;
        }

        final int alarmLeft = mAlarmButton.getLeft() + mAlarmButton.getPaddingLeft();
        final int alarmRight = mAlarmButton.getRight() - mAlarmButton.getPaddingRight();
        final float translationX = Math.max(view.getLeft() - alarmRight, 0)
                + Math.min(view.getRight() - alarmLeft, 0);
        getAlarmBounceAnimator(translationX, translationX < 0.0f ?
                R.string.description_direction_left : R.string.description_direction_right).start();
    }

    private void snooze() {
        mAlarmHandled = true;
        final int alertColor = getResources().getColor(R.color.hot_pink);
        setAnimatedFractions(1.0f /* snoozeFraction */, 0.0f /* dismissFraction */);

        final int snoozeMinutes = getSnoozedMinutes(this, App.mZanting);
        final String infoText = getResources().getQuantityString(
                R.plurals.alarm_alert_snooze_duration, snoozeMinutes, snoozeMinutes);
        final String accessibilityText = getResources().getQuantityString(
                R.plurals.alarm_alert_snooze_set, snoozeMinutes, snoozeMinutes);

        getAlertAnimator(mSnoozeButton, R.string.alarm_alert_snoozed_text, infoText,
                accessibilityText, alertColor, alertColor).start();

        //发送延时的消息
        Intent dismissIntent = new Intent(this, AlarmStopBroadcastReceiver.class);
        dismissIntent.putExtra("id", id);
        dismissIntent.putExtra("flg", flg);
        dismissIntent.putExtra("stop_flg", AlarmUtils.FLG_SNOOZE);
        dismissIntent.putExtra("alarm_time", alarm_time);
        sendBroadcast(dismissIntent);
    }

    public static int getSnoozedMinutes(Context context, int yanchi) {
        final String snoozeMinutesStr = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("snooze_duration", yanchi + "");
        return Integer.parseInt(snoozeMinutesStr);
    }

    private void dismiss() {
        mAlarmHandled = true;
        setAnimatedFractions(0.0f /* snoozeFraction */, 1.0f /* dismissFraction */);
        getAlertAnimator(mDismissButton, R.string.alarm_alert_off_text, null /* infoText */,
                getString(R.string.alarm_alert_off_text) /* accessibilityText */,
                Color.WHITE, mCurrentHourColor).start();

        //发送关闭的消息
        Intent dismissIntent = new Intent(this, AlarmStopBroadcastReceiver.class);
        dismissIntent.putExtra("id", id);
        dismissIntent.putExtra("flg", flg);
        dismissIntent.putExtra("stop_flg", AlarmUtils.FLG_DISMISS);
        sendBroadcast(dismissIntent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mAlarmHandled) {
            return false;
        }

        final int[] contentLocation = {0, 0};
        mContentView.getLocationOnScreen(contentLocation);

        final float x = motionEvent.getRawX() - contentLocation[0];
        final float y = motionEvent.getRawY() - contentLocation[1];

        final int alarmLeft = mAlarmButton.getLeft() + mAlarmButton.getPaddingLeft();
        final int alarmRight = mAlarmButton.getRight() - mAlarmButton.getPaddingRight();

        final float snoozeFraction, dismissFraction;
        if (mContentView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            snoozeFraction = getFraction(alarmRight, mSnoozeButton.getLeft(), x);
            dismissFraction = getFraction(alarmLeft, mDismissButton.getRight(), x);
        } else {
            snoozeFraction = getFraction(alarmLeft, mSnoozeButton.getRight(), x);
            dismissFraction = getFraction(alarmRight, mDismissButton.getLeft(), x);
        }
        setAnimatedFractions(snoozeFraction, dismissFraction);

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPulseAnimator.setRepeatCount(0);
                break;
            case MotionEvent.ACTION_UP:

                if (snoozeFraction == 1.0f) {
                    snooze();
                } else if (dismissFraction == 1.0f) {
                    dismiss();
                } else {
                    if (snoozeFraction > 0.0f || dismissFraction > 0.0f) {
                        // Animate back to the initial state.
                        AnimatorUtils.reverse(mAlarmAnimator, mSnoozeAnimator, mDismissAnimator);
                    } else if (mAlarmButton.getTop() <= y && y <= mAlarmButton.getBottom()) {
                        // User touched the alarm button, hint the dismiss action.
                        mDismissButton.performClick();
                    }

                    // Restart the pulse.
                    mPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    if (!mPulseAnimator.isStarted()) {
                        mPulseAnimator.start();
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    private float getFraction(float x0, float x1, float x) {
        return Math.max(Math.min((x - x0) / (x1 - x0), 1.0f), 0.0f);
    }

    private ValueAnimator getButtonAnimator(ImageView button, int tintColor) {
        return ObjectAnimator.ofPropertyValuesHolder(button,
                PropertyValuesHolder.ofFloat(View.SCALE_X, BUTTON_SCALE_DEFAULT, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, BUTTON_SCALE_DEFAULT, 1.0f),
                PropertyValuesHolder.ofInt(AnimatorUtils.BACKGROUND_ALPHA, 0, 255),
                PropertyValuesHolder.ofInt(AnimatorUtils.DRAWABLE_ALPHA,
                        BUTTON_DRAWABLE_ALPHA_DEFAULT, 255),
                PropertyValuesHolder.ofObject(AnimatorUtils.DRAWABLE_TINT,
                        AnimatorUtils.ARGB_EVALUATOR, Color.WHITE, tintColor));
    }

    private void setAnimatedFractions(float snoozeFraction, float dismissFraction) {
        final float alarmFraction = Math.max(snoozeFraction, dismissFraction);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mAlarmAnimator.setCurrentFraction(alarmFraction);
            mSnoozeAnimator.setCurrentFraction(snoozeFraction);
            mDismissAnimator.setCurrentFraction(dismissFraction);
        } else {
            AnimatorUtils.setAnimatedFraction(mAlarmAnimator, alarmFraction);
            AnimatorUtils.setAnimatedFraction(mSnoozeAnimator, snoozeFraction);
            AnimatorUtils.setAnimatedFraction(mDismissAnimator, dismissFraction);
        }
    }

    private ValueAnimator getAlarmBounceAnimator(float translationX, final int hintResId) {
        final ValueAnimator bounceAnimator = ObjectAnimator.ofFloat(mAlarmButton,
                View.TRANSLATION_X, mAlarmButton.getTranslationX(), translationX, 0.0f);
        bounceAnimator.setInterpolator(AnimatorUtils.DECELERATE_ACCELERATE_INTERPOLATOR);
        bounceAnimator.setDuration(ALARM_BOUNCE_DURATION_MILLIS);
        bounceAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mHintView.setText(hintResId);
                if (mHintView.getVisibility() != View.VISIBLE) {
                    mHintView.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mHintView, View.ALPHA, 0.0f, 1.0f).start();
                }
            }
        });
        return bounceAnimator;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Animator getAlertAnimator(final View source, final int titleResId,
                                      final String infoText, final String accessibilityText, final int revealColor,
                                      final int backgroundColor) {
        final ViewGroupOverlay overlay = mContainerView.getOverlay();

        // Create a transient view for performing the reveal animation.
        final View revealView = new View(this);
        revealView.setRight(mContainerView.getWidth());
        revealView.setBottom(mContainerView.getHeight());
        revealView.setBackgroundColor(revealColor);
        overlay.add(revealView);

        // Add the source to the containerView's overlay so that the animation can occur under the
        // status bar, the source view will be automatically positioned in the overlay so that
        // it maintains the same relative position on screen.
        overlay.add(source);

        final int centerX = Math.round((source.getLeft() + source.getRight()) / 2.0f);
        final int centerY = Math.round((source.getTop() + source.getBottom()) / 2.0f);
        final float startRadius = Math.max(source.getWidth(), source.getHeight()) / 2.0f;

        final int xMax = Math.max(centerX, mContainerView.getWidth() - centerX);
        final int yMax = Math.max(centerY, mContainerView.getHeight() - centerY);
        final float endRadius = (float) Math.sqrt(Math.pow(xMax, 2.0) + Math.pow(yMax, 2.0));

        final ValueAnimator sourceAnimator = ObjectAnimator.ofFloat(source, View.ALPHA, 0.0f);
        sourceAnimator.setDuration(ALERT_SOURCE_DURATION_MILLIS);
        sourceAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                overlay.remove(source);
            }
        });

        final Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                revealView, centerX, centerY, startRadius, endRadius);
        revealAnimator.setDuration(ALERT_REVEAL_DURATION_MILLIS);
        revealAnimator.setInterpolator(REVEAL_INTERPOLATOR);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mAlertView.setVisibility(View.VISIBLE);
                mAlertTitleView.setText(titleResId);

                if (infoText != null) {
                    mAlertInfoView.setText(infoText);
                    mAlertInfoView.setVisibility(View.VISIBLE);
                }
                mAlertView.announceForAccessibility(accessibilityText);
                mContentView.setVisibility(View.GONE);
                mContainerView.setBackgroundColor(backgroundColor);
            }
        });

        final ValueAnimator fadeAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
        fadeAnimator.setDuration(ALERT_FADE_DURATION_MILLIS);
        fadeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                overlay.remove(revealView);
            }
        });

        final AnimatorSet alertAnimator = new AnimatorSet();
        alertAnimator.play(revealAnimator).with(sourceAnimator).before(fadeAnimator);
        alertAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                revealView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                        finish();
                    }
                }, ALERT_DISMISS_DELAY_MILLIS);
            }
        });

        return alertAnimator;
    }
}
