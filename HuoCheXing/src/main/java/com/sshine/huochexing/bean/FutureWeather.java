package com.sshine.huochexing.bean;


/**
 *
 */
public class FutureWeather {
	private String city;
	
	private WeatherDetail weather1;
	private WeatherDetail weather2;
	private WeatherDetail weather3;
	
	public FutureWeather(){}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public WeatherDetail getWeather1() {
		return weather1;
	}

	public void setWeather1(WeatherDetail weather1) {
		this.weather1 = weather1;
	}

	public WeatherDetail getWeather2() {
		return weather2;
	}

	public void setWeather2(WeatherDetail weather2) {
		this.weather2 = weather2;
	}

	public WeatherDetail getWeather3() {
		return weather3;
	}

	public void setWeather3(WeatherDetail weather3) {
		this.weather3 = weather3;
	}
	
	
}
