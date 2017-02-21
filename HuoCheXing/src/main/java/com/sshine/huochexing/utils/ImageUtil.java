package com.sshine.huochexing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.sshine.huochexing.R;

public class ImageUtil {

	/**
	 * 从view 得到图片
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}
	
	/**
	 * 将图片旋转一圈
	 * @param context
	 * @param ivSwap
	 */
	public static void rotateImageOnce(Context context, ImageView ivSwap) {
		Animation operatingAnim = AnimationUtils.loadAnimation(context,
				R.anim.image_rotate_once);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		if (operatingAnim != null) {
			ivSwap.startAnimation(operatingAnim);
		}
	}
	public static void rotateImageForever(Context context, ImageView ivSwap, int id) {
		Animation operatingAnim = AnimationUtils.loadAnimation(context,id);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		if (operatingAnim != null) {
			ivSwap.startAnimation(operatingAnim);
		}
	}
}
