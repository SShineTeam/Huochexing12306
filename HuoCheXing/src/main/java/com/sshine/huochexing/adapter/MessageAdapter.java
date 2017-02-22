package com.sshine.huochexing.adapter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.sshine.huochexing.R;
import com.sshine.huochexing.chatroom.bean.ChatMessageItem;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.TimeUtil;

public class MessageAdapter extends BaseAdapter {
	public static final int LEFT_ITEM = 0;
	public static final int RIGHT_ITEM = 1;
	
	private static  Pattern patter = Pattern.compile("\\[(\\S+?)\\]");
	
	private Context context;
	private List<ChatMessageItem> messageList;
	private LayoutInflater infalter;
	private Map<String, Integer> mFaceMap;
	
	public MessageAdapter(Context context,  List<ChatMessageItem> messageList, Map<String, Integer> faceMap) {
		this.context = context;
		this.messageList = messageList;
		mFaceMap = faceMap;
		this.infalter = LayoutInflater.from(context);
	}
	
	public void setMessageList(List<ChatMessageItem> messageList){
		this.messageList = messageList;
		notifyDataSetChanged();
	}
	
	public void updateMessage(ChatMessageItem message){
		this.messageList.add(message);
		notifyDataSetChanged();
	}

	
	@Override
	public int getItemViewType(int position) {
		ChatMessageItem item = messageList.get(position);
		boolean isCome = item.isComMeg();
		if(isCome){
			return LEFT_ITEM;
		}else{
			return RIGHT_ITEM;
		}
		
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessageItem item = messageList.get(position);
		ViewHolder holder ;
		if(convertView == null){
			holder = new ViewHolder();
			
			switch (getItemViewType(position)) {
			case LEFT_ITEM:
				convertView = infalter.inflate(R.layout.chat_item_left, null);
				holder.head = (ImageView) convertView.findViewById(R.id.item_icon);
				holder.msg = (TextView) convertView.findViewById(R.id.item_message_textview);
				holder.time = (TextView) convertView.findViewById(R.id.item_datetime_textview);
				holder.nickname = (TextView) convertView.findViewById(R.id.item_nickname_textview);
				holder.progressBar = (ProgressBar) convertView.findViewById(R.id.item_progressbar);
				break;
			case RIGHT_ITEM:
				convertView = infalter.inflate(R.layout.chat_item_right, null);
				holder.head = (ImageView) convertView.findViewById(R.id.item_icon);
				holder.msg = (TextView) convertView.findViewById(R.id.item_message_textview);
				holder.time = (TextView) convertView.findViewById(R.id.item_datetime_textview);
				holder.progressBar = (ProgressBar) convertView.findViewById(R.id.item_progressbar);
				break;
			}
			convertView.setTag(holder);
		}else{
			holder =  (ViewHolder) convertView.getTag();
		}
		if(messageList.get(position).isComMeg()){
			holder.nickname.setText(item.getNickName()+":");
		}
		try{
			holder.head.setBackgroundResource(Integer.valueOf(item.getHeadId()));
		}catch(Exception e){
			e.printStackTrace();
			holder.head.setBackgroundResource(R.drawable.head000);
		}
		holder.time.setText(TimeUtil.getChatTime(item.getTime()));
		holder.time.setVisibility(View.VISIBLE);
		
		holder.msg.setText(
				convertNormalStringToSpannableString(item.getMessage()),
				BufferType.SPANNABLE);
		//holder.msg.setText(item.getMessage());
		holder.progressBar.setVisibility(View.GONE);
		holder.progressBar.setProgress(50);
		return convertView;
	}
	
	/**
	 * 解析表情
	 * @param message 
	 * @return
	 */
	private CharSequence convertNormalStringToSpannableString(String message) {
		/* // Direct use of Pattern:
		 Pattern p = Pattern.compile("Hello, (\\S+)");
		 Matcher m = p.matcher(inputString);
		 while (m.find()) { // Find each match in turn; String can't do this.
		     String name = m.group(1); // Access a submatch group; String can't do this.
		 }*/
		/*//不知道是干嘛的。。
		SpannableString spannableString ;
		if(message.startsWith("[") && message.endsWith("]")){
			spannableString = SpannableString.valueOf(message +" ");
		}else{
			spannableString = SpannableString.valueOf(message);
		}*/
		SpannableString spannableString = SpannableString.valueOf(message);
		//匹配 表情 字符串
		Matcher matcher = patter.matcher(spannableString);
		while(matcher.find()){
			String keyStr = matcher.group(0);
			int start = matcher.start();
			int end = matcher.end();
			//过长就不是表情了 所以只检测长度小于8的字符串
			if(end - start <8){
				//判断是否有此表情
				if(mFaceMap.containsKey(keyStr)){
					int faceId = mFaceMap.get(keyStr);
					Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),faceId);
					if(bitmap != null){
						ImageSpan imageSpan = new ImageSpan(context, bitmap);
						spannableString.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
				
			}
		}
		return spannableString;
	}

	class ViewHolder{
		public ImageView head;
		public TextView time;
		public TextView msg;
		public TextView nickname;
		public ProgressBar progressBar;
	}

}
