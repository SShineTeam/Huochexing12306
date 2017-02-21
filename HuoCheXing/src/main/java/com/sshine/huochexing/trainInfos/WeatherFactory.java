package com.sshine.huochexing.trainInfos;

import com.sshine.huochexing.bean.CurrentWeather;
import com.sshine.huochexing.bean.FutureWeather;

public class WeatherFactory {
	public static Object lock = new Object();
	public static WeatherFactory weatherFactory;
	
	private WeatherFactory(){}
	
	public static WeatherFactory getInstance(){
		if(weatherFactory == null){
			synchronized (lock) {
				if(weatherFactory == null){
					weatherFactory = new WeatherFactory();
				}
			}
		}
		return weatherFactory;
	}
	public CurrentWeather getCurrentWeather(String weatherJson){
		
		return null;
	}
	
	public FutureWeather getFutureWeather(String WeatherJson){
		
		return null;
	}
}
