package com.sshine.huochexing.listener;

import android.view.View;

public interface IMultiChoiceDialogListener {
	public void onMultiChoiceItemSelected(View v, int requestCode, int which, boolean isChecked);
	public void onMultiChoicePositiveButtonClicked(int requestCode);
	public void onMultiChoiceNagativeButtonClicked(int requestCode);
}
