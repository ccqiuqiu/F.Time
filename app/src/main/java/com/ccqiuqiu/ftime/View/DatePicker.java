package com.ccqiuqiu.ftime.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * container 3 wheelView implement timePicker
 * Created by JiangPing on 2015/6/17.
 */
public class DatePicker extends LinearLayout {
    private WheelView mWheelYear;
    private WheelView mWheelMonth;
    private WheelView mWheelDay;
    private ArrayList<String> mYearData, mMonthData, mDayData;
    private OnSelectListener onSelectListener;

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.date_picker, this);
        mWheelYear = (WheelView) findViewById(R.id.year);
        mWheelMonth = (WheelView) findViewById(R.id.month);
        mWheelDay = (WheelView) findViewById(R.id.day);

        mWheelYear.setData(mYearData = getYearData());
        mWheelMonth.setData(mMonthData = getMonthData());
        mWheelDay.setData(mDayData = getDayData());

        mWheelYear.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if(onSelectListener != null){
                    onSelectListener.endSelect(0, id, text);
                }
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
        mWheelMonth.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if(onSelectListener != null){
                    onSelectListener.endSelect(1, id, text);
                }
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
        mWheelDay.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if(onSelectListener != null){
                    onSelectListener.endSelect(2, id, text);
                }
            }

            @Override
            public void selecting(int id, String text) {
            }
        });
    }

    private ArrayList<String> getYearData() {
        int maxYear = DateUtils.getYear(new Date());
        ArrayList<String> list = new ArrayList<>();
        for (int i = maxYear; i >= 1900; i--) {
            list.add(String.valueOf(i) + getContext().getString(R.string.year));
        }
        return list;
    }

    private ArrayList<String> getMonthData() {
        List<String> list = Arrays.asList("正月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "冬月", "腊月");
        return new ArrayList<>(list);
    }

    private ArrayList<String> getDayData() {
        List<String> list = Arrays.asList("初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
                "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十");
        return new ArrayList<>(list);
    }

    public void setDefault(String lunar) {
        String year = lunar.substring(0, 5);
        String month = lunar.substring(5, 7);
        String day = lunar.substring(7);

        mWheelYear.setDefault(mYearData.indexOf(year));
        mWheelMonth.setDefault(mMonthData.indexOf(month));
        mWheelDay.setDefault(mDayData.indexOf(day));
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public String getDate() {
        return mWheelYear.getSelectedText() + mWheelMonth.getSelectedText() + mWheelDay.getSelectedText();
    }

    public interface OnSelectListener {
        void endSelect(int n, int id, String text);
    }
}
