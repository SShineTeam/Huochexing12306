package com.sshine.huochexing.userInfo;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.LoginAty;
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

public class ModifyPwdAty extends SherlockFragmentActivity implements OnClickListener {
	/*
	 * 修改密码
	 * 请求json:{"requestType":"modifyPwd","uid":"","pwd":"","newPwd":""}
	 * 			{"requestType":"modifyPwd","uid":"13","sessionCode":"90b2c4281b8e4ae2ff489c8119ef69f8","pwd":"","newPwd":""}
	 * 返回josn:{"resultCode":"1"}
	 */
//	private String strUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String strUrl = ServiceValue.getUserPath();
	private static final int MSG_RECEIVE_VERIFY_FAILED = -1;
	private static final int MSG_RECEIVE_FAIL = 0;
	private static final int MSG_RECEIVE_SUCCESS = 1;
	private static final int MSG_RECEIVE_PWD_ERROR = 3;
	
	private EditText etPwd, etNewPwd1, etNewPwd2;
	private Button btnOK;
	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_modifypwd);
		initActionBar();
		
		initViews();
	}

	private void initViews() {
		etPwd = (EditText)findViewById(R.id.pwd);
		etNewPwd1 = (EditText)findViewById(R.id.newPwd1);
		etNewPwd2 = (EditText)findViewById(R.id.newPwd2);
		btnOK = (Button)findViewById(R.id.ok);
		btnOK.setOnClickListener(this);
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("修改密码");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ok:
			final String strPwd = etPwd.getText().toString();
			final String strNewPwd1 = etNewPwd1.getText().toString();
			String strNewPwd2 = etNewPwd2.getText().toString();
			if (!RegexUtils.checkPwd(strPwd) || !RegexUtils.checkPwd(strNewPwd1)){
				showMsg("密码由6-16位数字、字母或'_'组成" + SF.TIP);
			}else if (!strNewPwd1.equals(strNewPwd2)){
				showMsg("再次输入密码不一致" + SF.FAIL);
			}else{
				if (!HttpUtil.isNetworkConnected(ModifyPwdAty.this)){
					showMsg("单机了，无法修改密码" + SF.NO_NETWORK);
				}else{
					new MyTask(ModifyPwdAty.this, "正在提交数据..."){
						
						@Override
						protected Object myDoInBackground(Object... params)
								throws Exception {
							HttpUtil httpUtil = new HttpUtil();
							JSONObject jObj = new JSONObject();
							jObj.put("requestType", "modifyPwd");
							jObj.put("uid", userSP.getUId());
							jObj.put("sessionCode", userSP.getSessionCode());
							jObj.put("pwd", MD5Encryptor.getMD5(strPwd));
							jObj.put("newPwd", MD5Encryptor.getMD5(strNewPwd1));
							L.i("修改密码:" + jObj.toString());
							if (httpUtil.post(strUrl, jObj.toString())){
								return httpUtil.getResponseStr();
							}else{
								return null;
							}
						}
						
						@Override
						protected void myOnPostExecute(Object result) {
							if (result == null) {
								showMsg("修改密码失败,请重试" + SF.FAIL);
							} else {
								try {
									L.i("修改密码结果:" + (String)result);
									JSONObject jsonObj = new JSONObject((String) result);
									int intResultCode = jsonObj
											.getInt(HttpUtil.RESULT_CODE);
									switch (intResultCode) {
									case MSG_RECEIVE_VERIFY_FAILED:
										//验证失败 需要重新登录
										showMsg("您的身份已过期,请重新登录" + SF.FAIL);
										MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
										Intent loginIntent = new Intent(ModifyPwdAty.this, LoginAty.class);
										startActivity(loginIntent);
										ModifyPwdAty.this.finish();
										break;
									case MSG_RECEIVE_FAIL:
										showMsg("修改密码失败" + SF.FAIL);
										break;
									case MSG_RECEIVE_PWD_ERROR:
										showMsg("原密码错误" + SF.FAIL);
										break;
									case MSG_RECEIVE_SUCCESS:
										showMsg("修改密码成功" + SF.NO_NETWORK);
										userSP.setLogin(false);
										Intent intent = new Intent(ModifyPwdAty.this, LoginAty.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(intent);
										ModifyPwdAty.this.finish();
										break;
									}
								} catch (JSONException e) {
									showMsg("修改密码时出错" + SF.FAIL);
									e.printStackTrace();
								}
							}
						}
						
						@Override
						protected void onException(Exception e) {
							showMsg("修改密码时出错" + SF.FAIL);
						}
						
					}.execute(this);
				}
			}
			break;
		}
	}
	
	private void showMsg(String str1){
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}