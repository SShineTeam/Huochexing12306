package com.sshine.huochexing.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpHelper {
	private DefaultHttpClient mHttpClient;
	private String mCharset = HTTP.UTF_8;
	
	public HttpHelper(DefaultHttpClient hc1){
		mHttpClient = hc1;
	}
	
	public HttpHelper(DefaultHttpClient hc1, String strCharset){
		this(hc1);
		mCharset = strCharset;
	}
	
	public String getCharset(){
		return mCharset;
	}

	private synchronized void setHeaders(Collection<BasicHeader> headers) {
		if (headers != null && headers.size() != 0){
			
			@SuppressWarnings("unchecked")
			Collection<BasicHeader> preHeaders = (Collection<BasicHeader>) mHttpClient.getParams().getParameter(ClientPNames.DEFAULT_HEADERS);
			if (preHeaders == null){
				preHeaders = new ArrayList<BasicHeader>();
			}
			for(BasicHeader bh:headers){
				for(BasicHeader bh1:preHeaders){
					if(bh.getName().equals(bh1.getName())){
						preHeaders.remove(bh1);
						break;
					}
				}
				if (bh.getValue() != null){
					preHeaders.add(bh);
				}
			}
		}
	}

	public synchronized String get(Collection<BasicHeader> headers, String url) {
		String strResult = null;
		HttpGet httpGet = new HttpGet(url);
		setHeaders(headers);
		L.i("get request url:" + url);
		HttpResponse response;
		try {
			response = mHttpClient.execute(httpGet);
			L.i("CustomHttpClient get:" + String.valueOf(response.getStatusLine()));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				strResult = getContentStr(response.getEntity().getContent());
//				L.i("get response string:" + strResult);
			}
		}catch (Exception e) {
			e.printStackTrace();
			L.i("get fail");
		}
		return strResult;
	}
	
	public synchronized String post(Collection<BasicHeader> headers, String url, List<NameValuePair> lstParams) {
		String strResult = null;
		HttpPost httpPost = new HttpPost(url);
		L.i("post request url:"+url);
		setHeaders(headers);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(lstParams, mCharset));
			HttpResponse response = mHttpClient.execute(httpPost);
			L.i("CustomHttpClient post:" + String.valueOf(response.getStatusLine()));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				strResult = getContentStr(response.getEntity().getContent());
//				L.i("post response string:" + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			L.i("post fail");
		}
		return strResult;
	}

	public synchronized Bitmap getBitmap(Collection<BasicHeader> headers, String url) {
		Bitmap bitmap = null;
		HttpGet httpGet = new HttpGet(url);
		setHeaders(headers);
		L.i("get request url:" + url);
		HttpResponse response;
		try {
			response = mHttpClient.execute(httpGet);
			InputStream is = null;
			L.i("CustomHttpClient get:" + String.valueOf(response.getStatusLine()));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				is = response.getEntity().getContent();
				bitmap = BitmapFactory.decodeStream(is);
				L.i("getted response Bitmap");
			}
			if (is != null){
				is.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
			L.i("get fail");
		}
		return bitmap;
	}
	public synchronized DefaultHttpClient getHttpClient(){
		return mHttpClient;
	}
	
//	public void setHttpClient(HttpClient hc1){
//		mHttpClient = hc1;
//	}
	
	public synchronized List<Cookie> getCookies(){
		List<Cookie> cookies = mHttpClient.getCookieStore().getCookies();
		return cookies;
	}
	
	private synchronized String getContentStr(InputStream is) throws IOException{
		if(is == null){
			return null;
		}
		Scanner sca = new Scanner(is, mCharset);
        StringBuffer sb = new StringBuffer();
        while(sca.hasNextLine()) {
              sb.append(sca.nextLine());
        }
        is.close();
        sca.close();
		return sb.toString();
	}

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}
	
	public void disconnect(){
		try{
			mHttpClient.getConnectionManager().shutdown();
		}catch(Exception e){
			L.i("断开所有连接");
		}
	}
	
//	private void printCookies(){
//		L.i("cookies:");
//		List<Cookie> cookies = ((AbstractHttpClient) getHttpClient())
//				.getCookieStore().getCookies();
//		for (Cookie cookie : cookies)
//			L.i("cookie:" + cookie);
//	}
}
