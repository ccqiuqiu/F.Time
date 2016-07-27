package com.ccqiuqiu.ftime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Model.Alarm;
import com.ccqiuqiu.ftime.Model.Progress;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Source;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ViewUtils.writeTxtFile("开机运行," + intent.getAction());
        AlarmUtils.initAlarm();
    }
}
