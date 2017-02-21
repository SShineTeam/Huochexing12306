package com.sshine.huochexing.ticketOnline;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.WebViewUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

public class TicketOnlineAty extends SherlockFragmentActivity implements View.OnClickListener {
	public static final String EXTRA_START_PAGE = "extraStartPage";
	public static final String EXTRA_POST_PARAMS = "extraPostParams";
	
	private WebView wv1;
	private EditText etUrl;
	private ImageButton btnGo,btnBack,btnForward,btnHome,btnStop, btnRefesh;
	private String mHomePage = "https://kyfw.12306.cn/otn/leftTicket/init";
	private ProgressBar pb1;
	private LinearLayout llytAddrBar, llytWebViewContainer;
	private String mStartPage, mPostParams;
	private A6UserInfoSPUtil a6Util = MyApp.getInstance().getA6UserInfoSPUtil();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_ticket_online);
		initActionBar();
		initViews();
	}
	private void initViews() {
		mStartPage = getIntent().getStringExtra(EXTRA_START_PAGE);
		mPostParams = getIntent().getStringExtra(EXTRA_POST_PARAMS);
		
		llytAddrBar = (LinearLayout)findViewById(R.id.llyt1);
		etUrl = (EditText) findViewById(R.id.content);
		btnGo = (ImageButton)findViewById(R.id.ok);
		btnGo.setOnClickListener(this);
		pb1 = (ProgressBar)findViewById(R.id.pb1);
		btnBack = (ImageButton)findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		btnForward = (ImageButton)findViewById(R.id.forward);
		btnForward.setOnClickListener(this);
		btnHome = (ImageButton)findViewById(R.id.home);
		btnHome.setOnClickListener(this);
		btnStop = (ImageButton)findViewById(R.id.stop);
		btnStop.setOnClickListener(this);
		btnRefesh = (ImageButton)findViewById(R.id.refesh);
		btnRefesh.setOnClickListener(this);
		llytWebViewContainer = (LinearLayout)findViewById(R.id.ticketOnline_llytWebViewContainer);
		wv1 = WebViewUtil.buildWebView(this, pb1);
		wv1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		llytWebViewContainer.addView(wv1);
		etUrl.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_ENTER:
						String url = etUrl.getText().toString();
						if (!url.startsWith("http://") || !url.startsWith("https://")){
							url = "http://" + url;
						}
						wv1.loadUrl(url);
						InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.hideSoftInputFromWindow(
								etUrl.getWindowToken(), 0);
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
		if (HttpUtil.isNetworkConnected(this)){
			if (mStartPage == null || "".equals(mStartPage)){
				etUrl.setText(mHomePage);
				wv1.loadUrl(mHomePage);
			}else{
				if (mPostParams == null){
					etUrl.setText(mStartPage);
					wv1.loadUrl(mStartPage);
				}else{
					//以post方式打开页面
					llytAddrBar.setVisibility(View.GONE);
					wv1.postUrl(mStartPage, EncodingUtils.getBytes(mPostParams, "BASE64"));
				}
			}
		}else{
			showMsg("没有网络" + SF.NO_NETWORK);
		}
	}

	private void showMsg(String str1) {
		Toast.makeText(this, str1, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem miAddrBar = menu.add("切换地址栏");
		miAddrBar.setTitle("切换地址栏");
		miAddrBar.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		miAddrBar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				int visibility = (llytAddrBar.getVisibility() == View.GONE)?View.VISIBLE:View.GONE;
				llytAddrBar.setVisibility(visibility);
				return false;
			}
		});
		MenuItem miReturn = menu.add("返回");
		miReturn.setTitle("返回");
		miReturn.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		miReturn.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				TicketOnlineAty.this.finish();
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.hide();
	}
	
	@Override
	public void onBackPressed() {
//		wv1.goBack();
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ok:
			wv1.loadUrl(etUrl.getText().toString());
			break;
		case R.id.back:
			wv1.goBack();
			break;
		case R.id.forward:
			wv1.goForward();
			break;
		case R.id.home:
			wv1.loadUrl(mHomePage);
			break;
		case R.id.stop:
			wv1.stopLoading();
			break;
		case R.id.refesh:
			wv1.loadUrl(getJsStr());
//			wv1.reload();
			break;
		}
	}
	
	private String getJsStr(){
		return "javascript:$('#username').val('tp7300');";
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
}
