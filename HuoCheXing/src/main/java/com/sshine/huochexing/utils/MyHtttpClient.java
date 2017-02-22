package com.sshine.huochexing.utils;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class MyHtttpClient {
	private static DefaultHttpClient mHttpClient;
	
	public synchronized static DefaultHttpClient getHttpClient() {
		try {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			// 超时设置
			// 从连接池中取连接的超时时间
			ConnManagerParams.setTimeout(params, 10000); // 连接超时
			HttpConnectionParams.setConnectionTimeout(params, 10000); // 请求超时
			HttpConnectionParams.setSoTimeout(params, 30000);
			SchemeRegistry registry = new SchemeRegistry();
			Scheme sch1 = new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80);
			registry.register(sch1);
			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, registry);
			mHttpClient = new DefaultHttpClient(conMgr, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mHttpClient;
	}
}
