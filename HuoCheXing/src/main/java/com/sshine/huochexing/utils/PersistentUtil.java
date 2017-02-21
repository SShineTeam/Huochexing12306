package com.sshine.huochexing.utils;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PersistentUtil {

	public static synchronized Object readObject(String strPath) {
		Object obj = null;
		ObjectInputStream ois = null;
		try {
			File file = new File(strPath);
			if (!file.exists()){
				file.createNewFile();
				obj = null;
			}
			ois = new ObjectInputStream(new FileInputStream(file));
			obj = ois.readObject();
		}catch(EOFException e){
			L.i("EOFException");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				if (ois != null){
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	public static synchronized boolean writeObject(Object paramObject, String strPath) {
		ObjectOutputStream localObjectOutputStream = null;
		try {
			File file = new File(strPath);
			if (!file.exists()){
				file.createNewFile();
			}
			localObjectOutputStream = new ObjectOutputStream(
					new FileOutputStream(file));
			localObjectOutputStream.writeObject(paramObject);
			localObjectOutputStream.flush();
			localObjectOutputStream.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (localObjectOutputStream != null){
				localObjectOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
