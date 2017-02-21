package com.sshine.huochexing.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
	private Map<String, String> mMap = new HashMap<String, String>();
	public MapUtil add(String key, String value){
		if (key != null && value != null){
			mMap.put(key, value);
		}
		return this;
	}
	public Map<String, String> build(){
		return mMap;
	}
}
