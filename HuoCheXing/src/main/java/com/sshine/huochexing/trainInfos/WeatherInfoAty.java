package com.sshine.huochexing.trainInfos;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.CurrentWeather;
import com.sshine.huochexing.bean.FutureWeather;
import com.sshine.huochexing.bean.WeatherDetail;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.value.SF;

public class WeatherInfoAty extends SherlockFragmentActivity {
	public static final String TAG = "WeatherInfoAty";
	
	public static final String EXTRA_CITY ="city";
	
	private String url =  "http://huochexing.duapp.com/server/weather.php";
	
	private String city; //城市
	private String cityCode;
	
	//ui元素
	private TextView tvCity ;
	private TextView tvTemp ;
	private TextView tvWind ;
	private TextView tvHumidity ;
	private TextView tvUpdateTime ;
	private TextView tvCurrWeather;
	private TextView tvWeather1;
	private TextView tvWeather2;
	private TextView tvWeather3;
	private TextView tvDate1;
	private TextView tvDate2;
	private TextView tvDate3;
	private TextView tvTemp1;
	private TextView tvTemp2;
	private TextView tvTemp3;
	
	private ImageView ivCurrImg;
	private ImageView ivImg1;
	private ImageView ivImg2;
	private ImageView ivImg3;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_tianqi);
		
		Intent intent = getIntent();
		city = intent.getStringExtra(EXTRA_CITY);
		initActionBar();
		
		initView();
		initData();
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		initSmallWeatherMap();
		initBigWeatherMap();
		new getCurrentWeatherTask(this).execute();
		new getfutureWeatherTask(this).execute();
	}
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		tvCity = (TextView) this.findViewById(R.id.weather_city);
		tvTemp = (TextView) this.findViewById(R.id.weather_curr_temp);
		tvWind = (TextView) this.findViewById(R.id.weather_wind);
		tvHumidity = (TextView) this.findViewById(R.id.weather_humidity);
		tvUpdateTime = (TextView) this.findViewById(R.id.weather_update_time);
		
		tvCurrWeather = (TextView) this.findViewById(R.id.weather_curr_weather);
		tvWeather1 = (TextView) this.findViewById(R.id.weather_weather1);
		tvWeather2 = (TextView) this.findViewById(R.id.weather_weather2);
		tvWeather3 = (TextView) this.findViewById(R.id.weather_weather3);
		tvDate1 = (TextView) this.findViewById(R.id.weather_date1);
		tvDate2 = (TextView) this.findViewById(R.id.weather_date2);
		tvDate3 = (TextView) this.findViewById(R.id.weather_date3);
		tvTemp1 = (TextView) this.findViewById(R.id.weather_temp1);
		tvTemp2 = (TextView) this.findViewById(R.id.weather_temp2);
		tvTemp3 = (TextView) this.findViewById(R.id.weather_temp3);

		ivCurrImg = (ImageView) this.findViewById(R.id.weather_curr_img);
		ivImg1 = (ImageView) this.findViewById(R.id.weather_img1);
		ivImg2 = (ImageView) this.findViewById(R.id.weather_img2);
		ivImg3 = (ImageView) this.findViewById(R.id.weather_img3);
		
		tvCity.setText("加载中...");
		
	}
	/**
	 * 初始化ActionBar
	 */
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle(city+"天气");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 *新线程取得实时天气
	 */
	class getCurrentWeatherTask extends MyTask{
		public getCurrentWeatherTask(FragmentActivity context) {
			super(context,null);
		}
		@Override
		protected Object myDoInBackground(Object... params) throws Exception {
			String jsonStr = "{\"requestType\":\"getCurrWeather\",\"station\":\"" + city + "\"}";
			HttpUtil httpUtil = new HttpUtil();
			if(httpUtil.post(url, jsonStr)){
				L.i("getTravels结果:" + httpUtil.getResponseStr());
				JSONObject jsonObj = new JSONObject(httpUtil.getResponseStr());
				int intResultCode = jsonObj.getInt(HttpUtil.RESULT_CODE);
				if(intResultCode == 1){
					JSONObject  currWeather = jsonObj.getJSONObject("currWeather");
					CurrentWeather currweather = new CurrentWeather();
					currweather.setCity(currWeather.getString("city"));
					currweather.setCityCode(currWeather.getString("cityCode"));
					currweather.setTemp(currWeather.getString("temp"));
					currweather.setWindDirection(currWeather.getString("windDirection"));
					currweather.setWindScale(currWeather.getString("windScale"));
					currweather.setHumidity(currWeather.getString("humidity"));
					currweather.setUpdateTime(currWeather.getString("updateTime"));
					return currweather;
				}
			}
			return null;
			
		}
		@Override
		protected void myOnPostExecute(Object result) {
			//更新UI
			if(result!=null){
				CurrentWeather currweather = (CurrentWeather) result;
				tvCity.setText(currweather.getCity());
				tvTemp.setText(currweather.getTemp());
				tvWind.setText(currweather.getWindDirection()+" "+currweather.getWindScale());
				tvHumidity.setText("湿度："+currweather.getHumidity());
				tvUpdateTime.setText("更新时间："+currweather.getUpdateTime());
			}
		}
		@Override
		protected void onException(Exception e) {
			Toast.makeText(WeatherInfoAty.this, "天气更新失败，请重试"+SF.FAIL, Toast.LENGTH_LONG).show();
		}
		
	}
	/**
	 *新线程取得未来天气 
	 */
	class getfutureWeatherTask extends MyTask{
		
		public getfutureWeatherTask(FragmentActivity context) {
			super(context);
		}
		@Override
		protected Object myDoInBackground(Object... params) throws Exception {
			String jsonStr = "{\"requestType\":\"getFutureWeather\",\"station\":\"" + city + "\"}";
			HttpUtil httpUtil = new HttpUtil();
			if(httpUtil.post(url, jsonStr)){
				L.i("getTravels结果:" + httpUtil.getResponseStr());
				JSONObject jsonObj = new JSONObject(httpUtil.getResponseStr());
				int intResultCode = jsonObj.getInt(HttpUtil.RESULT_CODE);
				if(intResultCode == 1){
					JSONObject  currWeather = jsonObj.getJSONObject("futureWeather");
					String city = currWeather.getString("city");
					String date1 = currWeather.getString("date1");
					String date2 = currWeather.getString("date2");
					String date3 = currWeather.getString("date3");
					String weather1 = currWeather.getString("weather1");
					String weather2 = currWeather.getString("weather2");
					String weather3 = currWeather.getString("weather3");
					String temp1 = currWeather.getString("temp1");
					String temp2 = currWeather.getString("temp2");
					String temp3 = currWeather.getString("temp3");
					String imgId1 = currWeather.getString("imgId1");
					String imgId2 = currWeather.getString("imgId2");
					String imgId3 = currWeather.getString("imgId3");
					FutureWeather futureWeather =  new FutureWeather();
					futureWeather.setCity(city);
					
					WeatherDetail weatherDtail1 = new WeatherDetail(date1, weather1, temp1, imgId1);
					WeatherDetail weatherDtail2 = new WeatherDetail(date2, weather2, temp2, imgId2);
					WeatherDetail weatherDtail3 = new WeatherDetail(date3, weather3, temp3, imgId3);
					
					futureWeather.setWeather1(weatherDtail1);
					futureWeather.setWeather2(weatherDtail2);
					futureWeather.setWeather3(weatherDtail3);
					
					return futureWeather;
				}
			}
			return null;
		}
		@Override
		protected void myOnPostExecute(Object result) {
			//更新UI
			if(result!=null){
				FutureWeather futureWeather = (FutureWeather) result;
				WeatherDetail weatherDetail1 = futureWeather.getWeather1();
				WeatherDetail weatherDetail2 = futureWeather.getWeather2();
				WeatherDetail weatherDetail3 = futureWeather.getWeather3();
				tvCurrWeather.setText(weatherDetail1.getWeather());
				tvWeather1.setText(weatherDetail1.getWeather());
				tvWeather2.setText(weatherDetail2.getWeather());
				tvWeather3.setText(weatherDetail3.getWeather());
				tvDate1.setText(weatherDetail1.getDate());
				tvDate2.setText(weatherDetail2.getDate());
				tvDate3.setText(weatherDetail3.getDate());
				tvTemp1.setText(weatherDetail1.getTemp());
				tvTemp2.setText(weatherDetail2.getTemp());
				tvTemp3.setText(weatherDetail3.getTemp());
				
				int currImgId = getBigWeatherMap().get(weatherDetail1.getImgId());
				int img1Id = getSmallWeatherMap().get(weatherDetail1.getImgId());
				int img2Id = getSmallWeatherMap().get(weatherDetail2.getImgId());
				int img3Id = getSmallWeatherMap().get(weatherDetail3.getImgId());
				
				ivCurrImg.setBackgroundResource(currImgId);
				ivImg1.setBackgroundResource(img1Id);
				ivImg2.setBackgroundResource(img2Id);
				ivImg3.setBackgroundResource(img3Id);
			}
		}
		@Override
		protected void onException(Exception e) {
			Toast.makeText(WeatherInfoAty.this, "天气更新失败，请重试"+SF.FAIL, Toast.LENGTH_LONG).show();
		}
	}
	
	private Map<String, Integer> mSmallWeatherMap = new HashMap<String, Integer>();
	private Map<String, Integer> mBigWeatherMap = new HashMap<String, Integer>();
	public Map<String, Integer> getSmallWeatherMap() {
		return mSmallWeatherMap;
	}

	private Map<String, Integer> getBigWeatherMap() {
		return mBigWeatherMap;
	}
	private void initSmallWeatherMap() {
		mSmallWeatherMap.put("0", R.drawable.tianqi_0b);
		mSmallWeatherMap.put("1", R.drawable.tianqi_1b);
		mSmallWeatherMap.put("2", R.drawable.tianqi_2b);
		mSmallWeatherMap.put("3", R.drawable.tianqi_3b);
		mSmallWeatherMap.put("4", R.drawable.tianqi_4b);
		mSmallWeatherMap.put("5", R.drawable.tianqi_5b);
		mSmallWeatherMap.put("6", R.drawable.tianqi_6b);
		mSmallWeatherMap.put("7", R.drawable.tianqi_7b);
		mSmallWeatherMap.put("8", R.drawable.tianqi_8b);
		mSmallWeatherMap.put("9", R.drawable.tianqi_9b);
		mSmallWeatherMap.put("10", R.drawable.tianqi_10b);
		mSmallWeatherMap.put("11", R.drawable.tianqi_11b);
		mSmallWeatherMap.put("12", R.drawable.tianqi_12b);
		mSmallWeatherMap.put("13", R.drawable.tianqi_13b);
		mSmallWeatherMap.put("14", R.drawable.tianqi_14b);
		mSmallWeatherMap.put("15", R.drawable.tianqi_15b);
		mSmallWeatherMap.put("16", R.drawable.tianqi_16b);
		mSmallWeatherMap.put("17", R.drawable.tianqi_17b);
		mSmallWeatherMap.put("18", R.drawable.tianqi_18b);
		mSmallWeatherMap.put("19", R.drawable.tianqi_19b);
		mSmallWeatherMap.put("20", R.drawable.tianqi_20b);
		mSmallWeatherMap.put("21", R.drawable.tianqi_21b);
		mSmallWeatherMap.put("22", R.drawable.tianqi_22b);
		mSmallWeatherMap.put("23", R.drawable.tianqi_24b);
		mSmallWeatherMap.put("24", R.drawable.tianqi_24b);
		mSmallWeatherMap.put("25", R.drawable.tianqi_25b);
		mSmallWeatherMap.put("26", R.drawable.tianqi_26b);
		mSmallWeatherMap.put("27", R.drawable.tianqi_27b);
		mSmallWeatherMap.put("28", R.drawable.tianqi_28b);
		mSmallWeatherMap.put("29", R.drawable.tianqi_29b);
		mSmallWeatherMap.put("30", R.drawable.tianqi_30b);
		mSmallWeatherMap.put("31", R.drawable.tianqi_31b);
		mSmallWeatherMap.put("32", R.drawable.tianqi_32b);
	}

	public void initBigWeatherMap() {
		mBigWeatherMap.put("0", R.drawable.tianqi_0a);
		mBigWeatherMap.put("1", R.drawable.tianqi_1a);
		mBigWeatherMap.put("2", R.drawable.tianqi_2a);
		mBigWeatherMap.put("3", R.drawable.tianqi_3a);
		mBigWeatherMap.put("4", R.drawable.tianqi_4a);
		mBigWeatherMap.put("5", R.drawable.tianqi_5a);
		mBigWeatherMap.put("6", R.drawable.tianqi_6a);
		mBigWeatherMap.put("7", R.drawable.tianqi_7a);
		mBigWeatherMap.put("8", R.drawable.tianqi_8a);
		mBigWeatherMap.put("9", R.drawable.tianqi_9a);
		mBigWeatherMap.put("10", R.drawable.tianqi_10a);
		mBigWeatherMap.put("11", R.drawable.tianqi_11a);
		mBigWeatherMap.put("12", R.drawable.tianqi_12a);
		mBigWeatherMap.put("13", R.drawable.tianqi_13a);
		mBigWeatherMap.put("14", R.drawable.tianqi_14a);
		mBigWeatherMap.put("15", R.drawable.tianqi_15a);
		mBigWeatherMap.put("16", R.drawable.tianqi_16a);
		mBigWeatherMap.put("17", R.drawable.tianqi_17a);
		mBigWeatherMap.put("18", R.drawable.tianqi_18a);
		mBigWeatherMap.put("19", R.drawable.tianqi_19a);
		mBigWeatherMap.put("20", R.drawable.tianqi_20a);
		mBigWeatherMap.put("21", R.drawable.tianqi_21a);
		mBigWeatherMap.put("22", R.drawable.tianqi_22a);
		mBigWeatherMap.put("23", R.drawable.tianqi_24a);
		mBigWeatherMap.put("24", R.drawable.tianqi_24a);
		mBigWeatherMap.put("25", R.drawable.tianqi_25a);
		mBigWeatherMap.put("26", R.drawable.tianqi_26a);
		mBigWeatherMap.put("27", R.drawable.tianqi_27a);
		mBigWeatherMap.put("28", R.drawable.tianqi_28a);
		mBigWeatherMap.put("29", R.drawable.tianqi_29a);
		mBigWeatherMap.put("30", R.drawable.tianqi_30a);
		mBigWeatherMap.put("31", R.drawable.tianqi_31a);
		mBigWeatherMap.put("32", R.drawable.tianqi_32a);
	}
}
