package com.sshine.huochexing.userInfo;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.sshine.huochexing.LoginAty;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.more.AuthPageAty;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyCookieDBManager;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.sshine.huochexing.value.StoreValue;
import com.umeng.analytics.MobclickAgent;

public class UserInfoAty extends BaseAty implements
		OnClickListener,IFavoriteCharacterDialogListener {
	private static final int MSG_RECEIVE_VERIFY_FAILED = -1;
	private static final int MSG_RECEIVE_SUCCESS= 1;
	private static final int MSG_RECEIVE_PHONE_EXIST= 2;
	/*
	 * 修改用户信息
	 * 请求json:{"requestType":"updateUserInfo","uid":"55","icon":"1dd","nickName":"火车行用户","sex":"女"}
	 * 返回json:{"resultCode":"1"}
	 */
//	private String strUrl = "http://huochexing.duapp.com/server/user_info.php";
	private String strUrl = ServiceValue.getUserPath();
	
	private ImageView ivIcon;
	private TextView tvIconResId,tvUserName, tvNickname, tvSex, tvPhone, tvEmail;
	private LinearLayout llytUserName,llytNickname, llytSex, llytEmail;

	// 选择性别
	private String[] sexItems = { "男", "女" };
	private Button btnModifyPwd;
	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	private A6UserInfoSPUtil a6UserSP = MyApp.getInstance().getA6UserInfoSPUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_userinfo);
		setTitle("个人资料");
		
		initViews();
	}

	private void initViews() {
		ivIcon = (ImageView) findViewById(R.id.icon);
		ivIcon.setOnClickListener(this);
		tvIconResId = (TextView)findViewById(R.id.iconResId);
		tvUserName = (TextView)findViewById(R.id.userinfo_tvUserName);
		tvNickname = (TextView) findViewById(R.id.nickName);
		tvSex = (TextView)findViewById(R.id.sex);
		tvEmail = (TextView) findViewById(R.id.email);
		findViewById(R.id.userinfo_btnAuthPage).setOnClickListener(this);
		btnModifyPwd = (Button)findViewById(R.id.modify);
		btnModifyPwd.setOnClickListener(this);
		findViewById(R.id.change).setOnClickListener(this);
		findViewById(R.id.user).setOnClickListener(this);
		llytUserName = (LinearLayout)findViewById(R.id.userinfo_llytUserName);
		llytNickname = (LinearLayout) findViewById(R.id.llytNickname);
		llytNickname.setOnClickListener(this);
		llytSex = (LinearLayout) findViewById(R.id.llytSex);
		llytSex.setOnClickListener(this);
		llytEmail = (LinearLayout) findViewById(R.id.llytEmail);
		llytEmail.setOnClickListener(this);
		findViewById(R.id.hisFootPrint).setOnClickListener(this);
		
		if(userSP.isLogin()){
			try{
				ivIcon.setImageResource(Integer.valueOf(userSP.getHeadIcon()));
				tvIconResId.setText(userSP.getHeadIcon());
			}catch(Exception e){
				e.printStackTrace();
			}
			if (userSP.isAutoRegister()){
				llytUserName.setVisibility(View.VISIBLE);
				tvUserName.setText(userSP.getUsername());
			}else{
				llytUserName.setVisibility(View.GONE);
			}
			if (TextUtils.isEmpty(MyApp.getInstance().mDefaultClearTextPwd)){
				btnModifyPwd.setText("修改密码");
			}else{
				btnModifyPwd.setText(Html.fromHtml("修改密码(<font color='red'>默认密码:"+MyApp.getInstance().mDefaultClearTextPwd+"</font>"));
			}
			tvNickname.setText(userSP.getNickName());
			tvSex.setText(userSP.getSex());
			tvEmail.setText(userSP.getEmail());
		}else{
			btnModifyPwd.setVisibility(View.GONE);
			try{
				ivIcon.setImageResource(Integer.valueOf(userSP.getHeadIconNotLogin()));
				tvIconResId.setText(userSP.getHeadIconNotLogin());
			}catch(Exception e){
				e.printStackTrace();
			}
			tvNickname.setText(userSP.getNickNameNotLogin());
			tvSex.setText(userSP.getSexNotLogin());
			tvEmail.setText(userSP.getEmailNotLogin());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Intent intentText = new Intent(this, EditTextAty.class);
		Bundle bundle = new Bundle();

		switch (v.getId()) {
		case R.id.icon:
			Intent intent1 = new Intent(this, EditImageAty.class);
			intent1.putExtra(EditImageAty.C_RESULT_CODE, 0);
			startActivityForResult(intent1, 0);
			break;
		case R.id.llytNickname:
			bundle.putString(EditTextAty.BAR_TITLE, "修改昵称");
			bundle.putString(EditTextAty.INFO, tvNickname.getText().toString());
			bundle.putBoolean(EditTextAty.INFO_READONLY, false);
			bundle.putString(EditTextAty.REGEX, "^.{1,20}$");
			bundle.putString(EditTextAty.ERROR_MSG, "昵称不得超过20位字符");
			bundle.putString(EditTextAty.TIP, "好名字可以让别人更容易注意到你哦.");
			bundle.putString(EditTextAty.OPERATE, EditTextAty.OPERATE_ACTIVITY_RESULT);
			bundle.putString(EditTextAty.OPERATE_TEXT, "确定");
			bundle.putString(EditTextAty.OPERATE_ARGS, "1");
			intentText.putExtras(bundle);
			startActivityForResult(intentText, 1);
			break;
		case R.id.llytSex:
			FavoriteCharacterDialogFragment.show(this, 1, "性别选择", sexItems);
			break;
		case R.id.llytEmail:
			bundle.putString(EditTextAty.BAR_TITLE, "修改邮箱");
			bundle.putString(EditTextAty.INFO, tvEmail.getText().toString());
			bundle.putBoolean(EditTextAty.INFO_READONLY, false);
			bundle.putString(EditTextAty.REGEX, RegexUtils.regexEmail);
			bundle.putString(EditTextAty.ERROR_MSG, "邮箱格式不正确");
			bundle.putString(EditTextAty.TIP, "请输入新邮箱.");
			bundle.putString(EditTextAty.OPERATE, EditTextAty.OPERATE_ACTIVITY_RESULT);
			bundle.putString(EditTextAty.OPERATE_TEXT, "确定");
			bundle.putString(EditTextAty.OPERATE_ARGS, "3");
			intentText.putExtras(bundle);
			startActivityForResult(intentText, 3);
			break;
		case R.id.modify:
			startActivity(new Intent(this, ModifyPwdAty.class));
			break;
		case R.id.userinfo_btnAuthPage:
			startActivity(new Intent(this, AuthPageAty.class));
			break;
//		case R.id.hisFootPrint:
//			startActivity(new Intent(this, HistoryAty.class));
//			break;
		case R.id.clear:
			a6UserSP.clearUserInfo();
			a6UserSP.clearPowerOperateTimeMillis();
			MyCookieDBManager.getInstance().clear();
			clearPersistentPInfos();
			finish();
			break;
		case R.id.change:
			// 检测是否已经登录
			if(!HttpUtil.isNetworkConnected(this)){
				MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
				startActivity(new Intent(UserInfoAty.this, LoginAty.class));
				UserInfoAty.this.finish();
				break;
			}
			//清除服务器session
			new MyTask(this,"正在注销...") {
				@Override
				protected void onException(Exception e) {
				}
				@Override
				protected void myOnPostExecute(Object result) {
					//注销
					MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
					startActivity(new Intent(UserInfoAty.this, LoginAty.class));
					UserInfoAty.this.finish();
				}
				@Override
				protected Object myDoInBackground(Object... params) throws Exception {
					HashMap<String, String> mapUserInfo = new HashMap<String, String>();
					mapUserInfo.put("requestType", "logOut");
					mapUserInfo.put("uid", String.valueOf(userSP.getUId()));
					mapUserInfo.put("sessionCode", userSP.getSessionCode());
					HttpUtil httpUtil = new HttpUtil();
					Gson gson = new Gson();
					httpUtil.post(strUrl, gson.toJson(mapUserInfo));
					return null;
				}
			}.execute();
			break;
		case R.id.user:
			startActivity(new Intent(this, A6UserInfoAty.class));
			break;
		}
	}
	
	private void clearPersistentPInfos() {
		PersistentUtil.writeObject(null, MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
	}

	@Override
	public void onListItemSelected(int requestCode, String value, int number) {
		// 选择性别后的处理代码
		tvSex.setText(value);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) return;
		switch(resultCode){
		case 0:
			ivIcon.setImageResource(data.getIntExtra(EditImageAty.RESULT, 0));
			tvIconResId.setText(String.valueOf(data.getIntExtra(EditImageAty.RESULT, 0)));
			break;
		case 1:
			tvNickname.setText(data.getStringExtra(EditTextAty.RESULT));
			break;
		case 2:
			tvPhone.setText(data.getStringExtra(EditTextAty.RESULT));
			break;
		case 3:
			tvEmail.setText(data.getStringExtra(EditTextAty.RESULT));
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem miSave = menu.add("保存");
    	miSave.setIcon(R.drawable.head_save);
    	miSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	miSave.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				//保存用户信息
				if (!userSP.isLogin()){
					userSP.setHeadIconNotLogin(tvIconResId.getText().toString());
					userSP.setNickNameNotLogin(tvNickname.getText().toString());
					userSP.setSexNotLogin(tvSex.getText().toString());
					userSP.setEmailNotLogin(tvEmail.getText().toString());
					showMsg("个人资料已保存" + SF.SUCCESS);
				}else{
					new MyTask(UserInfoAty.this, "保存信息中..."){

						private HashMap<String, String> mapUserInfo;

						@Override
						protected Object myDoInBackground(Object... params)
								throws Exception {
							mapUserInfo = new HashMap<String, String>();
							mapUserInfo.put("requestType", "updateUserInfo");
							mapUserInfo.put("uid", String.valueOf(userSP.getUId()));
							mapUserInfo.put("sessionCode", userSP.getSessionCode());
							mapUserInfo.put("icon", tvIconResId.getText().toString());
							mapUserInfo.put("nickName", tvNickname.getText().toString());
							mapUserInfo.put("sex",tvSex.getText().toString());
							mapUserInfo.put("email", tvEmail.getText().toString());
							HttpUtil httpUtil = new HttpUtil();
							Gson gson = new Gson();
							if (httpUtil.post(strUrl, gson.toJson(mapUserInfo))){
								return httpUtil.getResponseStr();
							}else{
								return null;
							}
						}

						@Override
						protected void myOnPostExecute(Object result) {
							if (result == null) {
								showMsg("访问服务器出错,请稍候再试" + SF.FAIL);
							} else {
								try {
									JSONObject jsonObj = new JSONObject(
											(String) result);
									int intResultCode = jsonObj.getInt(HttpUtil.RESULT_CODE);
									switch(intResultCode){
									case MSG_RECEIVE_VERIFY_FAILED:
										//验证失败 需要重新登录
										showMsg("您的身份已过期,请重新登录" + SF.FAIL);
										MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
										Intent loginIntent = new Intent(UserInfoAty.this, LoginAty.class);
										startActivity(loginIntent);
										UserInfoAty.this.finish();
										break;
									case MSG_RECEIVE_SUCCESS:
										userSP.setHeadIcon(tvIconResId.getText().toString());
										userSP.setNickName(mapUserInfo.get("nickName"));
										userSP.setSex(mapUserInfo.get("sex"));
										userSP.setEmail(mapUserInfo.get("email"));
										showMsg("个人信息已保存" + SF.SUCCESS);
										break;
									case MSG_RECEIVE_PHONE_EXIST:
										showMsg("手机号已被其它用户绑定，修改失败" + SF.FAIL);
										break;
									}
								}catch(Exception e){
									e.printStackTrace();
									showMsg("保存数据时出错" + SF.FAIL);
								}
							}
						}

						@Override
						protected void onException(Exception e) {
							showMsg("保存个人信息时出错" + SF.FAIL);
						}
					}.execute(UserInfoAty.this);
				}
				return true;
			}
		});
    	return super.onCreateOptionsMenu(menu);
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void doHeaderTask() {
	}

	@Override
	public void doFooterTask() {
	}
}
