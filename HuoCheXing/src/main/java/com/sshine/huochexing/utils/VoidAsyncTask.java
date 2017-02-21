package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public abstract class VoidAsyncTask extends AsyncTask<Object, Object, Object> {
	
	@SuppressLint("NewApi")
	public void start(){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			execute();
		} else {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
}
