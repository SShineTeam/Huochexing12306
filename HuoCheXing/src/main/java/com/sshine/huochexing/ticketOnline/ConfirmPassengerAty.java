package com.sshine.huochexing.ticketOnline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.PLayoutAdapter;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.ConfirmPassengersInfo;
import com.sshine.huochexing.bean.PassengerInfo;
import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
import com.sshine.huochexing.bean.SeatInfo;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.listener.IMultiChoiceDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.model.MultiChoiceFragment;
import com.sshine.huochexing.model.RandCodeDlg;
import com.sshine.huochexing.model.RandCodeDlg.RandCodeDlgListener;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.SeatHelper;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.sshine.huochexing.value.TT;
import com.umeng.analytics.MobclickAgent;

public class ConfirmPassengerAty extends BaseAty
        implements OnClickListener, IFavoriteCharacterDialogListener,
        IMultiChoiceDialogListener, RandCodeDlgListener {
    public static final String EXTRA_MODE = "extraMode";
    public static final int EXTRA_MODE_NORMAL = 0;    //正常购票
    public static final int EXTRA_MODE_MONITOR = 1;   //监控
    public static final int EXTRA_MODE_RESIGN = 2;   //改签

    public static final String EXTRA_TRAIN_INFO = "extraTrainInfo";
    public static final String EXTRA_TOUR_FLAG = "extraTour_flag";
    public static final String EXTRA_P_NATIVE_INDEXS = "extraPInfos";
    public static final String EXTRA_DEFAULT_SEAT_TYPE = "type";

    private static final int REQUEST_ADD_PLAYOUT = 1;
    private static final int REQUEST_LOGIN = 3;
    private QueryLeftNewDTOInfo mQLNDInfo;
    //	private EditText etRandCode;
//	private ImageView ivRandCode;
    private ListView lvPLayouts;
    private Button btnOK;
    private List<PassengerInfo> mLstPInfos;
    private String[] mSeats;
    private List<SeatInfo> mLstSeatInfos;
    private String[] mStrPInfos;
    private boolean[] mSelectedStrPInfoIndex;
    private List<Map<String, Object>> mLstPLayoutInfos = new ArrayList<Map<String, Object>>();
    private PLayoutAdapter mPLayoutAdpater;
    private static final int PLAYOUT_SEAT_START_REQUEST_CODE = 100;
    private static final int PLAYOUT_P_INFO_START_REQUEST_CODE = 10000;
    protected boolean mIsInitedFormValues;
    protected String mStrTour_flag;
    private BookingInfo mBInfo;
    private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
    private double mYZPrice = -1;
    private SeatHelper mSHelper;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case A6Util.MSG_INIT_FORM_VALUES_FINISH:
                    break;
                case A6Util.MSG_REQUEST_CHKCK_ORDER_INFO_FINISH:
                    btnOK.setEnabled(true);
                    break;
                case A6Util.MSG_CHKCK_ORDER_INFO_FAIL:
                    btnOK.setEnabled(true);
                    showMsg((String) msg.obj);
//				etRandCode.setText("");
                    break;
                case A6Util.MSG_GET_PASSENGERS_SUCCESS:
                    if (mLstPInfos.size() > 0) {
                        //设置第一个为默认乘车人
                        mLstPInfos.get(0).setCommon(true);
                        for (int i = 0; i < mLstPInfos.size(); i++) {
                            mLstPInfos.get(i).setNativeIndex(i + 1);
                        }
                        PersistentUtil.writeObject(mLstPInfos, MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
                        fixTicketType();
                        setStrPInfos();
                        addPLayout();
                    }
                    break;
//			case A6Util.MSG_REQUEST_ORDER_RAND_CODE_FAIL:
//				showMsg("请求验证码失败" + SF.FAIL);
//				ivRandCode.setEnabled(true);
//				ivRandCode.clearAnimation();
//				ivRandCode.setImageResource(R.drawable.refesh);
//				etRandCode.setText("");
//				break;
//			case A6Util.MSG_REQUEST_ORDER_RAND_CODE_SUCCESS:
//				ivRandCode.setEnabled(true);
//				ivRandCode.clearAnimation();
//				ivRandCode.setImageBitmap((Bitmap)msg.obj);
//				etRandCode.setText("");
//				break;
                case A6Util.MSG_REQUEST_ORDER_DATA_FAIL:
                    showMsg("获取订单数据时出错" + SF.FAIL);
                    stopPrepareData();
                    break;
                case A6Util.MSG_QUEUE_COUNT_OVERFLOW:
                    showDlg("目前排队人数已经超过余票张数，请您选择其他席别或车次" + SF.TIP);
                    break;
                case A6Util.MSG_QUERY_TICKET_PRICE_SUCCESS:
                    processSeats();
                    mSeats = new String[mLstSeatInfos.size()];
                    for (int i = 0; i < mLstSeatInfos.size(); i++) {
                        SeatInfo sInfo = mLstSeatInfos.get(i);
                        mSeats[i] = sInfo.getName() + "(" + sInfo.getPrice() + ")";
                    }
                    for (int i = 0; i < mLstPLayoutInfos.size(); i++) {
                        mLstPLayoutInfos.get(i).put(TT.SEAT_INFO, getDefaultSInfo());
                    }
                    setSubmitButtonText();
                    mPLayoutAdpater.notifyDataSetChanged();
                    break;
                case A6Util.MSG_CONFIRM_SINGLE_SUCCESS:
                case A6Util.MSG_RESULT_ORDER_FOR_QUEUE_SUCCESS:
                    showMsg("恭喜您，席位已锁定，请稍候..." + SF.SUCCESS);
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(ConfirmPassengerAty.this, A6OrderAty.class));
                            ConfirmPassengerAty.this.finish();
                        }

                        ;
                    }.start();
                    break;
            }
        }
    };
    private boolean isStopPrepareData = false;
    protected boolean mIsInitingFormValues;
    private int mDefaultSeatType;
    private RandCodeDlg mRandCodeDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDisableLoadingView(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_confirm_passenger);
        setTitle("订单确认");
        initViews();
    }

    /**
     * 用于在不售卖学生票期间将学生票改为成人票显示
     */
    private void fixTicketType() {
        if (!A6Util.isCanBookingStuTicket(System.currentTimeMillis())) {
            for (PassengerInfo pInfo : mLstPInfos) {
                if (pInfo.getPassenger_type_name().equals("学生")) {
                    pInfo.setPassenger_type_name("成人");
                    pInfo.setPassenger_type(TT.getUser_types().get("成人"));
                }
            }
        }
    }

    private void processSeats() {
        SparseIntArray saRNums = mSHelper.getSeatReferenceNums();
        if (mLstSeatInfos.size() > 0) {
            SeatInfo yzSInfo = null;
            for (int i = mLstSeatInfos.size() - 1; i >= 0; i--) {
                SeatInfo sInfo = mLstSeatInfos.get(i);
                if (yzSInfo == null && sInfo.getType() == SeatHelper.YZ) {
                    try {
                        yzSInfo = (SeatInfo) sInfo.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                if (saRNums.get(sInfo.getType()) == 0) {
                    mLstSeatInfos.remove(i);
                }
            }
            if (yzSInfo != null) {
                for (int i = mLstSeatInfos.size() - 1; i >= 0; i--) {
                    SeatInfo sInfo = mLstSeatInfos.get(i);
                    if (sInfo.getType() == SeatHelper.WZ) {
                        yzSInfo.setName("无座");
                        mLstSeatInfos.set(i, yzSInfo);
                        break;
                    }
                }
            }
        }
    }

    private void stopPrepareData() {
        isStopPrepareData = true;
        btnOK.setEnabled(false);
    }

    private void confirmSingleForQueue() {
        startHandle("订单排队提交中...", new Runnable() {
            @Override
            public void run() {
                boolean isOK = A6Util.confirmSingleForQueue(mBInfo);
                if (isOK) {
                    boolean isNeedWait = true;
                    long ms1 = System.currentTimeMillis();
                    String orderId = "null";
                    while (isNeedWait) {
                        A6Info a6Json = A6Util.queryOrderWaitTime(mBInfo);
                        try {
                            JSONObject jsonObj = new JSONObject(a6Json.getData());
                            isNeedWait = jsonObj.getBoolean("queryOrderWaitTimeStatus");
                            orderId = jsonObj.getString("orderId");
                            if (!"null".equalsIgnoreCase(orderId)) {
                                break;
                            }
                            String msg = jsonObj.optString("msg");
                            if ((msg != null) && msg.indexOf("用户过多") > 0) {
                                sendToast("当前提交订单的用户过多，正在重试");
                            }
                            if ((System.currentTimeMillis() - ms1) > 10000) {
                                sendToast("订单排队提交时出错1" + SF.FAIL);
                                break;
                            }
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendToast("订单排队提交时出错2" + SF.FAIL);
                            isNeedWait = false;
                            break;
                        }
                    }
                    mBInfo.setOrderSequence_no(orderId);
                    boolean isDone = A6Util.resultOrderForDcQueue(mBInfo);
                    if (isDone) {
                        sendDismissDialog();
                        Message msg = mHandler.obtainMessage(A6Util.MSG_RESULT_ORDER_FOR_QUEUE_SUCCESS);
                        mHandler.sendMessage(msg);
                    } else {
                        sendToast("订单排队时出错" + SF.FAIL);
                    }
                } else {
                    sendToast("订单确认失败" + SF.FAIL);
                }
            }
        });
    }

    private void setStrPInfos() {
        mStrPInfos = new String[mLstPInfos.size()];
        mSelectedStrPInfoIndex = new boolean[mLstPInfos.size()];
        for (int i = 0; i < mLstPInfos.size(); i++) {
            PassengerInfo pInfo = mLstPInfos.get(i);
            if (pInfo.isCommon()) {
                mSelectedStrPInfoIndex[i] = true;
            }
            mStrPInfos[i] = pInfo.getPassenger_name() + "(" + pInfo.getPassenger_type_name() + ")";
        }
    }

    //抢票模式下的设置
    private void setStrPInfosOnGrabTicketsMode(List<Integer> lstPNativeIndexes) {
        mStrPInfos = new String[mLstPInfos.size()];
        mSelectedStrPInfoIndex = new boolean[mLstPInfos.size()];
        for (int i = 0; i < mLstPInfos.size(); i++) {
            PassengerInfo pInfo = mLstPInfos.get(i);
            if (lstPNativeIndexes.contains(pInfo.getNativeIndex())) {
                mSelectedStrPInfoIndex[i] = true;
            }
            mStrPInfos[i] = pInfo.getPassenger_name() + "(" + pInfo.getPassenger_type_name() + ")";
        }
    }

    private void initViews() {
        L.i("onCreate");
        mQLNDInfo = (QueryLeftNewDTOInfo) this.getIntent().getSerializableExtra(EXTRA_TRAIN_INFO);
        if (mQLNDInfo == null) {
            return;
        }
        mStrTour_flag = getIntent().getStringExtra(EXTRA_TOUR_FLAG);
        if (getIntent().getIntExtra(EXTRA_MODE, EXTRA_MODE_NORMAL) == EXTRA_MODE_MONITOR) {
            setTitle("抢票监控订单确认");
            mBInfo = MyApp.getInstance().getBgdBInfo();
        } else {
            mBInfo = MyApp.getInstance().getCommonBInfo();
        }
        mDefaultSeatType = getIntent().getIntExtra(EXTRA_DEFAULT_SEAT_TYPE, -1);

        ImageView ivStartType = (ImageView) findViewById(R.id.fromStationType);
        ImageView ivEndType = (ImageView) findViewById(R.id.toStationType);
        lvPLayouts = (ListView) findViewById(R.id.playouts);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
//		etRandCode = (EditText)findViewById(R.id.randCode);
//		ivRandCode = (ImageView)findViewById(R.id.randCode1);
//		ivRandCode.setOnClickListener(this);
        btnOK = (Button) findViewById(R.id.ok);
        btnOK.setOnClickListener(this);

//		//先让用户感觉当前正在请求验证码
//		ImageUtil.rotateImageForever(this, ivRandCode,R.anim.imge_rotate_forever);
        mSHelper = new SeatHelper(mQLNDInfo);
        tv(R.id.trainNum, mQLNDInfo.getStation_train_code());
        tv(R.id.totalTime, mQLNDInfo.getLishi());
        boolean isStart = mQLNDInfo.getFrom_station_telecode().equals(
                mQLNDInfo.getStart_station_telecode());
        mQLNDInfo.setFlag_start(isStart);
        boolean isEnd = mQLNDInfo.getTo_station_telecode().equals(
                mQLNDInfo.getEnd_station_telecode());
        mQLNDInfo.setFlag_end(isEnd);
        ivStartType.setImageResource(mQLNDInfo.isFlag_start() ? R.drawable.station_start : R.drawable.station_pass);
        ivEndType.setImageResource(mQLNDInfo.isFlag_end() ? R.drawable.station_end_point : R.drawable.station_pass);
        tv(R.id.from, mQLNDInfo.getFrom_station_name());
        tv(R.id.to, mQLNDInfo.getTo_station_name());
        tv(R.id.d_time, mQLNDInfo.getStart_time());
        tv(R.id.a_time, getFomartArriveTimeString(mQLNDInfo.getArrive_time(),
                mQLNDInfo.getDay_difference()));
        tv(R.id.d_lateTime, TimeUtil.get_T_Str(mQLNDInfo.getD_LateTime()));
        tv(R.id.a_lateTime, TimeUtil.get_T_Str(mQLNDInfo.getA_LateTime()));
        tv(R.id.seatInfo, mSHelper.getSeatText());
        initPLayoutListView();
        setLstPInfos();
        queryTicketPrice();
        submitOrderRequest();
//		etRandCode.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				if (etRandCode.getText().length() == 4){
//					if (setSP.isAutoSubmit() && (!isStopPrepareData)){
//						checkOrderInfo();
//					}
//				}
//			}
//		});
    }

    private TextView tv(int id, CharSequence cs1) {
        TextView tv1 = (TextView) findViewById(id);
        tv1.setText(cs1);
        return tv1;
    }

    public void queryTicketPrice() {
        startHandle("正在取回票价...", new Runnable() {
            public void run() {
                mBInfo.setTrain_no(mQLNDInfo.getTrain_no());
                mBInfo.setFrom_station_no(mQLNDInfo.getFrom_station_no());
                mBInfo.setTo_station_no(mQLNDInfo.getTo_station_no());
                mBInfo.setSeat_types(mQLNDInfo.getSeat_types());
                mLstSeatInfos = A6Util.queryTicketPrice(mBInfo);
                sendDismissDialog();
                if (mLstSeatInfos != null) {
                    Message msg = mHandler.obtainMessage(A6Util.MSG_QUERY_TICKET_PRICE_SUCCESS);
                    mHandler.sendMessage(msg);
                } else {
                    sendToast("解析数据时出错" + SF.FAIL);
                }
            }

            ;
        });
    }

    @SuppressWarnings("unchecked")
    public void setLstPInfos() {
        mLstPInfos = (List<PassengerInfo>) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
        if (mLstPInfos == null) {
            startHandle("请求数据...", new Runnable() {
                public void run() {
                    ConfirmPassengersInfo cpInfo = A6Util.getPassengerDTOs(mBInfo);
                    sendDismissDialog();
                    if (cpInfo == null) {
                        sendToast("请求数据时出错" + SF.FAIL);
                    } else {
                        mLstPInfos = cpInfo.getNormal_passengers();
                        Message msg = mHandler.obtainMessage(A6Util.MSG_GET_PASSENGERS_SUCCESS);
                        msg.obj = mLstPInfos;
                        mHandler.sendMessage(msg);
                    }
                }

                ;
            });
        } else {
            fixTicketType();
            ArrayList<Integer> lstPNativeIndexs = (ArrayList<Integer>) getIntent().getSerializableExtra(EXTRA_P_NATIVE_INDEXS);
            if (lstPNativeIndexs != null) {
                //抢票模式下常用用户不再自动添加
                setStrPInfosOnGrabTicketsMode(lstPNativeIndexs);
            } else {
                setStrPInfos();
            }
            addPLayout();
        }
    }

    private void initPLayoutListView() {
        mPLayoutAdpater = new PLayoutAdapter(this, mLstPLayoutInfos, new PLayoutAdapter.PLayoutItemClickListener() {

            @Override
            public void onSeatButtonClick(View v, int pos) {
                //防止rquestCode污染
                int requestCode = PLAYOUT_SEAT_START_REQUEST_CODE + pos;
                FavoriteCharacterDialogFragment.show(ConfirmPassengerAty.this, requestCode, "座位选择", mSeats);
            }

            @Override
            public void onPassengerButtonClick(View v, int pos) {
                PassengerInfo pInfo = (PassengerInfo) mLstPLayoutInfos.get(pos).get(TT.PASSENGER_INFO);
                Intent intent = new Intent(ConfirmPassengerAty.this, EditPassengerAty.class);
                intent.putExtra(EditPassengerAty.EXTRA_PASSENGER_INFO, pInfo);
                intent.putExtra(EditPassengerAty.EXTRA_OPERATE, EditPassengerAty.EXTRA_OPERATE_EDIT);
                intent.putExtra(EditPassengerAty.EXTRA_IS_SHOW_OK, true);
                startActivityForResult(intent, PLAYOUT_P_INFO_START_REQUEST_CODE + pos);
            }

            @Override
            public void onDelButtonClick(View v, int pos) {
                Map<String, Object> map = mLstPLayoutInfos.get(pos);
                PassengerInfo pInfo = (PassengerInfo) map.get(TT.PASSENGER_INFO);
                int index = mLstPInfos.indexOf(pInfo);
                if (index != -1) {
                    mSelectedStrPInfoIndex[index] = false;
                    mLstPLayoutInfos.remove(pos);
                    setSubmitButtonText();
                    mPLayoutAdpater.notifyDataSetChanged();
                }
            }
        });
        lvPLayouts.setAdapter(mPLayoutAdpater);
    }

    private void addPLayout() {
        if (mLstPInfos == null) {
            return;
        }
        for (int i = 0; i < mSelectedStrPInfoIndex.length; i++) {
            PassengerInfo pInfo = mLstPInfos.get(i);
            if (mSelectedStrPInfoIndex[i]) {
                if (!isPassengerExist(pInfo)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(TT.SEAT_INFO, getDefaultSInfo());
                    map.put(TT.PASSENGER_INFO, pInfo);
                    mLstPLayoutInfos.add(map);
                }
            } else {
                for (int j = 0; j < mLstPLayoutInfos.size(); j++) {
                    PassengerInfo pInfo1 = (PassengerInfo) mLstPLayoutInfos.get(j).get(TT.PASSENGER_INFO);
                    if (pInfo1 == pInfo) {
                        mLstPLayoutInfos.remove(j);
                        break;
                    }
                }
            }
        }
        setSubmitButtonText();
        mPLayoutAdpater.notifyDataSetChanged();
    }

    private SeatInfo getDefaultSInfo() {
        if (mLstSeatInfos == null || mLstSeatInfos.size() == 0) {
            return null;
        }
        if (mDefaultSeatType == -1) {
            return mLstSeatInfos.get(0);
        }
        for (SeatInfo sInfo : mLstSeatInfos) {
            if (sInfo.getType() == mDefaultSeatType) {
                return sInfo;
            }
        }
        return mLstSeatInfos.get(0);
    }

    private boolean isPassengerExist(PassengerInfo pInfo) {
        for (int i = 0; i < mLstPLayoutInfos.size(); i++) {
            PassengerInfo pInfo1 = (PassengerInfo) mLstPLayoutInfos.get(i).get(TT.PASSENGER_INFO);
            //暂只进行引用比较
            if (pInfo1 == pInfo) {
                return true;
            }
        }
        return false;
    }

    private void showRandCodeDlg() {
        mRandCodeDlg = new RandCodeDlg(this, this, RandCodeDlg.MODE_BOOK);
        if (mRandCodeDlg != null && !mRandCodeDlg.isShowing()) {
            mRandCodeDlg.show();
        }
    }

    private String getFomartArriveTimeString(String strArriveTime,
                                             int day_difference) {
        String strRetValue = "";
        switch (day_difference) {
            case 0:
                strRetValue = "当日 " + strArriveTime;
                break;
            case 1:
                strRetValue = "次日 " + strArriveTime;
                break;
            default:
                strRetValue = day_difference + "日后 " + strArriveTime;
        }
        return strRetValue;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                MultiChoiceFragment.show(this, REQUEST_ADD_PLAYOUT, "乘车人选择", "确定", "乘车人管理", mStrPInfos, mSelectedStrPInfoIndex);
                break;
            case R.id.clear:
                mLstPLayoutInfos.clear();
                for (int i = 0; i < mSelectedStrPInfoIndex.length; i++) {
                    mSelectedStrPInfoIndex[i] = false;
                }
                setSubmitButtonText();
                mPLayoutAdpater.notifyDataSetChanged();
                break;
//		case R.id.randCode1:
//			break;
            case R.id.ok:
                showRandCodeDlg();
                break;
        }
    }

    private void submitOrderRequest() {
        startHandle("请求购票数据...", new Runnable() {
            @Override
            public void run() {
                int result = A6Util.submitOrderRequest(mBInfo);
                switch (result) {
                    case -1:
                        Intent intent1 = new Intent(ConfirmPassengerAty.this, A6LoginAty.class);
                        startActivityForResult(intent1, REQUEST_LOGIN);
                        sendToast("请登录");
                        break;
                    case 0:
                        sendToast("车票信息已过期，请重新查询最新车票信息" + SF.FAIL);
                        break;
                    case 1:
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                initFormValues();
                                sendDismissDialog();
                            }

                        });

                        break;
                    case 2:
                        sendDismissDialog();
                        Message msg = mHandler.obtainMessage();
                        msg.what = A6Util.MSG_WAIT_HANDLE_ORDERS;
                        mHandler.sendMessage(msg);
                        break;
                    case 3:
                        mBInfo.setQuery_detect_key(null);
                        mBInfo.setQuery_detect_value(null);
                        sendToast("网络繁忙，请您重试。如正在使用第三方购票软件或插件，请卸载后重试。");
                        finish();
                        break;
                }
            }
        });
    }

    private void checkOrderInfo() {
        if (mLstPLayoutInfos == null || mLstPLayoutInfos.size() == 0) {
            showMsg("请先添加乘车人" + SF.TIP);
            return;
        }
//		final String strRandCode = etRandCode.getText().toString();
//		if (TextUtils.isEmpty(strRandCode)){
//			showMsg("请先填写验证码" + SF.TIP);
//			return;
//		}
        btnOK.setEnabled(false);
        new Thread() {
            public void run() {
                if (!mIsInitedFormValues) {
                    if (!startHandle("正在获取订单数据...")) {
                        return;
                    }
                    String strHtml = A6Util.getConfirmPassengerHtml(mBInfo);
                    sendDismissDialog();
                    String strSubmitToken = A6Util.getSubmitToken(strHtml);
                    String key_check_isChange = A6Util.getKey_check_isChange(strHtml);
                    String trainLocation = A6Util.getTrainLocation(strHtml);
                    //不支持女宾包房与家庭包房
                    boolean nvbbf = false, jtbf = false;
                    String roomType = nvbbf ? "1" : "0";
                    roomType += jtbf ? "1" : "0";
                    //dwAll意义暂不明
                    mBInfo.setDWAll("N");
                    mBInfo.setOrderRoomType(roomType);
                    if (strSubmitToken != null && key_check_isChange != null && trainLocation != null) {
                        mBInfo.setRepeatSubmitToken(strSubmitToken);
                        mBInfo.setKey_check_isChange(key_check_isChange);
                        mBInfo.setTrain_location(trainLocation);
                        L.d("submitToken:" + strSubmitToken + ", key_check_isChange:" + key_check_isChange + ", train_location:" + trainLocation);
                        mIsInitedFormValues = true;
                    } else {
                        Message msg = mHandler.obtainMessage(A6Util.MSG_REQUEST_ORDER_DATA_FAIL);
                        mHandler.sendMessage(msg);
                        return;
                    }
                }
                mBInfo.setPassengerTicketStr(getPassengerTicketStr());
                mBInfo.setOldPassengerStr(getOldPassengerStr());
                if (!startHandle("校验订单...")) {
                    return;
                }
                Message msg = mHandler.obtainMessage(A6Util.MSG_REQUEST_CHKCK_ORDER_INFO_FINISH);
                mHandler.sendMessage(msg);
                String strResult = A6Util.checkOrderInfo(mBInfo);
                sendDismissDialog();
                if (strResult.isEmpty()) {
//                    getQueueCount();
                    confirmSingle();
                } else {
                    Message msg1 = mHandler.obtainMessage(A6Util.MSG_CHKCK_ORDER_INFO_FAIL);
                    msg1.obj = strResult;
                    mHandler.sendMessage(msg1);
                }
            }

            ;
        }.start();
    }

    private void confirmSingle() {
        if (!startHandle("确认订单信息...")) {
            return;
        }
        int indexPurpose = 0;
        for (int i = 0; i < TT.QUERY_TICKET_TYPE_VALUES.length; i++) {
            if (TT.QUERY_TICKET_TYPE_VALUES[i].equals(mBInfo.getPurpose_codes())) {
                indexPurpose = i;
                break;
            }
        }
        mBInfo.setOrder_purpose_codes(TT.ORDER_PURPOSE_CODES[indexPurpose]);
        new Thread() {
            public void run() {
                boolean isDone = A6Util.confirmSingle(mBInfo);
                if (isDone) {
                    sendDismissDialog();
                    Message msg = mHandler.obtainMessage(A6Util.MSG_CONFIRM_SINGLE_SUCCESS);
                    mHandler.sendMessage(msg);
                } else {
                    sendToast("订单确认时出错" + SF.FAIL);
                }
            }
        }.start();
    }

    private void getQueueCount() {
        if (!startHandle("正在加入12306订单队列...")) {
            return;
        }
        mBInfo.setOrder_train_date(A6Util.formatToEnglish(mBInfo.getTrain_date()));
        mBInfo.setTrain_no(mQLNDInfo.getTrain_no());
        mBInfo.setStationTrainCode(mQLNDInfo.getStation_train_code());
        SeatInfo sInfo = (SeatInfo) mLstPLayoutInfos.get(0).get(TT.SEAT_INFO);
        mBInfo.setSeatType(sInfo.getType_code());
        mBInfo.setFromStationTelecode(mQLNDInfo.getFrom_station_telecode());
        mBInfo.setToStationTelecode(mQLNDInfo.getTo_station_telecode());
        mBInfo.setLeftTicket(mQLNDInfo.getYp_info());
        int indexPurpose = 0;
        for (int i = 0; i < TT.QUERY_TICKET_TYPE_VALUES.length; i++) {
            if (TT.QUERY_TICKET_TYPE_VALUES[i].equals(mBInfo.getPurpose_codes())) {
                indexPurpose = i;
                break;
            }
        }
        mBInfo.setOrder_purpose_codes(TT.ORDER_PURPOSE_CODES[indexPurpose]);
        new Thread() {
            public void run() {
                A6Info a6Json = A6Util.getQueueCount(mBInfo);
                sendDismissDialog();
                if (a6Json == null || (!a6Json.isStatus())) {
                    sendToast("订单排队时出错" + SF.FAIL);
                } else {
                    try {
                        JSONObject jsonObj = new JSONObject(a6Json.getData());
                        boolean op_2 = jsonObj.optBoolean("op_2", false);
                        int countT = jsonObj.optInt("countT", 0);
                        mBInfo.setLeftTicket(jsonObj.getString("ticket"));
                        Message msg = mHandler.obtainMessage();
                        if (op_2) {
                            //排队人数已经超过余票张数
                            msg.what = A6Util.MSG_QUEUE_COUNT_OVERFLOW;
                            mHandler.sendMessage(msg);
                        } else if (countT >= 0) {
                            String strMsg = "目前排队人数为<strong><font color='#ff8c00'>" + countT + "</font><strong>人";
                            sendToast(Html.fromHtml(strMsg));
                            confirmSingleForQueue();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendToast("订单排队时出错" + SF.FAIL);
                    }
                }
            }

            ;
        }.start();
    }

    private String getOldPassengerStr() {
        String strReturnValue = "";
        for (int i = 0; i < mLstPLayoutInfos.size(); i++) {
            PassengerInfo pInfo = (PassengerInfo) mLstPLayoutInfos.get(i).get(TT.PASSENGER_INFO);
            try {
                if (mStrTour_flag.equals(TT.getTour_flags().get("fc"))
                        || mStrTour_flag.equals(TT.getTour_flags().get("gc"))) {
                    String strTemp = pInfo.getPassenger_name()
                            + "," + pInfo.getPassenger_id_type_code()
                            + "," + pInfo.getPassenger_id_no()
                            + "," + pInfo.getPassenger_type();
                    strReturnValue += strTemp + "_";
                } else {
                    String strTemp = pInfo.getPassenger_name()
                            + "," + pInfo.getPassenger_id_type_code()
                            + "," + pInfo.getPassenger_id_no()
                            + "," + pInfo.getPassenger_type();
                    strReturnValue += strTemp + "_";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return strReturnValue;
    }

    private String getPassengerTicketStr() {
        String strReturnValue = "";
        for (int i = 0; i < mLstPLayoutInfos.size(); i++) {
            SeatInfo sInfo = (SeatInfo) mLstPLayoutInfos.get(i).get(TT.SEAT_INFO);
            PassengerInfo pInfo = (PassengerInfo) mLstPLayoutInfos.get(i).get(TT.PASSENGER_INFO);
            try {
                strReturnValue += sInfo.getType_code() + ",0," + pInfo.getPassenger_type()
                        + "," + pInfo.getPassenger_name() + "," + pInfo.getPassenger_id_type_code()
                        + "," + pInfo.getPassenger_id_no() + "," + (pInfo.getMobile_no() == null ? "" : pInfo.getMobile_no())
                        + "," + "N" + "_";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (strReturnValue.length() > 1) {
            strReturnValue = strReturnValue.substring(0, strReturnValue.length() - 1);
        } else {
            sendToast("数据出错");
        }
        return strReturnValue;
    }

    @Override
    public void onListItemSelected(int requestCode, String key, int number) {
        switch (requestCode) {
            case 0:
                break;
            default:
                if (requestCode >= PLAYOUT_SEAT_START_REQUEST_CODE) {
                    SeatInfo sInfo = mLstSeatInfos.get(number);
                    Map<String, Object> map = mLstPLayoutInfos.get(requestCode - PLAYOUT_SEAT_START_REQUEST_CODE);
                    map.put(TT.SEAT_INFO, sInfo);
                    setSubmitButtonText();
                    mPLayoutAdpater.notifyDataSetChanged();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode >= PLAYOUT_P_INFO_START_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    PassengerInfo pInfo = (PassengerInfo) intent.getSerializableExtra(EditPassengerAty.EXTRA_PASSENGER_INFO);
                    for (int i = 0; i < mLstPLayoutInfos.size(); i++) {
                        Map<String, Object> map = mLstPLayoutInfos.get(i);
                        PassengerInfo pInfo1 = (PassengerInfo) map.get(TT.PASSENGER_INFO);
                        if (pInfo1.getNativeIndex() == pInfo.getNativeIndex()) {
                            Map<String, Object> map1 = new HashMap<String, Object>();
                            map1.put(TT.SEAT_INFO, map.get(TT.SEAT_INFO));
                            map1.put(TT.PASSENGER_INFO, pInfo);
                            mLstPLayoutInfos.set(i, map1);
                            break;
                        }
                    }
//					mLstPInfos.set(pInfo.getNativeIndex(), pInfo);
                    mPLayoutAdpater.notifyDataSetChanged();
                    setSubmitButtonText();
                } catch (Exception e) {
                    e.printStackTrace();
                    showMsg("返回信息时出错" + SF.FAIL);
                }
            }
        } else {
            switch (requestCode) {
                case REQUEST_LOGIN:
                    initFormValues();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onMultiChoiceItemSelected(View v, int requestCode, int whitch,
                                          boolean isChecked) {
        switch (requestCode) {
            case REQUEST_ADD_PLAYOUT:
                mSelectedStrPInfoIndex[whitch] = isChecked;
                break;
        }
    }

    @Override
    public void onMultiChoicePositiveButtonClicked(int requestCode) {
        switch (requestCode) {
            case REQUEST_ADD_PLAYOUT:
                addPLayout();
                break;
        }
    }

    @Override
    public void onMultiChoiceNagativeButtonClicked(int requestCode) {
        switch (requestCode) {
            case REQUEST_ADD_PLAYOUT:
                startActivity(new Intent(this, PassengerMangAty.class));
                break;
        }
    }

    private void initFormValues() {
        startHandle("初始化购票数据...", new Runnable() {
            public void run() {
                mIsInitedFormValues = false;
                String strHtml = A6Util.getConfirmPassengerHtml(mBInfo);
                String strSubmitToken = A6Util.getSubmitToken(strHtml);
                String key_check_isChange = A6Util.getKey_check_isChange(strHtml);
                String trainLocation = A6Util.getTrainLocation(strHtml);
                //不支持女宾包房与家庭包房
                boolean nvbbf = false, jtbf = false;
                String roomType = nvbbf ? "1" : "0";
                roomType += jtbf ? "1" : "0";
                //dwAll意义暂不明
                mBInfo.setDWAll("N");
                mBInfo.setOrderRoomType(roomType);
                if (strSubmitToken != null && key_check_isChange != null && trainLocation != null) {
                    mBInfo.setRepeatSubmitToken(strSubmitToken);
                    mBInfo.setKey_check_isChange(key_check_isChange);
                    mBInfo.setTrain_location(trainLocation);
                    L.d("submitToken:" + strSubmitToken + ", key_check_isChange:" + key_check_isChange + ", train_location:" + trainLocation);
                    mIsInitedFormValues = true;
                }
                //第一次请求错误也不提醒
                Message msg = mHandler.obtainMessage(A6Util.MSG_INIT_FORM_VALUES_FINISH);
                mHandler.sendMessage(msg);
            }
        });
    }

    private void setSubmitButtonText() {
        if (mLstSeatInfos == null) {
            return;
        }
        try {
            double sumPrice = 0;
            if (mYZPrice == -1) {
                for (SeatInfo sInfo : mLstSeatInfos) {
                    if (sInfo.getType() == SeatHelper.YZ) {
                        mYZPrice = Double.valueOf(sInfo.getPrice().substring(1));
                    }
                }
            }
            for (Map<String, Object> map : mLstPLayoutInfos) {
                SeatInfo sInfo = (SeatInfo) map.get(TT.SEAT_INFO);
                PassengerInfo pInfo = (PassengerInfo) map.get(TT.PASSENGER_INFO);
                if (pInfo.getPassenger_type_name().equals("学生")) {
                    sumPrice += mSHelper.getStuPrice(sInfo.getType(), Double.valueOf(sInfo.getPrice().substring(1)), mYZPrice);
                } else {
                    sumPrice += Double.valueOf(sInfo.getPrice().substring(1));
                }
            }
            DecimalFormat df1 = new DecimalFormat("#.0");  //只到角
            btnOK.setText("提交订单(约:￥" + df1.format(sumPrice) + ")");
        } catch (Exception e) {
            e.printStackTrace();
            btnOK.setText("提交订单(约:￥0)");
        }
    }

    public void onResume() {
        super.onResume();
        L.i("onResume()");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        L.i("onPause");
        MobclickAgent.onPause(this);
    }

    @Override
    public void doHeaderTask() {
    }

    @Override
    public void doFooterTask() {
    }

    @Override
    public void onRequestedRandCode(String strRandCode) {
        mBInfo.setOrderRandCode(strRandCode);
        checkOrderInfo();
    }
}
