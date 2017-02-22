package com.sshine.huochexing.utils;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sshine.huochexing.value.SF;

public class WebViewUtil {
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public static WebView buildWebView(final Context context, final ProgressBar pb1){
		WebView wv1 = new WebView(context);
		wv1.setWebViewClient(new WebViewClient() {
			@SuppressLint("NewApi")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				// TODO Auto-generated method stub
				return super.shouldInterceptRequest(view, url);
			}
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (pb1 != null){
					pb1.setProgress(0);
					pb1.setVisibility(View.VISIBLE);
				}
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				/* 将cookie保存起来*/
//				saveCookie(url);
				super.onPageFinished(view, url);
			}
			
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();  // 接受所有网站的证书
				super.onReceivedSslError(view, handler, error);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(context, 
						"加载页面出错了" + SF.FAIL, Toast.LENGTH_SHORT).show();
			}
		});
		wv1.setWebChromeClient(new WebChromeClient() {
			//显示进度条
			public void onProgressChanged(WebView view, int progress) {
				if (pb1 != null){
					pb1.setProgress(progress);
					if (pb1.getProgress() == pb1.getMax()){
						pb1.setVisibility(View.GONE);
					}
				}
			}
		});
		WebSettings websettings = wv1.getSettings();
		websettings.setAllowFileAccess(true);
		websettings.setJavaScriptEnabled(true);
	    websettings.setSupportZoom(true);
	    websettings.setCacheMode(2);
	    websettings.setDefaultTextEncodingName(HTTP.UTF_8);
		return wv1;
	}
	private static void saveCookie(String url){
		String cookie = CookieManager.getInstance().getCookie(url);
		if(cookie !=null && !cookie.equals("")){  
            String[] cookies = cookie.split(";");  
            for(int i=0; i< cookies.length; i++){  
                String[] nvp = cookies[i].split("=");  
                BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);  
                //c.setVersion(0);  
                c.setDomain("kyfw.12306.cn");
                MyCookieStore myCookieStore = null;
                if (MyApp.getInstance().getCommonBInfo().getHttpHelper().getHttpClient().getCookieStore()
                		instanceof MyCookieStore){
                	myCookieStore = (MyCookieStore)MyApp.getInstance().getCommonBInfo().getHttpHelper().getHttpClient().getCookieStore();
                }
                if (myCookieStore != null){
                	myCookieStore.addCookie(c);
                }
            }
       }  
		CookieSyncManager.getInstance().sync();
	}
	private void setWebViewCookie() {
//		List<Cookie> lstCookies = MyApp.getInstance().getCommonBInfo().getHttpHelper().getCookies();
//		a6Util.saveCookies(lstCookies);
//		CookieSyncManager.createInstance(this);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();
//        cookieManager.setCookie(ServiceValue.A6_DOMIN, a6Util.getCookiesStr());
//        CookieSyncManager.getInstance().sync();
	}
}
