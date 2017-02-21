package com.sshine.huochexing.userInfo;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.umeng.analytics.MobclickAgent;

public class EmailNextActivity extends SherlockFragmentActivity implements OnClickListener{
	public static final String RESEND_STR = "resendStr";
	public static final String VERIFY_NUM = "verifyNum";
	public static final String AUTH_CODE = "authCode";
	public static final String OPERATE = "operate";
	public static final String OPERATE_FIND_PWD_BY_EMAIL = "operateFindPwdByEmail";
	public static final String OPERATE_BIND_EMAIL = "operateBindEmail";
	
	private static final int MSG_RECEIVE_FAIL = 0;
	private static final int MSG_RECEIVE_SUCCESS = 1;
	private static final int MSG_RECEIVE_PWD_ERROR = 3;
	private static final int MSG_RECEIVE_VERIFY_NUM_EXIST = 4;
	private static final int MSG_RECEIVE_VERIFY_NUM_NOT_EXIST = 5;

	/*
	 * 确认新邮箱
	 * 请求json:{"requestType":"verifyNewEmail","uid":"","pwd":"","verifyNum":"saf@qq.com"}
	 * 返回json:{"resultCode":"1","authCode":"535983"}
	 * 
	 * 找回密码
	 * 请求json:{"requestType":"findPwdByEmail","verifyNum":"saf@qq.com"}
	 * 返回json:{"resultCode":"1","authCode":"535983"}
	 *
	 * 绑定邮箱
	 * 请求json:{"requestType":"bindEmail","uid":"","verifyNum":""}
	 * 返回json:{"resultCode":"1"}
	 */
//	private String strUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String strUrl = ServiceValue.getUserPath();
	private TextView tvVerifyNum;
	private String strTempAuthCode;

	private EditText etAuthCode;
	private String strReSend,strVerifyNum;
	private Button btnReSendKey;
	private String strOperate;
	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_emailnext);
		initActionBar();
		
		initViews();
	}

	private void initViews() {
		strReSend = this.getIntent().getStringExtra(RESEND_STR);
		strVerifyNum = this.getIntent().getStringExtra(VERIFY_NUM);
		strTempAuthCode = this.getIntent().getStringExtra(AUTH_CODE);
		strOperate = this.getIntent().getStringExtra(OPERATE);
		
		tvVerifyNum = (TextView) findViewById(R.id.verifyNum);
		etAuthCode = (EditText)findViewById(R.id.code);
		Button btnOK = (Button)findViewById(R.id.ok);
		btnOK.setOnClickListener(this);
		btnReSendKey = (Button)findViewById(R.id.resend);
		btnReSendKey.setOnClickListener(this);
		tvVerifyNum.setText(strVerifyNum);
		btnReSendKey.setEnabled(false);
		CountDownTimer cdTimer = new CountDownTimer(60000, 1000){

			@Override
			public void onFinish() {
				btnReSendKey.setEnabled(true);
				btnReSendKey.setText("重新发送验证码");
			}

			@Override
			public void onTick(long millisUntilFinished) {
				btnReSendKey.setText("重新发送(" + millisUntilFinished/1000 + "秒)");
			}
		};
		cdTimer.start();
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("验证码确认");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}
	protected void showMsg(String str1) {
		Toast.makeText(this, str1, Toast.LENGTH_SHORT).show();
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
			if ("".equals(etAuthCode.getText().toString())){
				showMsg("请输入验证码" + SF.TIP);
			}else if (strTempAuthCode.equals(etAuthCode.getText().toString())){
				if (strOperate.equals(OPERATE_FIND_PWD_BY_EMAIL)){
					showMsg("验证通过" + SF.SUCCESS);
					Intent intent = new Intent(EmailNextActivity.this, ResetPwdAty.class);
					intent.putExtra(ResetPwdAty.VERIFY_NUM, strVerifyNum);
					startActivity(intent);
				}else if (strOperate.equals(OPERATE_BIND_EMAIL)){
					bindEmail();
				}
			}else{
				showMsg("验证码错误" + SF.FAIL);
			}
			break;
		case R.id.resend:
			if (strOperate.equals(OPERATE_FIND_PWD_BY_EMAIL)){
				findPwdByEmail();
			}else if (strOperate.equals(OPERATE_BIND_EMAIL)){
				verifyNewEmail();
			}
			break;
		}
	}
	private void findPwdByEmail() {
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("单机了，无法发送邮件" + SF.NO_NETWORK);
		}else{
			new MyTask(EmailNextActivity.this, "正在发送验证码至" + tvVerifyNum.getText().toString()){
				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					L.i("找回密码:" + strReSend);
					if (httpUtil.post(strUrl, strReSend)){
						return httpUtil.getResponseStr();
					}else{
						return null;
					}
				}

				@Override
				protected void myOnPostExecute(Object result) {
					if (result == null) {
						showMsg("发送邮件失败,请重试" + SF.TIP);
					} else {
						try {
							L.i("找回密码结果:" + (String)result);
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
								showMsg("邮件已发送" + SF.SUCCESS);
								//倒计时
								btnReSendKey.setEnabled(false);
								CountDownTimer cdTimer = new CountDownTimer(60000, 1000){

									@Override
									public void onFinish() {
										btnReSendKey.setEnabled(true);
										btnReSendKey.setText("重新发送验证码");
									}

									@Override
									public void onTick(long millisUntilFinished) {
										btnReSendKey.setText("重新发送(" + millisUntilFinished/1000 + "秒)");
									}
								};
								cdTimer.start();
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
					showMsg("发送邮件时出错" + SF.FAIL);
				}
				
			}.execute(this);
		}
	}
	
	private void verifyNewEmail() {
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("单机了，无法发送邮件" + SF.NO_NETWORK);
		}else{
			new MyTask(EmailNextActivity.this, "正在发送验证码至" + tvVerifyNum.getText().toString()){
				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					if (httpUtil.post(strUrl, strReSend)){
						return httpUtil.getResponseStr();
					}else{
						return null;
					}
				}

				@Override
				protected void myOnPostExecute(Object result) {
					if (result == null) {
						showMsg("发送邮件失败,请重试" + SF.TIP);
					} else {
						try {
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
							case MSG_RECEIVE_VERIFY_NUM_EXIST:
								showMsg("邮箱已被其它用户绑定" + SF.TIP);
								break;
							case MSG_RECEIVE_SUCCESS:
								showMsg("邮件已发送" + SF.SUCCESS);
								//重置验证码
								strTempAuthCode = jsonObj.getString("authCode");
								//倒计时
								btnReSendKey.setEnabled(false);
								CountDownTimer cdTimer = new CountDownTimer(60000, 1000){

									@Override
									public void onFinish() {
										btnReSendKey.setEnabled(true);
										btnReSendKey.setText("重新发送验证码");
									}

									@Override
									public void onTick(long millisUntilFinished) {
										btnReSendKey.setText("重新发送(" + millisUntilFinished/1000 + "秒)");
									}
								};
								cdTimer.start();
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

	private void bindEmail() {
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("单机了，无法绑定邮箱" + SF.NO_NETWORK);
		}else{
			new MyTask(EmailNextActivity.this, "请求数据..."){

				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					JSONObject jObj = new JSONObject();
					jObj.put("requestType", "bindEmail");
					jObj.put("uid", userSP.getUId());
					jObj.put("verifyNum", tvVerifyNum.getText().toString());
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
							L.i("绑定邮箱结果:" + (String)result);
							JSONObject jsonObj = new JSONObject((String) result);
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case MSG_RECEIVE_FAIL:
								showMsg("绑定邮箱失败" + SF.FAIL);
								break;
							case MSG_RECEIVE_VERIFY_NUM_EXIST:
								showMsg("已有用户绑定此邮箱" + SF.FAIL);
								break;
							case MSG_RECEIVE_SUCCESS:
								showMsg("验证通过,绑定邮箱成功" + SF.SUCCESS);
								if (userSP.isLogin()){
									userSP.setEmail(strVerifyNum);
								}
								Intent intent = new Intent(EmailNextActivity.this, MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
