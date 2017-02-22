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
import com.sshine.huochexing.utils.MD5Encryptor;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.umeng.analytics.MobclickAgent;

public class ResetPwdAty extends SherlockFragmentActivity implements OnClickListener {
	/*
	 * 重置密码
	 * 请求json:{"requestType":"resetPwd","verifyNum":"dsf@gmail.com","newPwd":""}
	 * 返回json:{"resultCode":"1"}
	 */
//	private String strUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String strUrl = ServiceValue.getUserPath();
	
	public static final String VERIFY_NUM = "verifyNum";
	private EditText etNewPwd1, etNewPwd2;
	private Button btnOK;
	private String strVerifyNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_reset_pwd);
		initActionBar();
		
		initViews();
	}

	private void initViews() {
		strVerifyNum = this.getIntent().getStringExtra(VERIFY_NUM);
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
		actBar.setTitle("重置密码");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ok:
			String strNewPwd1 = etNewPwd1.getText().toString();
			String strNewPwd2 = etNewPwd2.getText().toString();
			if (!RegexUtils.checkPwd(strNewPwd1)){
				showMsg("密码由6-16位数字、字母或'_'组成" + SF.TIP);
			}else if (!strNewPwd1.equals(strNewPwd2)){
				showMsg("再次输入密码不一致" + SF.FAIL);
			}else{
				resetPwd(strNewPwd1);
			}
			break;
		}
	}
	
	private void resetPwd(final String strNewPwd){
		if (!HttpUtil.isNetworkConnected(ResetPwdAty.this)){
			showMsg("单机了，无法重置密码" + SF.NO_NETWORK);
		}else{
			new MyTask(ResetPwdAty.this, "正在提交数据..."){
				
				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					JSONObject jObj = new JSONObject();
					jObj.put("requestType", "resetPwd");
					jObj.put("verifyNum", strVerifyNum);
					jObj.put("newPwd", MD5Encryptor.getMD5(strNewPwd));
					if (httpUtil.post(strUrl, jObj.toString())){
						return httpUtil.getResponseStr();
					}else{
						return null;
					}
				}
				
				@Override
				protected void myOnPostExecute(Object result) {
					if (result == null) {
						showMsg("重置密码失败,请重试" + SF.FAIL);
					} else {
						try {
							JSONObject jsonObj = new JSONObject((String) result);
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case HttpUtil.MSG_RECEIVE_FAIL:
								showMsg("重置密码失败" + SF.FAIL);
								break;
							case HttpUtil.MSG_RECEIVE_EMPTY:
								showMsg("没有此账号" + SF.FAIL);
								break;
							case HttpUtil.MSG_RECEIVE_SUCCESS:
								showMsg("重置密码成功" + SF.SUCCESS);
								btnOK.setEnabled(false);
								Intent intent = new Intent(ResetPwdAty.this, LoginAty.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								break;
							}
						} catch (JSONException e) {
							showMsg("重置密码时出错" + SF.FAIL);
							e.printStackTrace();
						}
					}
				}
				
				@Override
				protected void onException(Exception e) {
					showMsg("重置密码时出错" + SF.FAIL);
				}
				
			}.execute(this);
		}
	}
	
	private void showMsg(String str1){
		Toast.makeText(this, str1, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			startActivity(new Intent(this, LoginAty.class));
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