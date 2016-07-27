package com.ccqiuqiu.ftime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ccqiuqiu.ftime.Utils.AlarmUtils;

public class NextAlarmReceiver extends BroadcastReceiver {
    public NextAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //设置下一次闹钟
        int id = intent.getIntExtra("id", 0);
        int flg = intent.getIntExtra("flg", 0);
        AlarmUtils.setNextAlarm(flg, id);

        //发送更新UI的广播
        Intent uiIntent = new Intent();
        uiIntent.setAction("com.ccqiuqiu.ftime.alarm.updateui");
        context.sendBroadcast(uiIntent);
    }
}
