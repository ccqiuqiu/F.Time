package com.ccqiuqiu.ftime.Server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.BroadcastReceiver.NextAlarmReceiver;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;

public class MissAlarmService extends Service {
    private String title, content;
    private int id,flg;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            missNotification();
        }
    };

    public MissAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        title = intent.getStringExtra("title")+" - "+getBaseContext().getString(R.string.miss);
        content = intent.getStringExtra("content");
        id = intent.getIntExtra("id",0);
        flg = intent.getIntExtra("flg", AlarmUtils.FLG_ALARM);
        handler.sendEmptyMessageDelayed(0, App.mShichang * 60 * 1000);
        //handler.sendEmptyMessageDelayed(0,10000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void missNotification() {
        //从通知栏删除通知
        NotificationManager notificationManager = (NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        //停止音乐
        Intent musicIntent = new Intent(getBaseContext(), MusicService.class);
        getBaseContext().stopService(musicIntent);

        Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_alarm);
        notificationManager.notify(id, mBuilder.build());

        //发送广播，设置下一次闹钟
        Intent dismissIntent = new Intent(getBaseContext(), NextAlarmReceiver.class);
        dismissIntent.putExtra("id", id);
        dismissIntent.putExtra("flg", flg);
        getBaseContext().sendBroadcast(dismissIntent);

        //停止自己
        Intent missIntent = new Intent(getBaseContext(), MissAlarmService.class);
        stopService(missIntent);
    }

    @Override
    public void onDestroy() {
        handler.removeMessages(0);
        super.onDestroy();
    }
}
