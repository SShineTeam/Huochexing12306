package com.sshine.huochexing.utils;

import java.io.File;
import java.io.IOException;

import com.sshine.huochexing.value.StoreValue;

import android.os.Environment;

public class FileUtil {
	public static File getSDParentDir() throws IOException{
		if (isSDCardExist()){
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "HuoCheXing");
			if (!file.exists()){
				file.mkdir();
			}
			return file;
		}
		return null;
	}
	public static String getSDTrainDetailDir() throws IOException{
		return (getSDParentDir()+File.separator+StoreValue.STORE_TRAIN_DETAIL);
	}
	public static boolean isSDCardExist(){
		return Environment.getExternalStorageState()
    			.equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
	}
}
