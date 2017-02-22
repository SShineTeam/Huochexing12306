/*
 * Copyright 2013 Inmite s.r.o. (www.inmite.eu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sshine.huochexing.model;

import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;

import eu.inmite.android.lib.dialogs.BaseDialogFragment;

/**
 * Sample implementation of eu.inmite.android.lib.dialogs.BaseDialogFragment -
 * styled list of items.
 * 
 * @author David Vávra (david@inmite.eu)
 */
public class FavoriteCharacterDialogFragment extends BaseDialogFragment {

	public static String TAG = "list";
	private static String ARG_TITLE = "title";
	private static String ARG_ITEMS = "items";
	private static String ARG_REQUEST_CODE = "requestCode";
	IFavoriteCharacterDialogListener mListener;
	
	private static void show(FragmentManager fm, int requestCode, String title, String[] items){
		FavoriteCharacterDialogFragment dialog = new FavoriteCharacterDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TITLE, title);
		args.putStringArray(ARG_ITEMS, items);
		args.putInt(ARG_REQUEST_CODE, requestCode);
		dialog.setArguments(args);
		dialog.show(fm, TAG);
	}
	// 用于模态选择对话框的呈现。
	@Deprecated
	public static void show(FragmentActivity activity, String title,
			String[] items) {
		show(activity.getSupportFragmentManager(), -1, title, items);
	}

	// 自定义，用于模态选择对话框的呈现。
	public static void show(FragmentActivity activity, int requestCode,
			String title, String[] items) {
		show(activity.getSupportFragmentManager(), requestCode, title, items);
	}

	// 自定义，用于模态选择对话框的呈现。
	public static void show(Fragment fragment, int requestCode, String title,
			String[] items) {
		show(fragment.getChildFragmentManager(), requestCode, title, items);
	}
	//用Map的Value组成数组
	public static void show(FragmentActivity activity, int requestCode,
			String title, Map<String, String> mapItems) {
		if (mapItems == null){
			return;
		}else{
			String[] strs1 = new String[mapItems.size()];
			int i=0;
			 for (Entry<String, String> entry : mapItems.entrySet()) {
				 strs1[i] = entry.getKey();
				 i++;
			 }
			show(activity.getSupportFragmentManager(), requestCode, title, strs1);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Fragment targetFragment = getTargetFragment();
		if (targetFragment != null
				&& targetFragment instanceof IFavoriteCharacterDialogListener) {
			mListener = (IFavoriteCharacterDialogListener) targetFragment;
		} else if (getActivity() instanceof IFavoriteCharacterDialogListener) {
			mListener = (IFavoriteCharacterDialogListener) getActivity();
		} else if (getParentFragment() instanceof IFavoriteCharacterDialogListener) { // custom
			mListener = (IFavoriteCharacterDialogListener) getParentFragment();
		}
	}

	@Override
	public Builder build(Builder builder) {
		builder.setTitle(getTitle());
		ListAdapter adapter = null;
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_sdlg_list,
				R.id.list_item_text, getItems());
		builder.setItems(adapter, 0, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mListener != null) {
					mListener.onListItemSelected(getRequestCode(),
							getItems()[position], position);
					dismiss();
				}
			}
		});
		builder.setPositiveButton("取消", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
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
}
