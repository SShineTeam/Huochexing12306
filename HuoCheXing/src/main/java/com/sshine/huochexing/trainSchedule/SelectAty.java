package com.sshine.huochexing.trainSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class SelectAty extends FragmentActivity implements View.OnClickListener, ISimpleDialogListener{
	public static final String RESULT_KEY = "resultKey";
	public static final String RESULT_VALUE = "resultValue";
	public static final String SEARCH_TYPE = "searchType";
	public static final int SEARCH_STATION = 0;
	public static final int SEARCH_TRAIN_NUM = 1;
	public static final int REQUEST_CLEAR_STATIONS = 2;
	public static final int REQUEST_CLEAR_TRAIN_NUMS = 3;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	
	private EditText etSearch;
	private GridView mGridInfos;
	private MyDatabase myDB;
	private SimpleAdapter mAdapter;
	private List<Map<String, String>> mLstDatas = new ArrayList<Map<String,String>>();
	private int searchType;
	private Button btnResultText;
	private LinearLayout llyTrash;
	private DialogFragment dlgClearStations, dlgClearTraiNums;
	protected String strXunFeiResult;
	private RecognizerDialog recognizerDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aty_select);
		
		initViews();
	}

	private void initViews() {
		myDB = new MyDatabase(this);
		searchType = this.getIntent().getIntExtra(SEARCH_TYPE, 0);
		btnResultText = (Button)findViewById(R.id.result);
		btnResultText.setOnClickListener(this);
		Button btnVoice = (Button)findViewById(R.id.voice);
		btnVoice.setOnClickListener(this);
		llyTrash = (LinearLayout)findViewById(R.id.llyt1);
		llyTrash.setOnClickListener(this);
		etSearch = (EditText)findViewById(R.id.content);
		mGridInfos = (GridView)findViewById(R.id.gv1);
		
		setResult(RESULT_CANCELED, null);
		//加载数据
				etSearch.addTextChangedListener(new TextWatcher() {
					
					
					@Override
					public void afterTextChanged(Editable s) {
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						
					}
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before,
							int count) {
						llyTrash.setVisibility(View.GONE);
						switch(searchType){
						case SEARCH_STATION:
							notifyAdapterDataChanged(myDB.getStations(s, false));
							break;
						case SEARCH_TRAIN_NUM:
							btnResultText.setVisibility(View.VISIBLE);
							mGridInfos.setVisibility(View.GONE);
							btnResultText.setText(s.toString().toUpperCase(Locale.CHINA));
							break;
						}
					}
				});
				
		mLstDatas = new ArrayList<Map<String,String>>();
		mAdapter = new SimpleAdapter(this, mLstDatas, R.layout.item_select,
			new String[]{MyDatabase.KEY, MyDatabase.VALUE},
			new int[]{R.id.item_select_itemKey, R.id.item_select_itemValue});
		mGridInfos.setAdapter(mAdapter);
		switch(searchType){
		case SEARCH_STATION:
			notifyAdapterDataChanged(myDB.getStations("", true));
			break;
		case SEARCH_TRAIN_NUM:
			notifyAdapterDataChanged(myDB.getTrainNumsHistory());
			break;
		}
		mGridInfos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv1 = (TextView) view.findViewById(R.id.item_select_itemKey);
				TextView tv2 = (TextView) view.findViewById(R.id.item_select_itemValue);
				String strKey = tv1.getText().toString();
				String strValue = tv2.getText().toString();
				//确定返回数据
				Intent intent = new Intent();
				intent.putExtra(RESULT_KEY, strKey);
				intent.putExtra(RESULT_VALUE, strValue);
				SelectAty.this.setResult(RESULT_OK, intent);
				//存储历史
				switch(searchType){
				case SEARCH_STATION:
					myDB.updateHistory("Station", strKey, strValue);
					break;
				case SEARCH_TRAIN_NUM:
					myDB.updateHistory("TrainNum", strKey, strValue);
					break;
				}
				//结束当前Activity
				SelectAty.this.finish();
			}
		});
	}
	
	public void notifyAdapterDataChanged(List<Map<String, String>> list) {
		mLstDatas.clear();
		if (list != null){
			for(Map<String, String> map:list){
				mLstDatas.add(map);
			}
		}
		if (mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 创建讯飞语音提示框
	 */
	private void createXunFeiDialog(){
		strXunFeiResult = "";
		if (recognizerDialog == null){
			recognizerDialog = new RecognizerDialog(
					SelectAty.this, "appid=" + MyApp.XunFei_KEY);
			recognizerDialog.setEngine("sms", null, null);
			recognizerDialog.setListener(new RecognizerDialogListener() {
				@Override
				public void onResults(ArrayList<RecognizerResult> results,
						boolean arg1) {
					// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加.
					for (RecognizerResult r : results) {
						strXunFeiResult += r.text;
					}
				}

				@Override
				public void onEnd(SpeechError error) {
					//error为null表示会话成功，可在此处理text结果，error不为null，表示发生错误，对话框停留在错误页面
					if (error == null){
						if (strXunFeiResult.endsWith("。")){
							strXunFeiResult = strXunFeiResult.substring(0, strXunFeiResult.length()-1);
						}
						etSearch.setText(strXunFeiResult);
						etSearch.setSelection(strXunFeiResult.length());
					}
				}
			});
		}
		if (recognizerDialog == null){
			L.i("迅飞对话框未实例化");
			return;
		}
		recognizerDialog.show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myDB != null){
			myDB.closeDB();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.llyt1:
			switch(searchType){
			case SEARCH_STATION:
				dlgClearStations = SimpleDialogFragment.createBuilder(SelectAty.this, getSupportFragmentManager())
					.setTitle("清除历史记录")
					.setMessage("确认要清除历史记录吗?")
					.setRequestCode(REQUEST_CLEAR_STATIONS)
					.setPositiveButtonText("确定")
					.setNegativeButtonText("取消")
					.show();
				
				break;
			case SEARCH_TRAIN_NUM:
				dlgClearTraiNums = SimpleDialogFragment.createBuilder(SelectAty.this, getSupportFragmentManager())
						.setTitle("清除历史记录")
						.setMessage("确认要清除历史记录吗?")
						.setRequestCode(REQUEST_CLEAR_TRAIN_NUMS)
						.setPositiveButtonText("确定")
						.setNegativeButtonText("取消")
						.show();
				break;
			}
			notifyAdapterDataChanged(null);
			break;
		case R.id.result:
			String strResult = btnResultText.getText().toString();
			Intent intent = new Intent();
			intent.putExtra(RESULT_KEY, strResult);
			intent.putExtra(RESULT_VALUE, strResult);
			SelectAty.this.setResult(RESULT_OK, intent);
			//存储历史
			switch(searchType){
			case SEARCH_STATION:
				myDB.updateHistory("Station", strResult, strResult);
				break;
			case SEARCH_TRAIN_NUM:
				myDB.updateHistory("TrainNum", strResult, strResult);
				break;
			}
			//结束当前Activity			
			SelectAty.this.finish();
			break;
		case R.id.voice:
			//打开迅飞语音识别界面
			createXunFeiDialog();
			break;
		}
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch (requestCode) {
		case REQUEST_CLEAR_STATIONS:
			dlgClearStations.dismiss();
			myDB.clearHistory("Station");
			clearLastStations();
			break;
		case REQUEST_CLEAR_TRAIN_NUMS:
			dlgClearTraiNums.dismiss();
			myDB.clearHistory("TrainNum");
			clearLastTrainNum();
			break;
		}
	}

	private void clearLastTrainNum() {
		setSP.setLastTrainNumKey(null);
	}

	private void clearLastStations() {
		setSP.setLastFromStationKey(null);
		setSP.setLastFromStationTagStr(null);
		setSP.setLastToStationKey(null);
		setSP.setLastToStationTagStr(null);
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
