package com.sshine.huochexing.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.umeng.analytics.MobclickAgent;

public class FindPwdActivity extends SherlockActivity implements OnClickListener {
	private Button btnEmail;
	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_findpwd);
		initActionBar();
		
		initViews();
	}
	private void initViews() {
		btnEmail = (Button)findViewById(R.id.email);
		btnEmail.setOnClickListener(this);
		if (userSP.isLogin()){
			if ("".equals(userSP.getEmail())){
				btnEmail.setEnabled(false);
			}else{
				btnEmail.setEnabled(true);
			}
		}
	}
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("找回密码");
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
		case R.id.email:
			Intent intentText = new Intent(this, EditTextAty.class);
			Bundle bundle = new Bundle();
			bundle.putString(EditTextAty.BAR_TITLE, "找回密码"); // EditAty的标题.
			bundle.putString(EditTextAty.INFO, ""); // EditTextAty中的tvInfo要显示的绑定信息。
			bundle.putBoolean(EditTextAty.INFO_READONLY, false); // tvInfo是否只读.
			bundle.putString(EditTextAty.REGEX, RegexUtils.regexEmail); // 验证合法的正则表达式,不需要则为空。
			bundle.putString(EditTextAty.ERROR_MSG, "邮箱格式不正确"); // 验证错误时要显示的文字.
			bundle.putString(EditTextAty.TIP, "请输入邮箱地址"); // 输入文本提示.
			bundle.putString(EditTextAty.OPERATE_TEXT, "确定"); // btnOperate显示文本.
			bundle.putString(EditTextAty.OPERATE, EditTextAty.OPERATE_FIND_PWD_BY_EMAIL); // btnOperate执行什么操作。
			bundle.putString(EditTextAty.OPERATE_ARGS, ""); // btnOperate具体操作参数。
			bundle.putSerializable(EditTextAty.OBJ, EmailNextActivity.class);
			intentText.putExtras(bundle);
			startActivity(intentText);
			break;
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
