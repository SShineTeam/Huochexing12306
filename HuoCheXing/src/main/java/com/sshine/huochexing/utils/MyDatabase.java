package com.sshine.huochexing.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyDatabase extends SQLiteAssetHelper {
	public static final String DB_PATH = MyApp.getInstance().getApplicationContext().getFilesDir().getParentFile().getPath() + "/databases/";
	public static final String DATABASE_NAME = "huochexing";
	private static final int DATABASE_VERSION = 1;
	public static final String KEY = "key";
	public static final String VALUE = "value";

	public MyDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public SQLiteDatabase getWritableDB(){
		return getWritableDatabase();
	}
	public String getStationTeleCode(String strStation){
		SQLiteDatabase db = getWritableDatabase();
		if (db == null){
			return null;
		}
		String strCode = null;
		String sql = "select Code from StationB where Station='" + strStation + "'";
		Cursor c = db.rawQuery(sql, null);
		if (c.moveToNext()){
			strCode = c.getString(c.getColumnIndex("Code"));
		}
		if (c != null){
			c.close();
		}
		if (db != null){
			db.close();
		}
		return strCode;
	}

	/**
	 * 获取站点信息
	 * 
	 * @param s
	 *            是要匹配的字串
	 * @param isFirstQuery
	 *            是否是第一次返回站点信息游标
	 * @return 站点信息游标
	 */
	public List<Map<String, String>> getStations(CharSequence s, boolean isFirstQuery) {
		SQLiteDatabase db = getWritableDatabase();
		if (db == null){
			return null;
		}
		Cursor c = null;
		if (isFirstQuery) {
			c = db.rawQuery(
					"select _id,[Key] as Station,[Value] as Code from RecentSearchB where Type='Station' order by [Time] DESC;",
					null);
		} else {
			if (s.equals("")) {
				c = db.rawQuery("select _id,Station,Code,Fullcode from StationB", null);
			} else {
				char[] chars = s.toString().toLowerCase(Locale.getDefault())
						.toCharArray();
				String arg1 = "";
				for (char ch : chars) {
					arg1 += "%%" + ch;
				}
				arg1 += "%%";
				c = db.query("StationB", new String[] { "_id", "Station", "Code" },
						"Station like ? or Fullcode like ?", new String[] {
								arg1, arg1 }, null, null, null);
			}
		}
		if (c == null){
			return null;
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while(c.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			map.put(KEY, c.getString(c.getColumnIndex("Station")));
			map.put(VALUE, c.getString(c.getColumnIndex("Code")));
			list.add(map);
		}
		if (c != null){
			c.close();
		}
		if (db != null){
			db.close();
		}
		return list;
	}

	public List<Map<String, String>> getTrainNumsHistory() {
		SQLiteDatabase db = getWritableDatabase();
		if (db == null){
			return null;
		}
		Cursor c;
		c = db.rawQuery(
				"select _id,Value as TrainNum from RecentSearchB where Type='TrainNum' order by [Time] DESC;",
				null);
		if (c == null){
			return null;
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while(c.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			map.put(KEY, c.getString(c.getColumnIndex("TrainNum")));
			map.put(VALUE, c.getString(c.getColumnIndex("TrainNum")));
			list.add(map);
		}
		if (c != null){
			c.close();
		}
		if (db != null){
			db.close();
		}
		return list;
	}

	/**
	 * 更新历史记录,只保留最新的30条记录.
	 * 
	 * @param type
	 *            Station:车站历史,TrainNum:车次历史.
	 * @param strKey
	 *            要存储的字符串
	 */
	public void updateHistory(String type, String strKey, String strValue) {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = null;
		try {
			c = db.rawQuery("select * from RecentSearchB  where Type='"
					+ type + "' and Key='" + strKey + "' and Value='" + strValue + "'", null);
			if (c.getCount() > 0) {
				db.execSQL("update RecentSearchB set Time=datetime('now','localtime') where Type='"
						+ type + "' and Value='" + strValue + "'");
			} else {
				// 插入数据
				String sql = "insert into RecentSearchB (Key, Value, Type, Time) values ('"
						+ strKey
						+ "','" + strValue
						+ "','" + type
						+ "', datetime('now','localtime'));";
				db.execSQL(sql);
			}
			// 更新表
			db.execSQL(
					"delete from RecentSearchB where (select count(Key) from RecentSearchB)> 30"
							+ " and Key in (select Key from RecentSearchB order by Time desc"
							+ " limit (select count(Key) from RecentSearchB) offset 30) and Type= ? ",
					new String[] { type });
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c != null){
			c.close();
		}
		if (db != null){
			db.close();
		}
	}

	/**
	 * 清除历史记录
	 * 
	 * @param type
	 *            Station:车站历史,TrainNum:车次历史.
	 */
	public void clearHistory(String type) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			String sql = "delete from RecentSearchB where Type='" + type + "';";
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (db != null){
			db.close();
		}
	}

	public void closeDB() {
		//已无额外效果
		if (this != null) {
			this.close();
		}
	}

	/**
	 * 取得火车票资讯数据
	 * 
	 * @param type
	 *            0:购票指南,1:退票、改签.
	 * @return 游标
	 */
	public List<Map<String, String>> getTicketInfos(int type) {
		SQLiteDatabase db = getWritableDatabase();
		if (db == null){
			return null;
		}
		String sql = "select * from TicketInfoB where Type='" + type + "'";
		Cursor c = db.rawQuery(sql, null);
		if (c == null){
			return null;
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while(c.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			map.put(KEY, c.getString(c.getColumnIndex("Question")));
			map.put(VALUE, c.getString(c.getColumnIndex("Answer")));
			list.add(map);
		}
		if (c != null){
			c.close();
		}
		if (db != null){
			db.close();
		}
		return list;
	}
	
	/**
	 * 更新UserTrainB
	 * @param id
	 * @param key
	 * @param value
	 */
	public void updateUserTrainB(int id, String key, String value){
		SQLiteDatabase db = getWritableDatabase();
		String sql = "update UserTrainB set " + key + "=" + value + " where _id=" + id;
		db.execSQL(sql);
		if (db != null){
			db.close();
		}
	}
}
