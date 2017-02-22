//package com.sshine.huochexing.history;
//
//
//import java.io.IOException;
//import java.sql.Date;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.client.ClientProtocolException;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.baidu.mapapi.map.ItemizedOverlay;
//import com.baidu.mapapi.map.MapController;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.OverlayItem;
//import com.baidu.mapapi.map.PopupClickListener;
//import com.baidu.mapapi.map.PopupOverlay;
//import com.baidu.platform.comapi.basestruct.GeoPoint;
//import com.sshine.huochexing.LoginActivity;
//import com.sshine.huochexing.R;
//import com.sshine.huochexing.bean.HistoryInfo;
//import com.sshine.huochexing.utils.HttpUtil;
//import com.sshine.huochexing.utils.L;
//import com.sshine.huochexing.utils.MyApp;
//import com.sshine.huochexing.utils.SF;
//import com.umeng.analytics.MobclickAgent;
//
//import eu.inmite.android.lib.dialogs.ProgressDialogFragment;
//
//public class HistoryAty extends SherlockFragmentActivity {
//	private static final String TAG = "HistoryAty";
//	
//	private static final int MSG_RECEIVE_VERIFY_FAILED = -1;
//	private static final int MSG_RECEIVE_FAIL = 0;
//	private static final int MSG_RECEIVE_SUCCESS= 1;
//	
//	private String url = "http://huochexing.duapp.com/server/user_train.php";
//	private ActionBar actBar;
//	
//	private MapView mMapView = null;
//	private MapController mMapController;
//
//	private MyOverlay mOverlay;
//	private PopupOverlay   pop  = null;
//	private OverlayItem mCurItem;
//	
//	private double centerLongitude = 108.437183;
//    private double centerLatitude = 38.552581;
//	/**
//	 * 保存历史数据
//	 */
//	private List<HistoryInfo> historyList = new ArrayList<HistoryInfo>();
//	private MyTask myTask;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.aty_history);
//		initActionBar();
//		initViews();
//		myTask = new MyTask(this);
//		myTask.execute();
//		
//	}
//	private void initViews() {
//		//初始化地图空间
//		if(MyApp.getInstance().mBMapManager==null){
//			MyApp.getInstance().initEngineManager(getApplication());
//		}
//		mMapView = (MapView) this.findViewById(R.id.history_bmapView);
//         //获取地图控制器
//        mMapController = mMapView.getController();
//         // 设置地图是否响应点击事件  .
//        mMapController.enableClick(true);
//         //设置地图缩放级别
//        mMapController.setZoom((float) 4.561589);
//        //设置中心点
//        GeoPoint centerPoint =new GeoPoint((int)(centerLatitude*1E6),(int)(centerLongitude*1E6));
//        mMapController.setCenter(centerPoint);
//        //显示内置缩放控件
//        mMapView.setBuiltInZoomControls(true);
//        
//        //initOverlay(1,1);
//        
//        /**
//         * 创建一个popupoverlay
//         */
//        PopupClickListener popListener = new PopupClickListener(){
//
//			@Override
//			public void onClickedPopup(int index) {
//				
//			}
//        };
//        pop = new PopupOverlay(mMapView,popListener);
//	}
//	/**
//	 * 初始化覆盖物
//	 */
//	private void initOverlay(double longitude,double latitude) {
//		/**
//    	 * 创建自定义overlay
//    	 */
//         mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.map_xiaoqi),mMapView);	
//         /**
//          * 准备overlay 数据
//          */
//         GeoPoint p1 = new GeoPoint ((int)(latitude*1E6),(int)(longitude*1E6));
//         OverlayItem item1 = new OverlayItem(p1,"覆盖物1","");
//         /**
//          * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
//          */
//         item1.setMarker(getResources().getDrawable(R.drawable.map_xiaoqi));
//         mOverlay.addItem(item1);
//         mMapView.getOverlays().add(mOverlay);
//         mMapView.refresh();
//	}
//
//	private void initActionBar() {
//		actBar = getSupportActionBar();
//		actBar.hide();
//	}
//	
//  
//    /**
//     * 异步更新历史位置，并逐个显示在地图上
//     * @author Administrator
//     *
//     */
//    class MyTask extends AsyncTask<Object, Integer, Object>{
//    	private DialogFragment dlg ;
//    	public MyTask(FragmentActivity context) {
//			dlg = ProgressDialogFragment.createBuilder(context, context.getSupportFragmentManager())
//					.setMessage("正在加载历史数据")
//					.setTitle("提示")
//					.setCancelable(false)
//					.show();
//		}
//		@Override
//		protected Object doInBackground(Object... params) {
//			int uid = MyApp.getInstance().getUserInfoSPUtil().getUId();
//			String sessionCode = MyApp.getInstance().getUserInfoSPUtil().getSessionCode();
//			String jsonStr = "{\"requestType\":\"getHistoryTrain\",\"uid\":\"" + uid + "\",\"sessionCode\":\"" + sessionCode + "\"}";
//			L.i("请求:" + jsonStr);
//			try {
//				HttpUtil httpUtil = new HttpUtil();
//				if(httpUtil.post(url, jsonStr)){
//					L.i("getTravels结果:" + httpUtil.getResponseStr());
//					JSONObject jsonObj = new JSONObject(httpUtil.getResponseStr());
//					int intResultCode = jsonObj.getInt(HttpUtil.RESULT_CODE);
//					if(intResultCode == MSG_RECEIVE_VERIFY_FAILED){
//						return MSG_RECEIVE_VERIFY_FAILED;
//					}else if(intResultCode == MSG_RECEIVE_SUCCESS){
//						JSONArray  jsonArray = jsonObj.getJSONArray("historyTrain");
//						for(int i=0; i < jsonArray.length(); i++){
//							JSONObject currTrain = jsonArray.getJSONObject(i);
//							HistoryInfo historyItem = new HistoryInfo();
//							historyItem.setTrainNumber(currTrain.getString("trainNumber"));
//							historyItem.setTravelName(currTrain.getString("travelName"));
//							historyItem.setEndStation(currTrain.getString("endStation"));
//							historyItem.setEndLongitude(currTrain.getString("endLongitude"));
//							historyItem.setEndLatitude(currTrain.getString("endLatitude"));
//							long endTime = currTrain.getLong("endLatitude");
//							historyItem.setEndTime(new Date(endTime));
//							historyList.add(historyItem);
//						}
//					}
//					dlg.dismiss();
//					
//					for (int i = 0; i < historyList.size(); i++) {
//						if(isCancelled()){
//							break;
//						}
//						try {
//							Thread.sleep(800);
//							publishProgress(i);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//					
//				}
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Object result) {
//			super.onPostExecute(result);
//			if(result == null){
//				return;
//			}
//			int resultCode = (Integer)result;
//			if(resultCode==MSG_RECEIVE_VERIFY_FAILED){
//				//验证失败 需要重新登录
//				Toast.makeText(HistoryAty.this, ("您的身份已过期,请重新登录" + SF.FAIL), Toast.LENGTH_SHORT).show();
//				MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
//				Intent loginIntent = new Intent(HistoryAty.this, LoginActivity.class);
//				startActivity(loginIntent);
//				HistoryAty.this.finish();
//			}
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			super.onProgressUpdate(values);
//			int index = values[0];
//			if(index < historyList.size()){
//				HistoryInfo currHistory = historyList.get(index);
//				double longitude = Double.valueOf(currHistory.getEndLongitude());
//				double latitude = Double.valueOf(currHistory.getEndLatitude());
//				if(isCancelled()){
//					return;
//				}
//				initOverlay(longitude,latitude);
//			}
//		}
//		
//    	
//    }
//    
//    public class MyOverlay extends ItemizedOverlay{
//
//		private OverlayItem mCurItem;
//
//		public MyOverlay(Drawable defaultMarker, MapView mapView) {
//			super(defaultMarker, mapView);
//		}
//		@Override
//		public boolean onTap(int index) {
//			OverlayItem item = getItem(index);
//			/*popupText.setText(getItem(index).getTitle());
//			Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(popupInfo), };
//			pop.showPopup(bitMaps, item.getPoint(), 32);*/
//			return true;
//		}
//		
//		@Override
//		public boolean onTap(GeoPoint pt , MapView mMapView){
//			if (pop != null){
//                pop.hidePop();
//			}
//			return false;
//		}
//    	
//    }
//    
//    @Override
//    protected void onPause() {
//    	/**
//    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
//    	 */
//        mMapView.onPause();
//        if(myTask!=null){
//        	myTask.cancel(true);
//        }
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }
//    
//    @Override
//    protected void onResume() {
//    	/**
//    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
//    	 */
//        mMapView.onResume();
//        super.onResume();
//        MobclickAgent.onResume(this);
//    }
//    
//    @Override
//    protected void onDestroy() {
//    	/**
//    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
//    	 */
//        mMapView.destroy();
//        super.onDestroy();
//    }
//    
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//    	super.onSaveInstanceState(outState);
//    	mMapView.onSaveInstanceState(outState);
//    	
//    }
//    
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//    	super.onRestoreInstanceState(savedInstanceState);
//    	mMapView.onRestoreInstanceState(savedInstanceState);
//    }
//    public void test(View v){
//    	Log.i(TAG, "zoom Level"+mMapView.getZoomLevel());
//    }
//}
