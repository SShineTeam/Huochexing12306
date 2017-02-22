package com.sshine.baidupush.server;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.android.pushservice.PushManager;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;

public class BaiduPushTagsHelper {
	
	/**
	 * 更新baiduPush服务端的 tags
	 * @param currentTags 当前服务端的tags
	 */
	public void updateTags(Context context , List<String> currentTags) {
		List<String> newTags = getTagsList();
		//向服务器更新
		if(currentTags==null||currentTags.size()==0){
			//没有tag直接设置
			if(newTags.size()!=0){
				PushManager.setTags(context,newTags);
			}
		}else{
			//生成需要添加的tag列表和需要删除的tag列表
			List<String> rmTags = currentTags; //先假设服务器中的tag都需要删除
			List<String> addTags = new ArrayList<String>();
			for (String tag : newTags) {
				if(rmTags.contains(tag)){
					//服务器已经包含此tag
					rmTags.remove(tag); //仍需要此tag所以将其从此删除列表中移除
				}else{
					addTags.add(tag);
				}
			}
			if(rmTags.size()!=0){
				PushManager.delTags(context,rmTags);
			}
			if(addTags.size()!=0){
				PushManager.setTags(context,addTags);
			}
		}
		//查看当前tag--log输出
		PushManager.listTags(context);
	}
	
	/**
	 * 取得应该设置的tag
	 * @return 应该设置的tags
	 */
	private List<String> getTagsList() {
		//先添加车次聊天室需要的tag
		List<String> newTags = new  ArrayList<String>();
		MyDatabase myDB = new MyDatabase(MyApp.getInstance().getApplicationContext());
		SQLiteDatabase db = myDB.getWritableDB();
		int userId = MyApp.getInstance().getUserInfoSPUtil().getUId();
		Cursor c= db.rawQuery("select T_id from UserTrainB where ReceiveMsg = 1 and  U_id = "+userId+" group by T_id order by _id DESC", null);
		while(c.moveToNext()){
			newTags.add(c.getString(c.getColumnIndex("T_id")));
		}
		//添加其他tag --如果有的话
		//添加公共聊天室
		if(MyApp.getInstance().getSettingSPUtil().isReceivePublicChatroom()){
			newTags.add("all");
		}
		c.close();
		myDB.closeDB();
		return newTags;
	}
	
	/**
	 * 设置tag
	 * @param tag 一条tag
	 */
	public void setTag(Context context,String tag) {
		List<String> tags = new ArrayList<String>();
		tags.add(tag);
		PushManager.setTags(context,tags);
	}
	/**
	 * 删除tag
	 * @param tag 一条tag
	 */
	public void deleteTag(Context context,String tag) {
		List<String> tags = new ArrayList<String>();
		tags.add(tag);
		PushManager.delTags(context, tags);;
	}

}
