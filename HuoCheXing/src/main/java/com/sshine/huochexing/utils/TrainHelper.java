package com.sshine.huochexing.utils;

import android.util.SparseArray;
import android.util.SparseIntArray;

public class TrainHelper {
	public static final int G = 0;
	public static final int D = 1;
	public static final int Z = 2;
	public static final int T = 3;
	public static final int K = 4;
	public static final int L = 5;
	public static final int QT = 6;
	
	private static SparseArray<String> trainNames;
	private static SparseArray<String> trainTypes;
	private static SparseIntArray trainSpeeds;
	
	public TrainHelper(){
		if (trainNames == null){
			trainNames = new SparseArray<String>();
			trainNames.put(G, "GC-高铁/城际");
			trainNames.put(D, "D-动车");
			trainNames.put(Z, "Z-直达");
			trainNames.put(T, "T-特快");
			trainNames.put(K, "K-快速");
			trainNames.put(L, "临时旅客列车");
			trainNames.put(QT, "其他");
		}
		if (trainTypes == null){
			trainTypes = new SparseArray<String>();
			trainTypes.put(G, "G");
			trainTypes.put(D, "D");
			trainTypes.put(Z, "Z");
			trainTypes.put(T, "T");
			trainTypes.put(K, "K");
			trainTypes.put(L, "L");
			trainTypes.put(QT, "QT");
		}
		if (trainSpeeds == null){
			trainSpeeds = new SparseIntArray();
			trainSpeeds.put(G, 6);
			trainSpeeds.put(D, 5);
			trainSpeeds.put(Z, 4);
			trainSpeeds.put(T, 3);
			trainSpeeds.put(K, 2);
			trainSpeeds.put(L, 1);
			trainSpeeds.put(QT, 0);
		}
	}
	public String[] getTrainTypeNames(){
		String[] stNames = new String[trainNames.size()];
		for(int i=0; i<trainNames.size(); i++){
			stNames[i] = trainNames.valueAt(i);
		}
		return stNames;
	}
	public int getTrainSpeedIndex(String station_train_code) {
		for (int i = 0; i < trainTypes.size(); i++) {
			if (station_train_code.startsWith(trainTypes.valueAt(i))) {
				return trainSpeeds.get(trainTypes.keyAt(i));
			}
		}
		//没有就是其它车次
		return 0;
	}
	public int getTrainType(String station_train_code){
		for (int i = 0; i < trainTypes.size(); i++) {
			if (station_train_code.startsWith(trainTypes.valueAt(i))) {
				return trainTypes.keyAt(i);
			}
		}
		return QT;
	}
	public SparseArray<String> getTrainNames(){
		return trainNames;
	}
	public SparseArray<String> getTrainTypes(){
		return trainTypes;
	}
	public SparseIntArray getTrainSpeeds(){
		return trainSpeeds;
	}
}
