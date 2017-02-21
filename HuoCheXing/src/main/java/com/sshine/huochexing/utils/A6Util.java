package com.sshine.huochexing.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.ConfirmPassengersInfo;
import com.sshine.huochexing.bean.QueryLeftNewInfo;
import com.sshine.huochexing.bean.SeatInfo;

public class A6Util {
	public static final int MSG_TOAST = 1;
	public static final int MSG_DISMISS_DIALOG = 2;
	public static final int MSG_START_PROGRESS = 5;
	public static final int MSG_NO_LOGIN = 6;
	public static final int MSG_SHOW_TICKET_PRICES = 10;
	public static final int MSG_QUERY_TICKETS_SUCCESS = 11;
	public static final int MSG_GET_PASSENGERS_SUCCESS = 12;
	public static final int MSG_REQUEST_ORDER_RAND_CODE_FAIL = 13;
	public static final int MSG_REQUEST_ORDER_RAND_CODE_SUCCESS = 14;
	public static final int MSG_WAIT_HANDLE_ORDERS = 16;
	public static final int MSG_REQUEST_CHKCK_ORDER_INFO_FINISH = 17;
	public static final int MSG_CHKCK_ORDER_INFO_FAIL = 19;
	public static final int MSG_REQUEST_ORDER_DATA_FAIL = 20;
	public static final int MSG_QUEUE_COUNT_OVERFLOW = 21;
	public static final int MSG_RESULT_ORDER_FOR_QUEUE_SUCCESS = 22;
	public static final int MSG_QUERY_TICKET_PRICE_SUCCESS = 23;
	public static final int MSG_CANCEL_ORDER_TOO_MANAY = 25;
	public static final int MSG_QUERY_ORDER_SUCCESS= 26;
	public static final int MSG_NO_COMPLETE_ORDER_EMPTY= 27;
	public static final int MSG_INIT_FORM_VALUES_FINISH= 28;
	public static final int MSG_CONFIRM_SINGLE_SUCCESS = 29;

	private static final String CHARSET = HTTP.UTF_8;
	private static DefaultHttpClient mHttpClient;
	public static synchronized DefaultHttpClient getHttpClient(){
		if (mHttpClient == null){
			mHttpClient = getHttpClient(true);
		}
		return mHttpClient;
	}
	
	public static Gson getGson(){
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	}
	public static DefaultHttpClient getHttpClient(boolean isThreadSafe) {
		DefaultHttpClient hc1;
		try {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET); 
//			HttpProtocolParams.setHttpElementCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, false);
			Collection<BasicHeader> headers = new ArrayList<BasicHeader>();
			// headers.add(new BasicHeader("Accept",
			// "text/html, application/xhtml+xml, */*"));
			headers.add(new BasicHeader("Host", "kyfw.12306.cn"));
//			headers.add(new BasicHeader("Referer", "https://kyfw.12306.cn/otn/login/init"));
//			headers.add(new BasicHeader("Origin", "https://kyfw.12306.cn"));
//			headers.add(new BasicHeader("Cache-Control", "no-cache"));
			 headers.add(new BasicHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"));
			 headers.add(new BasicHeader("User-Agent",
					 "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:23.0) Gecko/20100101 Firefox/23.0"));
//			 headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
			params.setParameter(ClientPNames.DEFAULT_HEADERS, headers);
			// 超时设置
			// 从连接池中取连接的超时时间
			ConnManagerParams.setTimeout(params, 30000); // 连接超时
			HttpConnectionParams.setConnectionTimeout(params, 10000); // 请求超时
			HttpConnectionParams.setSoTimeout(params, 10000);
			// 设置HttpClient支持HTTP和HTTPS两种模式
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch1 = new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80);
			Scheme sch2 = new Scheme("https", sf, 443);
			if (isThreadSafe){
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(sch1);
				registry.register(sch2);
				// 使用线程安全的连接管理来创建HttpClient
				ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
						params, registry);
				hc1 = new DefaultHttpClient(conMgr, params);
			}else{
				hc1 = new DefaultHttpClient(params);
				SchemeRegistry registry = hc1.getConnectionManager().getSchemeRegistry();
				registry.register(sch1);
				registry.register(sch2);
			}
		} catch (Exception e) {
			L.i("无法信任所有证书");
			e.printStackTrace();
			hc1 = new DefaultHttpClient();
		}
		hc1.setRedirectHandler(new RedirectHandler() {
            
            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                    L.d("isRedirectRequested.context:"+context.toString());
                    return false;
            }
            
            @Override
            public URI getLocationURI(HttpResponse response, HttpContext context)
                            throws ProtocolException {
                    return null;
            }
		});
		hc1.setCookieStore(new MyCookieStore());
		return hc1;
	}
		
	public static String encode(String src){
		try{
			return URLEncoder.encode(src, HTTP.UTF_8);
		}catch(Exception e){
			return "";
		}
	}
	
	public static String decode(String src){
		try{
			return URLDecoder.decode(src, HTTP.UTF_8);
		}catch(Exception e){
			return "";
		}
	}
	
	public static boolean isCanBooking(){
		String strNowHH_MM = TimeUtil.getTFormat().format(new Date());
		if (strNowHH_MM.compareTo("23:00")>=0 || strNowHH_MM.compareTo("07:00")<=0){
			return false;
		}else{
			return true;
		}
	}
	
	public static String formatToEnglish(String data){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zZ yyyy", Locale.ENGLISH);
			sdf.setTimeZone(new SimpleTimeZone(28800000, "UTC"));
			return sdf.format(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(data));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getKey_check_isChange(String strHtml){
		if (strHtml == null){
			return null;
		}else{
			//重复1次或更多次，但尽可能少重复
			return RegexUtils.getMatcher("'key_check_isChange':'(\\S+?)'", strHtml);
		}
	}

	public static String getTrainLocation(String strHtml){
		if (strHtml == null){
			return null;
		}else{
			return RegexUtils.getMatcher("'key_check_isChange':'(\\S+?)'", strHtml);
		}
	}
	
	/**
	 * 传入当前时间戳，判断能否购买学生票
	 * @param time
	 * @return
	 */
	public static boolean isCanBookingStuTicket(long time){
//		try{
//			Date currDate = new Date(time);
//			SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd", Locale.CHINA);
//			String currDateStr = sdf1.format(currDate);
//			if ((currDateStr.compareTo("06-01") >= 0 && currDateStr.compareTo("09-30")<=0)
//					|| ((currDateStr.compareTo("12-01")>=0 && currDateStr.compareTo("12-31")<=0)
//							&& (currDateStr.compareTo("01-01")>=0 && currDateStr.compareTo("03-31")<=0))){
//				return true;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return false;
		return true;
	}
	public static boolean isNeedCheckUser(BookingInfo bInfo){
		long currTime = System.currentTimeMillis();
		long hisTime = MyApp.getInstance().getA6UserInfoSPUtil().getLastPowerOperateTimeMillis();
		long checkInterval = 3600*30;   //超过30分钟则检测一次登录
		if ((currTime - hisTime)>checkInterval){
			return true;
		}else{
			return false;
		}
	}
	
	public static Collection<BasicHeader> makeRefererColl(String url){
		Collection<BasicHeader> headers = new ArrayList<BasicHeader>();
		headers.add(new BasicHeader("Referer", url));
		return headers;
	}
	
	public static Map<String, String> makeRefererHeader(String url){
		Map<String, String> map = new HashMap<String, String>();
		map.put("Referer", url);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> A6Info<T> exec(BookingInfo bInfo, Map<String, String> headers, String url, Map<String, String> postParams, TypeToken<T> typeToken) throws Exception{
		A6Info<T> a6Json = null;
		if (postParams == null){
			a6Json = (A6Info<T>)get(bInfo, makeHeaders(headers), url);
		}else{
			a6Json = (A6Info<T>)post(bInfo, makeHeaders(headers), url, makeParams(postParams));
		}
		a6Json.setDataObject((T) getGson().fromJson(a6Json.getData(), typeToken.getType()));
		return a6Json;
	}

	public static A6Info get(BookingInfo bInfo, Collection<BasicHeader> headers, String url) throws Exception{
		String result = bInfo.getHttpHelper().get(headers, url);
		A6Info a6Json = new A6Info();
		a6Json.setRawString(result);
		JSONObject jsonObj = new JSONObject(a6Json.getRawString());
		a6Json.setValidateMessagesShowId(jsonObj.optString("validateMessagesShowId"));
		a6Json.setStatus(jsonObj.optBoolean("status", true));
//		a6Json.setHttpstatus(jsonObj.getInt("httpstatus"));
		a6Json.setData(jsonObj.optString("data"));
		a6Json.setMessages(jsonObj.optString("messages"));
//		a6Json.setValidateMessages(jsonObj.optString("validateMessages"));
		return a6Json;
	}
	public static A6Info post(BookingInfo bInfo, Collection<BasicHeader> headers, String url, List<NameValuePair> lstParams) throws Exception{
		String result = bInfo.getHttpHelper().post(headers, url, lstParams);
		A6Info a6Json = new A6Info();
		a6Json.setRawString(result);
		JSONObject jsonObj = new JSONObject(a6Json.getRawString());
		a6Json.setStatus(jsonObj.optBoolean("status"));
		a6Json.setData(jsonObj.optString("data"));
		a6Json.setMessages(jsonObj.optString("messages"));
		return a6Json;
	}
	public static boolean checkUser(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/login/checkUser";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try{
			A6Info a6Json = post(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/leftTicket/init"), url, lstParams);
			JSONObject jObj = new JSONObject(a6Json.getData());
			if (jObj.getBoolean("flag")){
				MyApp.getInstance().getA6UserInfoSPUtil().updatePowerOperateTimeMillis();
				MyApp.getInstance().getA6UserInfoSPUtil().setLogin(true);
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		MyApp.getInstance().getA6UserInfoSPUtil().setLogin(false);
		return false;
	}
	
	public static int submitOrderRequest(BookingInfo bInfo){
//		if (TextUtils.isEmpty(bInfo.getQuery_detect_key())){
//			return -1;
//		}
		String url = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
//		lstParams.add(new BasicNameValuePair(bInfo.getQuery_detect_key(), bInfo.getQuery_detect_value()));
//		lstParams.add(new BasicNameValuePair("myversion", "undefined"));
		lstParams.add(new BasicNameValuePair("secretStr", bInfo.getSecretStr()));
		lstParams.add(new BasicNameValuePair("train_date", bInfo.getTrain_date()));
		lstParams.add(new BasicNameValuePair("back_train_date", bInfo.getBack_train_date()));
		lstParams.add(new BasicNameValuePair("tour_flag", bInfo.getTour_flag()));
		lstParams.add(new BasicNameValuePair("purpose_codes", bInfo.getPurpose_codes()));
		lstParams.add(new BasicNameValuePair("query_from_station_name", bInfo.getQuery_from_station_name()));
		lstParams.add(new BasicNameValuePair("query_to_station_name", bInfo.getQuery_to_station_name()));
		lstParams.add(new BasicNameValuePair("undefined", ""));
		try{
			Map<String, String> map = new MapUtil()
					.add("X-Requested-With", "XMLHttpRequest")
					.add("Referer", "https://kyfw.12306.cn/otn/leftTicket/init").build();
			A6Info a6Json = post(bInfo, makeHeaders(map), url, lstParams);
			if (a6Json.isStatus()){
				return 1;
			}else if (a6Json.getMessages().indexOf("未处理的订单") > 0){
				return 2;
			}else if (a6Json.getMessages().indexOf("插件") > 0){
				return 3;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	private static String queryTicketsInit(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/leftTicket/init";
		String strContent = null;
		try{
			strContent = bInfo.getHttpHelper().get(A6Util.makeRefererColl("https://kyfw.12306.cn/otn/index/init"), url);
		}catch(Exception e){
			e.printStackTrace();
		}
		return strContent;
	}
	
	public static A6Info<List<QueryLeftNewInfo>> queryTickets(BookingInfo bInfo){
		try{
			if (TextUtils.isEmpty(bInfo.getQuery_detect_key())){
				String strContent = A6Util.getDynamicJsContent(bInfo, A6Util.queryTicketsInit(bInfo));
				bInfo.setQuery_detect_key(A6Util.getDetectHelperKey(bInfo, strContent));
				bInfo.setQuery_detect_value(A6Util.getDetectHelperValue(MyApp.getInstance().getApplicationContext(), bInfo, strContent, bInfo.getQuery_detect_key()));
			}
//			String url = String.format("https://kyfw.12306.cn/otn/leftTicket/log?leftTicketDTO.train_date="
//					+ "%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=%s",
//					bInfo.getTrain_date(), bInfo.getFrom_station(),
//					bInfo.getTo_station(),bInfo.getPurpose_codes());
//			A6ResponseJsonMsg a6Json1 = get(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/leftTicket/init"), url);
			String url = String.format("https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date="
						+ "%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=%s",
						bInfo.getTrain_date(), bInfo.getFrom_station(),
						bInfo.getTo_station(),bInfo.getPurpose_codes());
				A6Info<List<QueryLeftNewInfo>> a6Info = exec(bInfo, makeRefererHeader("https://kyfw.12306.cn/otn/leftTicket/init"), url,
						null, new TypeToken<List<QueryLeftNewInfo>>(){});
				return a6Info;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static List<SeatInfo> queryTicketPrice(BookingInfo bInfo){
		String url = String.format("https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=%s"
				+"&from_station_no=%s&to_station_no=%s&seat_types=%s&train_date=%s"
			, bInfo.getTrain_no(), bInfo.getFrom_station_no(), bInfo.getTo_station_no(), bInfo.getSeat_types(),
			bInfo.getTrain_date());
		try{
			A6Info a6Json = get(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/leftTicket/init"), url);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			List<SeatInfo> lstSeatInfos = new ArrayList<SeatInfo>();
			Iterator<?> prices = jsonObj.keys();
			SeatHelper sHelper = new SeatHelper();
			while(prices.hasNext()){
				String key = prices.next().toString();
				SeatInfo sInfo = sHelper.getInfoBySeatPriceType(key);
				if (sInfo != null){
					sInfo.setPrice(jsonObj.getString(key));
					String strTypeCode = RegexUtils.getMatcher("\\D+(\\d+)", key);
					if (strTypeCode != null){
						sInfo.setType_code(strTypeCode);
					}else{
						sInfo.setType_code(key);
					}
					lstSeatInfos.add(sInfo);
				}
			}
			if (lstSeatInfos.isEmpty()){
				return null;
			}else{
				return lstSeatInfos;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<NameValuePair> makeParams(Map<String, String> map){
		if (map == null){
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Entry<String, String> entry:map.entrySet()){
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}
	
	public static Collection<BasicHeader> makeHeaders(Map<String, String> map){
		if (map == null){
			return null;
		}
		Collection<BasicHeader> headers = new ArrayList<BasicHeader>();
		for(Entry<String, String> entry:map.entrySet()){
			headers.add(new BasicHeader(entry.getKey(), entry.getValue()));
		}
		return headers;
	}
	
	public static A6Info login(BookingInfo bInfo){
		try{
			String url = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
			List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
			lstParams.add(new BasicNameValuePair("loginUserDTO.user_name", bInfo.getUser_name()));
			lstParams.add(new BasicNameValuePair("userDTO.password", bInfo.getPassword()));
			lstParams.add(new BasicNameValuePair("randCode", bInfo.getLoginRandCode()));
//			lstParams.add(new BasicNameValuePair("randCode_validate", ""));
//			lstParams.add(new BasicNameValuePair(bInfo.getLogin_detect_key(), bInfo.getLogin_detect_value()));
//			lstParams.add(new BasicNameValuePair("myversion", "undefined"));
			
			Map<String, String> map = new MapUtil()
					.add("X-Requested-With", "XMLHttpRequest")
					.add("Referer", "https://kyfw.12306.cn/otn/leftTicket/init").build();
			A6Info a6Json = A6Util.post(bInfo, makeHeaders(map), url, lstParams);
			if (a6Json != null){
				if("[]".equals(a6Json.getMessages())){
					MyApp.getInstance().getA6UserInfoSPUtil().updatePowerOperateTimeMillis();
					MyApp.getInstance().getA6UserInfoSPUtil().setLogin(true);
				}
			}
			return a6Json;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getLoginRandCode(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&"+Math.random();
		Map<String, String> map = new MapUtil().add("Origin", "https://kyfw.12306.cn")
				.add("Referer", "https://kyfw.12306.cn/otn/login/init").build();
		Bitmap bitmap = bInfo.getHttpHelper().getBitmap(makeHeaders(map), url);
		return bitmap;
	}
	
	public static boolean checkLoginRandCode(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
		Map<String, String> map = new MapUtil()
				.add("X-Requested-With", "XMLHttpRequest")
				.add("Referer", "https://kyfw.12306.cn/otn/leftTicket/init").build();
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("randCode", bInfo.getLoginRandCode()));
		lstParams.add(new BasicNameValuePair("rand", "sjrand"));
//		lstParams.add(new BasicNameValuePair("randCode_validate", ""));
		try{
			A6Info a6Json = A6Util.post(bInfo, makeHeaders(map), url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getInt("result") > 0){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static String initQueryUserInfo(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/modifyUser/initQueryUserInfo";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try{
			String strContent = bInfo.getHttpHelper().post(A6Util.makeRefererColl("https://kyfw.12306.cn/otn/userSecurity/init"), url, lstParams);
			return strContent;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String loginInit(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/login/init";
		String strContent = null;
		try{
			strContent = bInfo.getHttpHelper().get(A6Util.makeRefererColl("https://kyfw.12306.cn/otn/"), url);
		}catch(Exception e){
			e.printStackTrace();
		}
		return strContent;
	}
	
	public static String getDynamicJsContent(BookingInfo bInfo, String loginInitHtml){
		if (TextUtils.isEmpty(loginInitHtml)){
			return null;
		}
		try{
			String strContent = RegexUtils.getMatcher("src=\"/otn/dynamicJs/(\\S+)\"", loginInitHtml);
			if (!TextUtils.isEmpty(strContent)){
				String url = "https://kyfw.12306.cn/otn/dynamicJs/" + strContent;
				String jsContent = bInfo.getHttpHelper().get(makeRefererColl("https://kyfw.12306.cn/otn/login/init"), url);
				return jsContent;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDetectHelperKey(BookingInfo bInfo, String jsContent){
		if (TextUtils.isEmpty(jsContent)){
			return null;
		}
		try{
			String gcContent = extractFunctionByName(jsContent, "gc", 0);
			String strContent = RegexUtils.getMatcher("var\\s+\\w{3,10}\\s*=\\s*'(\\w+)';", gcContent);
			return strContent;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static String getDetectHelperValue(Context context, BookingInfo bInfo, String jsContent, String key){
		if (TextUtils.isEmpty(jsContent)){
			return null;
		}
		try{
			String base32Content = extractFunctionByName(jsContent, "Base32", 0);
			String deltaContent = RegexUtils.getMatcher("(var delta\\s*=\\s*\\w+;)", base32Content);
			String base32_encryptContent = "this." + extractFunctionByName(base32Content, "encrypt", 2);
			String base32_longArrayToStringContent = extractFunctionByName(base32Content, "longArrayToString", 2);
			String base32_stringToLongArray = extractFunctionByName(base32Content, "stringToLongArray", 2);
			String bin216Content = extractFunctionByName(jsContent, "bin216", 1);
			String encode32ParamOfKeyStr = RegexUtils.getMatcher("(var\\s+keyStr\\s*=\\s*\"\\S+\")", jsContent) + ";";
			String encode32Content = extractFunctionByName(jsContent, "encode32", 1);
			String tempJsContent = deltaContent +base32_longArrayToStringContent + base32_stringToLongArray
					+ base32_encryptContent;
			String tempJsContent2 = bin216Content + encode32ParamOfKeyStr + encode32Content;
			String strBase32 = runScript(context, tempJsContent, "encrypt", new String[]{"1111", key});
			String strBin216 = runScript(context, tempJsContent2, "bin216", new String[]{strBase32});
			String encode32 = runScript(context, tempJsContent2, "encode32", new String[]{strBin216});
			return encode32;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 从js中提取一个函数代码段，只取第一个
	 * @param jsContent
	 * @paramCount 函数参数个数
	 * @return
	 */
	public static String extractFunctionByName(String jsContent, String functionName, int paramsCount){
		if (TextUtils.isEmpty(jsContent)){
			return null;
		}
		try{
			String regex = "(function\\s*" + functionName + "\\(";
			for(int i=0; i<paramsCount; i++){
				regex += "\\s*\\w+\\s*,";
			}
			if (paramsCount > 0){
				regex = regex.substring(0, regex.length() - 1);
			}
			regex += "\\)\\s*\\{)";
			String functionDeclaration = RegexUtils.getMatcher(regex, jsContent);
			if (TextUtils.isEmpty(functionDeclaration)){
				regex = "((var\\s+){0,1}" + functionName + "\\s*=\\s*(new)*\\s*function\\(";
				for(int i=0; i<paramsCount; i++){
					regex += "\\s*\\w+\\s*,";
				}
				if (paramsCount > 0){
					regex = regex.substring(0, regex.length() - 1);
				}
				regex += "\\)\\s*\\{)";
				functionDeclaration = RegexUtils.getMatcher(regex, jsContent);
			}
			if (TextUtils.isEmpty(functionDeclaration)){
				return null;
			}
			int functionStartIndex = jsContent.indexOf(functionDeclaration);
			int index = functionStartIndex + functionDeclaration.length();
			int functionEndIndex = index;
			int bracePair = 1;
			char[] jsContentChars = jsContent.toCharArray();
			int length = jsContentChars.length;
			for(int i=index; i<length; i++){
				char symbol = jsContentChars[i];
				if (symbol == '{'){
					bracePair++;
				}else if (symbol == '}'){
					bracePair--;
				}
				if (bracePair == 0){
					functionEndIndex = i;
					break;
				}
			}
			if (bracePair == 0){
				//已找到相应函数体
				return jsContent.substring(functionStartIndex, functionEndIndex+1) + ";";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行JS
	 * 
	 * @param js js代码
	 * @param functionName js方法名称
	 * @param functionParams js方法参数
	 * @return
	 */
	public static String runScript(Context context, String js, String functionName, Object[] functionParams) {
		org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
		rhino.setOptimizationLevel(-1);
		try {
			Scriptable scope = rhino.initStandardObjects();

			ScriptableObject.putProperty(scope, "javaContext", org.mozilla.javascript.Context.javaToJS(context, scope));
			ScriptableObject.putProperty(scope, "javaLoader", org.mozilla.javascript.Context.javaToJS(context.getClass().getClassLoader(), scope));

			rhino.evaluateString(scope, js, context.getClass().getSimpleName(), 1, null);

			Function function = (Function) scope.get(functionName, scope);

			Object result = function.call(rhino, scope, scope, functionParams);
			if (result instanceof String) {
				return (String) result;
			} else if (result instanceof NativeJavaObject) {
				return (String) ((NativeJavaObject) result).getDefaultValue(String.class);
			} else if (result instanceof NativeObject) {
				return (String) ((NativeObject) result).getDefaultValue(String.class);
			}
			return result.toString();//(String) function.call(rhino, scope, scope, functionParams);
		} finally {
			org.mozilla.javascript.Context.exit();
		}
	}
	
	public static ConfirmPassengersInfo getPassengerDTOs(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken()));
		try{
			A6Info a6Json = post(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/confirmPassenger/initDc"), url, lstParams);
			ConfirmPassengersInfo cpInfo = getGson().fromJson(a6Json.getData(),
					new TypeToken<ConfirmPassengersInfo>(){}.getType());
			return cpInfo;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getOrderRandCode(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?" +
				"module=passenger&rand=randp&" + Math.random();
		Bitmap bitmap = bInfo.getHttpHelper().getBitmap(makeRefererColl("https://kyfw.12306.cn/otn/confirmPassenger/initDc"), url);
		return bitmap;
	}
	
	public static boolean checkOrderRandCode(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("randCode", bInfo.getOrderRandCode()));
		lstParams.add(new BasicNameValuePair("rand", "randp"));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken()));
		try{
			A6Info a6Json = post(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/confirmPassenger/initDc"), url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getInt("result") == 1){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getConfirmPassengerHtml(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try{
			String strHtml = bInfo.getHttpHelper().post(null, url, lstParams);
			return strHtml;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getSubmitToken(String strHtml){
		if (strHtml == null){
			return null;
		}else{
			return RegexUtils.getMatcher("globalRepeatSubmitToken\\s*=\\s*'(\\S+)'", strHtml);
		}
	}
	
	public static String checkOrderInfo(BookingInfo bInfo){
		try{
			String url = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
			List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
			lstParams.add(new BasicNameValuePair("cancel_flag", "2"));
			lstParams.add(new BasicNameValuePair("bed_level_order_num", "000000000000000000000000000000"));
			lstParams.add(new BasicNameValuePair("passengerTicketStr", bInfo.getPassengerTicketStr()));
			lstParams.add(new BasicNameValuePair("oldPassengerStr", bInfo.getOldPassengerStr()));
			lstParams.add(new BasicNameValuePair("tour_flag", bInfo.getTour_flag()));
			lstParams.add(new BasicNameValuePair("randCode", bInfo.getOrderRandCode()));
//			lstParams.add(new BasicNameValuePair(bInfo.getQuery_detect_key(), bInfo.getQuery_detect_value()));
			lstParams.add(new BasicNameValuePair("_json_att", ""));
			lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken()));
			Map<String, String> map = new MapUtil()
					.add("X-Requested-With", "XMLHttpRequest")
					.add("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc").build();
			A6Info a6Json = post(bInfo, makeHeaders(map), url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			return jsonObj.optString("errMsg");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static A6Info getQueueCount(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("train_date", bInfo.getOrder_train_date()));
		lstParams.add(new BasicNameValuePair("train_no", bInfo.getTrain_no()));
		lstParams.add(new BasicNameValuePair("stationTrainCode", bInfo.getStationTrainCode()));
		lstParams.add(new BasicNameValuePair("seatType", bInfo.getSeatType()));
		lstParams.add(new BasicNameValuePair("fromStationTelecode", bInfo.getFromStationTelecode()));
		lstParams.add(new BasicNameValuePair("toStationTelecode", bInfo.getToStationTelecode()));
		lstParams.add(new BasicNameValuePair("leftTicket", bInfo.getLeftTicket()));
		lstParams.add(new BasicNameValuePair("purpose_codes", bInfo.getOrder_purpose_codes()));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken()));
		try{
			A6Info a6Json = post(bInfo, null, url, lstParams);
			return a6Json;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static boolean confirmSingle(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingle";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("passengerTicketStr", bInfo.getPassengerTicketStr()));
		lstParams.add(new BasicNameValuePair("oldPassengerStr", bInfo.getOldPassengerStr()));
		lstParams.add(new BasicNameValuePair("tour_flag", bInfo.getTour_flag()));
		lstParams.add(new BasicNameValuePair("randCode", bInfo.getOrderRandCode()));
		lstParams.add(new BasicNameValuePair("purpose_codes", bInfo.getOrder_purpose_codes()));
		lstParams.add(new BasicNameValuePair("key_check_isChange", bInfo.getKey_check_isChange()));
		lstParams.add(new BasicNameValuePair("train_location", bInfo.getTrain_location()));
		lstParams.add(new BasicNameValuePair("roomType", bInfo.getOrderRoomType()));
		lstParams.add(new BasicNameValuePair("dwAll", bInfo.getDWAll()));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken()));
		try{
			A6Info a6Json = post(bInfo, null, url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getBoolean("submitStatus")){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean confirmSingleForQueue(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("passengerTicketStr", bInfo.getPassengerTicketStr()));  
		lstParams.add(new BasicNameValuePair("oldPassengerStr", bInfo.getOldPassengerStr())); 
		lstParams.add(new BasicNameValuePair("randCode", bInfo.getOrderRandCode()));
		lstParams.add(new BasicNameValuePair("purpose_codes", bInfo.getOrder_purpose_codes()));
		lstParams.add(new BasicNameValuePair("key_check_isChange", bInfo.getKey_check_isChange()));
		lstParams.add(new BasicNameValuePair("leftTicketStr",bInfo.getLeftTicket()));
		lstParams.add(new BasicNameValuePair("train_location", bInfo.getTrain_location()));
		lstParams.add(new BasicNameValuePair("_json_att", ""));  
		lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken())); 
		try{
			A6Info a6Json = post(bInfo, null, url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getBoolean("submitStatus")){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static A6Info queryOrderWaitTime(BookingInfo bInfo){
		String url = String.format("https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?"
			+ "random=%s&tourFlag=%s&_json_att=%s&REPEAT_SUBMIT_TOKEN=%s",
			System.currentTimeMillis(), bInfo.getTour_flag(), "", bInfo.getRepeatSubmitToken());
		try{
			A6Info a6Json = get(bInfo, null, url);
			return a6Json;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static boolean resultOrderForDcQueue(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/resultOrderForDcQueue";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("orderSequence_no", bInfo.getOrderSequence_no()));  
		lstParams.add(new BasicNameValuePair("_json_att", "")); 
		lstParams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", bInfo.getRepeatSubmitToken()));
		try{
			A6Info a6Json = post(bInfo, null, url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getBoolean("submitStatus")){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static Bitmap getLTimeRandCode(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew.do?" +
				"module=login&rand=sjrand&"+Math.random();
		Bitmap bitmap = bInfo.getHttpHelper().getBitmap(makeRefererColl("https://kyfw.12306.cn/otn/zwdch/init"), url);
		return bitmap;
	}
	
	/**
	 * 根据站点名称取得12306对应的真实车次名称
	 * @param bInfo
	 * @return
	 */
	public static String getA6StationTrainCode(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/zwdch/queryCC";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("train_station_code", bInfo.getToStationTelecode()));
		try{
			A6Info a6Json = post(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/zwdch/init"), url, lstParams);
			JSONArray jsonArray = new JSONArray(a6Json.getData());
			String strTrainStationCode = bInfo.getStationTrainCode();
			if (jsonArray != null){
				for(int i=0; i<jsonArray.length(); i++){
					String strTrainNum = jsonArray.getString(i);
					if (strTrainNum.equals(strTrainStationCode)){
						return strTrainNum;
					}
				}
				String[] strStations = strTrainStationCode.split("/");
				if (strStations != null){
					for(int i=0; i<strStations.length; i++){
						String strStation2 = strStations[i];
						for(int j=0; j<jsonArray.length(); j++){
							String strTrainNum = jsonArray.getString(j);
							if (strTrainNum.equals(strStation2)){
								return strTrainNum;
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询晚点时间，传入的station_name用to_station
	 * @param bInfo
	 * @return
	 */
	public static A6Info getLTime(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/zwdch/query";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("cxlx", bInfo.getCxlx()+""));
		if (bInfo.getTo_station() == null){
			return null;
		}
		lstParams.add(new BasicNameValuePair("cz", bInfo.getTo_station()));
		lstParams.add(new BasicNameValuePair("cc", bInfo.getStationTrainCode()));
		String czEn=null;
		try {
			czEn = URLEncoder.encode(bInfo.getTo_station(), bInfo.getHttpHelper().getCharset());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (TextUtils.isEmpty(czEn)){
			return null;
		}
		lstParams.add(new BasicNameValuePair("czEn", czEn.replace("%","-")));
		lstParams.add(new BasicNameValuePair("randCode", bInfo.getRand_code()));
		try{
			A6Info a6Json = post(bInfo, makeRefererColl("https://kyfw.12306.cn/otn/zwdch/init"), url, lstParams);
			return a6Json;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
