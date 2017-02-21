package com.sshine.huochexing.ticketOnline;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.PassengerInfo;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.sshine.huochexing.value.TT;
import com.umeng.analytics.MobclickAgent;

public class EditPassengerAty extends BaseAty
	implements OnClickListener,IFavoriteCharacterDialogListener{
	public static final String EXTRA_PASSENGER_INFO = "extraPInfo";
	public static final String EXTRA_OPERATE  = "extraOpearate";
	public static final int EXTRA_OPERATE_ADD  = 1;
	public static final int EXTRA_OPERATE_EDIT  = 2;
	public static final String EXTRA_IS_SHOW_OK  = "extraIsShowOK";
	
	private static final int REQUEST_SEX = 1;
	private static final int REQUEST_P_TYPE = 2;
	private static final int REQUEST_ID_TYPE_NAME = 3;
	private PassengerInfo mPInfo;
	private int mOperateType = 0;
	private boolean mIsShowOK = false;
	private TextView tvPType;
	private EditText etName,etNo,etPhone,etEmail;
	private Button btnSex,btnPType,btnIdTypeName;
	private ImageView ivCommon;
	private boolean mIsCanBookingStuTicket;
	private boolean mIsShowSaveButton = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_edit_passenger);
		setTitle("编辑购票人信息");
		
		initViews();
	}
	private void initViews() {
		mOperateType = getIntent().getIntExtra(EXTRA_OPERATE, EXTRA_OPERATE_ADD);
		if (mOperateType == EXTRA_OPERATE_EDIT){
			mPInfo = (PassengerInfo) getIntent().getSerializableExtra(EXTRA_PASSENGER_INFO);
			mIsShowOK = getIntent().getBooleanExtra(EXTRA_IS_SHOW_OK, false);
		}else{
			mPInfo = new PassengerInfo();
		}
		etName = et(R.id.name);
		etNo = et(R.id.no);
		etPhone = et(R.id.phone);
		etEmail = et(R.id.email);
		btnSex = btn(R.id.sex);
		tvPType = (TextView)findViewById(R.id.edit_passenger_tvPType);
		btnPType = btn(R.id.type);
		btnIdTypeName = btn(R.id.idTypeName);
		TextView tvStatus = (TextView)findViewById(R.id.status);
		Button btnOK = btn(R.id.ok);
		ivCommon = (ImageView)findViewById(R.id.common);
		ivCommon.setOnClickListener(this);
		
		setResult(RESULT_CANCELED);
		if (mOperateType == EXTRA_OPERATE_EDIT){
			etName.setEnabled(false);
			etNo.setEnabled(false);
			btnIdTypeName.setEnabled(false);
			
			etName.setText(mPInfo.getPassenger_name());
			etNo.setText(mPInfo.getPassenger_id_no());
			etPhone.setText(mPInfo.getPhone_no());
			etEmail.setText(mPInfo.getEmail());
			btnSex.setText(mPInfo.getSex_name());
			btnPType.setText(mPInfo.getPassenger_type_name());
			btnIdTypeName.setText(mPInfo.getPassenger_id_type_name());
			MyUtils.setToogleImageStatus(ivCommon, mPInfo.isCommon());
			if (mIsShowOK){
				mIsShowSaveButton = false;
				btnOK.setVisibility(View.VISIBLE);
				mIsCanBookingStuTicket = A6Util.isCanBookingStuTicket(System.currentTimeMillis());
				if (!mIsCanBookingStuTicket){
					tvPType.setText("车票类型:");
					
				}
			}
		}else{
			MyUtils.setToogleImageStatus(ivCommon, false);
		}
		String strStatus = "<font color='#0077FF'>已通过</font>";
		if (mPInfo.getTotal_times() != 99){
			strStatus = "<b>待核验</b>";
		}
		tvStatus.setText(Html.fromHtml(strStatus));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, PassengerMangAty.class));
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mIsShowSaveButton){
			MenuItem miSave = menu.add("保存");
			miSave.setIcon(R.drawable.head_save);
			miSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			miSave.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					savePInfo(true);
					return false;
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	/*
	 * 保存乘车人信息
	 * @param isSaveToLocal 是否保存乘车人信息到本地
	 */
	private void savePInfo(boolean isSaveToLocal) {
		mPInfo.setPassenger_name(etName.getText().toString());
		mPInfo.setPassenger_id_no(etNo.getText().toString());
		mPInfo.setPhone_no(etPhone.getText().toString());
		mPInfo.setEmail(etEmail.getText().toString());
		mPInfo.setCommon(MyUtils.getToogleImageStatus(ivCommon));
		if (!Pattern.matches(RegexUtils.regexA6Name, mPInfo.getPassenger_name())){
			showMsg("姓名格式不正确" + SF.TIP);
			return;
		}
		if ("1".equals(mPInfo.getPassenger_id_type_code())){
			if (!Pattern.matches(RegexUtils.regexA6Name, mPInfo.getPassenger_id_no()) || mPInfo.getPassenger_id_no().length()!=18){
				showMsg("证件号码格式不正确" + SF.FAIL);
				return;
			}
		}else if (!Pattern.matches(RegexUtils.regexA6OtherIDTemp, mPInfo.getPassenger_id_no())){
			showMsg("证件号码格式不正确" + SF.FAIL);
			return;
		}
		if ((!TextUtils.isEmpty(mPInfo.getPhone_no())) && (!Pattern.matches(RegexUtils.regexPhone, mPInfo.getPhone_no()))){
			showMsg("手机号码格式不正确" + SF.FAIL);
			return;
		}
		if ((!TextUtils.isEmpty(mPInfo.getEmail())) && (!Pattern.matches(RegexUtils.regexEmail, mPInfo.getEmail()))){
			showMsg("电子邮箱格式不正确" + SF.FAIL);
			return;
		}
		String strPath = MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE);
		@SuppressWarnings("unchecked")
		List<PassengerInfo> lstPInfos = (List<PassengerInfo>) PersistentUtil.readObject(strPath);
		if (lstPInfos == null){
			if (mOperateType == EXTRA_OPERATE_EDIT){
				showMsg("乘车人信息保存失败"+SF.FAIL);
				return;
			}else{
				lstPInfos = new ArrayList<PassengerInfo>();
			}
		}
		if (mOperateType == EXTRA_OPERATE_EDIT){
			for(int i=0; i<lstPInfos.size(); i++){
				if (lstPInfos.get(i).getNativeIndex() == mPInfo.getNativeIndex()){
					lstPInfos.set(i, mPInfo);
					break;
				}
			}
		}else{
			int nativeIndex = lstPInfos.size()==0?1:(lstPInfos.get(lstPInfos.size()-1).getNativeIndex()+1);
			mPInfo.setNativeIndex(nativeIndex);
			lstPInfos.add(mPInfo);
		}
		if (isSaveToLocal){
			if(!PersistentUtil.writeObject(lstPInfos, strPath)){
				showMsg("乘车人信息保存失败"+SF.FAIL);
			}else{
				showMsg("乘车人信息已保存" + SF.SUCCESS);
				Intent intent = new Intent();
				intent.putExtra(EXTRA_PASSENGER_INFO, mPInfo);
				setResult(RESULT_OK, intent);
				this.finish();
			}
		}else{
			Intent intent = new Intent();
			intent.putExtra(EXTRA_PASSENGER_INFO, mPInfo);
			setResult(RESULT_OK, intent);
			this.finish();
		}
	}
	private EditText et(int id){
		EditText et1 = (EditText) findViewById(id);
		return et1;
	}
	private Button btn(int id){
		Button btn1 = (Button)findViewById(id);
		btn1.setOnClickListener(this);
		return btn1;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.sex:
			FavoriteCharacterDialogFragment.show(this, REQUEST_SEX, "性别", TT.getSexs());
			break;
		case R.id.type:
			FavoriteCharacterDialogFragment.show(this, REQUEST_P_TYPE, (mIsShowOK?"车票类别":"乘客类型"), TT.getUser_types());
			break;
		case R.id.idTypeName:
			FavoriteCharacterDialogFragment.show(this, REQUEST_ID_TYPE_NAME, "证件类型", TT.getPassenger_card_types());
			break;
		case R.id.common:
			boolean b1 = MyUtils.getToogleImageStatus(ivCommon);
			mPInfo.setCommon(b1?false:true);
			MyUtils.setToogleImageStatus(ivCommon, mPInfo.isCommon());
			break;
		case R.id.ok:
			savePInfo(false);
			break;
		}
	}
	@Override
	public void onListItemSelected(int requestCode, String key, int number) {
		switch(requestCode){
		case REQUEST_SEX:
			mPInfo.setSex_code(TT.getSexs().get(key));
			mPInfo.setSex_name(key);
			btnSex.setText(key);
			break;
		case REQUEST_P_TYPE:
			//太麻烦，暂采用硬编码
			if (mIsShowOK && (!mIsCanBookingStuTicket) && key.equals("学生")){
				showDlg(getResources().getString(R.string.canNotBookingStuTicketTip));
			}else{
				mPInfo.setPassenger_type(TT.getUser_types().get(key));
				mPInfo.setPassenger_type_name(key);
				btnPType.setText(key);
			}
			break;
		case REQUEST_ID_TYPE_NAME:
			mPInfo.setPassenger_id_type_code(TT.getPassenger_card_types().get(key));
			mPInfo.setPassenger_id_type_name(key);
			btnIdTypeName.setText(key);
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
	@Override
	public void doHeaderTask() {
	}
	@Override
	public void doFooterTask() {
	}
}

