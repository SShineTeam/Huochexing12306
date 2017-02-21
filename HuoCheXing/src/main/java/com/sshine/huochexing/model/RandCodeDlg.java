package com.sshine.huochexing.model;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.VoidAsyncTask;
import com.sshine.huochexing.value.SF;

public class RandCodeDlg extends Dialog implements OnClickListener {

    private MaskView mMaskView;
    private Button btnOK, btnRefresh;
    private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
    private boolean mIsRefreshing = false;
    private RandCodeDlgListener mListener;

    public static final int MODE_LOGIN = 1;    //请求登录验证码
    public static final int MODE_BOOK = 2;     //请求预订验证码
    private int mMode = MODE_LOGIN;
    private LinearLayout mLoadingLayout;

    public interface RandCodeDlgListener {
        void onRequestedRandCode(String strRandCode);
    }

    protected RandCodeDlg(Context context) {
        super(context, R.style.Theme_CustomDialog);
    }

    protected RandCodeDlg(Context context, int theme) {
        super(context, R.style.Theme_CustomDialog);
    }

    public RandCodeDlg(Context context, RandCodeDlgListener listener, int mode) {
        super(context, R.style.Theme_CustomDialog);
        mListener = listener;
        mMode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rand_code_dlg);
        initViews();
    }

    private void showMsg(String str1) {
        Toast.makeText(getContext(), str1, Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        if (!HttpUtil.isNetworkConnected(getContext())) {
            showMsg("网络不可用，请检测网络状态" + SF.NO_NETWORK);
            return;
        }
        mMaskView = (MaskView) findViewById(R.id.rand_code_dlg_maskView);
        mLoadingLayout = (LinearLayout) findViewById(R.id.footer_loading_containter);
        btnRefresh = (Button) findViewById(R.id.rand_code_dlg_btnRefresh);
        btnRefresh.setOnClickListener(this);
        btnOK = (Button) findViewById(R.id.rand_code_dlg_btnOK);
        btnOK.setOnClickListener(this);
        refreshRandCode();
    }

    private void refreshRandCode() {
        if (mIsRefreshing) {
            return;
        }
        mIsRefreshing = true;
        btnRefresh.setEnabled(false);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mMaskView.setVisibility(View.GONE);
        new VoidAsyncTask() {
            Bitmap bitmap = null;

            @Override
            protected Object doInBackground(Object... params) {
                switch (mMode) {
                    case MODE_LOGIN:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bitmap = A6Util.getLoginRandCode(mBInfo);
                        break;
                    case MODE_BOOK:
                        bitmap = A6Util.getOrderRandCode(mBInfo);
                        break;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                if (bitmap == null) {
                    showMsg("请求验证码失败" + SF.FAIL);
                    return;
                }
                mIsRefreshing = false;
                btnRefresh.setEnabled(true);
                mLoadingLayout.setVisibility(View.GONE);
                mMaskView.setVisibility(View.VISIBLE);
                mMaskView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null){
                            mMaskView.setBackgroundBitmap(bitmap);
                            bitmap.recycle();
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rand_code_dlg_btnOK:
                List<Point> lstPoints = mMaskView.getSelectedPoints();
                if (lstPoints == null || lstPoints.size() == 0) {
                    showMsg("请选择图片");
                }
                String strPoints = "";
                if (mListener != null) {
                    for (Point p : lstPoints) {
                        strPoints += p.x + "," + p.y + ",";
                    }
                    if (!TextUtils.isEmpty(strPoints)) {
                        strPoints = strPoints.substring(0, strPoints.length() - 1);
                    }
                    final String strRandCode = strPoints;
                    new VoidAsyncTask() {
                        private boolean isRight = false;

                        @Override
                        protected Object doInBackground(Object... params) {
                            switch (mMode) {
                                case MODE_LOGIN:
                                    mBInfo.setLoginRandCode(strRandCode);
                                    isRight = A6Util.checkLoginRandCode(mBInfo);
                                    break;
                                case MODE_BOOK:
                                    mBInfo.setOrderRandCode(strRandCode);
                                    isRight = A6Util.checkOrderRandCode(mBInfo);
                                    break;
                            }

                            return null;
                        }

                        protected void onPostExecute(Object result) {
                            if (isRight) {
                                showMsg("验证码正确");
                                mListener.onRequestedRandCode(strRandCode);
                                dismiss();
                            } else {
                                showMsg("验证码错误");
                                refreshRandCode();
                            }
                        }

                        ;
                    }.start();

                }
//			String strPoints = "";
//			for(Point p:mMaskView.getSelectedPoints()){
//				strPoints += "("+p.x+", " + p.y + ") ";
//			}
                L.i("fixedPoints:" + strPoints);
                break;
            case R.id.rand_code_dlg_btnRefresh:
                refreshRandCode();
                break;
        }
    }

}
