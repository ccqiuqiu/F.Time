package com.ccqiuqiu.ftime.Fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;
import com.ccqiuqiu.ftime.Activity.AboutActivity;
import com.ccqiuqiu.ftime.Adapter.MyPagerAdapter;
import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.Other.SimpleAnimationListener;
import com.ccqiuqiu.ftime.Utils.SoftKeyboardUtil;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rey.material.widget.FloatingActionButton;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by cc on 2016/3/14.
 */
@ContentView(R.layout.fragment_main)
public class MainFragment extends BaseFragment
        implements View.OnClickListener {

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.top_view)
    private View mTopView;
    @ViewInject(R.id.drawer_layout)
    private DrawerLayout mDrawer;
    @ViewInject(R.id.viewpager)
    public com.ccqiuqiu.ftime.View.MyViewPager mViewPager;
    @ViewInject(R.id.tab_alarm)
    private View mTabAlarm;
    @ViewInject(R.id.tab_timeline)
    private View mTabTimeLine;
    @ViewInject(R.id.tab_alarm_img)
    private ImageView mTabAlarmImg;
    @ViewInject(R.id.tab_timeline_img)
    private ImageView mTabTimeLineImg;
    @ViewInject(R.id.drawer_layout)
    private DrawerLayout mDrawerLayout;
    @ViewInject(R.id.add_alarm_toolbar)
    private View mAddAlarmToolbar;
    @ViewInject(R.id.add_progress_toolbar)
    private View mAddProgressToolbar;
    @ViewInject(R.id.fab_line)
    public FloatingActionButton mFab;
    @ViewInject(R.id.shichang)
    private com.ccqiuqiu.ftime.View.MyRangeBar mShichang;
    @ViewInject(R.id.zanting)
    private com.ccqiuqiu.ftime.View.MyRangeBar mZanting;
    @ViewInject(R.id.drag_bg)
    private View mDragBg;
    @ViewInject(R.id.lingsheng)
    private com.rey.material.widget.Button mLingsheng;
    @ViewInject(R.id.tv_about)
    private View mAbout;
    @ViewInject(R.id.add_alarm_back)
    private ImageView mAddAlarmBack;
    @ViewInject(R.id.add_progress_back)
    private ImageView mAddProgressBack;


    private List<BaseFragment> mFragments = new ArrayList<>();
    public AddAlarmFragment mAddAlarmFragment;
    public AlarmFragment mAlarmFragment;
    public ProgressFragment mProgressFragment;
    public AddProgressFragment mAddProgressFragment;
    private int mPosition;
    private boolean mIsAddAlarm, mIsAddProgress, mIsAnim;
    private UpdateUIBroadcastReceiver updateUIBroadcastReceiver;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置toolbar相关
        ViewGroup.LayoutParams layoutParams = mTopView.getLayoutParams();
        layoutParams.height = ViewUtils.getStatusHeight();
        mTopView.setLayoutParams(layoutParams);
        mMainActivity.setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        //设置fab按钮事件
        mFab.setOnClickListener(this);

        //抽屉的事件
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        //设置背景颜色
        setBackgroundColor();

        //初始化ViewPager需要的fragment
        mFragments.add(mAddAlarmFragment = AddAlarmFragment.newInstance());
        mFragments.add(mAlarmFragment = AlarmFragment.newInstance(true));
        mFragments.add(mProgressFragment = ProgressFragment.newInstance());
        mFragments.add(mAddProgressFragment = AddProgressFragment.newInstance());

        //设置ViewPager
        mViewPager.setCanScroll(false);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(mMainActivity, mFragments);
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setOffscreenPageLimit(myPagerAdapter.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //mFab.setLineMorphingState((mFab.getLineMorphingState() + 1) % 2, true);
                mPosition = position;
                if (mPosition != 0) {
                    mAddAlarmFragment.stopMusic();
                }
                if (position == 0) {
                    mFab.setLineMorphingState(1, true);
                    mAddAlarmFragment.initTime();
                    //mAddAlarmFragment.initViewDate();
                    //锁定抽屉不能滑出
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toggAddAlermToolbar(true);
                } else if (position == 1) {
                    mFab.setLineMorphingState(0, true);
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    toggAddAlermToolbar(false);
                    DrawableCompat.setTint(mTabAlarmImg.getDrawable(), Color.WHITE);
                    DrawableCompat.setTint(mTabTimeLineImg.getDrawable(), getResources().getColor(R.color.white_t));
                } else if (position == 2) {
                    mFab.setLineMorphingState(0, true);
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    toggAddProgressToolbar(false);
                    DrawableCompat.setTint(mTabAlarmImg.getDrawable(), getResources().getColor(R.color.white_t));
                    DrawableCompat.setTint(mTabTimeLineImg.getDrawable(), Color.WHITE);
                } else if (position == 3) {
                    mFab.setLineMorphingState(1, true);
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toggAddProgressToolbar(true);
                }
                //软键盘弹出监听
                mFragments.get(position).observeSoftKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setCurrentItem(1);

        //设置toolbar上面的按钮点击事件
        mTabAlarm.setOnClickListener(this);
        mTabTimeLine.setOnClickListener(this);
        DrawableCompat.setTint(mTabTimeLineImg.getDrawable(), getResources().getColor(R.color.white_t));

        //初始化添加闹钟页面的toolbar
        mAddAlarmBack.setOnClickListener(this);
        mAddProgressBack.setOnClickListener(this);
        mAddAlarmToolbar.setBackgroundColor(App.mColorPrimary);
        mAddProgressToolbar.setBackgroundColor(App.mColorPrimary);

        //抽屉图片
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 9 && hourOfDay <= 18) {
            mDragBg.setBackground(getResources().getDrawable(R.mipmap.banner));
        } else {
            mDragBg.setBackground(getResources().getDrawable(R.mipmap.banner_ye));
        }
        //抽屉数据
        initSetting();

        //注册广播，闹钟响起，如果软件正在运行，更新ui
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ccqiuqiu.ftime.alarm.updateui");
        updateUIBroadcastReceiver = new UpdateUIBroadcastReceiver();
        mMainActivity.registerReceiver(updateUIBroadcastReceiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.checkPermission(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    //System.out.println("=====onPermissionGranted========");
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    ViewUtils.snackbar(mFab, getString(R.string.permission_denied));
                }

                @Override
                public void onPermissionRationaleShouldBeShown(final PermissionRequest permission, final PermissionToken token) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.permission)
                            .content(getString(R.string.permission_con))
                            .positiveText(R.string.commit)
                            .cancelable(true)
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    token.cancelPermissionRequest();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    token.continuePermissionRequest();
                                }
                            }).show();
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void initSetting() {
        mShichang.setSeekPinByValue(App.mShichang);
        mZanting.setSeekPinByValue(App.mZanting);
        mShichang.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                App.mShichang = Integer.parseInt(rightPinValue);
                ViewUtils.putIntToSharedPreferences("sett_lingsheng_shichang", App.mShichang);
            }
        });
        mZanting.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                App.mZanting = Integer.parseInt(rightPinValue);
                ViewUtils.putIntToSharedPreferences("sett_zanting", App.mZanting);
            }
        });
        mLingsheng.setOnClickListener(this);
        mAbout.setOnClickListener(this);

        String name = ViewUtils.getStringByRingtoneUri(App.mDefaultLingsheng);
        mLingsheng.setText(name);
    }

    private void toggAddAlermToolbar(final boolean open) {
        if (mIsAnim || open == mIsAddAlarm) {
            return;
        }
        mIsAnim = true;
        float start = mAddAlarmToolbar.getWidth();
        float end = 0;
        mAddAlarmToolbar.setVisibility(View.VISIBLE);
        if (!open) {
            start = 0;
            end = mAddAlarmToolbar.getWidth();
        }
        Animator animator = ObjectAnimator.ofFloat(mAddAlarmToolbar, View.X, start, end);
        animator.setDuration(200);
        animator.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnim = false;
                mIsAddAlarm = open;
                if (!open) {
                    mAddAlarmToolbar.setVisibility(View.INVISIBLE);
                }
            }
        });
        animator.start();
    }

    private void toggAddProgressToolbar(final boolean open) {
        if (mIsAnim || open == mIsAddProgress) {
            return;
        }
        mIsAnim = true;
        float start = -mAddProgressToolbar.getWidth();
        float end = 0;
        mAddProgressToolbar.setVisibility(View.VISIBLE);
        if (!open) {
            start = 0;
            end = -mAddProgressToolbar.getWidth();
        }
        Animator animator = ObjectAnimator.ofFloat(mAddProgressToolbar, View.X, start, end);
        animator.setDuration(200);
        animator.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnim = false;
                mIsAddProgress = open;
                if (!open) {
                    mAddProgressToolbar.setVisibility(View.INVISIBLE);
                }
            }
        });
        animator.start();
    }

    private void setBackgroundColor() {
//        App.mColorPrimary = getResources().getColor(R.color.colorPrimary);
//        App.mColorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);
        App.mColorAccent = getResources().getColor(R.color.colorAccent);

        final int duration = 5000;
        TypedArray sBackgroundSpectrum = getResources().obtainTypedArray(R.array.background_color_by_hour);
        int sDefaultBackgroundSpectrumColor = getResources().getColor(R.color.hour_12);
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int currHourColor = sBackgroundSpectrum.getColor(hourOfDay, sDefaultBackgroundSpectrumColor);
        App.mColorPrimary = currHourColor;

        AnimatorSet animatorSet = new AnimatorSet();
        final ObjectAnimator animator = ObjectAnimator.ofInt(mMainActivity.getWindow().getDecorView(),
                "backgroundColor", App.mColorPrimary, currHourColor);
        animator.setEvaluator(new ArgbEvaluator());
        final ObjectAnimator animator_toolbar = ObjectAnimator.ofInt(mToolbar,
                "backgroundColor", App.mColorPrimary, currHourColor);
        animator_toolbar.setEvaluator(new ArgbEvaluator());
        final ObjectAnimator animator_top = ObjectAnimator.ofInt(mTopView,
                "backgroundColor", App.mColorPrimary, currHourColor);
        animator_top.setEvaluator(new ArgbEvaluator());

        animatorSet.playTogether(animator, animator_toolbar, animator_top);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    public boolean onBackPressed() {
        if (mIsAddAlarm) {
            toggAddAlermToolbar(false);
            mViewPager.setCurrentItem(1);
            return true;
        }
        if (mIsAddProgress) {
            toggAddProgressToolbar(false);
            mViewPager.setCurrentItem(2);
            return true;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    private void about() {
//        AlarmUtils.setAlarm(5);
//        AlarmUtils.setAlarm(6);
        Intent intent = new Intent(getContext(), AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_line:
                mFragments.get(mPosition).onFabClick(mFab);
                break;
            case R.id.tab_alarm:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tab_timeline:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.add_alarm_back:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.add_progress_back:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.lingsheng:
                mMainActivity.selectLingsheng(App.mDefaultLingsheng, App.SELECT_LINGSHENG_FLG_DRAG);
                break;
            case R.id.tv_about:
                about();
                break;
        }
    }

    public void lingshengSelected(Uri pickedUri) {
        App.mDefaultLingsheng = pickedUri;
        ViewUtils.putStringToSharedPreferences("default_lingsheng", pickedUri.toString());
        mLingsheng.setText(ViewUtils.getStringByRingtoneUri(pickedUri));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销广播
        mMainActivity.unregisterReceiver(updateUIBroadcastReceiver);
    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mAlarmFragment.reload();
        }

    }
}
