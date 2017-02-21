package com.sshine.huochexing.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 * 提供验证邮箱、手机号、电话号码、身份证号码、数字等方法
 */
public final class RegexUtils {

	/**
	 * 验证Email
	 * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static final String regexEmail = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
	public static final String regexIdCard = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
	public static final String regexMobile = "(\\+\\d+)?1[3458]\\d{9}$";
	public static final String regexPhone = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
	public static final String regexDigit = "\\-?[1-9]\\d+";
	public static final String regexDecimals = "\\-?[1-9]\\d+(\\.\\d+)?";
	public static final String regexBlankSpace = "\\s+";
	public static final String regexChinese = "^[\u4E00-\u9FA5]+$";
	public static final String regexBirthday = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
	public static final String regexURL = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
	public static final String regexPostcode = "[1-9]\\d{5}";
	public static final String regexIpAddress = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
	public static final String regexTravelName = "^[\\w/]{1,20}$";
	public static final String regexUserName = "[a-zA-Z_0-9]{6,16}";
	public static final String regexPwd = "[a-zA-Z_0-9]{6,16}";
	public static final String regexA6Name = "^[a-zA-Z\\u4E00-\\u9FA50-9\\_]+$";
	public static final String regexA6OtherIDTemp = "^[a-zA-Z0-9\\_]+$";
	
	public static boolean checkEmail(String email) {
		return Pattern.matches(regexEmail, email);
	}
	
	/**
	 * 验证身份证号码
	 * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIdCard(String idCard) {
		return Pattern.matches(regexIdCard,idCard);
	}
	
	/**
	 * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
	 * @param mobile 移动、联通、电信运营商的号码段
	 *<p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
	 *、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
	 *<p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
	 *<p>电信的号段：133、153、180（未启用）、189</p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkMobile(String mobile) {
		return Pattern.matches(regexMobile,mobile);
	}
	
	/**
	 * 验证固定电话号码
	 * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
	 * <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
	 *  数字之后是空格分隔的国家（地区）代码。</p>
	 * <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
	 * 对不使用地区或城市代码的国家（地区），则省略该组件。</p>
	 * <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPhone(String phone) {
		return Pattern.matches(regexPhone, phone);
	}
	
	/**
	 * 验证整数（正整数和负整数）
	 * @param digit 一位或多位0-9之间的整数
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkDigit(String digit) {
		return Pattern.matches(regexDigit,digit);
	}
	
	/**
	 * 验证整数和浮点数（正负整数和正负浮点数）
	 * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkDecimals(String decimals) {
		return Pattern.matches(regexDecimals,decimals);
	} 
	
	/**
	 * 验证空白字符
	 * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkBlankSpace(String blankSpace) {
		return Pattern.matches(regexBlankSpace,blankSpace);
	}
	
	/**
	 * 验证中文
	 * @param chinese 中文字符
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkChinese(String chinese) {
		return Pattern.matches(regexChinese,chinese);
	}
	
	/**
	 * 验证日期（年月日）
	 * @param birthday 日期，格式：1992-09-03，或1992.09.03
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkBirthday(String birthday) {
		return Pattern.matches(regexBirthday,birthday);
	}
	
	/**
	 * 验证URL地址
	 * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkURL(String url) {
		return Pattern.matches(regexURL, url);
	}
	
	/**
	 * 匹配中国邮政编码
	 * @param postcode 邮政编码
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPostcode(String postcode) {
		return Pattern.matches(regexPostcode, postcode);
	}
	
	/**
	 * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
	 * @param ipAddress IPv4标准地址
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkIpAddress(String ipAddress) {
		return Pattern.matches(regexIpAddress, ipAddress);
	}
	
	/**
	 * 验证简体中文
	 */
	public static boolean checkSChinese(String str){
		return Pattern.matches(regexChinese, str);
	}
	
	public static boolean checkUserName(String str1){
		return Pattern.matches(regexUserName, str1);
	}
	public static boolean checkPwd(String str1){
		return Pattern.matches(regexUserName, str1);
	}
	
	public static String getMatcher(String regex, String source) {  
        String result = null;
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
        	result = matcher.group(1);//只取第一组
        }  
        return result;  
    }
}
