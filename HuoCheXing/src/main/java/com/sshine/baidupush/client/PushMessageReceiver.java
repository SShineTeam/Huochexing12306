package com.sshine.baidupush.client;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.sshine.huochexing.R;
import com.sshine.huochexing.chatroom.ChatRoomAty;
import com.sshine.huochexing.chatroom.bean.ChatMessage;
import com.sshine.huochexing.chatroom.bean.ChatMessageItem;
import com.sshine.huochexing.interfaces.EventHandler;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.UserInfoSPUtil;

public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	//回调方法的动作
	public static final int LIST_TAGS = 0; //查询TagS
	public static final int UPDATE_TAGS = 1;  //更新Tag
	
	public static final int NOTIFICATION_ID = 99;
	
	
	
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	private static int listTagsAction ;
	
	/**
	 * 启动推送服务的回调函数
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		StringBuffer sb = new StringBuffer();
		sb.append("绑定成功\n");
		sb.append("errCode:"+errorCode);
		sb.append("appid:"+appid+"\n");
		sb.append("userId:"+userId+"\n");
		sb.append("channelId:"+channelId+"\n");
		sb.append("requestId"+requestId+"\n");
		Log.d(TAG,sb.toString());
		
		if(errorCode == 0){
			//绑定成功
			UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
			userSP.setBaiduAppId(appid);
			userSP.setBaiduChannelId(channelId);
			userSP.setBaiduUserId(userId);
		}else{
			Toast.makeText(context.getApplicationContext(), "聊天服务初始化失败:"+errorCode, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		StringBuffer sb = new StringBuffer();
		sb.append("解绑成功\n");
		sb.append("errCode:"+errorCode);
		sb.append("requestId"+requestId+"\n");
		Log.d(TAG,sb.toString());
	}

	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags,
			String requestId) {
		StringBuffer sb = new StringBuffer();
		sb.append("设置tag成功\n");
		sb.append("errCode:"+errorCode);
		sb.append("success tags:");
		for(String tag:successTags){
			sb.append(tag+"\n");
		}
		sb.append("fail tags:");
		for(String tag:failTags){
			sb.append(tag+"\n");
		}
		sb.append("requestId"+requestId+"\n");
		Log.d(TAG,sb.toString());
	}

	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags,
			String requestId) {
		StringBuffer sb = new StringBuffer();
		sb.append("删除tag成功\n");
		sb.append("errCode:"+errorCode);
		sb.append("success tags:");
		for(String tag:successTags){
			sb.append(tag+"\n");
		}
		sb.append("fail tags:");
		for(String tag:failTags){
			sb.append(tag+"\n");
		}
		sb.append("requestId"+requestId+"\n");
		Log.d(TAG,sb.toString());
	}

	@Override
	public void onListTags(Context context, int errorCode,
			List<String> tags, String requestId) {
		StringBuffer sb = new StringBuffer();
		sb.append("list tag成功\n");
		sb.append("errCode:"+errorCode);
		sb.append("tags:");
		if(tags!=null&&tags.size()>0){
			for(String tag:tags){
				sb.append(tag+"\n");
			}
		}
		sb.append("requestId"+requestId+"\n");
		Log.d(TAG,sb.toString());
		
		switch (listTagsAction) {
		case UPDATE_TAGS:
			//复位标志位
			listTagsAction = LIST_TAGS;
			//更新聊天室接收信息状况
			MyApp.getInstance().getTagsHelper().updateTags(context,tags);
			break;
		}
		
	}
	/**
	 * 收到消息时的回调方法
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		StringBuffer sb = new StringBuffer();
		sb.append("收到消息\n");
		sb.append("内容是:"+customContentString+"\n");  //经测试customContentString的返回值为null
		sb.append("tags:");
		sb.append("message:"+message+"\n");
		Log.d(TAG,sb.toString());
		try {
			ChatMessage chatMessage = A6Util.getGson().fromJson(message, ChatMessage.class);
			praseMessage(chatMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		StringBuffer sb = new StringBuffer();
		sb.append("通知被点击\n");
		sb.append("title:"+title+"\n");
		sb.append("description:"+description);
		sb.append("customContentString:"+customContentString+"\n");
		Log.d(TAG,sb.toString());
	}
	
	/**
	 * 取得listTags回调方法的动作
	 * @return listTags回调方法的动作
	 */
	public static int getListTagsAction() {
		return listTagsAction;
	}

	/**
	 * 设置listTags回调方法的动作
	 * @param listTagsAction listTags回调方法的动作
	 */
	public static void setListTagsAction(int listTagsAction) {
		PushMessageReceiver.listTagsAction = listTagsAction;
	}
	
	/**
	 * 处理收到的聊天信息
	 * @param chatMessage
	 */
	private void praseMessage(ChatMessage chatMessage) {
		if(chatMessage.getUserId()==null){
			return ;
		}
		String userId = chatMessage.getUserId();
		if(userId.equals(MyApp.getInstance().getUserInfoSPUtil().getBaiduUserId())){
			//如果是自己的消息。忽略。
			return;
		}
		ChatMessageItem messageItem = new ChatMessageItem(ChatMessageItem.MESSAGE_TYPE_TEXT, chatMessage.getuId(), 
				chatMessage.getTrainId(), chatMessage.getNickName(), chatMessage.getTimeSamp(), chatMessage.getMessage(), chatMessage.getHeadId(), true);
		//保存到数据库
		MyApp.getInstance().getMessageDB().saveMessage(messageItem);
		//如果有监听传给监听者
		if(ehList.size()>0){
			for (EventHandler handler : ehList) {
				handler.onMessage(chatMessage,messageItem);
			}
		}else{
			//通知栏提醒
			showMessage(messageItem);
		}
	}
	/**
	 * 通知栏提醒
	 * @param messageItem 消息信息
	 */
	@SuppressWarnings("deprecation")
	private void showMessage(ChatMessageItem messageItem) {
		SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
		if (!setSP.isChatNotiOnBar()){
			return;
		}
		String messageContent = messageItem.getNickName()+":"+messageItem.getMessage();
		MyApp.getInstance().setNewMsgCount(MyApp.getInstance().getNewMsgCount()+1); //数目+1
		Notification notification = new Notification(R.drawable.ic_launcher,messageContent, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		if (setSP.isChatRing()){
			// 设置默认声音
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (setSP.isChatVibrate()){
			// 设定震动(需加VIBRATE权限)
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		Intent intent = new Intent(MyApp.getInstance(),ChatRoomAty.class);
		intent.putExtra("TrainId", messageItem.getTrainId());
		PendingIntent contentIntent = PendingIntent.getActivity(MyApp.getInstance(), 0, intent, 0);
		String contentText = null;
		if(MyApp.getInstance().getNewMsgCount()==1){
			contentText = messageContent;
		}else{
			contentText = MyApp.getInstance().getNewMsgCount()+"条未读消息";
		}
		
		notification.setLatestEventInfo(MyApp.getInstance(), "车友聊天室", contentText, contentIntent);
		NotificationManager manage = (NotificationManager) MyApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		manage.notify(NOTIFICATION_ID, notification);
	}
	
}
