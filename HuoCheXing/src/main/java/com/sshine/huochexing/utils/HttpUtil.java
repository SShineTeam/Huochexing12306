package com.sshine.huochexing.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;


public class HttpUtil {
    private String responseStr;
    private HttpHelper httpHelper = MyApp.getInstance().getMyHttpHelper();
    public static final String RESULT_CODE = "resultCode";
    public static final int MSG_SEND_FAIL = 0;
    public static final int MSG_SEND_SUCCESS = 1;
    public static final int MSG_RECEIVE_VERIFY_FAILED = -1;
    public static final int MSG_RECEIVE_FAIL = 0;
    public static final int MSG_RECEIVE_SUCCESS = 1;
    public static final int MSG_RECEIVE_EMPTY = 2;

    private AESCrypt mEncrypter = new AESCrypt();

    public String getResponseStr() {
        return responseStr;
    }

    public boolean post(String url, String jsonMessage) throws ClientProtocolException, IOException {
        List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
        try {
            jsonMessage = mEncrypter.encrypt(jsonMessage);
            lstParams.add(new BasicNameValuePair("message", jsonMessage));
            responseStr = httpHelper.post(null, url, lstParams);
            if (responseStr == null || responseStr.equals("")) {
                return false;
            } else {
                responseStr = mEncrypter.decrypt(responseStr);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        return HttpHelper.isNetworkConnected(context);
    }
}	
