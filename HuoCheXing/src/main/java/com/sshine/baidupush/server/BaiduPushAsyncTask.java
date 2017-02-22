package com.sshine.baidupush.server;



import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;

public class BaiduPushAsyncTask {

	public static final int NOT_SET = 0;
	public static final int SEND_MESSAGE_BY_TAG = 1;
	public static final int SEND_MESSAGE_BY_USERID = 2;
	public static final int SET_TAG = 3;
	public static final int DELETE_TAG = 4;
	public static final int SEND_NOTIFY_BY_TAG = 5;
	
	private String jsonMsg;
	private int sendType;
	private String tag;
	private String userId;
	private String notifyTitle;
	private String notifyMessage;
	private myAsyncTask asyncTask;
	private BaiduPush baiduPush;
	private OnSendSuccessListener sendSuccessListener;
	private Handler handler;
	
	private Runnable resent = new Runnable() {
		
		@Override
		public void run() {
			L.i("resend msg...");
			send();//再次发送
		}
	};
	
	
	public BaiduPushAsyncTask(){
		//初始化
		this.handler = new Handler();
		baiduPush = MyApp.getInstance().getBaiduPush();
	}
	
	/**
	 * 给指定tag组发送通知
	 */
	public void sendTagNotify(String title , String message , String tag){
		this.notifyTitle =  title;
		this.notifyMessage = message;
		this.tag = tag;
		this.sendType = SEND_NOTIFY_BY_TAG;
		send();
	}
	/**
	 * 给指定tag组发送信息
	 * @param jsonMsg  信息json字符串
	 * @param tag 组标签
	 */
	public void sendTagMessage(String jsonMsg,String tag){
		this.jsonMsg = jsonMsg;
		this.tag = tag;
		this.sendType = SEND_MESSAGE_BY_TAG;
		send();
	}
	/**
	 * 给指定用户发送信息
	 * @param jsonMsg 信息的json字符串
	 * @param userId 用户的userid
	 */
	public void sendMessageToUser(String jsonMsg , String userId){
		this.jsonMsg = jsonMsg;
		this.userId = userId;
		this.sendType = SEND_MESSAGE_BY_USERID;
		send();
	}
	
	/**
	 * 给用户设置标签
	 * @param tag 标签
	 * @param userId 用户id
	 */
	public void setTag(String tag, String userId){
		this.tag = tag;
		this.userId = userId;
		this.sendType = SET_TAG;
		send();
	}
	public void deleteTag(String tag, String userId){
		this.tag = tag;
		this.userId = userId;
		this.sendType = DELETE_TAG;
		send();
	}
	/*public BaiduPushAsyncTask(String jsonMsg , int sendType ,String sendTag){
		this.jsonMsg = jsonMsg;
		this.sendTag = sendTag;
		this.sendType = sendType;
		this.handler = new Handler();
		baiduPush = MyApplication.getInstance().getBaiduPush();
	}*/
	
	public void setOnSendSuccessListener (OnSendSuccessListener listener){
		this.sendSuccessListener = listener;
	}
	
	//发送
	public  void send(){
		//如果没有设置发送方式
		if(sendType == NOT_SET){
			return;
		}
		if(HttpUtil.isNetworkConnected(MyApp.getInstance())){//如果网络可用
			asyncTask = new myAsyncTask();
			asyncTask.execute();
		}else{
			Toast.makeText(MyApp.getInstance(), "网络不可用，请连接网络", Toast.LENGTH_LONG).show();
		}
	}
	/**
	 * 停止
	 */
	public void stop(){
		if(asyncTask!=null){
			asyncTask.cancel(true);
		}
	}
	class myAsyncTask extends AsyncTask<Void, Void, String>{
		/*
		 *  onPreExecute(), 该方法将在执行实际的后台操作前被UI thread调用。可以在该方法中做一些准备工作，如在界面上显示一个进度条。 
　　		  	doInBackground(Params...), 将在onPreExecute 方法执行后马上执行，该方法运行在后台线程中。这里将主要负责执行那些很耗时的后台计算工作。可以调用 publishProgress方法来更新实时的任务进度。该方法是抽象方法，子类必须实现。 
　　 			onProgressUpdate(Progress...),在publishProgress方法被调用后，UI thread将调用这个方法从而在界面上展示任务的进展情况，例如通过一个进度条进行展示。 
　　  			onPostExecute(Result), 在doInBackground 执行完成后，onPostExecute 方法将被UI thread调用，后台的计算结果将通过该方法传递到UI thread. 

		 */
		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			switch (sendType) {
			case SEND_MESSAGE_BY_TAG:
				result = baiduPush.PushTagMessage(jsonMsg, tag);
				break;

			case SEND_MESSAGE_BY_USERID:
				result = baiduPush.PushMessage(jsonMsg, userId);
				break;
			case SET_TAG:
				result = baiduPush.SetTag(tag, userId);
				break;
			case DELETE_TAG:
				result = baiduPush.DeleteTag(tag, userId);
				break;
			case SEND_NOTIFY_BY_TAG :
				result = baiduPush.PushTagNotify(notifyTitle, notifyMessage, tag);
				break;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			L.i("send msg result:"+result);
			if(result.contains(BaiduPush.SEND_MSG_ERROR)){
				//发送失败
				Log.e("PostError", sendType+":"+result);
				handler.postDelayed(resent, 100);
				
			}else if(sendSuccessListener != null){
				sendSuccessListener.onSendSuccess();
			}
		}
	}
	
	/**
	 * 监听发送成功的接口
	 *
	 */
	interface OnSendSuccessListener{
		void onSendSuccess();
	}
}

