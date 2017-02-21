package com.sshine.huochexing.utils;

import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

public class MyCookieStore extends BasicCookieStore {
	public MyCookieStore() {
		super();
		try {
			List<Cookie> cookies = MyCookieDBManager.getInstance().getAllCookies();
			MyCookieDBManager.getInstance().clear();
			Cookie[] cookiesArr = new Cookie[cookies.size()];
			for (int i = cookies.size() - 1; i >= 0; i--) {
				cookiesArr[i] = cookies.get(i);
			}
			addCookies(cookiesArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void addCookie(Cookie cookie) {
		super.addCookie(cookie);
		try {
			MyCookieDBManager.getInstance().saveCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void addCookies(Cookie[] cookies) {
		super.addCookies(cookies);
		try {
			MyCookieDBManager.getInstance().saveCookies(cookies);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void clear() {
		super.clear();
		try {
			MyCookieDBManager.getInstance().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized boolean clearExpired(Date date) {
		try {
			MyCookieDBManager.getInstance().clearExpired();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.clearExpired(date);
	}

	@Override
	public synchronized List<Cookie> getCookies() {
		return super.getCookies();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
