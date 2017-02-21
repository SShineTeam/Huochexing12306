package com.sshine.huochexing.interfaces;

import com.sshine.huochexing.chatroom.bean.ChatMessage;
import com.sshine.huochexing.chatroom.bean.ChatMessageItem;



public interface EventHandler {
	public void onMessage(ChatMessage chatMessage,ChatMessageItem messageItem);
	public void onBind(String method, int errorCode, String content);

	public void onNotify(String title, String content);

	public void onNetChange(boolean isNetConnected);

	//public void onNewFriend(User u);
}
