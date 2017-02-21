package com.sshine.huochexing.chatroom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.sshine.baidupush.client.PushMessageReceiver;
import com.sshine.baidupush.server.BaiduPushAsyncTask;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.FaceAdapter;
import com.sshine.huochexing.adapter.MessageAdapter;
import com.sshine.huochexing.chatroom.bean.ChatMessage;
import com.sshine.huochexing.chatroom.bean.ChatMessageItem;
import com.sshine.huochexing.chatroom.bean.ChatRoomInfo;
import com.sshine.huochexing.chatroom.view.MsgListView;
import com.sshine.huochexing.chatroom.view.MsgListView.IXListViewListener;
import com.sshine.huochexing.interfaces.EventHandler;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MessageDB;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.TT;

public class ChatRoomFragment extends Fragment implements OnClickListener, OnTouchListener, IXListViewListener, EventHandler {
    private static final String KEY_CONTENT = "TrainInfoFragment:Content";  //保存状态用
    //控件
	private MsgListView msgListView;
	private Button sendBtn;
	private ImageButton faceBtn;
	private EditText messageEditText;
	//表情
	private GridView faceGV;
	private List<String> faceKeysList = new ArrayList<String>();
	private Map<String, Integer> mFacemap = null;
	private boolean isFaceShow;		//标示表情是否显示
	
    public ChatRoomInfo cContent;   //内容数据
    
    private MessageDB messageDB;
	private UserInfoSPUtil userInfoSPUtil;
	
	private InputMethodManager inputMethodManager;
	private MessageAdapter messageAdapter;
	private LayoutParams layoutParams;
	private static int MsgPagerNum;
	
	private String trainId;
	private String chatRoomTag;
	
    
    public static ChatRoomFragment newInstance(ChatRoomInfo cContent1) {
    	ChatRoomFragment fragment = new ChatRoomFragment();
    	
    	fragment.cContent = cContent1;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
        	cContent = (ChatRoomInfo) savedInstanceState.getSerializable(KEY_CONTENT);
        }
        initData();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.fragment_chat_room, null);
    	this.setHasOptionsMenu(true);
    	
    	//初始化界面 查找控件并绑定事件.
    	initViews(v);
    	return v;
    }
	
	/**
     * 初始化数据
     */
    private void initData() {
    	trainId = cContent.getTrainNum();
    	chatRoomTag = cContent.getPushTag();
		messageDB = MyApp.getInstance().getMessageDB();
		userInfoSPUtil = MyApp.getInstance().getUserInfoSPUtil();
		//初始化表情文字
		mFacemap = TT.getFaceMap();
		faceKeysList.addAll(mFacemap.keySet());
		messageAdapter = new MessageAdapter(getActivity(), messageDB.getMessage(trainId,MsgPagerNum), mFacemap);
		//检查push服务是否开启
		if(!PushManager.isPushEnabled(getActivity())){
			PushManager.startWork(getActivity(),
					PushConstants.LOGIN_TYPE_API_KEY,
					MyApp.API_KEY);
		}
		
		
	}
    /**
     * 初始化控件
     * @param v 
     */
    private void initViews(View v) {
    	inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		layoutParams = getActivity().getWindow().getAttributes();
    	
    	msgListView = (MsgListView) v.findViewById(R.id.msg_listView);
		//触摸ListView隐藏输入法和头像
		msgListView.setOnTouchListener(this);
		//禁止上拉加载
		msgListView.setPullLoadEnable(false);
		msgListView.setXListViewListener(this);
		msgListView.setAdapter(messageAdapter);
		if(messageAdapter.getCount()>0){
			msgListView.setSelection(messageAdapter.getCount() - 1);
		}
		
		sendBtn = (Button) v.findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(this);
		//表情相关控件
		faceBtn = (ImageButton) v.findViewById(R.id.face_btn);
		faceBtn.setOnClickListener(this);
		faceGV = (GridView) v.findViewById(R.id.face_gv);
		faceGV.setAdapter(new FaceAdapter(getActivity(), mFacemap));
		faceGV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), (Integer) mFacemap.values().toArray()[position]);
				if(bitmap != null){
					int rawHeigh = bitmap.getHeight();
					int rawWidth = bitmap.getHeight();
					int newHeight = 40;
					int newWidth = 40;
					// 计算缩放因子
					float heightScale = ((float) newHeight) / rawHeigh;
					float widthScale = ((float) newWidth) / rawWidth;
					// 新建立矩阵
					Matrix matrix = new Matrix();
					matrix.postScale(heightScale, widthScale);
					
					Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
							rawWidth, rawHeigh, matrix, true);
					ImageSpan imageSpan = new ImageSpan(getActivity(), newBitmap);
					String keyStr = faceKeysList.get(position);
					SpannableString spannableString = new SpannableString(keyStr);
					spannableString.setSpan(imageSpan, keyStr.indexOf('['), keyStr.indexOf(']')+1
							, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					messageEditText.append(spannableString);
				}else{
					String msgStr = messageEditText.getText().toString();
					int insertIndex = messageEditText.getSelectionStart();
					StringBuilder msgSB = new StringBuilder(msgStr);
					msgSB.insert(insertIndex, faceKeysList.get(position));
					messageEditText.setText(msgSB);
					messageEditText.setSelection(insertIndex + faceKeysList.get(position).length());
				}
				
			}
		});
		faceGV.setVisibility(View.GONE);
		
		
		messageEditText = (EditText) v.findViewById(R.id.msg_et);
		messageEditText.setOnTouchListener(this);
		messageEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (layoutParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
							|| isFaceShow) {
						faceGV.setVisibility(View.GONE);
						isFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		messageEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0){
					sendBtn.setEnabled(true);
				}else{
					sendBtn.setEnabled(false);
				}
			}
		});
    }

	@Override
	public void onPause() {
    	Log.i("FragmentLife", cContent.getTrainNum()+": onPause()");
    	PushMessageReceiver.ehList.remove(this);
		super.onPause();
	}


	@Override
	public void onResume() {
		Log.i("FragmentLife", cContent.getTrainNum()+":onResume()");
		PushMessageReceiver.ehList.add(this);
		messageAdapter.setMessageList(messageDB.getMessage(trainId,MsgPagerNum));
		super.onResume();
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_CONTENT, cContent);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_btn://发送消息
			if(!cContent.isReceiveMsg()){//当前聊天室已关闭
				Toast.makeText(MyApp.getInstance(), "请先打开此聊天室"+SF.SUCCESS, Toast.LENGTH_LONG).show();
				return;
			}
			if(!HttpUtil.isNetworkConnected(MyApp.getInstance())){//如果网络可用
				Toast.makeText(MyApp.getInstance(), "网络不可用，请连接网络", Toast.LENGTH_LONG).show();
				return;
			}
			String message = messageEditText.getText().toString();
			ChatMessageItem messageItem = new ChatMessageItem(ChatMessageItem.MESSAGE_TYPE_TEXT, userInfoSPUtil.getUId(), 
					trainId, userInfoSPUtil.getNickName(), System.currentTimeMillis(), message, userInfoSPUtil.getHeadIcon(), false);
			messageAdapter.updateMessage(messageItem);
			msgListView.setSelection(messageAdapter.getCount() - 1);
			messageDB.saveMessage(messageItem);
			messageEditText.setText("");
			//向服务器发送
			/*ChatMessage chatMessage = new ChatMessage(spUtil.getUserId(), spUtil.getChannelId(), 
									spUtil.getNickName(), System.currentTimeMillis(), message, 0);*/
			ChatMessage chatMessage = new ChatMessage(userInfoSPUtil.getUId(),userInfoSPUtil.getBaiduUserId(), userInfoSPUtil.getBaiduChannelId(), 
					trainId, userInfoSPUtil.getNickName(),userInfoSPUtil.getHeadIcon(), System.currentTimeMillis(), message, 0);
			String jsonMessage= A6Util.getGson().toJson(chatMessage);
			new BaiduPushAsyncTask().sendTagMessage(jsonMessage, chatRoomTag);
			break;

		case R.id.face_btn:
			if(!isFaceShow){
				inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
				faceGV.setVisibility(View.VISIBLE);
				isFaceShow = true;
			}else{
				faceGV.setVisibility(View.GONE);
				isFaceShow = false;
			}
			break;
		}
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.msg_listView:
			inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken()	, 0);
			faceGV.setVisibility(View.GONE);
			isFaceShow = false;
			break;

		case R.id.msg_et:
			inputMethodManager.showSoftInput(messageEditText, 0);
			faceGV.setVisibility(View.GONE);
			isFaceShow = false;
			break;
		}
		return false;
	}


	@Override
	public void onRefresh() {
		MsgPagerNum++;
		List<ChatMessageItem> list =  messageDB.getMessage(trainId, MsgPagerNum);
		int position = messageAdapter.getCount();
		messageAdapter.setMessageList(list);
		msgListView.stopRefresh();
		msgListView.setSelection(messageAdapter.getCount()-position-1);
		
	}


	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onMessage(ChatMessage chatMessage, ChatMessageItem messageItem) {
		//TODO  还需要斟酌一下
		//如果不是本车次  则忽略此信息。
		if (messageItem.getTrainId().equals(trainId)) {
			messageAdapter.updateMessage(messageItem);
		}
	}
	@Override
	public void onBind(String method, int errorCode, String content) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onNotify(String title, String content) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
	}

	
}
