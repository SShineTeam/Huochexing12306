package com.sshine.huochexing.bean;

public class CurrentWeather {
	private String city;
	private String cityCode;
	private String temp;
	private String windDirection;
	private String windScale;
	private String humidity;
	private String updateTime;
	
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}
	public String getWindScale() {
		return windScale;
	}
	public void setWindScale(String windScale) {
		this.windScale = windScale;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	@Override
	public String toString() {
		return "CurrentWeather [city=" + city + ", cityCode=" + cityCode
				+ ", temp=" + temp + ", windDirection=" + windDirection
				+ ", windScale=" + windScale + ", humidity=" + humidity
				+ ", updateTime=" + updateTime + "]";
	}
	
	
	
}
