package com.ccqiuqiu.ftime.Server;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;

public class MusicService extends Service {
    private AudioManager mAm;
    MediaPlayer player;
    Vibrator vibrator;
    int volume;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int vol = msg.what;
            if (player != null && player.isPlaying()) {
                player.setVolume((float) (vol / 100.00), (float) (vol / 100.00));
            }
            if (vol < volume) {
                vol += 1;
                jianqiangVol(vol);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!player.isLooping()) {
                    player.stop();
                    mAm.abandonAudioFocus(afChangeListener);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int flg = intent.getIntExtra("flg", 0);
        volume = intent.getIntExtra("vol", 0);
        boolean zhendong = intent.getBooleanExtra("zhendong", false);
        boolean jianqiang = intent.getBooleanExtra("jianqiang", false);
        String uri = intent.getStringExtra("uri");
        if (flg != 0) {
            //震动
            if (zhendong) {
                vibrator = (Vibrator) getBaseContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{500, 500, 200, 100, 200, 100}, 0);
            }
            int vol = volume;
            if (jianqiang) {
                vol = 10;
                jianqiangVol(vol);
            }
            MusicService.this.play((float) (vol / 100.00), Uri.parse(uri), true);
        }
        return super.onStartCommand(intent, flags, startId);

    }

    private void resume() {
        if (player != null) {
            player.start();
        }
    }

    private void jianqiangVol(int vol) {
        Message msg = handler.obtainMessage();
        msg.what = vol;
        handler.sendMessageDelayed(msg, 800);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁player
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (mAm != null) {
            mAm.abandonAudioFocus(afChangeListener);
        }
    }

    class MusicController extends Binder implements ControllerInterface {

        @Override
        public void play(float vol, Uri uri) {
            MusicService.this.play(vol, uri, false);
        }

        @Override
        public void pause() {
            MusicService.this.pause();

        }

        @Override
        public void stop() {
            MusicService.this.stop();
        }

        @Override
        public void continuePlay() {
            MusicService.this.continuePlay();
        }

        @Override
        public boolean isPlay() {
            return MusicService.this.player.isPlaying();
        }

        @Override
        public void setVolume(float l, float r) {
            MusicService.this.player.setVolume(l, r);
        }
    }


    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                resume();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAm.abandonAudioFocus(afChangeListener);
                stop();
            }
        }
    };

    private void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (mAm != null) {
            mAm.abandonAudioFocus(afChangeListener);
        }
    }

    private boolean requestFocus() {
        int result = mAm.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void play(float vol, Uri uri, boolean loop) {
        //获取音频焦点后才开始播放
        if (requestFocus()) {
            if (player == null) {
                player = new MediaPlayer();
            }
            player.reset();
            try {
                player.setDataSource(getBaseContext(), uri);
                player.setVolume(vol, vol);
                player.setLooping(loop);
                //同步准备
//			player.prepare();
                //异步准备
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    //准备完毕时调用
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void continuePlay() {
        player.start();
    }
}
