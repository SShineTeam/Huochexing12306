package com.sshine.huochexing.chatroom;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.sshine.baidupush.server.BaiduPushTagsHelper;
import com.sshine.huochexing.LoginAty;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.chatroom.bean.ChatRoomInfo;
import com.sshine.huochexing.more.ChatSetupAty;
import com.sshine.huochexing.trainInfos.AddInfoAty;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.TrainInfoUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class ChatRoomAty extends SherlockFragmentActivity implements ISimpleDialogListener,ISimpleDialogCancelListener {
	private static final String TAG = "ChatRoomAty";
	private static final int REQUEST_ADD_INFO = 1;
	private ChatRoomFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;
    private boolean receiveMsg;
	private MenuItem receiveIcon;
	private MyDatabase myDB;
	private SQLiteDatabase db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_chat_room);
        //初始化数据库数据 在onDistory()中关闭
        myDB = new MyDatabase(this);
		db = myDB.getWritableDatabase();
		//通知开发者（与评委互动）
		/*BaiduPushAsyncTask pushTask = new BaiduPushAsyncTask();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDT = df.format(new Date());
		pushTask.sendTagNotify("打开聊天界面", nowDT+" 用户打开了聊天界面", "test");*/
        initActionBar();
        initViews();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	Log.i(TAG, "onCreateOptionsMenu() called");
    	receiveIcon = menu.add("接收");
    	if(receiveMsg){
    		receiveIcon.setIcon(R.drawable.chat_on);
    	}else{
    		receiveIcon.setIcon(R.drawable.chat_off);
    	}
    	receiveIcon.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	receiveIcon.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(receiveMsg){
					//receiveIcon.setIcon(R.drawable.chat_off);
					updataReceiveMsgTag(false);
				}else{
					//receiveIcon.setIcon(R.drawable.chat_on);
					updataReceiveMsgTag(true);
				}
				return true;
			}
		});
    	MenuItem miSetup = menu.add("设置");
    	miSetup.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    	miSetup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(ChatRoomAty.this, ChatSetupAty.class));
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
    /**
     * 更新此聊天室是否接受信息
     * @param isReceive 是否接收
     */
    protected void updataReceiveMsgTag(final boolean isReceive) {
    	//先更新服务器中的信息。
    	String opreateMsg = "正在关闭聊天室";
    	if(isReceive){
    		opreateMsg = "正在打开聊天室";
    	}
    	new MyTask(this,opreateMsg) {
    		
			@Override
			protected void onException(Exception e) {
				String errorMsg = "很抱歉，聊天室未能关闭"+SF.FAIL;
	    		if(isReceive){
	    			errorMsg = "很抱歉，聊天室未能打开"+SF.FAIL;
	    		}
				Toast.makeText(ChatRoomAty.this, errorMsg, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			protected void myOnPostExecute(Object result) {
				if((Boolean)result){
					// 1.更新adapter中List<ChatRoomInfo> cContents 的信息
			    	mAdapter.getData(mPager.getCurrentItem()).setReceiveMsg(isReceive);
			    	// 2.设置或删除 百度云推送 Tag
			    	//使用 frontiaPush实现
			    	String tag = mAdapter.getData(mPager.getCurrentItem()).getPushTag();
			    	BaiduPushTagsHelper pushHelper = MyApp.getInstance().getTagsHelper();
			    	if(isReceive){
			    		pushHelper.setTag(ChatRoomAty.this.getApplicationContext(),tag);
			    	}else{
			    		pushHelper.deleteTag(ChatRoomAty.this.getApplicationContext(),tag);
			    	}
			    	/*	使用 REST API 实现
			    	String userId = MyApplication.getInstance().getUserInfoSPUtil().getBaiduUserId();
			    	BaiduPushAsyncTask pushTask = new BaiduPushAsyncTask();
			    	if(isReceive){
			    		pushTask.setTag(tag, userId);
			    	}else{
			    		pushTask.deleteTag(tag, userId);
			    	}*/
			    	// 3.设置数据库UserTrainB表中ReceiveMsg字段
			    	if(!(mAdapter.getData(mPager.getCurrentItem()).getPushTag().equals("all"))){
			    		//非公共聊天室则更新本地数据库
			    		ContentValues cv = new ContentValues();
			    		cv.put("ReceiveMsg", isReceive);
			    		db.update("UserTrainB", cv, "T_id = ?", new String[]{tag});
			    	}
			    	// 4.设置 boolean receiveMsg
			    	receiveMsg = isReceive;
			    	//5.设置图标
			    	if(isReceive){
			    		receiveIcon.setIcon(R.drawable.chat_on);
			    	}else{
			    		receiveIcon.setIcon(R.drawable.chat_off);
			    	}
				}else{
					String errorMsg = "很抱歉，聊天室未能关闭"+SF.FAIL;
		    		if(isReceive){
		    			errorMsg = "很抱歉，聊天室未能打开"+SF.FAIL;
		    		}
					Toast.makeText(ChatRoomAty.this, errorMsg, Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			protected Object myDoInBackground(Object... params) throws Exception {
				//如果是公共聊天室则更改本地设置
				if(mAdapter.getData(mPager.getCurrentItem()).getPushTag().equals("all")){
					MyApp.getInstance().getSettingSPUtil().setReceivePublicChatroom(isReceive);
					
					return true;
				}
				
				//如果是添加的车次则更改服务器设置
				String isReceiveStr = "0";
				if(isReceive){
					isReceiveStr = "1";
				}
				
				String strJson1 = "{\"requestType\":\"updateChatStatus\",\"uid\":\""
						+ MyApp.getInstance().getUserInfoSPUtil().getUId()
						+ "\",\"tid\":\""
						+ mAdapter.getData(mPager.getCurrentItem()).getTrainNum()
						+ "\",\"isReceive\":\""
						+ isReceiveStr + "\"}";
				Log.i(TAG, strJson1);
				HttpUtil httpUtil = new HttpUtil();
				String strUrl = "http://huochexing.duapp.com/server/user_train.php";
				if (httpUtil.post(strUrl, strJson1)) {
					JSONObject jsonObj = new JSONObject(
							(String) httpUtil.getResponseStr());
					Log.i(TAG, "实时信息:" + jsonObj.toString());
					int intResultCode = jsonObj
							.getInt(HttpUtil.RESULT_CODE);
					if(intResultCode==1){
						return true;
					}
				}
				return false;
			}
		}.execute(this);
    	
	}

	/**
     *  取得房间列表 
     * @return 聊天室类集合
     */
	private List<ChatRoomInfo> getTrainInfo(){
		
    	List<ChatRoomInfo> cContents = new ArrayList<ChatRoomInfo>();
    	int userId = MyApp.getInstance().getUserInfoSPUtil().getUId();
		Cursor c = db.rawQuery("select T_id ,TravelName ,ReceiveMsg from UserTrainB where U_id = "+userId+" group by T_id order by _id DESC", null);
		while(c.moveToNext()){
			ChatRoomInfo  cContent =  new  ChatRoomInfo();
			cContent.setTrainNum(c.getString(c.getColumnIndex("T_id")));
			cContent.setTravelName(c.getString(c.getColumnIndex("TravelName")));
			//普通车次的推送tag与车次相同
			cContent.setPushTag(c.getString(c.getColumnIndex("T_id")));
			//TODO 因为添加车次时没有添加ReceiveMsg字段，暂时注释以下代码
			boolean receiveMsg = c.getInt(c.getColumnIndex("ReceiveMsg"))==1? true : false;
			cContent.setReceiveMsg(receiveMsg);
			cContents.add(cContent);
		}
		c.close();
		
		//添加公共聊天室
		ChatRoomInfo publicChatroom = new ChatRoomInfo();
		publicChatroom.setTrainNum("公共聊天室");
		publicChatroom.setPushTag("all");
		//是否接受公共聊天室信息
		publicChatroom.setReceiveMsg(MyApp.getInstance().getSettingSPUtil().isReceivePublicChatroom());
		cContents.add(publicChatroom);
    	return cContents;
    }

	private void initViews() {
		
		//如果没有登录，提示要登录
		if(!MyApp.getInstance().getUserInfoSPUtil().isLogin()){
			startActivity(new Intent(this, LoginAty.class));
			Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		//
		new MyTask(this,"正在更新车次…") {
			
			@Override
			protected void onException(Exception e) {
			}
			
			@Override
			protected void myOnPostExecute(Object result) {
				//得到车次信息
				List<ChatRoomInfo> cContents = getTrainInfo();
				if(cContents.isEmpty()){
					SimpleDialogFragment.createBuilder(ChatRoomAty.this, getSupportFragmentManager())
					.setTitle("提示")
					.setMessage("您当前没有添加任何车次，是否立即添加?")
					.setPositiveButtonText("好的")
					.setNegativeButtonText("算了")
					.setRequestCode(REQUEST_ADD_INFO)
					.show();
					
					return;
				}
				//ViewPager设置
				mAdapter = new ChatRoomFragmentAdapter(getSupportFragmentManager(), cContents);
		        mPager = (ViewPager)findViewById(R.id.pager);
		        mPager.setAdapter(mAdapter);
		        TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		        indicator.setViewPager(mPager);
		        mIndicator = indicator;
		        
		        //设置页面切换监听器，改变接收开关状态。
		        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		        	@Override
		        	public void onPageSelected(int position) {
		        		//设置此页面 接收按钮状态
		                receiveMsg = mAdapter.getData(mPager.getCurrentItem()).isReceiveMsg();
		        	}
		        	
		        	@Override
		        	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		        	}
		        	
		        	@Override
		        	public void onPageScrollStateChanged(int state) {
		        	}
		        });
		        
		        //设置当前页面
		        mPager.setCurrentItem(0);
		        //设置此页面 接收按钮状态
		        receiveMsg = mAdapter.getData(mPager.getCurrentItem()).isReceiveMsg();
		        //TODO 更改
			}
			
			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {
				if (MyApp.getInstance().getSettingSPUtil().isTravelFirstShow()) {
					//更新车次列表
					int result = TrainInfoUtil.updateUserTrainList(db);
					if(result == 1){
						MyApp.getInstance().getSettingSPUtil().setTravelFirstShow(false);
					}
					return result ; 
				}
				return 1;
			}
		}.execute();
		
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("车友聊天室");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		L.i("1onDestory");
		myDB.close();
		db.close();
		super.onDestroy();
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch(requestCode){
			case REQUEST_ADD_INFO:
				startActivity(new Intent(ChatRoomAty.this, AddInfoAty.class));
				this.finish();
				break;
		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_ADD_INFO:
			this.finish();
			break;
		}
	}

	@Override
	public void onCancelled(int requestCode) {
		this.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//未读消息数清零
		MyApp.getInstance().setNewMsgCount(0);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MyApp.getInstance().setNewMsgCount(0);
		MobclickAgent.onPause(this);
	}
}
