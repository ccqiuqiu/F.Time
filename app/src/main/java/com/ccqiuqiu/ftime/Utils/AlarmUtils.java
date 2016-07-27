package com.ccqiuqiu.ftime.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.BroadcastReceiver.AlarmStopBroadcastReceiver;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cc on 2016/3/25.
 */
public class AlarmUtils {

    public static final int FLG_ALARM = 0;
    public static final int FLG_PROGRESS = 1;
    public static final int FLG_SNOOZE = 20;
    public static final int FLG_DISMISS = 21;

    public static void setAlarm(Alarm alarm) {
        if (alarm.getNextDate() <= System.currentTimeMillis()) return;
        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("com.ccqiuqiu.ftime.alarm");
        intent.putExtra("id", alarm.getId());
        intent.putExtra("flg", AlarmUtils.FLG_ALARM);
        intent.putExtra("alarm_time", alarm.getNextDate());
        PendingIntent sender = PendingIntent.getBroadcast(
                App.mContext, alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getNextDate(), 0, sender);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getNextDate(), sender);
//        }else{
//            am.setExact(AlarmManager.RTC_WAKEUP, alarm.getNextDate(), sender);
//        }
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarm.getNextDate(),sender);
        am.setAlarmClock(alarmClockInfo,sender);

        //System.out.println("=====设置闹钟====" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(alarm.getNextDate())));
        ViewUtils.writeTxtFile("设置闹钟-" + alarm.getName() + "，时间-"+
        new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(alarm.getNextDate())));
    }

    public static void setAlarm(int id) {
        long time = System.currentTimeMillis() + 10 * 1000;
        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("com.ccqiuqiu.ftime.alarm");
        intent.putExtra("id", id);
        intent.putExtra("flg", AlarmUtils.FLG_ALARM);
        intent.putExtra("alarm_time", time);
        PendingIntent sender = PendingIntent.getBroadcast(
                App.mContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, sender);
//        }else{
//            am.setExact(AlarmManager.RTC_WAKEUP, time, sender);
//        }
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time,sender);
        am.setAlarmClock(alarmClockInfo,sender);
    }

    //    public static void setAlarm(Progress progress, long time) {
//        if (time == 0) {
//            time = getAlarmTime(progress, null);
//        }
//        //如果闹钟时间在当前时间之前，不用设置闹钟
////        Calendar now = Calendar.getInstance();
////        now.set(Calendar.HOUR_OF_DAY, 0);
////        now.set(Calendar.MINUTE, 0);
////        now.set(Calendar.SECOND, 0);
//        if (time <= System.currentTimeMillis()) {
//            return;
//        }
//
//        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent();
//        intent.setAction("com.ccqiuqiu.ftime.alarm");
//        intent.putExtra("id", progress.getId());
//        intent.putExtra("flg", AlarmUtils.FLG_PROGRESS);
//        intent.putExtra("alarm_time", time);
//        PendingIntent sender = PendingIntent.getBroadcast(
//                App.mContext, -progress.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, sender);
//        System.out.println("=====设置闹钟(进度条)====" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time)));
//    }
    public static void setAlarm(Progress progress) {
        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
        long time = App.getProgressService().getNextDate(progress);
        if(time <= System.currentTimeMillis())return;
        Intent intent = new Intent();
        intent.setAction("com.ccqiuqiu.ftime.alarm");
        intent.putExtra("id", progress.getId());
        intent.putExtra("flg", AlarmUtils.FLG_PROGRESS);
        intent.putExtra("alarm_time", time);
        PendingIntent sender = PendingIntent.getBroadcast(
                App.mContext, -progress.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, sender);
//        }else{
//            am.setExact(AlarmManager.RTC_WAKEUP, time, sender);
//        }
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time,sender);
        am.setAlarmClock(alarmClockInfo,sender);


        //System.out.println("=====设置闹钟(进度条)====" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time));
        ViewUtils.writeTxtFile("设置闹钟（进度条）-" + progress.getName() + "，时间-"+
                new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(time)));
    }

    public static void delAlarm(Alarm alarm) {
        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("com.ccqiuqiu.ftime.alarm");
        PendingIntent sender = PendingIntent.getBroadcast(
                App.mContext, alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);

    }

    public static void delAlarm(Progress progress) {
        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("com.ccqiuqiu.ftime.alarm");
        PendingIntent sender = PendingIntent.getBroadcast(
                App.mContext, -progress.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }

    public static void setNextAlarm(int flg, int id) {
        if (flg == FLG_ALARM) {
            Alarm alarm = App.getAlarmService().getById(Alarm.class, id);
            if (alarm == null) return;
            if (alarm.getType() == App.ALARM_TYPE_ONE) {
                alarm.setIsOpen(false);
            } else {
                alarm.setNextDate(App.getAlarmService().getNextDate(alarm, new Date(),false));
            }
            App.getAlarmService().updateAlarm(alarm);
        } else {
            Progress progress = App.getProgressService().getById(Progress.class, Math.abs(id));
            if (progress == null) return;
            //只有分期付款需要设置下一次提醒
            if (progress.getType() == App.PROGRESS_TYPE_FENQI && progress.getTixing() != 0) {
//                long time = App.getProgressService().getNextDate(progress);
//                progress.setNextDate(time);
                long time = progress.getNextDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                //跳过一期
                calendar.add(Calendar.MONTH, 1);
                progress.setNextDate(calendar.getTimeInMillis());
                App.getProgressService().update(progress);
                AlarmUtils.setAlarm(progress);
            }
        }
    }

    public static void setNextAlarm(int flg, int id, long time) {
        AlarmManager am = (AlarmManager) App.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("com.ccqiuqiu.ftime.alarm");
        intent.putExtra("id", id);
        intent.putExtra("flg", flg);
        intent.putExtra("yanchi_time", time);
        PendingIntent sender = PendingIntent.getBroadcast(
                App.mContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, sender);

        ViewUtils.writeTxtFile("延迟闹钟" + "，时间-"+
                new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(time)));
    }

    public static void initAlarm() {
        List<Alarm> alarms = App.getAlarmService().getAll();
        List<Progress> progresses = App.getProgressService().getAll();
        for (Alarm alarm : alarms) {
            if (alarm.getNextDate() <= System.currentTimeMillis()) {
                if (alarm.getType() == App.ALARM_TYPE_ONE) {
                    alarm.setIsOpen(false);
                    App.getAlarmService().update(alarm);
                }
                alarm.setNextDate(App.getAlarmService().getNextDate(alarm, new Date(),false));
                App.getAlarmService().updateAlarm(alarm);
            } else {
                if (alarm.getIsOpen()) {
                    setAlarm(alarm);
                }
            }
        }
        for (Progress progress : progresses) {
            if (progress.getType() != App.PROGRESS_TYPE_TIME && progress.getTixing() != 0) {
                if (progress.getNextDate() == 0) {
                    continue;
                }
                long time = progress.getNextDate();
                if (time <= System.currentTimeMillis()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    //跳过一期
                    calendar.add(Calendar.MONTH, 1);
                    progress.setNextDate(calendar.getTimeInMillis());
                    App.getProgressService().update(progress);
                    AlarmUtils.setAlarm(progress);
                } else {
                    AlarmUtils.setAlarm(progress);
                }
            }
        }
    }

    public static void setNotification(Context context,int id,int flg,long alarm_time,String title,String content) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_ALARM)
                //.setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_alarm))
                .setSmallIcon(R.drawable.ic_alarm);//设置通知小ICON

        //设置悬挂式通知，保证通知窗口不会自动消失
        Intent fullScreenIntent = new Intent();
        fullScreenIntent.setAction("fullscreen_activity");
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        mBuilder.setFullScreenIntent(PendingIntent.getActivity(context,
                id, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT), true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        //延迟按钮处理
        String yanchi = ViewUtils.getIntBySharedPreferences("sett_zanting", 10) + "";
        Intent snoozeIntent = new Intent(context, AlarmStopBroadcastReceiver.class);
        snoozeIntent.putExtra("id", id);
        snoozeIntent.putExtra("flg", flg);
        snoozeIntent.putExtra("stop_flg", AlarmUtils.FLG_SNOOZE);
        snoozeIntent.putExtra("alarm_time", alarm_time);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context,
                -id, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_snooze,
                context.getString(R.string.yanchi) + yanchi + context.getString(R.string.fenzhong),
                snoozePendingIntent);

        //关闭按钮处理
        Intent dismissIntent = new Intent(context, AlarmStopBroadcastReceiver.class);
        dismissIntent.putExtra("id", id);
        dismissIntent.putExtra("flg", flg);
        dismissIntent.putExtra("stop_flg", AlarmUtils.FLG_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context,
                id, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_alarm_off,
                context.getString(R.string.close), dismissPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(id, notification);
    }
}
