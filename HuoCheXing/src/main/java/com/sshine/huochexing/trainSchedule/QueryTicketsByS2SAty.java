//package com.sshine.huochexing.trainSchedule;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.view.ContextMenu;
//import android.view.ContextMenu.ContextMenuInfo;
//import android.view.View;
//import android.view.View.OnCreateContextMenuListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.AdapterContextMenuInfo;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.sshine.huochexing.R;
//import com.sshine.huochexing.base.BaseAty;
//import com.sshine.huochexing.bean.BookingInfo;
//import com.sshine.huochexing.bean.MonitorInfo;
//import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
//import com.sshine.huochexing.bean.QueryLeftNewInfo;
//import com.sshine.huochexing.bean.S2SQueryInfo;
//import com.sshine.huochexing.utils.A6Util;
//import com.sshine.huochexing.utils.MyApp;
//import com.sshine.huochexing.value.SF;
//
//public class QueryTicketsByS2SAty extends BaseAty {
//	private String strUrl = "http://huochexing.duapp.com/server/train_schedule.php";
//	public static final String EXTRA_P2PQUERYINFO = "extraInfo";
//	public static final String EXTRA_QUERYED_QLNINFOS = "extraQueryedQLNInfos";
//	
//	private S2SQueryInfo mS2SInfo;
//	private ListView lvTrains;
//	private List<QueryLeftNewInfo> mLstInfos;
//	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
//	private int intCurrDataPos;
//	private TextView tvDepartDate, tvQueryStatus,tvTrainTypeRange;
//	private ProgressBar pb1;
//	
//	protected void onCreate(android.os.Bundle savedInstanceState) {
//		setDisableLoadingView(true);
//		super.onCreate(savedInstanceState);
//		setTitle("查询中途票");
//		setContentView(R.layout.aty_query_tickets_by_s2s);
//		initViews();
//	};
//	
//	@SuppressWarnings("unchecked")
//	private void initViews() {
//		mS2SInfo = (S2SQueryInfo) getIntent().getSerializableExtra(EXTRA_P2PQUERYINFO);
//		if (mS2SInfo == null){
//			showMsg("调用出错");
//			return;
//		}
//		mLstInfos = (List<QueryLeftNewInfo>) getIntent().getSerializableExtra(EXTRA_QUERYED_QLNINFOS);
//		if (mLstInfos == null){
//			mLstInfos = new ArrayList<QueryLeftNewInfo>();
//		}
//		
//		pb1 = (ProgressBar)findViewById(R.id.query_tickets_by_s2s_pb1);
//		tvDepartDate = (TextView)findViewById(R.id.query_tickets_by_s2s_tvDapartDate);
//		tvQueryStatus = (TextView)findViewById(R.id.query_tickets_by_s2s_tvQueryStatus);
//		tvTrainTypeRange = (TextView)findViewById(R.id.query_tickets_by_s2s_tvTrainTypeRange);
//		lvTrains = (ListView) findViewById(R.id.query_tickets_by_s2s_lvTrains);
//		lvTrains.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				intCurrDataPos = position;
//				//取得对应对象
//				QueryLeftNewDTOInfo qlndInfo = ((QueryLeftNewInfo)mLstInfos.get(position)).getQueryLeftNewDTO();
//				if (!A6Util.isCanBooking()){
//					showDlg("23:00-07:00为12306系统维护时间，此段时间内不能购票哦" + SF.TIP);
//					return;
//				}
//				if (!qlndInfo.getCanWebBuy().equals("Y")){
//					showMsg("此趟车次不能预订" + SF.TIP);
//					return;
//				}
//				//TODO
////				checkUser();
//			}
//		});
//		// 右键菜单
//		lvTrains.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View v,
//					ContextMenuInfo menuInfo) {
//				intCurrDataPos = ((AdapterContextMenuInfo) menuInfo).position;
//				menu.setHeaderTitle("快捷操作");
//				menu.setHeaderIcon(R.drawable.setup);
//				menu.add(0, 0, 0, "添加车次");
//				menu.add(0, 1, 1, "查看车次详情");
//			}
//		});
//		requestData();
//	}
//	
//	private void requestData() {
//		switch(mS2SInfo.getQuery_mode()){
//		case S2SQueryInfo.MODE_QUERY_HALF_WAY_TICKETS:
//			queryHalfWayTickets();
//			break;
//		case S2SQueryInfo.MODE_QUERY_TRAINSIT_TICKETS:
//			break;
//		}
//	}
//
//	/**
//	 * 搜索半途票
//	 */
//	private void queryHalfWayTickets() {
//		prepareFirstBatchOfMInfos();
//		startHandle(new Runnable(){
//			@Override
//			public void run() {
//			}
//		});
//	}
//
//	private void prepareFirstBatchOfMInfos() {
//		MonitorInfo mInfo = new MonitorInfo();
//		mInfo.setFrom_station_name(mS2SInfo.getFrom_station_name());
//		mInfo.setFrom_station_telecode(mS2SInfo.getFrom_station_telecode());
//		mInfo.setTo_station_name(mS2SInfo.getTo_station_name());
//		mInfo.setTo_station_telecode(mS2SInfo.getTo_station_telecode());
//	}
//
//	@Override
//	public void doHeaderTask() {
//	}
//
//	@Override
//	public void doFooterTask() {
//	}
//
//}
