package com.sshine.huochexing.more;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.model.LockPatternUtils;
import com.sshine.huochexing.model.LockPatternView;
import com.sshine.huochexing.model.LockPatternView.Cell;
import com.sshine.huochexing.model.LockPatternView.DisplayMode;
import com.sshine.huochexing.model.LockPatternView.OnPatternListener;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

public class LockPatternAty extends Activity implements OnClickListener {
	
	public static final String EXTRA_OPERATE = "operate";
	public static final int EXTRA__OPERATE_CHECK = 1;
	public static final int EXTRA_OPERATE_RESET = 2;
	public static final int OPERATE_SET = 3;
	
	public static final String EXTRA_REQUEST_OPERATE_RESOURCE_ID = "resourceId";
	
	private LockPatternView mLpv1;
	private LockPatternUtils mLpUtils;
	private Button btnRetry, btnContinue;
	private TextView tvMsg;
	private int mOperate;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	
	//0:prepare,1:drawed1,2:continued,2:drawed2,3:confirmed.
	private int mSetState = 0;
	private String mStrTempSetPattern;
	protected List<Cell> mSetPattern;
	
	//0:prepare,1:drawedOld,2:drawed1,3:continued,4:drawed2,5:confirmed.
	private int mResetState = 0;
	private String mStrTempResetPattern;
	protected List<Cell> mResetPattern;
	
	//0:prepare,1:drawedOld
	private int mCheckState = 0;
	private Intent mResultIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_lock_pattern);
		initViews();
	}
	
	private void initViews() {
		mLpv1 = (LockPatternView)findViewById(R.id.lpv1);
		btnRetry = (Button)findViewById(R.id.retry);
		btnRetry.setOnClickListener(this);
		btnContinue = (Button)findViewById(R.id.continue1);
		btnContinue.setOnClickListener(this);
		tvMsg = (TextView)findViewById(R.id.msg);
		
		mResultIntent = new Intent();
		mResultIntent.putExtra(EXTRA_REQUEST_OPERATE_RESOURCE_ID, getIntent().getIntExtra(EXTRA_REQUEST_OPERATE_RESOURCE_ID, -1));
		setResult(RESULT_CANCELED, null);
		mLpUtils = new LockPatternUtils(this);
		mLpv1.setOnPatternListener(mOnPatternListener);
		btnContinue.setEnabled(false);
		mOperate = getIntent().getIntExtra(EXTRA_OPERATE, -1);
		if (mOperate == -1){
			showMsg("调用出错");
			this.finish();
		}
		//如果是第一次使用则让先设备解锁图案.
		if (setSP.isLockPattternFirstUse()){
			mOperate = OPERATE_SET;
			tvMsg.setText("您是第一次使用图案锁定防护，请先设置解锁图案.");
		}
		switch(mOperate){
		case EXTRA__OPERATE_CHECK:
			this.setTitle("解除锁定");
			btnRetry.setVisibility(View.GONE);
			btnContinue.setVisibility(View.GONE);
			tvMsg.setVisibility(View.GONE);
			break;
		case EXTRA_OPERATE_RESET:
			this.setTitle("修改解锁图案");
			tvMsg.setText("请先确认您已保存的解锁图案.");
			btnRetry.setVisibility(View.GONE);
			break;
		case OPERATE_SET:
			this.setTitle("设置解锁图案");
			btnRetry.setVisibility(View.GONE);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.retry:
			mLpv1.clearPattern();
			break;
		case R.id.continue1:
			switch(mOperate){
			case EXTRA__OPERATE_CHECK:
				break;
			case EXTRA_OPERATE_RESET:
				if (mResetState == 2){
					mResetState = 3;
					btnContinue.setEnabled(false);
					mLpv1.clearPattern();
					mLpv1.setEnabled(true);
					tvMsg.setText("请再次绘制图案以进行确认.");
				}else if (mResetState == 4){
					mResetState = 5;
					//保存解锁图案信息
					mLpUtils.saveLockPattern(mResetPattern);
					showMsg("解锁图案已成功保存" + SF.SUCCESS);
					setResult(Activity.RESULT_OK, mResultIntent);
					this.finish();
				}
				break;
			case OPERATE_SET:
				if (mSetState == 1){
					mSetState = 2;
					btnContinue.setEnabled(false);
					mLpv1.clearPattern();
					mLpv1.setEnabled(true);
					tvMsg.setText("请再次绘制图案以进行确认.");
				}else if (mSetState == 3){
					mSetState = 4;
					//保存解锁图案信息
					mLpUtils.saveLockPattern(mSetPattern);
					setSP.setLockPatternFirstUse(false);
					showMsg("解锁图案已成功保存" + SF.SUCCESS);
					setResult(Activity.RESULT_OK, mResultIntent);
					this.finish();
				}
				break;
			}
			break;
		}
	}
	
	private void showMsg(String str1){
		Toast.makeText(getApplicationContext(), str1, Toast.LENGTH_LONG).show();
	}
	
	private OnPatternListener mOnPatternListener = new OnPatternListener() {
		
		@Override
		public void onPatternStart() {
		}
		
		@Override
		public void onPatternDetected(List<Cell> pattern) {
			switch(mOperate){
			case EXTRA__OPERATE_CHECK:
				if (mCheckState == 0){
					int result = mLpUtils.checkPattern(pattern);
					if (result == 1){
						showMsg("校验成功" + SF.SUCCESS);
						setResult(RESULT_OK, mResultIntent);
					}else{
						showMsg("校验失败" + SF.FAIL);
						setResult(RESULT_CANCELED, null);
					}
					LockPatternAty.this.finish();
				}
				break;
			case EXTRA_OPERATE_RESET:
				if (mResetState == 0){
					int result = mLpUtils.checkPattern(pattern);
					if (result == 0){
						tvMsg.setText("原解锁图案错误，请重试.");
						mLpv1.setDisplayMode(DisplayMode.Wrong);
					}else if (result == 1){
						mResetState = 1;
						mLpv1.clearPattern();
						tvMsg.setText("原解锁图案检验成功，请输入请的解锁图案.");
					}else{
						tvMsg.setText("无效操作");
					}
				}else if (mResetState == 1){
					mStrTempResetPattern = LockPatternUtils.patternToString(pattern);
					mResetState = 2;
					tvMsg.setText("图案已记录，请点击继续.");
					mLpv1.setEnabled(false);
					btnContinue.setEnabled(true);
				}else if (mResetState == 3){
					if (mStrTempResetPattern.equals(LockPatternUtils.patternToString(pattern))){
						mResetState = 4;
						btnContinue.setEnabled(true);
						btnContinue.setText("确认");
						mLpv1.setEnabled(false);
						mResetPattern = pattern;
						tvMsg.setText("解锁图案校验成功，请确认.");
					}else{
						mLpv1.clearPattern();
						tvMsg.setText("解锁图案校验失败，请重试.");
					}
				}
				break;
			case OPERATE_SET:
				if (mSetState == 0){
					mStrTempSetPattern = LockPatternUtils.patternToString(pattern);
					mSetState = 1;
					tvMsg.setText("图案已记录，请点击继续.");
					mLpv1.setEnabled(false);
					btnContinue.setEnabled(true);
				}else if (mSetState == 2){
					if (mStrTempSetPattern.equals(LockPatternUtils.patternToString(pattern))){
						mSetState = 3;
						btnContinue.setEnabled(true);
						btnContinue.setText("确认");
						mLpv1.setEnabled(false);
						//stringToPattern方法似乎有问题，改用这个保存解锁图案.
						mSetPattern = pattern;
						tvMsg.setText("解锁图案校验成功，请确认.");
					}else{
						mLpv1.clearPattern();
						tvMsg.setText("解锁图案校验失败，请重试.");
					}
				}
				break;
			}
		}
		
		@Override
		public void onPatternCleared() {
		}
		
		@Override
		public void onPatternCellAdded(List<Cell> pattern) {
		}
	};
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
