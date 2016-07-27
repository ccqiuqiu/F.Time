package com.ccqiuqiu.ftime.Fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Server.ControllerInterface;
import com.ccqiuqiu.ftime.Server.MusicService;
import com.ccqiuqiu.ftime.Utils.DateUtils;
import com.ccqiuqiu.ftime.Utils.LunarCalendar;
import com.ccqiuqiu.ftime.Utils.SoftKeyboardUtil;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.ccqiuqiu.ftime.View.DatePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cc on 2016/3/14.
 */
@ContentView(R.layout.fragment_add_alarm)
public class AddAlarmFragment extends BaseFragment
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    @ViewInject(R.id.type_one)
    private View mTypeOne;
    @ViewInject(R.id.type_work)
    private View mTypeWork;
    @ViewInject(R.id.type_week)
    private View mTypeWeek;
    @ViewInject(R.id.type_month)
    private View mTypeMonth;
    @ViewInject(R.id.type_year)
    private View mTypeYear;
    @ViewInject(R.id.type_year_cn)
    private View mTypeYearCn;
    @ViewInject(R.id.type_zdy)
    private View mTypeZdy;
    @ViewInject(R.id.tv_type_one)
    private TextView mTvTypeOne;
    @ViewInject(R.id.tv_type_work)
    private TextView mTvTypeWork;
    @ViewInject(R.id.tv_type_week)
    private TextView mTvTypeWeek;
    @ViewInject(R.id.tv_type_month)
    private TextView mTvTypeMonth;
    @ViewInject(R.id.tv_type_year)
    private TextView mTvTypeYear;
    @ViewInject(R.id.tv_type_year_cn)
    private TextView mTvTypeYearCn;
    @ViewInject(R.id.tv_type_zdy)
    private TextView mTvTypeZdy;

    @ViewInject(R.id.week_1_p)
    private ViewGroup mWeek1P;
    @ViewInject(R.id.week_2_p)
    private ViewGroup mWeek2P;
    @ViewInject(R.id.week_3_p)
    private ViewGroup mWeek3P;
    @ViewInject(R.id.week_4_p)
    private ViewGroup mWeek4P;
    @ViewInject(R.id.week_5_p)
    private ViewGroup mWeek5P;
    @ViewInject(R.id.week_6_p)
    private ViewGroup mWeek6P;
    @ViewInject(R.id.week_7_p)
    private ViewGroup mWeek7P;
    @ViewInject(R.id.week_1)
    private TextView mWeek1;
    @ViewInject(R.id.week_2)
    private TextView mWeek2;
    @ViewInject(R.id.week_3)
    private TextView mWeek3;
    @ViewInject(R.id.week_4)
    private TextView mWeek4;
    @ViewInject(R.id.week_5)
    private TextView mWeek5;
    @ViewInject(R.id.week_6)
    private TextView mWeek6;
    @ViewInject(R.id.week_7)
    private TextView mWeek7;

    @ViewInject(R.id.yinliangRang)
    private com.ccqiuqiu.ftime.View.MyRangeBar mYinliangRang;
    @ViewInject(R.id.cb_jianqiang)
    private android.widget.CheckBox mJianqiang;
    @ViewInject(R.id.cb_zhendong)
    private android.widget.CheckBox mZhendong;
    @ViewInject(R.id.time)
    private TextView mTime;
    @ViewInject(R.id.select_date)
    private TextView mDate;
    @ViewInject(R.id.select_dayofmonth)
    private com.ccqiuqiu.ftime.View.WheelViewV mDayOfMonth;
    @ViewInject(R.id.select_zdy)
    private View mSelectZdy;
    @ViewInject(R.id.open)
    private Switch mOpen;
    @ViewInject(R.id.select_week)
    private View mWeekView;
    @ViewInject(R.id.name)
    private TextView mName;
    @ViewInject(R.id.lingsheng)
    private TextView mLingsheng;
    @ViewInject(R.id.zdy_work)
    private MaterialEditText mZdyWork;
    @ViewInject(R.id.zdy_un_work)
    private MaterialEditText mZdyUnWork;
    @ViewInject(R.id.zdy_cur)
    private MaterialEditText mZdyCur;

    private TextView mTitle;
    private ImageView mDelBtn;

    private View mSelectedTvType, mSelectView;
    private int mType = App.ALARM_TYPE_ONE;
    private String[] mWeekArr = new String[]{"0", "0", "0", "0", "0", "0", "0"};
    private GregorianCalendar mCurDate;
    private BottomSheetDialog mBottomSheetDialog;
    private com.ccqiuqiu.ftime.View.DatePicker mDateCn;
    private Uri mLingshengUri;
    private int mEditPosition;
    public Alarm mEditAlarm;
    public ControllerInterface mCi;
    private ServiceConnection mServiceConnection;
    private boolean mIsBinding;

    public static AddAlarmFragment newInstance() {
        return new AddAlarmFragment();
    }

    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
    //让Fragment重写onCreateOptionsMenu，onOptionsItemSelected
//        setHasOptionsMenu(true);
//    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLingsheng.setText((String) msg.obj);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle = (TextView) mMainActivity.mMainFragment.getView().findViewById(R.id.add_alarm_title);
        mDelBtn = (ImageView) mMainActivity.mMainFragment.getView().findViewById(R.id.add_alarm_del);
        //设置控件颜色
        mTvTypeOne.setTextColor(App.mColorPrimary);
        mTvTypeWork.setTextColor(App.mColorPrimary);
        mTvTypeWeek.setTextColor(App.mColorPrimary);
        mTvTypeMonth.setTextColor(App.mColorPrimary);
        mTvTypeYear.setTextColor(App.mColorPrimary);
        mTvTypeYearCn.setTextColor(App.mColorPrimary);
        mTvTypeZdy.setTextColor(App.mColorPrimary);
        //设置单击监听
        mDelBtn.setOnClickListener(this);
        mTypeOne.setOnClickListener(this);
        mTypeWork.setOnClickListener(this);
        mTypeWeek.setOnClickListener(this);
        mTypeMonth.setOnClickListener(this);
        mTypeYear.setOnClickListener(this);
        mTypeYearCn.setOnClickListener(this);
        mTypeZdy.setOnClickListener(this);
        mWeek1P.setOnClickListener(this);
        mWeek2P.setOnClickListener(this);
        mWeek3P.setOnClickListener(this);
        mWeek4P.setOnClickListener(this);
        mWeek5P.setOnClickListener(this);
        mWeek6P.setOnClickListener(this);
        mWeek7P.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mLingsheng.setOnClickListener(this);
        mJianqiang.setOnCheckedChangeListener(this);
        mZhendong.setOnCheckedChangeListener(this);
        mOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCi != null) {
                    mCi.stop();
                }
            }
        });
        mName.setOnClickListener(this);
        //初始化日期选择控件
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            items.add(i + "");
        }
        mDayOfMonth.setItems(items);
        mDayOfMonth.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDayOfMonth.setVisibility(View.GONE);
            }
        }, 200);
        //初始化音量调整控件
        mYinliangRang.setDrawTicks(false);
        //mYinliangRang.setSeekPinByValue(30);
        //初始化时间
        initTime();

        //bindMusicService();
        mYinliangRang.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                if (mLingshengUri != null && !mIsBinding) {
                    if (mCi == null) {
                        bindMusicService();
                    } else {
                        float vol = (float) (rightPinIndex / 100.00);
                        if (mCi.isPlay()) {
                            mCi.setVolume(vol, vol);
                        } else {
                            mCi.play(vol, mLingshengUri);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void observeSoftKeyboard() {
        super.observeSoftKeyboard();
        //软键盘弹出监听
        SoftKeyboardUtil.observeSoftKeyboard(getActivity(), new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
                int h = ViewUtils.getScreemHeight() - mName.getBottom() -
                        mMainActivity.mMainFragment.mFab.getHeight() - ViewUtils.dp2px(16);
                h = Math.min(h, softKeybardHeight);
                if (visible) {
                    //mMainActivity.mMainFragment.mFab.animate().translationY(-softKeybardHeight).setDuration(150);
                    mMainActivity.mMainFragment.mFab.animate().translationY(-h)
                            .setDuration(150);
                } else {
                    mMainActivity.mMainFragment.mFab.animate().translationY(0).setDuration(200);
                }
            }
        });
    }

    private void bindMusicService() {
        mIsBinding = true;
        //创建播放音乐的服务
        Intent intent = new Intent(getContext(), MusicService.class);
        //绑定服务获取中间人对象
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIsBinding = false;
                mCi = (ControllerInterface) service;
                float vol = (float) (mYinliangRang.getRightIndex() / 100.00);
                mCi.play(vol, mLingshengUri);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        mMainActivity.bindService(intent, mServiceConnection, getContext().BIND_AUTO_CREATE);
    }

    @Override
    public void onFabClick(FloatingActionButton fab) {
        save(fab);
    }

    private void save(FloatingActionButton fab) {
        Alarm alarm = new Alarm();
        alarm.setDate(mDate.getText().toString());
        alarm.setTime(mTime.getText().toString());
        if (mType == App.ALARM_TYPE_ZDY) {
            if (TextUtils.isEmpty(mZdyWork.getText().toString())
                    || TextUtils.isEmpty(mZdyUnWork.getText().toString())
                    || TextUtils.isEmpty(mZdyCur.getText().toString())) {

                ViewUtils.snackbar(fab, getString(R.string.err_zdy));
                return;
            }
            int work = Integer.parseInt(mZdyWork.getText().toString());
            int unWork = Integer.parseInt(mZdyUnWork.getText().toString());
            int cur = Integer.parseInt(mZdyCur.getText().toString());
            if (cur > work + unWork || cur < 1) {
                ViewUtils.snackbar(fab, MessageFormat.format(getString(R.string.err_zdy_2), 0, work + unWork));
                return;
            }
            alarm.setWorkDays(work);
            alarm.setUnWorkDays(unWork);
            //计算开始时间
            mCurDate.add(Calendar.DAY_OF_MONTH, -(cur - 1));
            alarm.setDate(new SimpleDateFormat("yyyy-MM-dd").format(mCurDate.getTime()));

        } else if (mType == App.ALARM_TYPE_WEEK) {
            StringBuffer sb = new StringBuffer();
            for (String i : mWeekArr) {
                sb.append(i + ",");
            }
            alarm.setWeeks(sb.toString().substring(0, sb.length() - 1));
        }

        alarm.setDay(mDayOfMonth.getSelectedPosition() + 1);
        alarm.setType(mType);
        alarm.setVolume(mYinliangRang.getRightIndex());
        alarm.setIsVibration(mZhendong.isChecked());
        alarm.setIsJianqiang(mJianqiang.isChecked());
        alarm.setIsOpen(mOpen.isChecked());
        alarm.setMusicUri(mLingshengUri.toString());
        if (!TextUtils.isEmpty(mName.getText().toString())) {
            alarm.setName(mName.getText().toString().trim());
        }
        String s = getString(R.string.add_alarm_success);

        alarm.setNextDate(App.getAlarmService().getNextDate(alarm, new Date(),false));

        if (mEditAlarm != null) {//修改
            alarm.setId(mEditAlarm.getId());
            alarm.setOrder(mEditAlarm.getOrder());
            App.getAlarmService().updateAlarm(alarm);
            mMainActivity.mMainFragment.mAlarmFragment.notifyItemChanged(alarm, mEditPosition);
            s = getString(R.string.edit_alarm_success);
        } else {//新增
            App.getAlarmService().saveAlarm(alarm);
            mMainActivity.mMainFragment.mAlarmFragment.notifyItemInserted(alarm);
        }
        mMainActivity.mMainFragment.mViewPager.setCurrentItem(1);

        if (alarm.getIsOpen()) {
            int[] re = DateUtils.getInterval(alarm.getNextDate(), System.currentTimeMillis());
            StringBuffer sb = new StringBuffer();
            if (re[0] != 0) {
                sb.append(re[0] + getString(R.string.day2));
            }
            if (re[1] != 0) {
                sb.append(re[1] + getString(R.string.hours));
            }
            if (re[2] != 0) {
                sb.append(re[2] + getString(R.string.minutes));
            }
            if (re[3] != 0) {
                sb.append(re[3] + getString(R.string.second));
            }
            s = getString(R.string.add_alarm_success_0) + sb.toString() + getString(R.string.add_alarm_success_1);
        }
        ViewUtils.toast(s);
        ViewUtils.HideKeyboard(mName);
        //fab按钮回到初始位置
        mMainActivity.mMainFragment.mFab.animate().translationY(0).setDuration(200);
    }

    public void editAlarm(Alarm alarm, int position) {
        mLingshengUri = null;
        mType = alarm.getType();
        mEditPosition = position;
        mEditAlarm = alarm;
        mTitle.setText(getString(R.string.edit_alarm));
        mDelBtn.setVisibility(View.VISIBLE);

        mTime.setText(alarm.getTime());
        mDate.setText(alarm.getDate());
        mCurDate.set(Calendar.YEAR, Integer.parseInt(alarm.getDate().substring(0, 4)));
        mCurDate.set(Calendar.MONTH, Integer.parseInt(alarm.getDate().substring(5, 7)) - 1);
        mCurDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(alarm.getDate().substring(8)));
        mCurDate.set(Calendar.HOUR, Integer.parseInt(alarm.getTime().substring(0, 2)));
        mCurDate.set(Calendar.MINUTE, Integer.parseInt(alarm.getTime().substring(3)));
        mCurDate.set(Calendar.SECOND, 0);
        mCurDate.set(Calendar.MILLISECOND, 0);

        mOpen.setChecked(alarm.getIsOpen());
        if (mType == App.ALARM_TYPE_ONE) {
            changeType(mTypeOne,false);
        } else if (mType == App.ALARM_TYPE_WORK) {
            changeType(mTypeWork,false);
        } else if (mType == App.ALARM_TYPE_WEEK) {
            changeType(mTypeWeek,false);
            mWeekArr = alarm.getWeeks().split(",");
            selectWeek(mWeek1, 1);
            selectWeek(mWeek2, 2);
            selectWeek(mWeek3, 3);
            selectWeek(mWeek4, 4);
            selectWeek(mWeek5, 5);
            selectWeek(mWeek6, 6);
            selectWeek(mWeek7, 7);
            selectWeek(mWeek1, 1);
            selectWeek(mWeek2, 2);
            selectWeek(mWeek3, 3);
            selectWeek(mWeek4, 4);
            selectWeek(mWeek5, 5);
            selectWeek(mWeek6, 6);
            selectWeek(mWeek7, 7);

        } else if (mType == App.ALARM_TYPE_MONTH) {
            changeType(mTypeMonth,false);
            mDayOfMonth.selectIndex(alarm.getDay() - 1);
        } else if (mType == App.ALARM_TYPE_YEAR) {
            changeType(mTypeYear,false);
        } else if (mType == App.ALARM_TYPE_YEAR_CN) {
            changeType(mTypeYearCn,false);
        } else if (mType == App.ALARM_TYPE_ZDY) {
            changeType(mTypeZdy,false);
            mZdyWork.setText(alarm.getWorkDays() + "");
            mZdyUnWork.setText(alarm.getUnWorkDays() + "");
            Date dateone = DateUtils.getDate(alarm.getDate(), alarm.getTime());
            int days = DateUtils.getIntervalDays(dateone, new Date());
            int cur = days % (alarm.getWorkDays() + alarm.getUnWorkDays()) + 1;
            mZdyCur.setText(cur + "");
        }

        mZhendong.setChecked(alarm.isVibration());
        mJianqiang.setChecked(alarm.isJianqiang());
        if (!TextUtils.isEmpty(alarm.getName())) {
            mName.setText(alarm.getName());
        } else {
            mName.setText("");
        }
        mYinliangRang.setSeekPinByValue(alarm.getVolume());

        //铃声
        mLingshengUri = Uri.parse(alarm.getMusicUri());
        if (!ViewUtils.uriIsMedia(mLingshengUri)) {
            mLingshengUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        //根据音乐的uri获得文件名。比较耗时所以用新线程执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                String lingshengName = ViewUtils.getStringByRingtoneUri(mLingshengUri);
                Message msg = new Message();
                msg.obj = lingshengName;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void delAlarm() {
        mMainActivity.mMainFragment.mViewPager.setCurrentItem(1);
        mMainActivity.mMainFragment.mAlarmFragment.notifyItemRemoved(mEditPosition);
    }

    public void initViewDate() {
        mEditAlarm = null;
        mTitle.setText(getString(R.string.add_alarm));
        mDelBtn.setVisibility(View.GONE);
        mName.setText("");
        //初始化默认选择的闹钟类型
        changeType(mTypeOne,true);
        mZdyWork.setText("");
        mZdyUnWork.setText("");
        mZdyCur.setText("");
        mZhendong.setChecked(false);
        mJianqiang.setChecked(true);
        mOpen.setChecked(true);

        //默认选中周一到周日
        mWeekArr = new String[]{"0", "0", "0", "0", "0", "0", "0"};
        selectWeek(mWeek1, 1);
        selectWeek(mWeek2, 2);
        selectWeek(mWeek3, 3);
        selectWeek(mWeek4, 4);
        selectWeek(mWeek5, 5);
        selectWeek(mWeek6, 6);
        selectWeek(mWeek7, 7);

        //初始化音量调整控件
        mLingshengUri = null;
        mYinliangRang.setSeekPinByValue(60);

        //铃声
        mLingshengUri = App.mDefaultLingsheng;
        //根据音乐的uri获得文件名。比较耗时所以用新线程执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                String lingshengName = ViewUtils.getStringByRingtoneUri(mLingshengUri);
                Message msg = new Message();
                msg.obj = lingshengName;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void initTime() {
        mCurDate = new GregorianCalendar();
        //默认下一天
        //mCurDate.add(Calendar.DAY_OF_MONTH, 1);
        mDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(mCurDate.getTime()));
        mTime.setText(new SimpleDateFormat("HH:mm").format(mCurDate.getTime()));

        mDayOfMonth.selectIndex(mCurDate.get(Calendar.DAY_OF_MONTH) - 1);
    }

    private void selectWeek(TextView v, int i) {
        if (mWeekArr[i - 1].equals("0")) {
            mWeekArr[i - 1] = "1";
            v.setBackground(getResources().getDrawable(R.drawable.shape_point));
            v.setTextColor(App.mColorPrimary);
        } else {
            mWeekArr[i - 1] = "0";
            v.setBackground(null);
            v.setTextColor(getResources().getColor(R.color.white_t));
        }
    }

    private void selectDate() {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this, mCurDate.get(Calendar.YEAR), mCurDate.get(Calendar.MONTH), mCurDate.get(Calendar.DAY_OF_MONTH));
        dpd.setTitle(getString(R.string.select_date));
        dpd.show(mMainActivity.getFragmentManager(), "Datepickerdialog");
    }

    private void selectTime() {
        int h = Integer.parseInt(mTime.getText().toString().substring(0, 2));
        int m = Integer.parseInt(mTime.getText().toString().substring(3, 5));
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, h, m, true);
        tpd.setTitle(getString(R.string.select_time));
        tpd.show(mMainActivity.getFragmentManager(), "Timepickerdialog");
    }

    private void changeType(View v,boolean openSelect) {
        Drawable drawable_selected = getResources().getDrawable(R.drawable.btn_background_white_1);
        if (mSelectedTvType != null) {
            mSelectedTvType.setBackgroundDrawable(null);
        }
        v.setBackground(drawable_selected);
        mSelectedTvType = v;
        if (mSelectView != null) {
            mSelectView.setVisibility(View.GONE);
        }
        int id = v.getId();
        switch (id) {
            case R.id.type_one:
                mType = App.ALARM_TYPE_ONE;
                mDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(mCurDate.getTime()));
                mDate.setVisibility(View.VISIBLE);
                mSelectView = mDate;
                break;
            case R.id.type_work:
                mType = App.ALARM_TYPE_WORK;
                break;
            case R.id.type_week:
                mType = App.ALARM_TYPE_WEEK;
                mWeekView.setVisibility(View.VISIBLE);
                mSelectView = mWeekView;
                break;
            case R.id.type_month:
                mType = App.ALARM_TYPE_MONTH;
                mDayOfMonth.setVisibility(View.VISIBLE);
                mSelectView = mDayOfMonth;
                break;
            case R.id.type_year:
                mType = App.ALARM_TYPE_YEAR;
                mDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(mCurDate.getTime()));
                mDate.setVisibility(View.VISIBLE);
                mSelectView = mDate;
                if (openSelect) {
                    selectDate();
                }
                break;
            case R.id.type_year_cn:
                String lunar = mDate.getText().toString();
                if (mType != App.ALARM_TYPE_YEAR_CN) {
                    lunar = LunarCalendar.solarToLunar(mDate.getText().toString());
                }
                mType = App.ALARM_TYPE_YEAR_CN;
                mDate.setVisibility(View.VISIBLE);
                if (openSelect) {
                    selectDateCn(lunar);
                }
                mSelectView = mDate;
                break;
            case R.id.type_zdy:
                mType = App.ALARM_TYPE_ZDY;
                mSelectZdy.setVisibility(View.VISIBLE);
                mSelectView = mSelectZdy;
                break;
        }
    }

    private void selectDateCn(String lunar) {
        mBottomSheetDialog = new BottomSheetDialog(mMainActivity, R.style.Material_App_BottomSheetDialog);
        View v = LayoutInflater.from(mMainActivity).inflate(R.layout.bottomsheet_nongli, null);
        v.setBackgroundColor(App.mColorPrimary);
        mDateCn = (DatePicker) v.findViewById(R.id.select_date_cn);
        com.rey.material.widget.Button btn_close = (Button) v.findViewById(R.id.btn_close);
        if (!TextUtils.isEmpty(lunar) && lunar.length() == 9) {
            mDateCn.setDefault(lunar);
        }
        //mBottomSheetDialog.heightParam(ViewGroup.LayoutParams.MATCH_PARENT);
        mBottomSheetDialog.contentView(v).show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDate.setText(mDateCn.getDate());
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mCi != null) {
            mCi.pause();
        }
        switch (id) {
            case R.id.week_1_p:
                selectWeek(mWeek1, 1);
                break;
            case R.id.week_2_p:
                selectWeek(mWeek2, 2);
                break;
            case R.id.week_3_p:
                selectWeek(mWeek3, 3);
                break;
            case R.id.week_4_p:
                selectWeek(mWeek4, 4);
                break;
            case R.id.week_5_p:
                selectWeek(mWeek5, 5);
                break;
            case R.id.week_6_p:
                selectWeek(mWeek6, 6);
                break;
            case R.id.week_7_p:
                selectWeek(mWeek7, 7);
                break;
            case R.id.select_date:
                if (mType != App.ALARM_TYPE_YEAR_CN) {
                    selectDate();
                } else {
                    selectDateCn(mDate.getText().toString());
                }
                break;
            case R.id.time:
                selectTime();
                break;
            case R.id.lingsheng:
                mMainActivity.selectLingsheng(mLingshengUri, App.SELECT_LINGSHENG_FLG_ADD_ALARM);
                break;
            case R.id.add_alarm_del:
                delAlarm();
                break;
            case R.id.name:
                break;
            default:
                changeType(v,true);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar mCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(mCalendar.getTime()));
        mCurDate = mCalendar;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = DateUtils.getTime("2016-01-01 " + hourOfDay + ":" + minute, "HH:mm");
        mTime.setText(time);
        //如果选择的时间已经过去，那日期改为下一天
        if (mType == App.ALARM_TYPE_ONE) {
            Date selectDate = DateUtils.getDate(mDate.getText().toString(), mTime.getText().toString());
            if (selectDate.before(new Date())) {
                mDate.setText(DateUtils.DateToString(DateUtils.addDay(new Date(), 1), "yyyy-MM-dd"));
            } else {
                mDate.setText(DateUtils.DateToString(selectDate, "yyyy-MM-dd"));
            }
        }
    }

    public void lingshengSelected(Uri pickedUri) {
        if (pickedUri != null && pickedUri != mLingshengUri) {
            mLingshengUri = pickedUri;
            mLingsheng.setText(ViewUtils.getStringByRingtoneUri(pickedUri));
        }
    }

    public void stopMusic() {
        if (mCi != null) {
            mCi.stop();
        }
    }

    @Override
    public void onDestroy() {
        if (mServiceConnection != null) {
            mMainActivity.unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mCi != null) {
            mCi.stop();
        }
    }
}
