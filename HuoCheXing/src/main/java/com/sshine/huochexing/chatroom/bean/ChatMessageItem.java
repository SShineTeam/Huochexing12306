package com.sshine.huochexing.chatroom.bean;

public class ChatMessageItem {
	    // Text
		public static final int MESSAGE_TYPE_TEXT = 1;
		// image
		public static final int MESSAGE_TYPE_IMG = 2;
		// file
		public static final int MESSAGE_TYPE_FILE = 3;

		private int msgType;
		private int userId;
		private String trainId;
		private String nickName;//昵称
		private long time;// 时间
		private String message;// 内容
		private String headId;
		private boolean isComMeg = true;

		
		public ChatMessageItem(){}
		

		public ChatMessageItem(int msgType, int userId, String trainId,
				String nickName, long time, String message, String icon,
				boolean isComMeg) {
			super();
			this.msgType = msgType;
			this.userId = userId;
			this.trainId = trainId;
			this.nickName = nickName;
			this.time = time;
			this.message = message;
			this.headId = icon;
			this.isComMeg = isComMeg;
		}


		public int getMsgType() {
			return msgType;
		}

		public void setMsgType(int msgType) {
			this.msgType = msgType;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getTrainId() {
			return trainId;
		}

		public void setTrainId(String trainId) {
			this.trainId = trainId;
		}

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getHeadId() {
			return headId;
		}


		public void setHeadId(String headId) {
			this.headId = headId;
		}


		public boolean isComMeg() {
			return isComMeg;
		}

		public void setComMeg(boolean isComMeg) {
			this.isComMeg = isComMeg;
		}

	
		public static int getMessageTypeText() {
			return MESSAGE_TYPE_TEXT;
		}

		public static int getMessageTypeImg() {
			return MESSAGE_TYPE_IMG;
		}

		public static int getMessageTypeFile() {
			return MESSAGE_TYPE_FILE;
		}

		@Override
		public String toString() {
			return "ChatMessageItem [msgType=" + msgType + ", userId=" + userId
					+ ", trainId=" + trainId + ", nickName=" + nickName
					+ ", time=" + time + ", message=" + message + ", headPath="
					+ headId + ", isComMeg=" + isComMeg + "]";
		}

		
		
}
