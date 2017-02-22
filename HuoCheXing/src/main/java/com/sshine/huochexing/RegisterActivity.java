package com.sshine.huochexing;

import java.io.IOException;
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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.sshine.huochexing.utils.A6SettingSPUtil;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MD5Encryptor;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ProgressDialogFragment;

public class RegisterActivity extends SherlockFragmentActivity implements OnClickListener {
	private static final String TAG = "RegisterActivity";
	private static final int MSG_SEND_FAILD = 0;
	private static final int MSG_SEND_SUCCEED = 1;
	
	private static final int MSG_RECEIVE_FAIL = 0;
	private static final int MSG_RECEIVE_SUCCESS= 1;
	private static final int MSG_RECEIVE_USERNAME_EXIST= 2;
	
	/*
	 * 注册
	 * 请求json:{"requestType":"register","userName":"tp7309","password":"","email":""}
	 * 返回json:{"resultCode":"1","uid":1}
	 */
//	private String registUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String registUrl = ServiceValue.getUserPath();
	
	private EditText etUserName,etPwd;
	private ImageView ivEye;
	private Button btnRegist;
	private Map<String, String> mapUserInfo;
	private A6SettingSPUtil a6SetSP = MyApp.getInstance().getA6SettingSPUtil();
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			//发送成功
			case MSG_SEND_SUCCEED:
				String responseMsg = msg.getData().getString("response");
				if (mProgDlg != null){
					mProgDlg.dismissAllowingStateLoss();
				}
				try {
					JSONObject jsonObj = new JSONObject(responseMsg);
					int resultCode = jsonObj.getInt("resultCode");
					switch(resultCode){
					case MSG_RECEIVE_FAIL:
						showMsg("注册失败" + SF.FAIL);
						break;
					case MSG_RECEIVE_SUCCESS:
						//注册成功
						showMsg("注册成功" + SF.SUCCESS);
						String sessionCode = jsonObj.getString("sessionCode");
						//保存注册信息到sharepreferences
						UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
						userSP.setUId(jsonObj.getInt("uid"));
						userSP.setUsername(mapUserInfo.get("userName"));
						userSP.setNickName(mapUserInfo.get("nickName"));
						userSP.setEmail(mapUserInfo.get("email"));
						userSP.setSessionCode(sessionCode);
						userSP.setLogin(true);
						userSP.setAutoLogin(true);
						
						//清除默认注册信息
						a6SetSP.setDefaultRegisterUserName(null);
						a6SetSP.setDefaultRegisterPwd(null);
						a6SetSP.setDefaultRegisterEmail(null);
						Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						RegisterActivity.this.finish();
						break;
					case MSG_RECEIVE_USERNAME_EXIST:
						showMsg("用户名已存在" + SF.CONFLICT);
						break;
					}
				} catch (JSONException e) {
					showMsg("服务器返回信息错误，注册失败" + SF.FAIL);
					e.printStackTrace();
				}
				
				break;
			//发送失败
			case MSG_SEND_FAILD:
				if (mProgDlg != null){
					mProgDlg.dismissAllowingStateLoss();
				}
				String errorMsg = "连接服务器失败" + SF.FAIL;
				Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
			}
		}
		
	};
	private EditText etEmail;
	private DialogFragment mProgDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_register);
		initActionBar();
		initViews();
	}

	private void initViews() {
		
		etUserName = (EditText)findViewById(R.id.userName);
		etPwd=(EditText)findViewById(R.id.pwd);
		ivEye = (ImageView)findViewById(R.id.register_eye);
		ivEye.setOnClickListener(this);
		etEmail = (EditText)findViewById(R.id.email);
		btnRegist = (Button)findViewById(R.id.ok);
		btnRegist.setOnClickListener(this);
		
		if (a6SetSP.getDefaultRegisterUserName() != null){
			etUserName.setText(a6SetSP.getDefaultRegisterUserName());
			etPwd.setText(a6SetSP.getDefaultRegisterPwd());
			etEmail.setText(a6SetSP.getDefaultRegisterEmail());
		}
		setPwdVisible(false);
	}

	private void setPwdVisible(boolean b) {
		if (!b){
			ivEye.setBackgroundResource(R.drawable.eye_close);
			ivEye.setTag(false);
			etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		}else{
			ivEye.setBackgroundResource(R.drawable.eye_open);
			ivEye.setTag(true);
			etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		}
		etPwd.invalidate();
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("用户注册");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.register_eye:
			setPwdVisible(!(Boolean)ivEye.getTag());
			break;
		case R.id.ok:
			String userNameText = this.etUserName.getText().toString().trim();
			String passwordText = this.etPwd.getText().toString().trim();
			String emailText = this.etEmail.getText().toString().trim();
			//检查输入是否合法
			if(!validate(userNameText,passwordText,emailText)){
				return;
			}
			//注册
			if(HttpUtil.isNetworkConnected(this)){
				try {
					regist(userNameText,MD5Encryptor.getMD5(passwordText), emailText);
				}catch (Exception e) {
					showMsg("网络不可用，请检查网络状态" + SF.NO_NETWORK);
					e.printStackTrace();
				}
			}else{
				showMsg("网络不可用，请检查网络状态" + SF.NO_NETWORK);
			}
			break;
		}
	}

	private void regist(String userName,String password, String email) {

		Log.i(TAG, "注册开始");
		mapUserInfo = new HashMap<String, String>();
		mapUserInfo.put("requestType", "register");
		mapUserInfo.put("userName", userName);
		mapUserInfo.put("password", password);
		mapUserInfo.put("email", email);
		Gson gson = new Gson();
		final String jsonMessage= gson.toJson(mapUserInfo);
		mProgDlg = ProgressDialogFragment.createBuilder(this, this.getSupportFragmentManager())
				.setTitle("注册")
				.setMessage("正在注册...")
				.show();
		//新线程 完成注册
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					HttpUtil httpHelper = new HttpUtil();
					if(httpHelper.post(registUrl, jsonMessage)){
						Message message = new Message();
						message.what = MSG_SEND_SUCCEED;
						message.getData().putString("response", httpHelper.getResponseStr());
						handler.sendMessage(message);
					}else{
						Message message = new Message();
						message.what = MSG_SEND_FAILD;
						message.getData().putString("response", httpHelper.getResponseStr());
						handler.sendMessage(message);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	/**
	 * 检查输入内容是否合法
	 * @param userName 用户名
	 * @param password 密码
	 * @param email 电子邮箱
	 * @return
	 */
	private boolean validate(String userName,String password,String email) {
		if(!RegexUtils.checkUserName(userName)){
			showMsg("用户名由6-16位数字、字母或'_'组成" + SF.FAIL);
			return false;
		}
		if(!RegexUtils.checkPwd(password)){
			showMsg("密码由6-16位数字、字母或'_'组成" + SF.FAIL);
			return false;
		}
		if (!"".equals(email) && (!RegexUtils.checkEmail(email))){	
			showMsg("邮箱格式不正确");
			return false;
		}
		return true;
	}
	private void showMsg(String str1){
		Toast.makeText(this, str1, Toast.LENGTH_LONG).show();
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
