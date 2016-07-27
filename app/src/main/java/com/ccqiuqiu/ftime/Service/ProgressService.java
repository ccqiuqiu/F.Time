package com.ccqiuqiu.ftime.Service;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.DateUtils;
import com.ccqiuqiu.ftime.Utils.MathUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cc on 2016/3/8.
 */
public class ProgressService extends BaseService {

    public void saveProgress(Progress progress) {
        progress.setOrder(getMaxOrder() + 1);
        super.saveBindingId(progress);
        if (progress.getType() != App.PROGRESS_TYPE_TIME && progress.getTixing() != 0) {
            progress.setNextDate(getNext(progress));
            AlarmUtils.setAlarm(progress);
        }
    }

    public void saveProgress_undo(Progress progress) {
        if (progress.getType() != App.PROGRESS_TYPE_TIME && progress.getTixing() != 0) {
            progress.setNextDate(getNext(progress));
            AlarmUtils.setAlarm(progress);
        }
        super.saveBindingId(progress);
    }

    private int getMaxOrder() {
        try {
            return getAllSelector(Progress.class).orderBy("order", true).findFirst().getOrder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Progress> getAll() {
        List<Progress> re = null;
        try {
            re = getAllSelector(Progress.class).orderBy("order", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (re == null) {
            re = new ArrayList<>();
        }
        return re;
    }

    public void del(Progress progress) {
        //先删除闹钟
        AlarmUtils.delAlarm(progress);
        super.delete(progress);
    }

    public void move(List<Progress> mItems, int fromPosition, int toPosition) {
        final Progress item = mItems.get(toPosition);
        item.setOrder(item.getOrder() + fromPosition - toPosition);
        update(item);
        //更新排序
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Progress itemTemp = mItems.get(i);
                itemTemp.setOrder(itemTemp.getOrder() + 1);
                update(itemTemp);
            }
        } else {
            for (int i = toPosition + 1; i <= fromPosition; i++) {
                Progress itemTemp = mItems.get(i);
                itemTemp.setOrder(itemTemp.getOrder() - 1);
                update(itemTemp);
            }
        }
    }

    public void updateProgress(Progress progress) {
        //先删除原有闹钟
        AlarmUtils.delAlarm(progress);
        if (progress.getType() != App.PROGRESS_TYPE_TIME && progress.getTixing() != 0) {
            progress.setNextDate(getNext(progress));
            AlarmUtils.setAlarm(progress);
        }
        super.update(progress);
    }

    public long getNext(Progress progress) {
        //结束时间
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(Calendar.YEAR, Integer.parseInt(progress.getEnd().substring(0, 4)));
        calendarEnd.set(Calendar.MONTH, Integer.parseInt(progress.getEnd().substring(5, 7)) - 1);
        calendarEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getEnd().substring(8)));
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        calendarEnd.set(Calendar.MINUTE, 0);
        calendarEnd.set(Calendar.SECOND, 0);
        calendarEnd.set(Calendar.MILLISECOND,0);
        //当前时间大于结束时间，说明进度条已经过期，停止闹钟
        if(System.currentTimeMillis() >= calendarEnd.getTimeInMillis()){
            return 0;
        }

        if(progress.getType() == App.PROGRESS_TYPE_DATE){
            //日期倒计时的下期就是结束时间
            return  calendarEnd.getTimeInMillis();
        }

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(Calendar.YEAR, Integer.parseInt(progress.getStart().substring(0, 4)));
        calendarStart.set(Calendar.MONTH, Integer.parseInt(progress.getStart().substring(5, 7)) - 1);
        calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getStart().substring(8)));
        long startTime = calendarStart.getTimeInMillis();
        if (startTime > System.currentTimeMillis()) {
            return calendarStart.getTimeInMillis();
        } else {
            Calendar calendarXiaqi = Calendar.getInstance();
            calendarXiaqi.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getStart().substring(8)));
            calendarXiaqi.set(Calendar.HOUR_OF_DAY, 0);
            calendarXiaqi.set(Calendar.MINUTE, 0);
            calendarXiaqi.set(Calendar.SECOND, 0);
            calendarXiaqi.set(Calendar.MILLISECOND,0);
            if (calendarXiaqi.getTimeInMillis() <= System.currentTimeMillis()) {
                calendarXiaqi.add(Calendar.MONTH, 1);
            }
            return calendarXiaqi.getTimeInMillis();
        }
    }

    public long getNextDate(Progress progress) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(progress.getNextDate());
//        if (calendar == null) {
//            calendar = Calendar.getInstance();
//            if (progress.getType() == App.PROGRESS_TYPE_DATE) {
//                calendar.setTime(DateUtils.StringToDate(progress.getEnd(), "yyyy-MM-dd"));
//            } else {
//                //计算下期时间
//                long xiaqi = App.getProgressService().getNext(progress);
//                //没有跳过
//                if (progress.getNextDate() >= xiaqi) {
//                    xiaqi = progress.getNextDate();
//                }
//                calendar.setTimeInMillis(xiaqi);
//            }
//        }
        int day = 0;
        if (progress.getTixing() == 1) {
            day = 0;
        } else if (progress.getTixing() == 2) {
            day = 1;
        } else if (progress.getTixing() == 3) {
            day = 3;
        } else if (progress.getTixing() == 4) {
            day = 7;
        }
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        calendar.set(Calendar.HOUR_OF_DAY, 10);//10点钟提醒
        //calendar.set(Calendar.MINUTE,3);
        calendar.set(Calendar.MINUTE, MathUtils.random(31));//在10：00---10:30随机响
        return calendar.getTimeInMillis();
    }
}
