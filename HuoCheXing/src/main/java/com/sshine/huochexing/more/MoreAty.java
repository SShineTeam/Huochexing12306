package com.sshine.huochexing.more;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

public class MoreAty extends SherlockFragmentActivity
	implements OnClickListener, IFavoriteCharacterDialogListener{
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	private Button btnLockPattern;
	private ImageView ivAutoSubmit;
	private static final int REQUEST_SET_LOCK_PATTERN = 2;
	private static final int REQUEST_RESET_LOCK_PATTERN = 3;
	private String[] mDefaultAtys = {"默认", "车次查询", "我的车次", "车友聊天", "安全防盗", "抢票监控", "我的订单", "12306订单", "资讯信息"};
	private int[] mDefaultAtyValues = {0,1,2,3,4,5,6,8,7};
	private Button btnDefaultAty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_more);
		initActionBar();
		initViews();
	}

	private void initViews() {
		ivAutoSubmit = (ImageView)findViewById(R.id.autoSubmit);
		ivAutoSubmit.setOnClickListener(this);
		btnDefaultAty = (Button) findViewById(R.id.defaultAty);
		btnDefaultAty.setOnClickListener(this);
		Button btnReminderSetup = (Button)findViewById(R.id.reminder);
		btnReminderSetup.setOnClickListener(this);
		Button btnAntiTheftSetup = (Button)findViewById(R.id.antiTheft);
		btnAntiTheftSetup.setOnClickListener(this);
		findViewById(R.id.chat).setOnClickListener(this);
		btnLockPattern = (Button)findViewById(R.id.lockPattern);
		btnLockPattern.setOnClickListener(this);
		findViewById(R.id.feedback).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);
		findViewById(R.id.grade).setOnClickListener(this);
		Button btnAbout = (Button)findViewById(R.id.about);
		btnAbout.setOnClickListener(this);
		
		ivAutoSubmit.setImageResource(setSP.isAutoSubmit()?R.drawable.chat_on:R.drawable.chat_off);
		btnDefaultAty.setText(mDefaultAtys[setSP.getDefaultAtyIndex()]);
	}
	
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar(); 
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("更多");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.autoSubmit:
			boolean b1 = setSP.isAutoSubmit()==true?false:true;
			if (b1){
				ivAutoSubmit.setImageResource(R.drawable.chat_on);
			}else{
				ivAutoSubmit.setImageResource(R.drawable.chat_off);
			}
			setSP.setAutoSubmit(b1);
			break;
		case R.id.defaultAty:
			FavoriteCharacterDialogFragment.show(this, 1, "主界面", mDefaultAtys);
			break;
		case R.id.reminder:
			startActivity(new Intent(MoreAty.this, ReminderSetupAty.class));
			break;
		case R.id.antiTheft:
			startActivity(new Intent(MoreAty.this, AntiTheftSetupAty.class));
			break;
		case R.id.chat:
			startActivity(new Intent(MoreAty.this, ChatSetupAty.class));
			break;
		case R.id.lockPattern:
			Intent intent1 = new Intent(this, LockPatternAty.class);
			if (setSP.isLockPattternFirstUse()){
				intent1.putExtra(LockPatternAty.EXTRA_OPERATE, LockPatternAty.OPERATE_SET);
				startActivityForResult(intent1, REQUEST_SET_LOCK_PATTERN);
			}else{
				intent1.putExtra(LockPatternAty.EXTRA_OPERATE, LockPatternAty.EXTRA_OPERATE_RESET);
				startActivityForResult(intent1, REQUEST_RESET_LOCK_PATTERN);
			}
			break;
		case R.id.feedback:
			 FeedbackAgent agent = new FeedbackAgent(this);
			 agent.startFeedbackActivity();
			break;
		case R.id.share:
			File file = getAPKFile();
			Intent share = new Intent(Intent.ACTION_SEND);
			if (file != null){
				share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				share.setType("*/*");
			}else{
				String strMsg = "火车行为您提供列车实时信息、历史晚点、车友聊天室、安全防盗、购票、抢票、查时刻、查订单、看票价等等功能，赶紧下载体验吧:"
					+ "http://huochexing.duapp.com/";
				share.putExtra(Intent.EXTRA_TEXT, strMsg);
				share.setType("text/plain");
				showMsg("未检测到离线安装包，您可分享火车行下载链接给好友"+SF.TIP);
			}
			startActivity(Intent.createChooser(share, "分享火车行"));
			break;
		case R.id.grade:
			try{
				Uri uri = Uri.parse("market://details?id="+getPackageName());
				Intent intent = new Intent(Intent.ACTION_VIEW,uri);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}catch(Exception e){
				showMsg("没有发现任何应用市场哦"+SF.TIP);
			}
			break;
		case R.id.about:
			startActivity(new Intent(MoreAty.this, AboutAty.class));
			break;
		}
	}
	
	/**
	 * 取得安装包文件
	 * @return 获取失败则返回null
	 */
	private File getAPKFile() {
		File targetFile = null;
		String versionName = null;
		PackageManager pm = getPackageManager();
		String strUmengAPKDownloadDir = Environment.getExternalStorageDirectory()+"/Download/.um/apk";
		File file = new File(strUmengAPKDownloadDir);
		if (file.exists() && file.isDirectory()){
			File[] subFiles = file.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".apk")){
						return true;
					}else{
						return false;
					}
				}
			});
			if (subFiles != null){
				int maxVersionCode = 0;
				for(File f:subFiles){
					PackageInfo pInfo = pm.getPackageArchiveInfo(f.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
					if (pInfo != null){
						String strPName = pInfo.applicationInfo.packageName;
						//可能存在多个离线安装包的情况，所以有寻找最新版
						if (strPName.equals(getApplicationInfo().packageName) && (pInfo.versionCode>maxVersionCode)){
							 targetFile = f;
							 versionName = pInfo.versionName;
						}
					}
				}
			}
		}
		if (targetFile != null){
			//重命名文件
			File newFile = new File(targetFile.getParent()
					+ "/huochexing"+(versionName==null?"":("_"+versionName)) + ".apk");
			targetFile.renameTo(newFile);
		}
		return targetFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case REQUEST_SET_LOCK_PATTERN:
			break;
		case REQUEST_RESET_LOCK_PATTERN:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		L.i("onResume");
		if (setSP.isLockPattternFirstUse()){
			btnLockPattern.setText("设置解锁图案");
		}else{
			btnLockPattern.setText("修改解锁图案");
		}
		MobclickAgent.onResume(this);
	}

	public void showMsg(String strMsg){
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onListItemSelected(int requestCode, String key, int number) {
		setSP.setDefaultAtyIndex(mDefaultAtyValues[number]);
		btnDefaultAty.setText(mDefaultAtys[number]);
	}
}
