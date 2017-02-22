package com.sshine.huochexing.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sshine.huochexing.R;

public class CustomDialog extends AlertDialog
	implements View.OnClickListener{
	@SuppressWarnings("unused")
	private Context context;
	private CharSequence csTitle, csMessage, csCheckBox, csPositive,csNatural, csNagative;
	private boolean cancelable = true, checkable = false;
	private Button btnPositive, btnNatural, btnNagative; 
	private CheckBox chkNoRepeat;
	private DialogInterface.OnClickListener mListener;
	
	private CustomDialog(Context context) {
		super(context, R.style.Theme_CustomDialog);
	}
	public static class Builder{
		private CustomDialog mDialog = null;
		
		public Builder(Context context, DialogInterface.OnClickListener listener){
			mDialog = new CustomDialog(context);
			mDialog.mListener = listener;
			mDialog.context = context;
		}
		public CustomDialog create(){
			return mDialog;
		}
		
		public boolean getCancelable(){
			return mDialog.cancelable;
		}
		public Builder setCancelable(boolean b){
			mDialog.cancelable = b;
			return this;
		}
		
		public CharSequence getTitle() {
			return mDialog.csTitle;
		}
		public Builder setTitle(CharSequence title) {
			this.mDialog.csTitle = title;
			return this;
		}
		public CharSequence getMessage() {
			return mDialog.csMessage;
		}
		public Builder setMessage(CharSequence message) {
			mDialog.csMessage = message;
			return this;
		}
		public boolean getCheckable(){
			return mDialog.cancelable;
		}
		public Builder setCheckable(boolean b){
			mDialog.checkable = b;
			return this;
		}
		public CharSequence getCheckboxText() {
			return mDialog.csCheckBox;
		}
		public Builder setCheckboxText(CharSequence checkbox) {
			mDialog.csCheckBox = checkbox;
			return this;
		}
		public CharSequence getPositiveButton() {
			return mDialog.csPositive;
		}
		public Builder setPositiveButton(CharSequence positiveButton) {
			mDialog.csPositive = positiveButton;
			return this;
		}
		public CharSequence getNaturalButton() {
			return mDialog.csNatural;
		}
		public Builder setNaturalButton(CharSequence naturalButton) {
			mDialog.csNatural = naturalButton;
			return this;
		}
		public CharSequence getNagativeButton() {
			return mDialog.csNagative;
		}
		public Builder setNagativeButton(CharSequence nagativeButton) {
			mDialog.csNagative = nagativeButton;
			return this;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_custom);
		initViews();
	}

	private void initViews() {
		TextView tvTitle = (TextView)findViewById(R.id.customDlg_tvTitle);
		TextView tvSeparator0 = (TextView)findViewById(R.id.customDialog_tvSeparator0);
		TextView tvInfo = (TextView)findViewById(R.id.customDlg_tvInfo);
		chkNoRepeat = (CheckBox)findViewById(R.id.customDialog_chkNoRepeat);
		TextView tvSeparator1 = (TextView)findViewById(R.id.customDialog_tvSeparator1);
		LinearLayout llytButtons = (LinearLayout)findViewById(R.id.customDialog_llytButtons);
		btnPositive = (Button)findViewById(R.id.customDlg_btnPositive);
		btnPositive.setOnClickListener(this);
		TextView tvSeparator2 = (TextView)findViewById(R.id.customDialog_tvSeparator2);
		btnNatural = (Button)findViewById(R.id.customDlg_btnNatural);
		btnNatural.setOnClickListener(this);
		TextView tvSeparator3 = (TextView)findViewById(R.id.customDialog_tvSeparator3);
		btnNagative = (Button)findViewById(R.id.customDlg_btnNagative);
		btnNagative.setOnClickListener(this);
		
		setCancelable(cancelable);
		if (isEmpty(csTitle)){
			setGone(tvTitle);
			setGone(tvSeparator0);
		}else{
			tvTitle.setText(csTitle);
		}
		tvInfo.setText(csMessage);
		if (isEmpty(csCheckBox)){
			setGone(chkNoRepeat);
		}else{
			chkNoRepeat.setChecked(checkable);
			chkNoRepeat.setText(csCheckBox);
		}
		if (isEmpty(csPositive)){
			setGone(tvSeparator1);
			setGone(llytButtons);
		}else{
			btnPositive.setText(csPositive);
		}
		if (isEmpty(csNatural)){
			setGone(tvSeparator2);
			setGone(btnNatural);
		}else{
			btnNatural.setText(csNatural);
		}
		if (isEmpty(csNagative)){
			setGone(tvSeparator3);
			setGone(btnNagative);
		}else{
			btnNagative.setText(csNagative);
		}
	}
	
	private void setGone(View v){
		v.setVisibility(View.GONE);
	}
	
	private boolean isEmpty(CharSequence cs1){
		return (cs1 == null || cs1.length() == 0);
	}
	
	public boolean isCheckBoxChecked(){
		return chkNoRepeat.isChecked();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.customDlg_btnPositive:
			if (mListener != null){
				mListener.onClick(this, BUTTON_POSITIVE);
			}
			dismiss();
			break;
		case R.id.customDlg_btnNatural:
			if (mListener != null){
				mListener.onClick(this, BUTTON_NEUTRAL);
			}
			dismiss();
			break;
		case R.id.customDlg_btnNagative:
			if (mListener != null){
				mListener.onClick(this, BUTTON_NEGATIVE);
			}
			dismiss();
			break;
		}
	}
}
