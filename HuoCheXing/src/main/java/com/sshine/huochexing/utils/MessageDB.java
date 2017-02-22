package com.sshine.huochexing.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sshine.huochexing.chatroom.bean.ChatMessageItem;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageDB {
	
	public static final String DB_NAME = "huochexing";
	public static final String TABLE_NAME = "ChatMessageB";
	
	private SQLiteDatabase db;
	
	/**
	 * 初始化数据库，与聊天纪录表
	 * @param context
	 */
	public MessageDB(Context context) {
		db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,
				null);
		db.execSQL("CREATE table IF NOT EXISTS "
				+ TABLE_NAME
				+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT,U_id INTEGER, T_id TEXT,NickName TEXT,HeadId INTEGER, Message TEXT,Time TEXT,IsCome INTEGER)");
	}
	/**
	 * 获取纪录
	 * @param t_Id 车次
	 * @param msgPagerNum 获取页数
	 * @return
	 */
	public List<ChatMessageItem> getMessage(String t_Id, int msgPagerNum) {
		List<ChatMessageItem> messageList = new ArrayList<ChatMessageItem>();
		int ItemNum = 10 * (msgPagerNum + 1);
		Cursor c = db.rawQuery("SELECT * from " + TABLE_NAME
				+ " WHERE T_id = '"+ t_Id +"' ORDER BY ID DESC LIMIT " + ItemNum, null);
		if(c == null){
			return messageList;
		}
		while (c.moveToNext()) {
			int userId = c.getInt(c.getColumnIndex("U_id"));
			String trainId = c.getString(c.getColumnIndex("T_id"));
			String nickName = c.getString(c.getColumnIndex("NickName"));
			Long time = Long.parseLong(c.getString(c.getColumnIndex("Time")));
			String message = c.getString(c.getColumnIndex("Message"));
			int headId = c.getInt(c.getColumnIndex("HeadId"));
			int isCome = c.getInt(c.getColumnIndex("IsCome"));
			boolean isComMeg = true;
			if(isCome == 0){
				isComMeg = false; 
			}
			ChatMessageItem item  = new ChatMessageItem(ChatMessageItem.MESSAGE_TYPE_TEXT, userId, 
						trainId, nickName, time, message, String.valueOf(headId), isComMeg);
			messageList.add(item);
		}
		c.close();
		Collections.reverse(messageList);  
		return messageList;
	}
	
	/**
	 * 保存一条聊天记录
	 * @param item
	 */
	public void saveMessage(ChatMessageItem item){
		
		int iscome = 0;
		if(item.isComMeg()){
			iscome = 1;
		}
		//U_id INTEGER, T_id TEXT,NickName TEXT,HeadPath TEXT, Message TEXT,Time TEXT,IsCome INTEGER
		db.execSQL("insert into "
						+ TABLE_NAME
						+ " (U_id,T_id,NickName,HeadId,Message,Time,IsCome) values(?,?,?,?,?,?,?)",
				new Object[] { item.getUserId(),item.getTrainId(),item.getNickName(),item.getHeadId(),item.getMessage(),
								item.getTime(),iscome});
	}
}
