package com.sshine.huochexing.more;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.ShareCore;
import cn.sharesdk.wechat.friends.Wechat;

import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;

public class AuthPageAty extends BaseAty implements OnClickListener {
	/*
	 * 获取第三方绑定列表
	 * 请求:{"requestType":"get_third_party_bind_list","data":{uid:""}}
	 * 返回:{"resultCode":"1","data":[{"platform_code":""},{"platform_code":""}]}
	 */
	/* 第三方绑定
	 * 请求：{"requestType":"third_party_bind","data":{"uid":"","platform_name":"","platform_code":"","open_id":"","token:"","expire_time":""}
	 * 返回：{"resultCode":"1"}
	 */
	/*
	 * 解除第三方绑定
	 * 请求:{"requestType":"third_party_unbind","data":{"uid":"","platform_code":"","open_id","open_id":"","token:"","expire_time":""}
	 * 返回:{"resultCode":"1"}
	 */
	private ListView lvPlats;
	private AuthAdapter mAdapter;
	private Handler mHandler = new Handler();
	private DialogFragment mLoginDlg, mRegisterDlg;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_auth_page);
		initViews();
	}
	private void initViews() {
		ShareSDK.initSDK(this);
		lvPlats.setSelector(new ColorDrawable());
		mAdapter = new AuthAdapter(this);
		lvPlats.setAdapter(mAdapter);
		lvPlats.setOnItemClickListener(mAdapter);
	}
	@Override
	protected void onDestroy() {
		if (mLoginDlg != null){
			mLoginDlg.dismissAllowingStateLoss();
		}
		if (mRegisterDlg != null){
			mRegisterDlg.dismissAllowingStateLoss();
		}
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	private class AuthAdapter extends BaseAdapter implements OnItemClickListener {
		private ArrayList<Platform> platforms;
		private Context context;
		public AuthAdapter(Context context) {
			this.context = context;
			// 获取平台列表
			Platform[] tmp = ShareSDK.getPlatformList();
			platforms = new ArrayList<Platform>();
			if (tmp == null) {
				return;
			}

			for (Platform p : tmp) {
				String name = p.getName();
				if ((p instanceof CustomPlatform)
						|| !ShareCore.canAuthorize(p.getContext(), name)) {
					continue;
				}
				if (p.getName().equals(Wechat.NAME)){
					continue;
				}
				platforms.add(p);
			}
		}

		public int getCount() {
			return platforms == null ? 0 : platforms.size();
		}

		public Platform getItem(int position) {
			return platforms.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.auth_page_item, null);
			}

			int count = getCount();
			View llItem = convertView.findViewById(R.id.llItem);
			int dp_10 = cn.sharesdk.framework.utils.R.dipToPx(parent.getContext(), 10);
			if (count == 1) {
				llItem.setBackgroundResource(R.drawable.list_item_single_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, dp_10);
			}
			else if (position == 0) {
				llItem.setBackgroundResource(R.drawable.list_item_first_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, 0);
			}
			else if (position == count - 1) {
				llItem.setBackgroundResource(R.drawable.list_item_last_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, dp_10);
			}
			else {
				llItem.setBackgroundResource(R.drawable.list_item_middle_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, 0);
			}

			Platform plat = getItem(position);
			ImageView ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
			Bitmap logo = getIcon(plat);
			if (logo != null && !logo.isRecycled()) {
				ivLogo.setImageBitmap(logo);
			}
			CheckedTextView ctvName = (CheckedTextView) convertView.findViewById(R.id.ctvName);
			ctvName.setChecked(plat.isValid());
			if (plat.isValid()) {
				String userName = plat.getDb().get("nickname");
				if (userName == null || userName.length() <= 0 || "null".equals(userName)) {
					userName = getName(plat);
				}
				ctvName.setText(userName);
			} else {
				ctvName.setText(R.string.not_yet_authorized);
			}
			return convertView;
		}

		private Bitmap getIcon(Platform plat) {
			if (plat == null) {
				return null;
			}

			String name = plat.getName();
			if (name == null) {
				return null;
			}

			String resName = "logo_" + plat.getName();
			int resId = cn.sharesdk.framework.utils.R.getResId(R.drawable.class, resName);
			return BitmapFactory.decodeResource(context.getResources(), resId);
		}

		private String getName(Platform plat) {
			if (plat == null) {
				return "";
			}

			String name = plat.getName();
			if (name == null) {
				return "";
			}

			int resId = cn.sharesdk.framework.utils.R.getStringRes(context, plat.getName());
			return context.getString(resId);
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Platform plat = getItem(position);
			CheckedTextView ctvName = (CheckedTextView) view.findViewById(R.id.ctvName);
			if (plat == null) {
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			if (plat.isValid()) {
				unregisterThirdPartyAccount(plat, ctvName);
				return;
			}

			plat.setPlatformActionListener(new PlatformActionListener() {
				
				@Override
				public void onError(Platform platform, int action, Throwable t) {
					showMsg("授权失败");
					t.printStackTrace();
					platform.removeAccount();
				}
				
				@Override
				public void onComplete(final Platform platform, int action, HashMap<String, Object> res) {
					showMsg("授权成功");
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							mAdapter.notifyDataSetChanged();
						}
					});
				}
				
				@Override
				public void onCancel(Platform platform, int action) {
					showMsg("已取消自动授权");
					platform.removeAccount();
				}
			});
			plat.showUser(null);
		}

	}

	private void showMsg(final String str1) {
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				Toast.makeText(AuthPageAty.this, str1, Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	
	public void unregisterThirdPartyAccount(Platform plat, CheckedTextView ctvName) {
		plat.removeAccount();
		ctvName.setChecked(false);
		ctvName.setText(R.string.not_yet_authorized);
	}
	@Override
	public void onClick(View v) {
	}

	@Override
	public void doHeaderTask() {
	}

	@Override
	public void doFooterTask() {
	}

}
