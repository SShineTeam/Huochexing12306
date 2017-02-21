package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sshine.huochexing.R;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class MyUtils {
	
	public static void showMsg(FragmentActivity context, String msg){
		SimpleDialogFragment.createBuilder(context, context.getSupportFragmentManager())
			.setTitle("提示").setMessage(msg).setPositiveButtonText("确定").show();
	}
	
	/**
	 * 设置ListView全部显示的高度
	 * @param listView 要设置的ListView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
	    ListAdapter listAdapter = listView.getAdapter(); 
	    if (listAdapter == null) {
	        // pre-condition
	        return;
	    }
	
	    int totalHeight = 0;
	    for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
	        View listItem = listAdapter.getView(i, null, listView);
	        listItem.measure(0, 0);  //计算子项View 的宽高
	        totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
	    }
	
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    //listView.getDividerHeight()获取子项间分隔符占用的高度
	    //params.height最后得到整个ListView完整显示需要的高度
	    listView.setLayoutParams(params);
	}

	public static String getIMEI(Context context) {
		TelephonyManager phoneMgr = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
		String imei = phoneMgr.getDeviceId();
		return imei;
	}
	
	//用于显示应用程序详情界面，加强了兼容性.
	private static final String SCHEME = "package";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails所在包名
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails类名
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	/**
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
	 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	 * 
	 * @param context
	 * 
	 * @param packageName
	 *            应用程序的包名
	 */
	@SuppressLint("InlinedApi")
	public static void showInstalledAppDetails(Context context, String packageName) {
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
					: APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME,
					APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}
	
	/**
	 * 播放通知音
	 */
	public static void ringNotifycation(Context context) {
		try {
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//系统自带提示音
			Ringtone rt = RingtoneManager.getRingtone(context.getApplicationContext(), uri);
			rt.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setToogleImageStatus(ImageView iv1, boolean b){
		iv1.setImageResource(b?R.drawable.chat_on : R.drawable.chat_off);
		iv1.setTag(b);
	}
	
	public static boolean getToogleImageStatus(ImageView iv1){
		if (iv1 == null || iv1.getTag() == null){
			return false;
		}else{
			return (Boolean)iv1.getTag();
		}
	}
	
	public static void setToogleTextViewStatus(TextView tv1, boolean b, String strText){
		CharSequence cs1 = Html.fromHtml("<u>" + strText + "</u>");
		CharSequence cs2 = Html.fromHtml("<u><font color=\"#4cb848\">" + strText + "</font><u>");
		tv1.setText(b?cs2:cs1);
		tv1.setTag(b);
	}
	
	public static boolean getToogleTextViewStatus(TextView btn1){
		if (btn1 == null || btn1.getTag() == null){
			return false;
		}else{
			return (Boolean)btn1.getTag();
		}
	}
	
	/**
	 * 取得版本号
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context)
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionName;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
            return null;
        }  
    }
	public static int getVersionCode(Context context)
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionCode;
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
            return -1;
        }  
    }
	public static void startApp(Context context, String strPackageName, CharSequence csTipMsg) {
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(strPackageName);
		if (intent != null){
			context.startActivity(intent);
		}else if (!TextUtils.isEmpty(csTipMsg)){
			Toast.makeText(context, csTipMsg, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void setBackgroundResource(Context context, View v, int resId){
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bm);
		v.setBackgroundDrawable(bd);
	}
	
	public static void releaseBackgroundResource(Context context, View v){
		BitmapDrawable bd=(BitmapDrawable)v.getBackground();
		v.setBackgroundResource(0);
		if (bd != null){
			bd.setCallback(null);
			if (bd.getBitmap() != null){
				bd.getBitmap().recycle();
			}
		}
	}
}
