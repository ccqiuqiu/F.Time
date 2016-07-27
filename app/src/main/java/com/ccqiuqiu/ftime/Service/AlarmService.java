package com.ccqiuqiu.ftime.Service;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Holiday;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.DateUtils;
import com.ccqiuqiu.ftime.Utils.DbUtils;
import com.ccqiuqiu.ftime.Utils.LunarCalendar;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cc on 2016/3/8.
 */
public class AlarmService extends BaseService {

    public void saveAlarm(Alarm alarm) {
        alarm.setOrder(getMaxOrder() + 1);
        super.saveBindingId(alarm);
        if(alarm.getIsOpen()){
            //设置系统闹钟
            AlarmUtils.setAlarm(alarm);
        }
    }
    public void saveAlarm_undo(Alarm alarm) {
        super.saveBindingId(alarm);
        if(alarm.getIsOpen()){
            //设置系统闹钟
            AlarmUtils.setAlarm(alarm);
        }
    }

    public void updateAlarm(Alarm alarm) {
        super.update(alarm);
        AlarmUtils.delAlarm(alarm);
        if(alarm.getIsOpen()){
            //设置系统闹钟
            AlarmUtils.setAlarm(alarm);
        }
    }

    private int getMaxOrder() {
        try {
            return getAllSelector(Alarm.class).orderBy("order", true).findFirst().getOrder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Alarm> getAll() {
        List<Alarm> re = null;
        try {
            re = getAllSelector(Alarm.class).orderBy("order", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (re == null) {
            re = new ArrayList<>();
        }
        return re;
    }

    public long getNextDate(Alarm alarm,Date curDate,boolean skip) {
        if(skip){
            //curDate = new Date(alarm.getNextDate());
            curDate = DateUtils.addDay(curDate,1);
        }
        Date nextDate = DateUtils.getDate(DateUtils.DateToString(curDate, "yyyy-MM-dd"), alarm.getTime());
        if (alarm.getType() == App.ALARM_TYPE_ONE) {
            //如果选择的时间已经过去，那日期改为下一天
            Date date = DateUtils.getDate(alarm.getDate(), alarm.getTime());
            nextDate = date;
            if (date.before(curDate)) {
                nextDate = DateUtils.addDay(nextDate, 1);
            }
        } else if (alarm.getType() == App.ALARM_TYPE_WORK) {
            Date date = DateUtils.getDate(DateUtils.DateToString(curDate, "yyyy-MM-dd"), alarm.getTime());
            //如果时间已经过去，直接加一天
            if (date.before(curDate)) {
                nextDate = DateUtils.addDay(nextDate, 1);
            }
            //如果不是工作日，加一天
            while (!isWork(nextDate)) {
                nextDate = DateUtils.addDay(nextDate, 1);
            }
        } else if (alarm.getType() == App.ALARM_TYPE_WEEK) {
            //需要增加的天数
            int day = 0;
            //现在是星期几
            int curWeek = DateUtils.getWeek(curDate).getNumber();
            //时间已经过去
            Date date = DateUtils.getDate(DateUtils.DateToString(curDate, "yyyy-MM-dd"), alarm.getTime());
            if (date.before(curDate)) {
                curWeek++;
                day++;
                if (curWeek == 8) {
                    curWeek = 1;
                }
            }
            //取到星期数据，并重新排序，让今天在第一位
            String[] weeksArr = alarm.getWeeks().split(",");
            String[] arr1 = new String[weeksArr.length - curWeek + 1];
            String[] arr2 = new String[curWeek - 1];
            System.arraycopy(weeksArr, arr2.length, arr1, 0, arr1.length);
            System.arraycopy(weeksArr, 0, arr2, 0, arr2.length);
            List<String> weeksList = new ArrayList<>();
            weeksList.addAll(Arrays.asList(arr1));
            weeksList.addAll(Arrays.asList(arr2));

            //循环数组，找到下一个响铃的星期，计算要跳过几天
            for (String s : weeksList) {
                int i = Integer.parseInt(s.trim());
                if (i == 0) {
                    day++;
                } else {
                    break;
                }
            }
            nextDate = DateUtils.addDay(nextDate, day);
        } else if (alarm.getType() == App.ALARM_TYPE_MONTH) {
            //获取本月最后一天
            int lastDay = DateUtils.getLastDayOfMonth();
            //取有效的日期
            int day = Math.min(lastDay, alarm.getDay());
            //组装本月的闹钟日期
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.getTime().substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(alarm.getTime().substring(3, 5)));
            calendar.set(Calendar.SECOND, 0);
            //如果时间已经过去，加一个月
            if (calendar.getTimeInMillis() <= curDate.getTime()) {
                calendar.add(Calendar.MONTH, 1);
            }
            nextDate = calendar.getTime();
        } else if (alarm.getType() == App.ALARM_TYPE_YEAR) {
            Date date = DateUtils.getDate(alarm.getDate(), alarm.getTime());
            if (date.before(curDate)) {
                nextDate = DateUtils.addYear(date, 1);
            } else {
                nextDate = date;
            }
        } else if (alarm.getType() == App.ALARM_TYPE_YEAR_CN) {
            //农历先转为公历
            String dateStr = LunarCalendar.lunarToSolar(alarm.getDate());
            Date date = DateUtils.getDate(dateStr, alarm.getTime());
            //如果时间已过，取到阴历的年份加1，再转公历
            if (date.before(curDate)) {
                int year = Integer.parseInt(alarm.getDate().substring(0, 4)) + 1;
                dateStr = LunarCalendar.lunarToSolar(year + alarm.getDate().substring(4));
                nextDate = DateUtils.getDate(dateStr, alarm.getTime());
            } else {
                nextDate = date;
            }
        } else if (alarm.getType() == App.ALARM_TYPE_ZDY) {
            int work = alarm.getWorkDays();
            int unWork = alarm.getUnWorkDays();
            //第一天闹钟时间
            Date dateone = DateUtils.getDate(alarm.getDate(), alarm.getTime());
            //和当前相差的天数
            int days = DateUtils.getIntervalDays(dateone, curDate);
            //如果时间已经过去，那么相隔天数加1
            Date date = DateUtils.getDate(DateUtils.DateToString(curDate, "yyyy-MM-dd"), alarm.getTime());
            if (date.before(curDate)) {
                days++;
                nextDate = DateUtils.addDay(nextDate, 1);
            }
            //下一次是周期里面的第几天第几天
            int cur = days % (work + unWork) + 1;
            if (cur > work) {
                nextDate = DateUtils.addDay(nextDate, unWork - (cur - work) + 1);
            }
        }
        return nextDate.getTime();
    }

    public boolean isWork(Date date) {
        int week = DateUtils.getWeek(date).getNumber();
        if (week > 5) {//休息
            //判断是否加班
            try {
                Holiday holiday = getAllSelector(Holiday.class).where("year", "=", DateUtils.getYear(date))
                        .and("month", "=", DateUtils.getMonth(date))
                        .and("date", "=", DateUtils.getDay(date)).findFirst();
                if (holiday != null && holiday.getStatus() == Holiday.JIABAN) {
                    return true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            //判断是否休息
            try {
                Holiday holiday = getAllSelector(Holiday.class).where("year", "=", DateUtils.getYear(date))
                        .and("month", "=", DateUtils.getMonth(date))
                        .and("date", "=", DateUtils.getDay(date)).findFirst();
                if (holiday != null && holiday.getStatus() == Holiday.XIUXI) {
                    return false;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void del(Alarm alarmDel) {
        if (alarmDel != null) {
            //删除系统闹钟
            AlarmUtils.delAlarm(alarmDel);
            super.delete(alarmDel);
        }
    }

    public void move(List<Alarm> mItems, int fromPosition, int toPosition) {
        final Alarm alarm = mItems.get(toPosition);
        alarm.setOrder(alarm.getOrder() + fromPosition - toPosition);
        update(alarm);
        //更新排序
        if(fromPosition < toPosition){
            for(int i = fromPosition;i < toPosition;i++){
                Alarm alarmTemp = mItems.get(i);
                alarmTemp.setOrder(alarmTemp.getOrder() + 1);
                update(alarmTemp);
            }
        }else{
            for(int i = toPosition+1;i <= fromPosition;i++){
                Alarm alarmTemp = mItems.get(i);
                alarmTemp.setOrder(alarmTemp.getOrder() - 1);
                update(alarmTemp);
            }
        }
    }
}
