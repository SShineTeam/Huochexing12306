package com.sshine.huochexing.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.view.View;
import android.widget.ScrollView;

import com.sshine.huochexing.bean.Travel;

public class ShareUtil {
	private static ShareUtil shareUtil;
	private static Object lock = new Object();
	
	private ShareUtil(){}
	
	public static ShareUtil getInstance(){
		if(shareUtil == null){
			synchronized (lock) {
				if(shareUtil == null){
					shareUtil = new ShareUtil();
				}
			}
		}
		return shareUtil;
	}
	/**
	 * 根据当前车次 取得车此分享状态
	 * @param travel 当前车次
	 * @return
	 */
	public String getShareConetnt(Travel travel) {
		if (travel == null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		switch (travel.getMsgType()) {
		case 0:
			//上车前
			sb.append("我将要乘坐").append(travel.getTrainNum()).append("次列车");
			sb.append("从").append(travel.getStartStation()).append("站，");
			sb.append("去往").append(travel.getEndStation()).append("站。");
			break;
		case 1:
			//在车上
			sb.append("我正在乘坐").append(travel.getTrainNum()).append("次列车");
			sb.append("从").append(travel.getStartStation()).append("站，");
			sb.append("去往").append(travel.getEndStation()).append("站。");
			break;
		case 2:
			//已到站
			sb.append("我已经乘坐").append(travel.getTrainNum()).append("次列车");
			sb.append("到达了").append(travel.getEndStation()).append("站。");
			break;
		case 3:
			//未运行
			sb.append("我将要乘坐").append(travel.getTrainNum()).append("次列车");
			sb.append("去往").append(travel.getEndStation()).append("站。");
			break;
		}
		sb.append("\r\n");
		String strMsg = "火车行提供列车实时信息、历史晚点、车友聊天室、安全防盗、购票、抢票、查时刻、查订单、看票价等等功能，需要的朋友赶紧下载体验吧^_^";
		sb.append(strMsg);
		return sb.toString();
	}
	
	/**
	 * 根据磁盘路径取得
	 * @param pathString
	 * @return
	 */
	public Bitmap getDiskBitmap(String pathString)  
	{  
	    Bitmap bitmap = null;  
	    try  
	    {  
	        File file = new File(pathString);  
	        if(file.exists())  
	        {  
	            bitmap = BitmapFactory.decodeFile(pathString);  
	        }
	    } catch (Exception e)  
	    {  
	    	e.printStackTrace();
	    	return null;
	    }  
	    return bitmap;  
	}  
	
	/**
	 * 截取scrollview的屏幕
	 * **/
	public static Bitmap getBitmapByView(ScrollView scrollView) {
		int h = 0;
		Bitmap bitmap = null;
		// 获取listView实际高度
		for (int i = 0; i < scrollView.getChildCount(); i++) {
			h += scrollView.getChildAt(i).getHeight();
		}
		// 创建对应大小的bitmap
		bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
				Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmap);
		scrollView.draw(canvas);
		return bitmap;
	}
	
	/**
	 * 保存截图到sd卡 并返回截图路径
	 * @param activity
	 * @return
	 */
	public String getScreenShut(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		//保存到sd卡
		String  sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		String fileName = "shareIMG"+new Date().getTime()+".png";
		String path = sdCardRoot+"/HuoCheXing/shareImage/"+fileName;
		if(saveBitmapToSDCard(b,path)){
			return path;
		}
		return null;
	}
	
	/**
	 * 保存bitmap到SD卡 并返回保存路径
	 * @param bitmap
	 * @return
	 */
	private boolean saveBitmapToSDCard(Bitmap bitmap,String path) {
		File file = new File(path);
		FileOutputStream fOutSteam = null;
		try {
			//创建文件夹
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			//创建文件
			if(!file.exists()){
				file.createNewFile();
			}
			fOutSteam = new FileOutputStream(file);
			if(!bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutSteam)){
				return false;
			}
			fOutSteam.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				fOutSteam.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public Location getLocation(Activity activity)
    {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

        String provider = locationManager.getBestProvider(criteria, false); // 获取GPS信息
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        return location;
    }
	
	
}
