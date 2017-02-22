package com.sshine.huochexing.bean;

public class WeatherDetail {
	private String date;
	private String weather;
	private String temp;
	private String imgId; 
	
	public WeatherDetail() {
	}

	public WeatherDetail(String date, String weather, String temp, String imgId) {
		super();
		this.date = date;
		this.weather = weather;
		this.temp = temp;
		this.imgId = imgId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	@Override
	public String toString() {
		return "WeatherDetail [date=" + date + ", weather=" + weather
				+ ", temp=" + temp + ", imgId=" + imgId + "]";
	}
	
}
