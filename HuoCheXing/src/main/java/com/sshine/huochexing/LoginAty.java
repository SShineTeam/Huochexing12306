package com.sshine.huochexing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.line.Line;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.yixin.friends.Yixin;

import com.google.gson.Gson;
import com.sshine.huochexing.bean.ThirdPartyPlatformInfo;
import com.sshine.huochexing.userInfo.FindPwdActivity;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MD5Encryptor;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.utils.VoidAsyncTask;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.sshine.huochexing.value.TT;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ProgressDialogFragment;

public class LoginAty extends FragmentActivity implements OnClickListener {
	private static final String TAG = "LoginAty";
	private Button btnFindPwd, btnLogin, btnRegister;
	private EditText etUserName, etPwd;
	private ImageView ivQQ, ivSinaWeibo, ivDouban, ivGoogleplus, ivWeichat, ivYixin, ivRenren, ivLine;
	private DialogFragment mLoginDlg;
	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	private SettingSPUtil setSP= MyApp.getInstance().getSettingSPUtil();
	/*
	 * 登录
	 * 请求json:{"requestType":"login","loginName":"","password":""}
	 * 返回json:{"resultCode:"
	 * 1","userInfo":{"uid":"1","userName":"","nickName":"","
	 * icon":"","sex":"","email":"","point":""}}
	 * 
	 * 第三方注册
	 * 请求：{"requestType":"third_party_login",data:{"platform_name":"","platform_code":"","open_id":"","token:"","expire_time":"","nick_name":"","icon":"","gender":""}
	 * 返回：{"resultCode":"1","userInfo":{"uid":"1","userName":"","nickName":"","
	 * icon":"","sex":"","email":"","point":""}}
	 */
//	private String loginUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String loginUrl = ServiceValue.getUserPath();

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 发送成功并收到返回信息
			case HttpUtil.MSG_SEND_SUCCESS:
				if (mLoginDlg != null){
					mLoginDlg.dismissAllowingStateLoss();
					mLoginDlg = null;
				}
				String responseMsg = msg.getData().getString("response");
				try {
					JSONObject jsonObj = new JSONObject(responseMsg);
					int resultCode = jsonObj.getInt("resultCode");
					if (resultCode == HttpUtil.MSG_SEND_FAIL) {
						// 用户名密码错误
						showMsg("用户名或密码错误，登录失败" + SF.FAIL);
					} else if (resultCode == HttpUtil.MSG_RECEIVE_EMPTY) {
						showMsg("此用户不存在" + SF.FAIL);
					} else if (resultCode == HttpUtil.MSG_RECEIVE_SUCCESS) {
						JSONObject subObj = jsonObj.getJSONObject("userInfo");
						// 保存用户信息
						userSP.setSessionCode(subObj.getString("sessionCode"));
						userSP.setUId(subObj.getInt("uid"));
						userSP.setUsername(subObj.getString("userName"));
						userSP.setNickName(subObj.getString("nickName"));
						String strIcon = subObj.getString("icon");
						if (!"".equals(strIcon)) {
							userSP.setHeadIcon(subObj.getString("icon"));
						}
						userSP.setSex(subObj.getString("sex"));
						userSP.setEmail(subObj.getString("email"));
						userSP.setPoint(subObj.getInt("point"));
						switch(msg.arg1){
						case 2:
							userSP.setThirdPartyLogin(true);
							break;
						default:
							userSP.setThirdPartyLogin(false);
						}
						
						MyApp.getInstance().mDefaultClearTextPwd = subObj.optString("cleartext_pwd");
						userSP.setLogin(true);
						userSP.setAutoLogin(true);
						showMsg("登录成功" + SF.SUCCESS);
						setResult(RESULT_OK);
						if (mIsFirstUseLogin){
							startActivity(new Intent(LoginAty.this, MainActivity.class));
						}else{
							LoginAty.this.finish();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			// 发送失败
			case HttpUtil.MSG_SEND_FAIL:
				String errorMsg = "连接服务器失败";
				if (mLoginDlg != null){
					mLoginDlg.dismissAllowingStateLoss();
					mLoginDlg = null;
				}
				showMsg(errorMsg);
				break;
			}
		}
	};
	//是否是第一次使用登录界面
	private boolean mIsFirstUseLogin = false;
	protected Object mClearTextPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aty_login);
		setResult(RESULT_CANCELED);
		initViews();
	}

	private void initViews() {
		btnFindPwd = (Button) findViewById(R.id.login_btnFindPwd);
		btnFindPwd.setOnClickListener(this);
		Button btnSkip = (Button)findViewById(R.id.login_btnSkip);
		btnSkip.setOnClickListener(this);
		btnLogin = (Button) findViewById(R.id.login_btnLogin);
		btnLogin.setOnClickListener(this);
		btnRegister = (Button) findViewById(R.id.login_btnUserRegister);
		btnRegister.setOnClickListener(this);
		etUserName = (EditText) findViewById(R.id.login_etUserName);
		etPwd = (EditText) findViewById(R.id.login_etPwd);
		ivQQ = (ImageView)findViewById(R.id.login_ivQQ);
		ivQQ.setOnClickListener(this);
		ivSinaWeibo = (ImageView)findViewById(R.id.login_ivSinaWeibo);
		ivSinaWeibo.setOnClickListener(this);
		ivDouban = (ImageView)findViewById(R.id.login_ivDouban);
		ivDouban.setOnClickListener(this);
		ivGoogleplus = (ImageView)findViewById(R.id.login_ivGoogleplus);
		ivGoogleplus.setOnClickListener(this);
		ivWeichat = (ImageView)findViewById(R.id.login_ivWeichat);
		ivWeichat.setOnClickListener(this);
		ivYixin = (ImageView)findViewById(R.id.login_ivYixin);
		ivYixin.setOnClickListener(this);
		ivRenren = (ImageView)findViewById(R.id.login_ivRenren);
		ivRenren.setOnClickListener(this);
		ivLine = (ImageView)findViewById(R.id.login_ivLine);
		ivLine.setOnClickListener(this);
		
		ShareSDK.initSDK(this);
		
		if (!userSP.isThirdPartyLogin()){
			etUserName.setText(userSP.getUsername());
		}
		if (setSP.isFirstUse()){
			mIsFirstUseLogin = true;
			setSP.setFirstUse(false);
			btnSkip.setVisibility(View.VISIBLE);
		}else{
			btnSkip.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onDestroy() {
		if (mLoginDlg != null){
			mLoginDlg.dismissAllowingStateLoss();
			mLoginDlg = null;
		}
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if (setSP.isFirstUse()){
			startActivity(new Intent(this, MainActivity.class));
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btnSkip:
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
			break;
		case R.id.login_btnLogin:
			// 登录验证代码
			String usernameText = "";
			String passwordText = "";
			try {
				usernameText = etUserName.getText().toString().trim();
				passwordText = etPwd.getText().toString().trim();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 检验输入是否合法
			if (!validate(usernameText, passwordText)) {
				return;
			}
			// 登录
			// 检查网络是否可用
			if (HttpUtil.isNetworkConnected(LoginAty.this)) {
				// 登录
				login(usernameText, passwordText, false);
			} else {
				showMsg("网络不可用，请检查网络状态" + SF.NO_NETWORK);
			}
			break;
		case R.id.login_btnFindPwd:
			startActivity(new Intent(this, FindPwdActivity.class));
			break;
		case R.id.login_btnUserRegister:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		case R.id.login_ivQQ:
			doThirdPartyLogin(new QQ(this));
			break;
		case R.id.login_ivSinaWeibo:
			doThirdPartyLogin(new SinaWeibo(this));
			break;
		case R.id.login_ivDouban:
			break;
		case R.id.login_ivGoogleplus:
			doThirdPartyLogin(new GooglePlus(this));
			break;
		case R.id.login_ivWeichat:
			doThirdPartyLogin(new Wechat(this));
			break;
		case R.id.login_ivYixin:
			doThirdPartyLogin(new Yixin(this));
			break;
		case R.id.login_ivRenren:
			doThirdPartyLogin(new Renren(this));
			break;
		case R.id.login_ivLine:
			doThirdPartyLogin(new Line(this));
			break;
		}
	}
	
	private void doThirdPartyLogin(final Platform plat){
		if (plat == null){
			return;
		}
		//每次都重新授权
		plat.removeAccount();
		plat.setPlatformActionListener(new PlatformActionListener() {
			
			@Override
			public void onError(Platform platform, int action, Throwable t) {
				showMsg("登录失败");
				t.printStackTrace();
				platform.removeAccount();
			}
			
			@Override
			public void onComplete(final Platform platform, int action, HashMap<String, Object> res) {
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						getUserInfo(platform);
					}
				});
			}
			
			@Override
			public void onCancel(Platform platform, int action) {
				showMsg("已取消自动登录");
				platform.removeAccount();
			}
		});
		plat.SSOSetting(false);
		plat.showUser(null);
	}
	private void getUserInfo(final Platform plat) {
		if (mLoginDlg != null){
			mLoginDlg.dismissAllowingStateLoss();
			mLoginDlg = null;
		}
		mLoginDlg = ProgressDialogFragment
				.createBuilder(this, this.getSupportFragmentManager())
				.setMessage("火车行账户登录中...").setTitle("登录").show();
		new VoidAsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				ThirdPartyPlatformInfo pInfo = getThirdPartyPlatfromInfo(plat);
				if (pInfo == null){
					return null;
				}
//				* 请求：{"requestType":"third_party_login",data:{"platform_name":"","platform_code":"","open_id":"","nick_name":"","icon":"","gender":""}
//				 * 返回：{"resultCode":"1","uid":"","pwd":""}
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put(TT.REQUEST_TYPE, "third_party_login");
					JSONObject subObj = new JSONObject();
					subObj.put("platform_name", pInfo.getName());
					subObj.put("platform_code", pInfo.getCode());
					subObj.put("open_id", plat.getDb().getUserId());
					subObj.put("token", plat.getDb().getToken());
					subObj.put("expire_time", plat.getDb().getExpiresTime());  //毫秒
					subObj.put("nick_name", plat.getDb().getUserName());
					subObj.put("icon", "");
					subObj.put("gender", ("m".equalsIgnoreCase(plat.getDb().getUserGender())?1:0));
					jsonObj.put("data", subObj);
					HttpUtil httpUtil = new HttpUtil();
					if (httpUtil.post(loginUrl, jsonObj.toString())) {
						Message message = new Message();
						message.what = HttpUtil.MSG_SEND_SUCCESS;
						message.getData().putString("response",
								httpUtil.getResponseStr());
						//标识是第三方登录
						message.arg1 = 2;
						mHandler.sendMessage(message);
					}
					return null;
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = new Message();
				message.what = HttpUtil.MSG_SEND_FAIL;
				mHandler.sendMessage(message);
				return null;
			}
			protected void onPostExecute(Object result) {
				if (mLoginDlg != null){
					mLoginDlg.dismissAllowingStateLoss();
					mLoginDlg = null;
				}
			};
		}.start();
	}
	
	/**
	 * 转为自己平台的表示
	 * @param plat
	 * @return
	 */
	private ThirdPartyPlatformInfo getThirdPartyPlatfromInfo(Platform plat) {
		Map<Integer, ThirdPartyPlatformInfo> map = TT.getPlatformInfos();
		if (QQ.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_QQ);
		}else if (SinaWeibo.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_SINA_WEIBO);
		}
//		else if (Douban.NAME.equals(plat.getName())){
//			return map.get(TT.PLATFORM_DOUBAN);
//		}
		else if (GooglePlus.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_GOOGLE_PLUS);
		}else if (Wechat.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_WECHAT);
		}else if (Yixin.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_YIXIN);
		}else if (Renren.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_RENREN);
		}else if (Line.NAME.equals(plat.getName())){
			return map.get(TT.PLATFORM_LINE);
		}else{
			return null;
		}
	}

	private void showMsg(final String str1) {
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				Toast.makeText(LoginAty.this, str1, Toast.LENGTH_SHORT).show();
			}
			
		});
	}

	private void login(String username, String password, boolean isMD5Pwd) {
		String md5Pwd = null;
		if (isMD5Pwd){
			md5Pwd = password;
		}else{
			try {
				md5Pwd = MD5Encryptor.getMD5(password);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (TextUtils.isEmpty(md5Pwd)){
			showMsg("数据有误");
			return;
		}
		Map<String, String> mapLoginInfo = new HashMap<String, String>();
		mapLoginInfo.put("requestType", "login");
		mapLoginInfo.put("loginName", username);
		mapLoginInfo.put("password", md5Pwd);
		Gson gson = new Gson();
		final String jsonMessage = gson.toJson(mapLoginInfo);
		if (mLoginDlg != null && mLoginDlg.isVisible()){
			return;
		}
		mLoginDlg = ProgressDialogFragment
				.createBuilder(this, this.getSupportFragmentManager())
				.setMessage("火车行账户登录中...").setTitle("登录").show();
		// 新线程 完成登录
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					HttpUtil httpHelper = new HttpUtil();
					if (httpHelper.post(loginUrl, jsonMessage)) {
						Message message = new Message();
						message.what = HttpUtil.MSG_SEND_SUCCESS;
						message.getData().putString("response",
								httpHelper.getResponseStr());
						mHandler.sendMessage(message);
					} else {
						Message message = new Message();
						message.what = HttpUtil.MSG_SEND_FAIL;
						message.getData().putString("response",
								httpHelper.getResponseStr());
						//标识是是火车行账号登录
						message.arg1 = 1;
						mHandler.sendMessage(message);
					}
				} catch (SocketTimeoutException e) {
					showMsg("连接超时");
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 检验输入的内容是否合法
	 * 
	 * @param loginName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	private boolean validate(String loginName, String password) {
		if (!RegexUtils.checkUserName(loginName)) {
			showMsg("用户名需由6-16位数字、字母或_组成" + SF.FAIL);
			return false;
		}
		if (!RegexUtils.checkPwd(password)) {
			showMsg("密码由6-16位数字、字母或_组成" + SF.FAIL);
			return false;
		}
		return true;
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
