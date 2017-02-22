package com.sshine.huochexing.ticketOnline;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.RegisterActivity;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.ConfirmPassengersInfo;
import com.sshine.huochexing.bean.PassengerInfo;
import com.sshine.huochexing.model.RandCodeDlg;
import com.sshine.huochexing.model.RandCodeDlg.RandCodeDlgListener;
import com.sshine.huochexing.userInfo.FindPwdActivity;
import com.sshine.huochexing.utils.A6SettingSPUtil;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.AESCrypt;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.VoidAsyncTask;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ProgressDialogFragment;

public class A6LoginAty extends FragmentActivity implements OnClickListener, RandCodeDlgListener {
	private static final int MSG_LOGIN_SUCCESS = 6;
	private static final int MSG_LOGIN_FAIL = 7;
	private Button btnFindPwd, btnLogin, btnRegister;
	private EditText etUserName, etPwd;
	private TextView tvGetRandCode;
	private A6UserInfoSPUtil a6UserSP = MyApp.getInstance().getA6UserInfoSPUtil();
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	private A6SettingSPUtil a6SetSP = MyApp.getInstance().getA6SettingSPUtil();
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
	private DialogFragment mProgDlg;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case A6Util.MSG_TOAST:
				showMsg((String)msg.obj);
				break;
			case A6Util.MSG_DISMISS_DIALOG:
				endHandle();
				break;
			case MSG_LOGIN_SUCCESS:
				showMsg("欢迎您，" + mBInfo.getUser_name() + SF.SUCCESS);
				try {
					if (!mBInfo.getUser_name().equals(a6UserSP.getUserName())){
						clearPersistentPInfos();
					}
					AESCrypt aes = new AESCrypt();
					a6UserSP.saveUserInfo(mBInfo.getUser_name(), aes.encrypt(mBInfo.getPassword()));
					//不再在内存中保留用户名与密码
					mBInfo.setUser_name(null);
					mBInfo.setPassword(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				setResult(RESULT_OK);
				A6LoginAty.this.finish();
				break;
			case MSG_LOGIN_FAIL:
				showMsg((String)msg.obj);
				break;
			}
		}
	};
	private RandCodeDlg mRandCodeDlg;
	private void clearPersistentPInfos() {
		PersistentUtil.writeObject(null, MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aty_a6login);
		initViews();
	}
	private void initViews() {
		btnFindPwd = (Button) findViewById(R.id.findPwd);
		btnFindPwd.setOnClickListener(this);
		etUserName = (EditText) findViewById(R.id.userName);
		etPwd = (EditText) findViewById(R.id.pwd);
		tvGetRandCode = (TextView)findViewById(R.id.a6Login_tvGetRandCode);
		tvGetRandCode.setClickable(true);
		tvGetRandCode.setOnClickListener(this);
		btnLogin = (Button) findViewById(R.id.login);
		btnLogin.setOnClickListener(this);
		btnRegister = (Button) findViewById(R.id.register);
		btnRegister.setOnClickListener(this);
		
		setResult(RESULT_CANCELED);
		tvGetRandCode.setText(Html.fromHtml("<u>获取验证码</u>"));
		try{
//			initRequestParams();
			etUserName.setText(a6UserSP.getUserName());
			AESCrypt aes;
			try {
				aes = new AESCrypt();
				etPwd.setText(aes.decrypt(a6UserSP.getPwd()));
			} catch (Exception e) {
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
		case R.id.a6Login_tvGetRandCode:
			showRandCodeDlg();
			break;
		case R.id.findPwd:
			startActivity(new Intent(this, FindPwdActivity.class));
			break;
		case R.id.register:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		}
	}

	public void doLogin(String strRandCode) {
		String strUserName = etUserName.getText() == null ? null:etUserName.getText().toString().trim();
		String strPwd = etPwd.getText() == null ? null:etPwd.getText().toString().trim();
		if (!validate(strUserName, strPwd)) {
			return;
		}
		login(strUserName, strPwd, strRandCode);
	}
	
	private void initRequestParams(){
		new VoidAsyncTask() {
			
			@Override
			protected Object doInBackground(Object... params) {
				String strContent = A6Util.getDynamicJsContent(mBInfo, A6Util.loginInit(mBInfo));
				return strContent;
			}
			
			protected void onPostExecute(final Object result) {
				new VoidAsyncTask() {
					
					@Override
					protected Object doInBackground(Object... params) {
						if (result == null){
							return null;
						}
						String strContent = (String)result;
						mBInfo.setLogin_detect_key(A6Util.getDetectHelperKey(mBInfo, strContent));
						mBInfo.setLogin_detect_value(A6Util.getDetectHelperValue(A6LoginAty.this, mBInfo, strContent, mBInfo.getLogin_detect_key()));
						return null;
					}
					protected void onPostExecute(Object result) {
						if (TextUtils.isEmpty(mBInfo.getLogin_detect_key()) || TextUtils.isEmpty(mBInfo.getLogin_detect_value())){
							showMsg("数据初始化错误，请重新进入界面");
							return;
						}
					};
				}.start();
				
			};
		}.start();
	}
	private void showRandCodeDlg() {
		mRandCodeDlg = new RandCodeDlg(this, this, RandCodeDlg.MODE_LOGIN);
		if (mRandCodeDlg != null && !mRandCodeDlg.isShowing()){
			mRandCodeDlg.show();
		}
	}
	@Override
	protected void onDestroy() {
		if (mRandCodeDlg != null){
			mRandCodeDlg.dismiss();
		}
		super.onDestroy();
	}
	private void showMsg(String str1){
		Toast.makeText(this, str1, Toast.LENGTH_SHORT).show();
	}
	
	private boolean startHandle(String strMsg){
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("网络不可用，请检测网络状态" + SF.NO_NETWORK);
			return false;
		}else{
			if (strMsg != null){
				endHandle();
				mProgDlg = ProgressDialogFragment.createBuilder(this, getSupportFragmentManager())
						.setMessage(strMsg)
						.setTitle("提示")
						.setCancelable(true)
						.show();
			}
			return true;
		}
	}
	
	private void endHandle(){
		if (mProgDlg != null){
			mProgDlg.dismiss();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void login(final String username, final String password, String randCode) {
		mBInfo.setUser_name(username);
		mBInfo.setPassword(password);
		mBInfo.setLoginRandCode(randCode);
		if (!startHandle("12306账户登录中...")){
			return;
		}
		new Thread(){
			public void run() {
				Message msg = mHandler.obtainMessage();
				A6Info a6Json = A6Util.login(mBInfo);
				if(a6Json == null){
					return;
				}
				mHandler.sendMessage(mHandler.obtainMessage(A6Util.MSG_DISMISS_DIALOG));
				if("[]".equals(a6Json.getMessages())){
					msg.what = MSG_LOGIN_SUCCESS;
					mHandler.sendMessage(msg);
				}else{
					msg.what = MSG_LOGIN_FAIL;
					if (a6Json.getMessages().length() > 2){
						msg.obj = a6Json.getMessages().substring(2, a6Json.getMessages().length()-2);
					}else{
						msg.obj = a6Json.getMessages();
					}
					mHandler.sendMessage(msg);
					return;
				}
				String strContent = A6Util.initQueryUserInfo(mBInfo);
				String strUserRealName = getUserRealName(strContent);
				if (!TextUtils.isEmpty(strUserRealName)){
					a6UserSP.setUserRealName(strUserRealName);
					//取得默认注册信息
					List<PassengerInfo> lstPInfos = (List<PassengerInfo>) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
					if (lstPInfos == null){
						ConfirmPassengersInfo cpInfo = A6Util.getPassengerDTOs(mBInfo);
						if (cpInfo != null){
							lstPInfos = cpInfo.getNormal_passengers();
							if (lstPInfos != null && lstPInfos.size() > 0){
								//设置第一个为默认乘车人
								lstPInfos.get(0).setCommon(true);
								for(int i=0; i<lstPInfos.size(); i++){
									lstPInfos.get(i).setNativeIndex(i+1);
								}
								PersistentUtil.writeObject(lstPInfos, MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
								//设置默认注册信息
								PassengerInfo pInfo = setUserSelfFlag(lstPInfos);
								if (pInfo != null){
									a6SetSP.setDefaultRegisterUserName(username);
									a6SetSP.setDefaultRegisterPwd(pInfo.getMobile_no());
									a6SetSP.setDefaultRegisterEmail(pInfo.getEmail());
								}
							}
						}
					}else{
						//设置默认注册信息
						PassengerInfo pInfo = null;
						for(PassengerInfo pInfo1:lstPInfos){
							if (pInfo1.isUserSelf()){
								pInfo = pInfo1;
								break;
							}
						}
						if (pInfo != null){
							a6SetSP.setDefaultRegisterUserName(username);
							a6SetSP.setDefaultRegisterPwd(pInfo.getMobile_no());
							a6SetSP.setDefaultRegisterEmail(pInfo.getEmail());
						}
					}
				}
			};
		}.start();
	}

	private PassengerInfo setUserSelfFlag(List<PassengerInfo> lstPInfos) {
		for(int i=0;i<lstPInfos.size(); i++){
			PassengerInfo pInfo = lstPInfos.get(i);
			if (pInfo.getPassenger_name().equals(a6UserSP.getUserRealName())){
				pInfo.setUserSelf(true);
				return pInfo;
			}
		}
		return null;
	}
	
	/**
	 * 检验输入的内容是否合法
	 * @param loginName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	private boolean validate(String loginName, String password) {
		if (loginName == null || (!RegexUtils.checkUserName(loginName)) && (!RegexUtils.checkEmail(loginName))){
			showMsg("用户名格式不正确" + SF.FAIL);
			return false;
		}
		if (password == null || !RegexUtils.checkPwd(password)) {
			showMsg("密码由6-16位数字、字母或'_'组成" + SF.FAIL);
			return false;
		}
		return true;
	}
	
	protected void sendToast(String strMsg){
		Message msg = mHandler.obtainMessage(A6Util.MSG_TOAST, strMsg);
		mHandler.sendMessage(msg);
		mHandler.sendMessage(mHandler.obtainMessage(A6Util.MSG_DISMISS_DIALOG));
	}
	
	private String getUserRealName(String strContent){
		if (strContent == null){
			return null;
		}
		int intIndex = strContent.indexOf("userDTO.studentInfoDTO.student_name");
		String strSub = strContent.substring(intIndex, intIndex+80);
		String strRealName = RegexUtils.getMatcher("value=\"(\\w+)\"", strSub);
		return strRealName;
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
	public void onRequestedRandCode(final String strRandCode) {
		mBInfo.setLoginRandCode(strRandCode);
		doLogin(strRandCode);
	}
}
