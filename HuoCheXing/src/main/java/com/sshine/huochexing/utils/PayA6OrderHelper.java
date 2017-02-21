package com.sshine.huochexing.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.net.Uri;
import android.text.TextUtils;

import com.sshine.huochexing.bean.A6OrderPayInfo;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;

public class PayA6OrderHelper {
	private A6OrderPayInfo mInfo;
	
	public PayA6OrderHelper(String sequence_no){
		mInfo = new A6OrderPayInfo();
		mInfo.setSequence_no(sequence_no);
	}
	public boolean continuePayNoCompleteMyOrder(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/queryOrder/continuePayNoCompleteMyOrder";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("sequence_no", mInfo.getSequence_no()));
		lstParams.add(new BasicNameValuePair("pay_flag", "pay"));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try{
			A6Info a6Json = A6Util.post(bInfo, A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/initNoComplete"), url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			String existError = jsonObj.optString("existError", null);
			if (existError == null){
				return false;
			}else if (existError.equals("N")){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 取得订单支付信息
	 * @param bInfo
	 * @return 是否取得成功
	 */
	public synchronized boolean payOrderInit(BookingInfo bInfo){
		String url = "https://kyfw.12306.cn/otn/payOrder/init";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try{
			String strHtml = bInfo.getHttpHelper().post(A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/initNoComplete"), url, lstParams);
			if (!TextUtils.isEmpty(strHtml)){
				mInfo.setInterfaceName(RegexUtils.getMatcher("interfaceName\\s*=\\s*'(\\S+)';", strHtml));
				mInfo.setInterfaceVersion(RegexUtils.getMatcher("interfaceVersion\\s*=\\s*'(\\S+)';", strHtml));
				mInfo.setTranData(RegexUtils.getMatcher("tranData\\s*=\\s*'(\\S+)';", strHtml));
				mInfo.setMerSignMsg(RegexUtils.getMatcher("merSignMsg\\s*=\\s*'(\\S+)';", strHtml));
				mInfo.setAppId(RegexUtils.getMatcher("appId\\s*=\\s*'(\\S+)';", strHtml));
				mInfo.setTransType(RegexUtils.getMatcher("transType\\s*=\\s*'(\\S+)';", strHtml));
				if (TextUtils.isEmpty(mInfo.getInterfaceName()) || TextUtils.isEmpty(mInfo.getInterfaceVersion())
						|| TextUtils.isEmpty(mInfo.getTranData()) || TextUtils.isEmpty(mInfo.getMerSignMsg())
						|| TextUtils.isEmpty(mInfo.getAppId()) || TextUtils.isEmpty(mInfo.getTransType())){
					return false;
				}else{
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public A6OrderPayInfo getA6OrderPayInfo() {
		return mInfo;
	}
	
	/**
	 * 取得打开手机支付界面的post参数
	 * @return
	 */
	public String getA6PayPostParams(){
		StringBuffer sb = new StringBuffer();
		sb.append("interfaceName=");
		sb.append(Uri.encode(mInfo.getInterfaceName()));
		sb.append("&interfaceVersion=");
		sb.append(Uri.encode(mInfo.getInterfaceVersion()));
		sb.append("&tranData=");
		sb.append(Uri.encode(mInfo.getTranData()));
		sb.append("&merSignMsg=");
		sb.append(Uri.encode(mInfo.getMerSignMsg()));
		sb.append("&appId=");
		sb.append(Uri.encode(mInfo.getAppId()));
		sb.append("&transType=");
		sb.append(Uri.encode(mInfo.getTransType()));
		return sb.toString();
	}
	public String getPayUrl(){
		return "https://epay.12306.cn/pay/wapPayGateway";
	}
}
