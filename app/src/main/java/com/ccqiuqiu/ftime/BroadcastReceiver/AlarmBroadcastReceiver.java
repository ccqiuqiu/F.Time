package com.ccqiuqiu.ftime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;

import com.ccqiuqiu.ftime.Activity.AlarmActivity;
import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Server.MissAlarmService;
import com.ccqiuqiu.ftime.Server.MusicService;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.DateUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public AlarmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ViewUtils.writeTxtFile("收到闹钟广播");
        int id = intent.getIntExtra("id", 0);
        int flg = intent.getIntExtra("flg", 0);
        long alarm_time = intent.getLongExtra("alarm_time", 0);
        long yanchi_time = intent.getLongExtra("yanchi_time", 0);//正常的闹钟为0，延迟的闹钟会有值
        if (id == 0) return;

        Alarm alarm;
        Progress progress;
        String title, content, yanchiStr = "";
        int vol;
        Uri uri;
        long time;
        boolean jianqiang, zhendong;

        if (yanchi_time == 0) {
        } else {
            yanchiStr = "（" + context.getString(R.string.yanchi) + "）";
        }

        if (flg == AlarmUtils.FLG_ALARM) {
            alarm = App.getAlarmService().getById(Alarm.class, id);
            if (alarm == null) return;

            System.out.println("======闹钟工作，闹钟名称-========" + alarm.getName());
            ViewUtils.writeTxtFile("闹钟工作，闹钟名称-" + alarm.getName());

            title = context.getString(R.string.alarm);
            if (!TextUtils.isEmpty(alarm.getName())) {
                title += "：" + alarm.getName();
            }
            title += yanchiStr;

            time = alarm.getNextDate();
            content = new SimpleDateFormat("EE HH:mm").format(new Date(time));

            jianqiang = alarm.isJianqiang();
            uri = Uri.parse(alarm.getMusicUri());
            if(!ViewUtils.uriIsMedia(uri)){
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
            vol = alarm.getVolume();
            zhendong = alarm.isVibration();

        } else {
            progress = App.getProgressService().getById(Progress.class, id);
            if (progress == null) return;

            ViewUtils.writeTxtFile("闹钟工作，进度条名称-" + progress.getName());

            title = context.getString(R.string.progress);
            if (progress.getType() != App.PROGRESS_TYPE_FENQI) {
                time = DateUtils.StringToDate(progress.getEnd(), "yyyy-MM-dd").getTime();
            } else {
                //计算下期时间
                time = App.getProgressService().getNext(progress);
                //跳过
                if (progress.getNextDate() >= time) {
                    time = progress.getNextDate();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.YEAR, Integer.parseInt(progress.getStart().substring(0, 4)));
                calendar.set(Calendar.MONTH, Integer.parseInt(progress.getStart().substring(5, 7)) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(progress.getStart().substring(8)));
                long startTime = calendar.getTimeInMillis();
                int guoqu = DateUtils.getIntervalMonth(new Date(time), new Date(startTime));
                title += " " + MessageFormat.format(context.getString(R.string.progress_di), guoqu + 1);
            }
            if (!TextUtils.isEmpty(progress.getName())) {
                title += "：" + progress.getName();
            }
            title += yanchiStr;
            content = MessageFormat.format(context.getString(R.string.end_progress)
                    , new SimpleDateFormat("yyyy-MM-dd EE").format(new Date(time)));


            vol = 70;
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            jianqiang = true;
            zhendong = true;

            //区分闹钟和精度条的通知类型
            id = -id;
        }
        //修改系统音量，防止系统音量过低时闹钟听不到铃声
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        App.mSysAlarmVol = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        App.mSysMusicVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        int alarmVol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * vol / 100;
        int musicVol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * vol / 100;
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVol, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVol, 0);
        //启动service播放音乐
        Intent musicIntent = new Intent(context, MusicService.class);
        musicIntent.putExtra("vol", vol);
        musicIntent.putExtra("jianqiang", jianqiang);
        musicIntent.putExtra("uri", uri.toString());
        musicIntent.putExtra("flg", 1);//标识是闹钟播放音乐，而不是调整音量的时候播放的音乐
        musicIntent.putExtra("zhendong", zhendong);
        context.startService(musicIntent);

        // 由于用Notification在miu上有bug，暂时找不到解决办法，所以不分是否亮屏，统一显示闹钟的页面
        Intent intentAlarm = new Intent(context, AlarmActivity.class);
        intentAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentAlarm.putExtra("id", id);
        intentAlarm.putExtra("flg", flg);
        intentAlarm.putExtra("title", title);
        intentAlarm.putExtra("content", content);
        intentAlarm.putExtra("alarm_time", alarm_time);
        context.startActivity(intentAlarm);

/*        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //判断屏幕是否点亮
        boolean isScreenOn = pm.isInteractive();
        if (!isScreenOn) {

            Intent intentAlarm = new Intent(context, AlarmActivity.class);
            intentAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentAlarm.putExtra("id", id);
            intentAlarm.putExtra("flg", flg);
            intentAlarm.putExtra("title", title);
            intentAlarm.putExtra("content", content);
            intentAlarm.putExtra("alarm_time", alarm_time);
            context.startActivity(intentAlarm);
        } else {
            //下面点亮屏幕是为了解决有屏保状态下，屏幕是开的，但是通知不会弹出
            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            //休眠
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            wl.release();

            //亮屏的时候直接弹出通知
            AlarmUtils.setNotification(context,id,flg,alarm_time,title,content);

            //启动服务，在闹铃时间结束后通知用户错过闹钟
            Intent missIntent = new Intent(context, MissAlarmService.class);
            missIntent.putExtra("id", id);
            missIntent.putExtra("flg", flg);
            missIntent.putExtra("title", title);
            missIntent.putExtra("content", content);
            context.startService(missIntent);
        }*/
    }
}
