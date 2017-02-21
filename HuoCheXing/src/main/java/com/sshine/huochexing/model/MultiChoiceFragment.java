package com.sshine.huochexing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.SimpleAdapter;

import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.listener.IMultiChoiceDialogListener;

import eu.inmite.android.lib.dialogs.BaseDialogFragment;

public class MultiChoiceFragment extends BaseDialogFragment {
	
	public static String TAG = "list";
	private static String ARG_TITLE = "title";
	private static String ARG_ITEMS = "items";
	private static String ARG_SELECTED_INDEXES = "selectedIndexes";
	private static String ARG_REQUEST_CODE = "requestCode";
	private static String ARG_POSITIVE_TEXT = "positiveText";
	private static String ARG_NAGATIVE_TEXT = "nagativeText";
	private static String ITEM = "item";
	private static String SELECT_STATUS = "selectStatus";
	private IMultiChoiceDialogListener mListener;
	
	private static void show(FragmentManager fm, int requestCode, String title, CharSequence csPositive, CharSequence csNagative, String[] items, boolean hasDefaultSelect, boolean[] selectedIndexes){
		MultiChoiceFragment dialog = new MultiChoiceFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TITLE, title);
		args.putStringArray(ARG_ITEMS, items);
		if (!hasDefaultSelect){
			boolean[] bs = new boolean[args.getStringArray(ARG_ITEMS).length];
			for(int i=0; i<bs.length; i++){
				bs[i] = false;
			}
			args.putBooleanArray(ARG_SELECTED_INDEXES, bs);
		}else{
			args.putBooleanArray(ARG_SELECTED_INDEXES, selectedIndexes);
		}
		args.putInt(ARG_REQUEST_CODE, requestCode);
		args.putCharSequence(ARG_POSITIVE_TEXT, csPositive);
		args.putCharSequence(ARG_NAGATIVE_TEXT, csNagative);
		dialog.setArguments(args);
		dialog.setCancelable(false);
		dialog.show(fm, TAG);
	}
	/*
	 *  自定义，用于复选对话框的实现,已过期，请用show(FragmentActivity activity, int requestCode,String title, String[] items)代替。
	 */
	@Deprecated
	public static void show(FragmentActivity activity, String title,
			String[] items) {
		show(activity.getSupportFragmentManager(), -1, title, "确定", null, items, false, new boolean[1]);
	}

	/**
	 *  自定义，用于复选对话框的实现
	 * @param activity
	 * @param requestCode
	 * @param title
	 * @param items
	 */
	public static void show(FragmentActivity activity, int requestCode,
			String title, String[] items) {
		show(activity.getSupportFragmentManager(), requestCode, title, "确定", null, items, false, new boolean[1]);
	}
	
	public static void show(FragmentActivity activity, int requestCode, String title, CharSequence csPositive, CharSequence csNagative,
			String[] items) {
		show(activity.getSupportFragmentManager(), requestCode, title, csPositive, csNagative, items, false, new boolean[1]);
	}
	public static void show(FragmentActivity activity, int requestCode, String title, CharSequence csPositive, CharSequence csNagative,
			String[] items, boolean[] selectedIndexes) {
		show(activity.getSupportFragmentManager(), requestCode, title, csPositive, csNagative, items, true, selectedIndexes);
	}
	
	public static void show(FragmentActivity activity, int requestCode,
			String title, String[] items, boolean[] selectedIndexes) {
		show(activity.getSupportFragmentManager(), requestCode, title, "确定", null, items, true, selectedIndexes);
	}

	// 自定义，用于模态选择对话框的呈现。
	public static void show(Fragment fragment, int requestCode, String title,
			String[] items) {
		show(fragment.getChildFragmentManager(), requestCode, title, "确定", null, items, false, new boolean[1]);
	}
	/**
	 * 自定义，用于复选对话框的实现，items.length必须与selectedIndexes.lengh相同
	 * @param fragment
	 * @param requestCode
	 * @param title
	 * @param items
	 */
		public static void show(Fragment fragment, int requestCode, String title,
				String[] items, boolean[] selectedIndexes) {
			show(fragment.getChildFragmentManager(), requestCode, title, "确定", null, items, true, selectedIndexes);
		}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Fragment targetFragment = getTargetFragment();
		if (targetFragment != null
				&& targetFragment instanceof IFavoriteCharacterDialogListener) {
			mListener = (IMultiChoiceDialogListener) targetFragment;
		} else if (getActivity() instanceof IMultiChoiceDialogListener) {
			mListener = (IMultiChoiceDialogListener) getActivity();
		} else if (getParentFragment() instanceof IMultiChoiceDialogListener) { // custom
			mListener = (IMultiChoiceDialogListener) getParentFragment();
		}
	}

	@Override
	public Builder build(Builder builder) {
		builder.setTitle(getTitle());
		SimpleAdapter adapter;
		boolean[] bs = getSelectedIndexes();
		String[] items = getItems();
		final List<Map<String, Object>> lst1 = new ArrayList<Map<String,Object>>();
		for(int i=0; i<bs.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ITEM, items[i]);
			map.put(SELECT_STATUS, bs[i]);
			lst1.add(map);
		}
		adapter = new SimpleAdapter(getActivity(), lst1, R.layout.item_list_multi_choice,
				new String[]{ITEM, SELECT_STATUS},
				new int[]{R.id.item_list_multi_choice_tv1, R.id.item_list_multi_choice_tv1});
		builder.setItems(adapter, 0, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckedTextView tv1 = (CheckedTextView)view.findViewById(R.id.item_list_multi_choice_tv1);
				tv1.setChecked(tv1.isChecked()?false:true);
				if (mListener != null) {
					mListener.onMultiChoiceItemSelected(view, getRequestCode(), position, tv1.isChecked());
				}
			}
		});
		CharSequence csPositive = getArguments().getCharSequence(ARG_POSITIVE_TEXT);
		CharSequence csNagative = getArguments().getCharSequence(ARG_NAGATIVE_TEXT);
		if (csPositive == null){
			csPositive = "确定";
		}
		builder.setPositiveButton(csPositive, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mListener != null){
					mListener.onMultiChoicePositiveButtonClicked(getRequestCode());
				}
				dismiss();
			}
		}).setNegativeButton(csNagative, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mListener != null){
					mListener.onMultiChoiceNagativeButtonClicked(getRequestCode());
				}
				dismiss();
			}
		});
		return builder;
	}

	private String getTitle() {
		return getArguments().getString(ARG_TITLE);
	}

	private String[] getItems() {
		return getArguments().getStringArray(ARG_ITEMS);
	}

	private int getRequestCode() {
		return getArguments().getInt(ARG_REQUEST_CODE, -1);
	}
	
	private boolean[] getSelectedIndexes(){
		return getArguments().getBooleanArray(ARG_SELECTED_INDEXES);
	}
}
