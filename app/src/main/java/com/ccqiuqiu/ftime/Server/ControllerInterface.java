package com.ccqiuqiu.ftime.Server;

import android.net.Uri;

public interface ControllerInterface {

	void play(float vol,Uri uri);
	void pause();
	void stop();
	void continuePlay();
	boolean isPlay();
	void setVolume(float l,float r);
}
