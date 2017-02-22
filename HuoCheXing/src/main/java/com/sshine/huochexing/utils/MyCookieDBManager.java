package com.sshine.huochexing.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyCookieDBManager {

	private final String DB_NAME = "a6_cookies.db";
	private final String TABLE_NAME = "CookieB";

	private DBHelper dbHelper;
	private SQLiteDatabase db;

	private static MyCookieDBManager instance;

	public static synchronized MyCookieDBManager getInstance() {
		if (instance == null) {
			instance = new MyCookieDBManager();
		}
		return instance;
	}

	public MyCookieDBManager() {
		dbHelper = new DBHelper(MyApp.getInstance(), DB_NAME, null, 1);
		db = dbHelper.getWritableDatabase();
	}

	private class DBHelper extends SQLiteOpenHelper {

		private String SQL_CAREATE_DB = "CREATE TABLE IF NOT EXISTS "//
				+ TABLE_NAME + " (" + //
				Column.AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + //
				Column.VALUE + " TEXT," + //
				Column.NAME + " TEXT," + //
				Column.COMMENT + " TEXT," + //
				Column.DOMAIN + " TEXT," + //
				Column.EXPIRY_DATE + " INTEGER," + //
				Column.PATH + " TEXT," + //
				Column.SECURE + " INTEGER," + //
				Column.VERSION + " TEXT)";//

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CAREATE_DB);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			db.execSQL(SQL_CAREATE_DB);
		}

	}

	public List<Cookie> getAllCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();

		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(Column.NAME));
			String value = cursor
					.getString(cursor.getColumnIndex(Column.VALUE));

			BasicClientCookie cookie = new BasicClientCookie(name, value);

			cookie.setComment(cursor.getString(cursor
					.getColumnIndex(Column.COMMENT)));
			cookie.setDomain(cursor.getString(cursor
					.getColumnIndex(Column.DOMAIN)));
			long expireTime = cursor.getLong(cursor
					.getColumnIndex(Column.EXPIRY_DATE));
			if (expireTime != 0) {
				cookie.setExpiryDate(new Date(expireTime));
			}
			cookie.setPath(cursor.getString(cursor.getColumnIndex(Column.PATH)));
			cookie.setSecure(cursor.getInt(cursor.getColumnIndex(Column.SECURE)) == 1);
			cookie.setVersion(cursor.getInt(cursor
					.getColumnIndex(Column.VERSION)));

			cookies.add(cookie);
		}

		cursor.close();

		return cookies;
	}

	public void saveCookie(Cookie cookie) {
		L.d("saveCookie:" + cookie);
		if (cookie == null) {
			return;
		}
		db.delete(TABLE_NAME, Column.NAME + " = ? ",
				new String[] { cookie.getName() });
		ContentValues values = new ContentValues();
		values.put(Column.VALUE, cookie.getValue());
		values.put(Column.NAME, cookie.getName());
		values.put(Column.COMMENT, cookie.getComment());
		values.put(Column.DOMAIN, cookie.getDomain());
		if (cookie.getExpiryDate() != null) {
			values.put(Column.EXPIRY_DATE, cookie.getExpiryDate().getTime());
		}
		values.put(Column.PATH, cookie.getPath());
		values.put(Column.SECURE, cookie.isSecure() ? 1 : 0);
		values.put(Column.VERSION, cookie.getVersion());

		db.insert(TABLE_NAME, null, values);
	}

	public void saveCookies(Cookie[] cookies) {
		L.d("saveCookies:" + cookies);
		if (cookies == null) {
			return;
		}

		db.beginTransaction();

		for (Cookie cookie : cookies) {
			saveCookie(cookie);
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void clear() {
		db.delete(TABLE_NAME, null, null);
	}

	public void clearExpired() {
		long time = System.currentTimeMillis();
		db.delete(TABLE_NAME, "EXPIRY_DATE < ? AND EXPIRY_DATE != 0",
				new String[] { String.valueOf(time) });
	}

	private static class Column {
		public static final String AUTO_ID = "AUTO_ID";
		public static final String VALUE = "VALUE";
		public static final String NAME = "NAME";
		public static final String COMMENT = "COMMENT";
		public static final String DOMAIN = "DOMAIN";
		public static final String EXPIRY_DATE = "EXPIRY_DATE";
		public static final String PATH = "PATH";
		public static final String SECURE = "SECURE";
		public static final String VERSION = "VERSION";
	}
}
