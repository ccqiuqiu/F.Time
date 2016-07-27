package com.ccqiuqiu.ftime.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Server.MissAlarmService;
import com.ccqiuqiu.ftime.Server.MusicService;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmStopBroadcastReceiver extends BroadcastReceiver {

    public AlarmStopBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int flg = intent.getIntExtra("flg", AlarmUtils.FLG_ALARM);
        int stop_flg = intent.getIntExtra("stop_flg", AlarmUtils.FLG_DISMISS);
        int id = intent.getIntExtra("id", 0);
        //System.out.println("=====onReceive=======" + id);

        //从通知栏删除通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);

        //停止错过闹钟的服务
        Intent missIntent = new Intent(context, MissAlarmService.class);
        context.stopService(missIntent);

        //停止音乐
        Intent musicIntent = new Intent(context, MusicService.class);
        context.stopService(musicIntent);
        //将音量调回响铃前
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, App.mSysMusicVol, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, App.mSysAlarmVol, 0);

        //延时再响
        if (stop_flg == AlarmUtils.FLG_SNOOZE) {
            ViewUtils.writeTxtFile("延迟");
            //long time = intent.getLongExtra("alarm_time", System.currentTimeMillis());
            long time = System.currentTimeMillis() + App.mZanting * 60 * 1000;
            AlarmUtils.setNextAlarm(flg, id, time);
            //AlarmUtils.setNextAlarm(flg, id, System.currentTimeMillis() + 10 * 1000);
            //更新下次时间
            Alarm alarm;
            Progress progress;
            if (flg == AlarmUtils.FLG_ALARM) {
                alarm = App.getAlarmService().getById(Alarm.class, id);
                if (alarm == null) return;
                alarm.setNextDate(time);
                App.getAlarmService().update(alarm);
            } else {
                progress = App.getProgressService().getById(Progress.class, Math.abs(id));
                if (progress == null) return;

                progress.setNextDate(time);
                App.getProgressService().update(progress);
            }
        } else {
            ViewUtils.writeTxtFile("关闭，设置下一次");
            //发送广播，设置下一次闹钟
            Intent dismissIntent = new Intent(context, NextAlarmReceiver.class);
            dismissIntent.putExtra("id", id);
            dismissIntent.putExtra("flg", flg);
            context.sendBroadcast(dismissIntent);
        }
    }
}
