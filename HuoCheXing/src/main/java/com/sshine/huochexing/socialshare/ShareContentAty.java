package com.sshine.huochexing.socialshare;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.ShareUtil;
import com.umeng.analytics.MobclickAgent;

public class ShareContentAty extends SherlockFragmentActivity implements OnClickListener {
	public static final String TAG = "ShareContentAty";
	
	public static final String CONTENT = "content";
	public static final String IMAGE_DATA_PATH = "imageDataPath";
	
	private static final String WEB_SITE = "http://huochexing.duapp.com/";
	
	private EditText contentEditText;
	private ImageView imgImageView;
	private Button btnShare;
	
	private String shareContent ;
	private String imageDataPath;
	private Bitmap imageData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_share_content);
		
		initActionBar();
		initData();
		initViews();
	}
	/**
	 * 初始化界面
	 */
	private void initViews() {
		
		if (getWindow() != null){
			getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}
		ShareSDK.initSDK(this);
		contentEditText = (EditText) this.findViewById(R.id.content);
		imgImageView = (ImageView) this.findViewById(R.id.iv1);
		btnShare = (Button) this.findViewById(R.id.ok);
		
		btnShare.setOnClickListener(this);
		contentEditText.setText(shareContent);
		imgImageView.setImageBitmap(imageData);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		shareContent = bundle.getString(CONTENT);
		imageDataPath = bundle.getString(IMAGE_DATA_PATH);
		imageData = ShareUtil.getInstance().getDiskBitmap(imageDataPath);
	}

	/**
	 * 初始化ActionBar
	 */
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("分享");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
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
	
	private void showShare(String shareText) {
		ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("火车行");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(WEB_SITE);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(shareText);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(imageDataPath);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(WEB_SITE);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(shareText);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(WEB_SITE);

        // 启动分享GUI
        oks.show(this);
   }
	
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (contentEditText.getText() == null || contentEditText.getText().toString() == ""){
			return;
		}
		String shareText = contentEditText.getText().toString();
		showShare(shareText);
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
