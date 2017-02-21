package com.sshine.huochexing.utils;

import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import eu.inmite.android.lib.dialogs.ProgressDialogFragment;

public abstract class MyTask extends AsyncTask<Object, Integer, Object> {
	public static final String NO_NETWORK = "⊙﹏⊙";
	private DialogFragment dlg;
	private FragmentActivity mContext;
	private boolean isContinue = true;
	
	public MyTask(FragmentActivity context,String strMsg) {
		this.mContext = context;
		if (strMsg != null){
			try{
				dlg = ProgressDialogFragment.createBuilder(context, context.getSupportFragmentManager())
						.setMessage(strMsg)
						.setCancelable(true)
						.show();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		isContinue = true;
	}
	
	public MyTask(FragmentActivity context){
		this(context, true);
	}
	
	public MyTask(FragmentActivity context, boolean isCheckNetwork){
		if (isCheckNetwork && !HttpUtil.isNetworkConnected(context)){
			Toast.makeText(context, "网络不可用，请检测网络状态" + NO_NETWORK, Toast.LENGTH_SHORT).show();
			isContinue = false;
		}else{
			this.mContext = context;
			isContinue = true;
		}
	}
	
	public MyTask(FragmentActivity context,String strMsg, boolean isCheckNetwork) {
		this.mContext = context;
		if (isCheckNetwork && !HttpUtil.isNetworkConnected(context)){
			Toast.makeText(mContext, "网络不可用，请检测网络状态" + NO_NETWORK, Toast.LENGTH_SHORT).show();
			isContinue = false;
		}else{
			if (strMsg != null){
				try{
					dlg = ProgressDialogFragment.createBuilder(context, context.getSupportFragmentManager())
							.setMessage(strMsg)
							.setCancelable(true)
							.show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			isContinue = true;
		}
	}
	
	protected abstract Object myDoInBackground(Object... params) throws Exception;
	@Override
	protected Object doInBackground(Object... params) {
		if (!isContinue){
			return null;
		}
		try{
			Object obj = myDoInBackground(params);
			return obj;
		}catch(Exception e){
			e.printStackTrace();
			return e;
		}
	}
	protected abstract void myOnPostExecute(Object result);
	@Override
	protected void onPostExecute(Object result) {
		if (!isContinue){
			return;
		}
		if (mContext == null || mContext.isFinishing()){
			return;
		}
		if (dlg != null){
			dlg.dismissAllowingStateLoss();
		}
		if (result instanceof Exception){
			//如若抛出异常则显示信息
			Exception e = (Exception)result;
			onException(e);
		}else{
			myOnPostExecute(result);
		}
	}
	
	protected abstract void onException(Exception e);
}
