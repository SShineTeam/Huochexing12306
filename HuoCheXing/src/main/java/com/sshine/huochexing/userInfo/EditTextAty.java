package com.sshine.huochexing.userInfo;

import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MD5Encryptor;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.umeng.analytics.MobclickAgent;

public class EditTextAty extends SherlockFragmentActivity implements OnClickListener {
	/*
	 * 确认新邮箱
	 * 请求json:{"requestType":"verifyNewEmail","uid":"","pwd":"","verifyNum":"saf@qq.com"}
	 * 返回json:{"resultCode":"1","authCode":"535983"}
	 * 
	 * 找回密码
	 * 请求json:{"requestType":"findPwdByEmail","verifyNum":"saf@qq.com"}
	 * 返回json:{"resultCode":"1","authCode":"535983"}
	 */
//	private String strUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String strUrl = ServiceValue.getUserPath();
	
	private static final int MSG_RECEIVE_FAIL = 0;
	private static final int MSG_RECEIVE_SUCCESS = 1;
	private static final int MSG_RECEIVE_PWD_ERROR = 3;
	private static final int MSG_RECEIVE_VERIFY_NUM_EXIST = 4;
	private static final int MSG_RECEIVE_VERIFY_NUM_NOT_EXIST = 5;
	
	//供外部传值
	public static final String BAR_TITLE = "barTitle"; //EditAty的标题.
	public static final String INFO = "info";  //EditTextAty中的tvInfo要显示的绑定信息。
	public static final String INFO_READONLY = "infoReadonly";   //tvInfo是否只读.
	public static final String REGEX = "regex";  //验证合法的正则表达式,不需要则为空。
	public static final String ERROR_MSG = "errorMsg";  //验证错误时要显示的文字.
	public static final String TIP = "tip";  //输入文本提示.
	
	public static final String OPERATE = "operate";   //btnOperate执行什么操作
	public static final String OPERATE_ACTIVITY_RESULT = "operateActivityResult";   //返回值操作
	public static final String OPERATE_START_INTENT = "operateStartIntent";
	public static final String OPERATE_FIND_PWD_BY_EMAIL = "operateFindPwdByEmail";
	public static final String OPERATE_FIND_PWD_BY_PHONE = "operateFindPwdByPhone";
	public static final String OPERATE_BIND_EMAIL = "operateBindEmail";
	public static final String OPERATE_BIND_PHONE = "operateBindPhone";
	
	public static final String OPERATE_ARGS = "operateArgs";    //btnOperate具体操作参数。
	public static final String OPERATE_TEXT = "operateText";   //btnOperate显示文本.
	public static final String OBJ = "obj";    //要传递的类
	public static final String RESULT = "textResult";
	
	private ActionBar actBar;
	private EditText etPwd,etInfo;   //输入框
	private TextView tvTip, tvErrorMsg;
	private Button btnOperate;
	private Bundle bundle;
	private String strRegex;
	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_edittext);
		initActionBar();
		
		initViews();
	}

	private void initViews() {
		etPwd = (EditText)findViewById(R.id.pwd);
		etInfo = (EditText)findViewById(R.id.info);
		tvTip = (TextView)findViewById(R.id.tip);
		tvErrorMsg = (TextView)findViewById(R.id.errorMsg);
		btnOperate = (Button)findViewById(R.id.ok);
		btnOperate.setOnClickListener(this);
		
		//取得从上一个Activity传递过来的数据
		bundle = this.getIntent().getExtras();
		actBar.setTitle(bundle.getString(BAR_TITLE));
		etInfo.setText(bundle.getString(INFO));
		if (bundle.getBoolean(INFO_READONLY)){
			etInfo.setKeyListener(null);
		}else{
			etInfo.setSelection(etInfo.getText().length());
		}
		strRegex = bundle.getString(REGEX);
		tvTip.setText(bundle.getString(TIP));
		btnOperate.setText(bundle.getString(OPERATE_TEXT));
		
		//设置隐藏项
		String strOperate = bundle.getString(OPERATE);
		if (strOperate.equals(OPERATE_BIND_EMAIL) || strOperate.equals(OPERATE_BIND_PHONE)){
			etPwd.setVisibility(View.VISIBLE);
		}else{
			etPwd.setVisibility(View.GONE);
		}
	}
	private void initActionBar() {
		actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("火车行");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
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
		case R.id.ok:
			if (strRegex.equals("") || Pattern.matches(strRegex, etInfo.getText().toString())){
				tvErrorMsg.setVisibility(View.GONE);
				String strOperate = bundle.getString(OPERATE);
				if (strOperate.equals(OPERATE_ACTIVITY_RESULT)){
					Intent intent = new Intent();
					intent.putExtra(RESULT, etInfo.getText().toString());
					EditTextAty.this.setResult(Integer.valueOf(bundle.getString(OPERATE_ARGS)), intent);
					EditTextAty.this.finish();
				}else if (strOperate.equals(OPERATE_FIND_PWD_BY_EMAIL)){
					findPwdByEmail();
				}else if (strOperate.equals(OPERATE_BIND_EMAIL)){
					if (!RegexUtils.checkPwd(etPwd.getText().toString())){
						showMsg("密码由6-16位数字、字母或'_'组成" + SF.TIP);
					}else{
						verifyNewEmail();
					}
				}
			}else{
				tvErrorMsg.setVisibility(View.VISIBLE);
				tvErrorMsg.setText(bundle.getString(ERROR_MSG));
			}
		}
	}

	private void findPwdByEmail() {
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("单机了，无法发送邮件" + SF.NO_NETWORK);
		}else{
			new MyTask(EditTextAty.this, "正在发送验证码到" + etInfo.getText().toString()){
				private String strRequest;
				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					JSONObject jObj = new JSONObject();
					jObj.put("requestType", "findPwdByEmail");
					jObj.put("verifyNum", etInfo.getText().toString());
					strRequest = jObj.toString();
					L.i("找回密码:" + strRequest);
					if (httpUtil.post(strUrl, jObj.toString())){
						return httpUtil.getResponseStr();
					}else{
						return null;
					}
				}

				@Override
				protected void myOnPostExecute(Object result) {
					if (result == null) {
						showMsg("发送邮件失败,请重试" + SF.FAIL);
					} else {
						try {
							L.i("找回密码发送邮件结果:" + (String)result);
							JSONObject jsonObj = new JSONObject((String) result);
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case MSG_RECEIVE_FAIL:
								showMsg("发送邮件失败" + SF.FAIL);
								break;
							case MSG_RECEIVE_PWD_ERROR:
								showMsg("密码错误" + SF.FAIL);
								break;
							case MSG_RECEIVE_VERIFY_NUM_NOT_EXIST:
								showMsg("没有用户绑定此邮箱" + SF.TIP);
								break;
							case MSG_RECEIVE_SUCCESS:
								btnOperate.setEnabled(false);
								
								Intent intent = new Intent(EditTextAty.this, EmailNextActivity.class);
								intent.putExtra(EmailNextActivity.RESEND_STR, strRequest);
								intent.putExtra(EmailNextActivity.VERIFY_NUM, etInfo.getText().toString());
								intent.putExtra(EmailNextActivity.AUTH_CODE, jsonObj.getString("authCode"));
								if (bundle.getString(OPERATE).equals(OPERATE_FIND_PWD_BY_EMAIL)){
									intent.putExtra(EmailNextActivity.OPERATE, EmailNextActivity.OPERATE_FIND_PWD_BY_EMAIL);
									startActivity(intent);
								}else if (bundle.getString(OPERATE).equals(OPERATE_BIND_EMAIL)){
									intent.putExtra(EmailNextActivity.OPERATE, EmailNextActivity.OPERATE_BIND_EMAIL);
									startActivity(intent);
								}else{
									showMsg("未知操作" + SF.TIP);
								}
								break;
							}
						} catch (JSONException e) {
							showMsg("发送邮件时出错" + SF.FAIL);
							e.printStackTrace();
						}
					}
				}

				@Override
				protected void onException(Exception e) {
					if (e instanceof ConnectTimeoutException){
						showMsg("请求超时" + SF.TIP);
					}else{
						showMsg("发送邮件时出错" + SF.FAIL);
					}
				}
				
			}.execute(this);
		}
	}

	private void verifyNewEmail() {
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("单机了，无法发送邮件" + SF.NO_NETWORK);
		}else{
			new MyTask(EditTextAty.this, "正在发送验证码至" + etInfo.getText().toString()){
				private String strRequest;
				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					JSONObject jObj = new JSONObject();
					jObj.put("requestType", "verifyNewEmail");
					jObj.put("uid", userSP.getUId());
					jObj.put("pwd", MD5Encryptor.getMD5(etPwd.getText().toString()));
					jObj.put("verifyNum", etInfo.getText().toString());
					strRequest = jObj.toString();
					L.i("确认新邮箱:" + strRequest);
					if (httpUtil.post(strUrl, strRequest)){
						return httpUtil.getResponseStr();
					}else{
						return null;
					}
				}

				@Override
				protected void myOnPostExecute(Object result) {
					if (result == null) {
						showMsg("发送邮件失败,请重试" + SF.FAIL);
					} else {
						try {
							JSONObject jsonObj = new JSONObject((String) result);
							L.i("确认新邮箱结果:" + result);
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case MSG_RECEIVE_FAIL:
								showMsg("发送邮件失败" + SF.FAIL);
								break;
							case MSG_RECEIVE_PWD_ERROR:
								showMsg("密码错误" + SF.FAIL);
								break;
							case MSG_RECEIVE_VERIFY_NUM_EXIST:
								showMsg("邮箱已被其它用户绑定" + SF.TIP);
								break;
							case MSG_RECEIVE_SUCCESS:
								//禁用发送
								btnOperate.setEnabled(false);
								Intent intent = new Intent(EditTextAty.this, EmailNextActivity.class);
								intent.putExtra(EmailNextActivity.RESEND_STR, strRequest);
								intent.putExtra(EmailNextActivity.VERIFY_NUM, etInfo.getText().toString());
								intent.putExtra(EmailNextActivity.AUTH_CODE, jsonObj.getString("authCode"));
								if (bundle.getString(OPERATE).equals(OPERATE_FIND_PWD_BY_EMAIL)){
									intent.putExtra(EmailNextActivity.OPERATE, EmailNextActivity.OPERATE_FIND_PWD_BY_EMAIL);
									startActivity(intent);
								}else if (bundle.getString(OPERATE).equals(OPERATE_BIND_EMAIL)){
									intent.putExtra(EmailNextActivity.OPERATE, EmailNextActivity.OPERATE_BIND_EMAIL);
									startActivity(intent);
								}else{
									showMsg("未知操作" + SF.TIP);
								}
								break;
							}
						} catch (JSONException e) {
							showMsg("发送邮件时出错" + SF.FAIL);
							e.printStackTrace();
						}
					}
				}

				@Override
				protected void onException(Exception e) {
					if (e instanceof ConnectTimeoutException){
						showMsg("请求超时" + SF.TIP);
					}else{
						showMsg("发送邮件时出错" + SF.FAIL);
					}
				}
				
			}.execute(this);
		}
	}

	protected void showMsg(String str1) {
		Toast.makeText(this, str1, Toast.LENGTH_SHORT).show();
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
