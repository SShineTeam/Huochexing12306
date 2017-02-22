package com.sshine.huochexing.value;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.ThirdPartyPlatformInfo;

public class TT {
	private static Map<String, String> user_types;
	private static Map<String, String> passenger_card_types;
	private static Map<String, String> tour_flags;
	private static Map<String, String> sexs;
	public static final String[] QUERY_TYPE_KEYS = {"单程", "往返"};
	public static final String[] QUERY_TYPE_VALUES = {"dc", "wc"};
	public static final String[] QUERY_TICKET_TYPE_KEYS = {"成人票", "学生票"};
	public static final String[] QUERY_TICKET_TYPE_VALUES = {"ADULT", "0X00"};
	public static final String[] ORDER_PURPOSE_CODES = {"00", "0X00"};
	public static final String[] TIME_RANGE_KEYS = {"00:00--24:00", "00:00--06:00","06:00--12:00","12:00--18:00","18:00--24:00"};
	public static final String[] TIME_RANGE_VALUES = {"00:00,24:00", "00:00,06:00","06:00,12:00","12:00,18:00","18:00,24:00"};
	
	public static final String TRAIN_NO = "train_no";
	public static final String STATION_TRAIN_CODE = "station_train_code";
	public static final String START_STATION_TELECODE = "start_station_telecode";
	public static final String START_STATION_NAME = "start_station_name";
	public static final String END_STATION_TELECODE = "end_station_telecode";
	public static final String END_STATION_NAME = "end_station_name";
	public static final String FROM_STATION_TELECODE = "from_station_telecode";
	public static final String FROM_STATION_NAME = "from_station_name";
	public static final String TO_STATION_TELECODE = "to_station_telecode";
	public static final String TO_STATION_NAME = "to_station_name";
	public static final String START_TIME = "start_time";
	public static final String ARRIVE_TIME = "arrive_time";
	public static final String DAY_DIFFERENCE = "day_difference";
	public static final String TRAIN_CLASS_NAME = "train_class_name";
	public static final String LI_SHI = "lishi";
	public static final String CAN_WEB_BUY = "canWebBuy";
	public static final String LI_SHI_VALUE = "lishiValue";
	public static final String START_TRAIN_DATE = "start_train_date";
	public static final String SEAT_TYPES = "seat_types";
	public static final String LOCATION_CODE = "location_code";
	public static final String FROM_STATION_NO = "from_station_no";
	public static final String TO_STATION_NO = "to_station_no";
	public static final String SALE_TIME = "sale_time";
//	public static final String GG_NUM = "gg_num";
//	public static final String GR_NUM = "gr_num";
//	public static final String QT_NUM = "qt_num";
//	public static final String RW_NUM = "rw_num";
//	public static final String RZ_NUM = "rz_num";
//	public static final String TZ_NUM = "tz_num";
//	public static final String WZ_NUM = "wz_num";
//	public static final String YB_NUM = "yb_num";
//	public static final String YW_NUM = "yw_num";
//	public static final String YZ_NUM = "yz_num";
//	public static final String ZE_NUM = "ze_num";
//	public static final String ZY_NUM = "zy_num";
//	public static final String SWZ_NUM = "swz_num";
	public static final String D_LATE_TIME_STR = "d_lateTime_str";
	public static final String D_LATE_TIME = "d_LateTime";
	public static final String A_LATE_TIME_STR = "a_lateTime_str";
	public static final String A_LATE_TIME = "a_LateTime";
	public static final String TRAIN_START_TIME = "train_start_time";  //车次开车时间，HH:mm:SS格式，发请求时不要用START_TRAIN_DATE代替。

	public static final String FLAG_START = "flag_start";
	public static final String FLAG_END = "flag_end";
	public static final String SEAT_STRING = "seat_string";
	public static final String SPEED_INDEX = "speed_index";
	public static final String QUERY_LEFT_NEW_INFO = "queryLeftNewInfo";
	
	public static final String STATION_NAME = "station_name";
	public static final String REMAIN = "remain";
	public static final String DEPART_DATE = "depart_date";
	
	public static final String REQUEST_TYPE = "requestType";
	public static final String RESULT_CODE = "resultCode";
	
	public static final String SEAT_INFO = "seatInfo";
	public static final String PASSENGER_INFO = "passengerInfo";
	
	public static final int PLATFORM_QQ = 1;
	public static final int PLATFORM_SINA_WEIBO = 2;
	public static final int PLATFORM_TENCENT_WEIBO = 3;
	public static final int PLATFORM_DOUBAN = 4;
	public static final int PLATFORM_GOOGLE_PLUS = 5;
	public static final int PLATFORM_WECHAT = 6;
	public static final int PLATFORM_YIXIN = 7;
	public static final int PLATFORM_RENREN = 8;
	public static final int PLATFORM_LINE = 9;
	
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, ThirdPartyPlatformInfo> getPlatformInfos(){
		Map<Integer, ThirdPartyPlatformInfo> map = new HashMap<Integer, ThirdPartyPlatformInfo>();
		map.put(PLATFORM_QQ, new ThirdPartyPlatformInfo(PLATFORM_QQ, "QQ"));
		map.put(PLATFORM_SINA_WEIBO, new ThirdPartyPlatformInfo(PLATFORM_SINA_WEIBO, "SinaWeibo"));
		map.put(PLATFORM_TENCENT_WEIBO, new ThirdPartyPlatformInfo(PLATFORM_TENCENT_WEIBO, "TencentWeibo"));
		map.put(PLATFORM_DOUBAN, new ThirdPartyPlatformInfo(PLATFORM_DOUBAN, "Douban"));
		map.put(PLATFORM_GOOGLE_PLUS, new ThirdPartyPlatformInfo(PLATFORM_DOUBAN, "Google+"));
		map.put(PLATFORM_WECHAT, new ThirdPartyPlatformInfo(PLATFORM_DOUBAN, "Wechat"));
		map.put(PLATFORM_YIXIN, new ThirdPartyPlatformInfo(PLATFORM_DOUBAN, "Yixin"));
		map.put(PLATFORM_RENREN, new ThirdPartyPlatformInfo(PLATFORM_RENREN, "Renren"));
		map.put(PLATFORM_LINE, new ThirdPartyPlatformInfo(PLATFORM_DOUBAN, "Line"));
		return map;
	}

	public static Map<String, String> getUser_types() {
		if (user_types == null){
			user_types = new LinkedHashMap<String, String>();
			user_types.put("成人","1");
			user_types.put("儿童","2");
			user_types.put("学生","3");
			user_types.put("残疾军人、伤残人民警察","4");
		}
		return user_types;
	}
	public static Map<String, String> getPassenger_card_types() {
		if (passenger_card_types == null){
			passenger_card_types = new LinkedHashMap<String, String>();
			passenger_card_types.put("一代身份证","2");
			passenger_card_types.put("二代身份证","1");
			passenger_card_types.put("临时证","3");
			passenger_card_types.put("护照","B");
			passenger_card_types.put("港澳通行证","C");
			passenger_card_types.put("台湾通行证","G");
		}
		return passenger_card_types;
	}
	public static Map<String, String> getTour_flags() {
		if (tour_flags == null){
			tour_flags = new LinkedHashMap<String, String>();
			tour_flags.put("dc", "dc");
			tour_flags.put("wc", "wc");
			tour_flags.put("fc", "gc");
			tour_flags.put("gc", "gc");
			tour_flags.put("l1", "lc1");
			tour_flags.put("l2", "lc2");
		}
		return tour_flags;
	}
	public static Map<String, String> getSexs() {
		if (sexs == null){
			sexs = new HashMap<String, String>();
			sexs.put("男", "M");
			sexs.put("女", "F");
		}
		return sexs;		
	}
	
	/**
	 * 初始化表情 Map
	 */
	public static Map<String, Integer> getFaceMap() {
		Map<String, Integer> faceMap = new LinkedHashMap<String, Integer>();
		faceMap.put("[呲牙]", R.drawable.f000);
		faceMap.put("[调皮]", R.drawable.f001);
		faceMap.put("[流汗]", R.drawable.f002);
		faceMap.put("[偷笑]", R.drawable.f003);
		faceMap.put("[再见]", R.drawable.f004);
		faceMap.put("[敲打]", R.drawable.f005);
		faceMap.put("[擦汗]", R.drawable.f006);
		faceMap.put("[猪头]", R.drawable.f007);
		faceMap.put("[玫瑰]", R.drawable.f008);
		faceMap.put("[流泪]", R.drawable.f009);
		faceMap.put("[大哭]", R.drawable.f010);
		faceMap.put("[嘘]", R.drawable.f011);
		faceMap.put("[酷]", R.drawable.f012);
		faceMap.put("[抓狂]", R.drawable.f013);
		faceMap.put("[委屈]", R.drawable.f014);
		faceMap.put("[便便]", R.drawable.f015);
		faceMap.put("[炸弹]", R.drawable.f016);
		faceMap.put("[菜刀]", R.drawable.f017);
		faceMap.put("[可爱]", R.drawable.f018);
		faceMap.put("[色]", R.drawable.f019);
		faceMap.put("[害羞]", R.drawable.f020);

		faceMap.put("[得意]", R.drawable.f021);
		faceMap.put("[吐]", R.drawable.f022);
		faceMap.put("[微笑]", R.drawable.f023);
		faceMap.put("[发怒]", R.drawable.f024);
		faceMap.put("[尴尬]", R.drawable.f025);
		faceMap.put("[惊恐]", R.drawable.f026);
		faceMap.put("[冷汗]", R.drawable.f027);
		faceMap.put("[爱心]", R.drawable.f028);
		faceMap.put("[示爱]", R.drawable.f029);
		faceMap.put("[白眼]", R.drawable.f030);
		faceMap.put("[傲慢]", R.drawable.f031);
		faceMap.put("[难过]", R.drawable.f032);
		faceMap.put("[惊讶]", R.drawable.f033);
		faceMap.put("[疑问]", R.drawable.f034);
		faceMap.put("[睡]", R.drawable.f035);
		faceMap.put("[亲亲]", R.drawable.f036);
		faceMap.put("[憨笑]", R.drawable.f037);
		faceMap.put("[爱情]", R.drawable.f038);
		faceMap.put("[衰]", R.drawable.f039);
		faceMap.put("[撇嘴]", R.drawable.f040);
		faceMap.put("[阴险]", R.drawable.f041);

		faceMap.put("[奋斗]", R.drawable.f042);
		faceMap.put("[发呆]", R.drawable.f043);
		faceMap.put("[右哼哼]", R.drawable.f044);
		faceMap.put("[拥抱]", R.drawable.f045);
		faceMap.put("[坏笑]", R.drawable.f046);
		faceMap.put("[飞吻]", R.drawable.f047);
		faceMap.put("[鄙视]", R.drawable.f048);
		faceMap.put("[晕]", R.drawable.f049);
		faceMap.put("[大兵]", R.drawable.f050);
		faceMap.put("[可怜]", R.drawable.f051);
		faceMap.put("[强]", R.drawable.f052);
		faceMap.put("[弱]", R.drawable.f053);
		faceMap.put("[握手]", R.drawable.f054);
		faceMap.put("[胜利]", R.drawable.f055);
		faceMap.put("[抱拳]", R.drawable.f056);
		faceMap.put("[凋谢]", R.drawable.f057);
		faceMap.put("[饭]", R.drawable.f058);
		faceMap.put("[蛋糕]", R.drawable.f059);
		faceMap.put("[西瓜]", R.drawable.f060);
		faceMap.put("[啤酒]", R.drawable.f061);
		faceMap.put("[飘虫]", R.drawable.f062);

		faceMap.put("[勾引]", R.drawable.f063);
		faceMap.put("[OK]", R.drawable.f064);
		faceMap.put("[爱你]", R.drawable.f065);
		faceMap.put("[咖啡]", R.drawable.f066);
		faceMap.put("[钱]", R.drawable.f067);
		faceMap.put("[月亮]", R.drawable.f068);
		faceMap.put("[美女]", R.drawable.f069);
		faceMap.put("[刀]", R.drawable.f070);
		faceMap.put("[发抖]", R.drawable.f071);
		faceMap.put("[差劲]", R.drawable.f072);
		faceMap.put("[拳头]", R.drawable.f073);
		faceMap.put("[心碎]", R.drawable.f074);
		faceMap.put("[太阳]", R.drawable.f075);
		faceMap.put("[礼物]", R.drawable.f076);
		faceMap.put("[足球]", R.drawable.f077);
		faceMap.put("[骷髅]", R.drawable.f078);
		faceMap.put("[挥手]", R.drawable.f079);
		faceMap.put("[闪电]", R.drawable.f080);
		faceMap.put("[饥饿]", R.drawable.f081);
		faceMap.put("[困]", R.drawable.f082);
		faceMap.put("[咒骂]", R.drawable.f083);

		faceMap.put("[折磨]", R.drawable.f084);
		faceMap.put("[抠鼻]", R.drawable.f085);
		faceMap.put("[鼓掌]", R.drawable.f086);
		faceMap.put("[糗大了]", R.drawable.f087);
		faceMap.put("[左哼哼]", R.drawable.f088);
		faceMap.put("[哈欠]", R.drawable.f089);
		faceMap.put("[快哭了]", R.drawable.f090);
		faceMap.put("[吓]", R.drawable.f091);
		faceMap.put("[篮球]", R.drawable.f092);
		faceMap.put("[乒乓球]", R.drawable.f093);
		faceMap.put("[NO]", R.drawable.f094);
		faceMap.put("[跳跳]", R.drawable.f095);
		faceMap.put("[怄火]", R.drawable.f096);
		faceMap.put("[转圈]", R.drawable.f097);
		faceMap.put("[磕头]", R.drawable.f098);
		faceMap.put("[回头]", R.drawable.f099);
		faceMap.put("[跳绳]", R.drawable.f100);
		faceMap.put("[激动]", R.drawable.f101);
		faceMap.put("[街舞]", R.drawable.f102);
		faceMap.put("[献吻]", R.drawable.f103);
		faceMap.put("[左太极]", R.drawable.f104);

		faceMap.put("[右太极]", R.drawable.f105);
		faceMap.put("[闭嘴]", R.drawable.f106);
		return faceMap;
	}
}
