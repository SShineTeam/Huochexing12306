package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.value.SF;

public class RCodeDialog extends Dialog implements android.view.View.OnClickListener {
	private Button btnPositive, btnNagative;
	private EditText etRandCode;
	private ImageView ivRandCode;
	private Context mContext;
	private RCodeDialogListener mListener;
	private CharSequence csTitle, csInfo;
	private static final int MSG_RQUEST_RAND_CODE_SUCCESS = 1;
	private static final int MSG_REQUET_RAND_CODE_FAIL = 2;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	@SuppressLint("HandlerLeak")
	private Handler mHandler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_REQUET_RAND_CODE_FAIL:
				showMsg("请求验证码失败" + SF.FAIL);
				ivRandCode.setEnabled(true);
				ivRandCode.clearAnimation();
				ivRandCode.setImageResource(R.drawable.refesh);
				etRandCode.setText("");
				break;
			case MSG_RQUEST_RAND_CODE_SUCCESS:
				ivRandCode.setEnabled(true);
				ivRandCode.clearAnimation();
				ivRandCode.setImageBitmap((Bitmap)msg.obj);
				etRandCode.setText("");
				break;
			}
		};
	};
	
	public RCodeDialog(Context context, CharSequence csTitle, CharSequence csInfo, RCodeDialogListener listener){
		super(context, R.style.Theme_CustomDialog);
		this.mContext = context;
		this.csTitle = csTitle;
		this.csInfo = csInfo;
		mListener = listener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_rcode);
		initViews();
	}

	private void initViews() {
		TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
		TextView tvInfo = (TextView)findViewById(R.id.tvInfo);
		etRandCode = (EditText)findViewById(R.id.etRandCode);
		ivRandCode = (ImageView)findViewById(R.id.ivRandCode);
		ivRandCode.setOnClickListener(this);
		btnPositive = (Button)findViewById(R.id.btnPositive);
		btnPositive.setOnClickListener(this);
		btnNagative = (Button)findViewById(R.id.btnNagative);
		btnNagative.setOnClickListener(this);
		
		setCancelable(true);
		tvTitle.setText(csTitle);
		tvInfo.setText(csInfo);
		btnPositive.setText("确定");
		btnNagative.setText("取消");
		etRandCode.requestFocus();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(etRandCode, InputMethodManager.SHOW_FORCED);
			}
		}, 100);
		etRandCode.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (etRandCode.getText().length() == 4){
					if (setSP.isAutoSubmit()){
						if (mListener != null){
							mListener.onClick(RCodeDialog.this, BUTTON_POSITIVE);
						}
						dismiss();
					}
				}
			}
		});
		setRandCodeNew();
	}
	private void setRandCodeNew(){
		if (mListener != null){
			ImageUtil.rotateImageForever(getContext(), ivRandCode,R.anim.imge_rotate_forever);
			ivRandCode.setEnabled(false);
			new Thread(){
				public void run() {
					Bitmap bitmap = mListener.onRCodeRequest(RCodeDialog.this);
					if (bitmap == null){
						Message msg = mHandler.obtainMessage(MSG_REQUET_RAND_CODE_FAIL);
						mHandler.sendMessage(msg);
					}else{
						Message msg = mHandler.obtainMessage(MSG_RQUEST_RAND_CODE_SUCCESS);
						msg.obj = bitmap;
						mHandler.sendMessage(msg);
					}
				};
			}.start();
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ivRandCode:
			setRandCodeNew();
			break;
		case R.id.btnPositive:
			if (mListener != null){
				mListener.onClick(this, BUTTON_POSITIVE);
			}
			dismiss();
			break;
		case R.id.btnNagative:
			if (mListener != null){
				mListener.onClick(this, BUTTON_NEGATIVE);
			}
			dismiss();
			break;
		}
	}
	public CharSequence getRCodeText(){
		return etRandCode.getText();
	}
	public interface RCodeDialogListener{
		/**
		 * 会自动在内部用新线程执行此方法
		 * @param dlg
		 * @return
		 */
		Bitmap onRCodeRequest(DialogInterface dlg);
		void onClick(DialogInterface dlg, int which);
	}
	private void showMsg(CharSequence cs1){
		Toast.makeText(getContext().getApplicationContext(), cs1, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void dismiss() {
		if (mContext != null){
			super.dismiss();
		}
	}
}
