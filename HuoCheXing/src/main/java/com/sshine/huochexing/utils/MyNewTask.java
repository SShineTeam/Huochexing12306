package com.sshine.huochexing.utils;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.sshine.huochexing.LoginAty;
import com.sshine.huochexing.value.SF;

import eu.inmite.android.lib.dialogs.ProgressDialogFragment;

public abstract class MyNewTask extends AsyncTask<String, Integer, Object> {
	private DialogFragment dlg;
	private FragmentActivity mContext;
	private boolean isContinue = true;
	HttpUtil httpUtil = new HttpUtil();
	
	public MyNewTask(FragmentActivity context){
		this(context, null);
	}
	
	public MyNewTask(FragmentActivity context,String strMsg) {
		mContext = context;
		if (!HttpUtil.isNetworkConnected(context)){
			showMsg("网络不可用，请检测网络状态" + SF.NO_NETWORK);
			isContinue = false;
		}else{
			if (strMsg != null){
				try{
					dlg = ProgressDialogFragment.createBuilder(context, context.getSupportFragmentManager())
							.setMessage(strMsg)
							.setCancelable(false)
							.show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			isContinue = true;
		}
	}
	@Override
	protected Object doInBackground(String... params) {
		if (!isContinue){
			return null;
		}
		try {
			if (httpUtil.post(params[0], params[1])) {
				return httpUtil.getResponseStr();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		}
		return null;
	}
	
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
		if (result instanceof ConnectTimeoutException){
			showMsg("请求数据超时" + SF.FAIL);
		}else if (result instanceof Exception){
			showMsg("请求数据时出错" + SF.FAIL);
		}else{
			try {
				JSONObject resultJson = new JSONObject(
						(String) result);
				int intResultCode = resultJson
						.getInt(HttpUtil.RESULT_CODE);
				switch (intResultCode) {
					case HttpUtil.MSG_RECEIVE_VERIFY_FAILED:
						//验证失败 需要重新登录
						showMsg("您的身份已过期,请重新登录" + SF.FAIL);
						MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
						Intent loginIntent = new Intent(mContext, LoginAty.class);
						mContext.startActivity(loginIntent);
						mContext.finish();
						break;
					case HttpUtil.MSG_RECEIVE_FAIL:
						showMsg("获取数据失败" + SF.FAIL);
						break;
					case HttpUtil.MSG_RECEIVE_EMPTY:
						showMsg("没有找到符合条件的数据" + SF.FAIL);
						break;
					case HttpUtil.MSG_RECEIVE_SUCCESS:
						myOnPostExecute(resultJson);
						break;
				}
			}catch(Exception e){
				e.printStackTrace();
				showMsg("处理数据时出错" + SF.FAIL);
				myOnPostExecute(null);
			}
		}
		super.onPostExecute(result);
	}
	protected abstract void myOnPostExecute(JSONObject jsonObj);
	
	private void showMsg(String strMsg){
		Toast.makeText(mContext.getApplicationContext(), strMsg, Toast.LENGTH_SHORT).show();
	}
}
