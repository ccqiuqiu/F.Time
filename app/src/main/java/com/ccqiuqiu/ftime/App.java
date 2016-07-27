package com.ccqiuqiu.ftime;

import android.Manifest;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import com.ccqiuqiu.ftime.Service.AlarmService;
import com.ccqiuqiu.ftime.Service.HolidayService;
import com.ccqiuqiu.ftime.Service.ProgressService;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.Arrays;
import java.util.List;

public class App extends Application {
    public static final int ALARM_TYPE_ONE = 0;
    public static final int ALARM_TYPE_WORK = 1;
    public static final int ALARM_TYPE_WEEK = 2;
    public static final int ALARM_TYPE_MONTH = 3;
    public static final int ALARM_TYPE_YEAR = 4;
    public static final int ALARM_TYPE_YEAR_CN = 5;
    public static final int ALARM_TYPE_ZDY = 6;
    public static final int PROGRESS_TYPE_TIME = 0;
    public static final int PROGRESS_TYPE_DATE = 1;
    public static final int PROGRESS_TYPE_FENQI = 3;
    public static final int SELECT_LINGSHENG_FLG_DRAG = 90;
    public static final int SELECT_LINGSHENG_FLG_ADD_ALARM = 91;

    public static Context mContext;
    public static int mColorPrimary;
    public static int mColorPrimaryDark;
    public static int mColorAccent;
    public static String[] mWeek;
    public static Uri mDefaultLingsheng;
    public static List<String> mTixings;
    public static int mShichang;
    public static int mZanting;

    private static AlarmService mAlarmService;
    private static ProgressService mProgressService;
    public static int mSysMusicVol;
    public static int mSysAlarmVol;
    private int mHolidayVer = 20161231;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        Dexter.initialize(this);
        mContext = this.getBaseContext();

        mTixings = Arrays.asList(getString(R.string.tx_0)
                , getString(R.string.tx_1), getString(R.string.tx_2)
                , getString(R.string.tx_3), getString(R.string.tx_4));
        mWeek = new String[]{getString(R.string.week_21), getString(R.string.week_22), getString(R.string.week_23)
                , getString(R.string.week_24), getString(R.string.week_25), getString(R.string.week_26), getString(R.string.week_27)};

        mShichang = ViewUtils.getIntBySharedPreferences("sett_lingsheng_shichang", 5);
        mZanting = ViewUtils.getIntBySharedPreferences("sett_zanting", 10);
        //默认的闹铃铃声
        String lingsheng_d = ViewUtils.getStringBySharedPreferences("default_lingsheng");
        Uri uri = null;
        if (lingsheng_d == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ViewUtils.putStringToSharedPreferences("default_lingsheng", uri.toString());
        } else {
            uri = Uri.parse(lingsheng_d);
        }
        //检查是否是正确的音乐文件
        if (!ViewUtils.uriIsMedia(uri)) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ViewUtils.putStringToSharedPreferences("default_lingsheng", uri.toString());
        }
        mDefaultLingsheng = uri;


        //如果节假日数据更新,初始化节假日表
        int holidayVer = ViewUtils.getIntBySharedPreferences("holiday_ver", 0);
        if (holidayVer < mHolidayVer) {
            initHoliday();
            ViewUtils.putIntToSharedPreferences("holiday_ver", mHolidayVer);
        }
    }

    public static AlarmService getAlarmService() {
        if (mAlarmService == null) {
            mAlarmService = new AlarmService();
        }
        return mAlarmService;
    }

    public static ProgressService getProgressService() {
        if (mProgressService == null) {
            mProgressService = new ProgressService();
        }
        return mProgressService;
    }

    public static List<Integer> getImgIds() {
        return Arrays.asList(R.mipmap.zs0, R.mipmap.zs1, R.mipmap.zs2, R.mipmap.zs3, R.mipmap.zs4, R.mipmap.zs5
                , R.mipmap.zs6, R.mipmap.zs7, R.mipmap.zs8, R.mipmap.zs9, R.mipmap.zs10, R.mipmap.zs11
                , R.mipmap.zs12, R.mipmap.zs13, R.mipmap.zs14, R.mipmap.zs15, R.mipmap.zs16, R.mipmap.zs17
                , R.mipmap.zs18, R.mipmap.zs19, R.mipmap.zs20, R.mipmap.zs21, R.mipmap.zs22, R.mipmap.zs23
                , R.mipmap.zs24, R.mipmap.zs25, R.mipmap.zs26, R.mipmap.zs27, R.mipmap.zs28, R.mipmap.zs29
                , R.mipmap.zs30, R.mipmap.zs31, R.mipmap.zs32, R.mipmap.zs33, R.mipmap.zs34, R.mipmap.zs35
                , R.mipmap.zs36, R.mipmap.zs37, R.mipmap.zs38, R.mipmap.zs39, R.mipmap.zs40, R.mipmap.zs41, R.mipmap.zs42);
    }

    private void initHoliday() {
        HolidayService holidayService = new HolidayService();
        try {
            holidayService.db.getDatabase().beginTransaction();
            try {
                //删除所有数据
                holidayService.db.execNonQuery("delete holiday");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String sql = "CREATE TABLE holiday (id integer primary key autoincrement, " +
                    "year text not null, month long not null,date text not null,status text not null)";
            holidayService.db.execNonQuery(sql.toString());

            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 1, 1, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 1, 2, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 1, 3, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 6, 1)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 7, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 8, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 9, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 10, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 11, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 12, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 13, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 2, 14, 1)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 4, 2, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 4, 3, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 4, 4, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 4, 30, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 5, 1, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 5, 2, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 6, 9, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 6, 10, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 6, 11, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 6, 12, 1)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 9, 15, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 9, 16, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 9, 17, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 9, 18, 1)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 1, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 2, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 3, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 4, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 5, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 6, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 7, 0)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 8, 1)";
            holidayService.db.execNonQuery(sql.toString());
            sql = "INSERT INTO holiday (year, month, date, status) VALUES (2016, 10, 9, 1)";
            holidayService.db.execNonQuery(sql.toString());

            holidayService.db.getDatabase().setTransactionSuccessful();
            holidayService.db.getDatabase().endTransaction();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
