package com.ccqiuqiu.ftime.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ccqiuqiu.ftime.App;
import com.ccqiuqiu.ftime.Fragment.MainFragment;
import com.ccqiuqiu.ftime.R;
import com.ccqiuqiu.ftime.Utils.AlarmUtils;
import com.ccqiuqiu.ftime.Utils.ViewUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public MainFragment mMainFragment;
    private int mSelectLingshengFlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏完全透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        x.view().inject(this);

        mMainFragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, mMainFragment)
                .show(mMainFragment)
                .commit();

        //初始化闹钟。主要是处理闹钟时间已过，而闹钟没有更新
        ViewUtils.writeTxtFile("程序运行，初始化闹钟");
        AlarmUtils.initAlarm();
    }

    @Override
    public void onBackPressed() {
        if (!mMainFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void hideFab() {
        mMainFragment.mFab.animate().translationY(ViewUtils.dp2px(90)).setDuration(100);
    }

    public void showFab() {
        mMainFragment.mFab.animate().translationY(0).setDuration(100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 0:
                Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                handleRingtonePicked(pickedUri);
                break;
        }
    }

    private void handleRingtonePicked(Uri pickedUri) {
        if(mSelectLingshengFlg == App.SELECT_LINGSHENG_FLG_DRAG){
            mMainFragment.lingshengSelected(pickedUri);
        }else if(mSelectLingshengFlg == App.SELECT_LINGSHENG_FLG_ADD_ALARM){
            mMainFragment.mAddAlarmFragment.lingshengSelected(pickedUri);
        }
    }

    public void selectLingsheng(Uri uri,int flg) {
        mSelectLingshengFlg = flg;
        if (uri == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);

        startActivityForResult(intent, 0);
    }


}
