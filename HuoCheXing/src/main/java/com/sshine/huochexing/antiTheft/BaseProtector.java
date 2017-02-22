package com.sshine.huochexing.antiTheft;


public abstract class BaseProtector {
	private int id;
	private String name = "";
	private boolean isProtected;
	private boolean isAlarmTriggered;
	private boolean isNeedDelay;
	private boolean isVibrateConflict;
	private AntiTheftService mServiceContext;
	
	@SuppressWarnings("unused")
	private BaseProtector(){
	}
	
	public BaseProtector(int id, AntiTheftService context){
		this.setId(id);
		this.setmServiceContext(context);
	}
	
	public abstract boolean start(Object obj);
	public abstract boolean stop();
	
	public boolean isProtected() {
		return isProtected;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	public boolean isAlarmTriggered() {
		return isAlarmTriggered;
	}

	public void setAlarmTriggered(boolean isAlarmTriggered) {
		this.isAlarmTriggered = isAlarmTriggered;
	}

	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public AntiTheftService getmServiceContext() {
		return mServiceContext;
	}

	private void setmServiceContext(AntiTheftService mServiceContext) {
		this.mServiceContext = mServiceContext;
	}
	
	public void request(int requestType, String msg){
		mServiceContext.request(this, requestType, msg);
	}
	
	public void onProgress(int requestType, String msg){
		mServiceContext.mOnProgressListener.onProgress(requestType, msg);
	}
	public String getName(){
		return this.name;
	}

	void setName(String name) {
		this.name = name;
	}

	public boolean isNeedDelay() {
		return isNeedDelay;
	}

	void setNeedDelay(boolean isNeedDelay) {
		this.isNeedDelay = isNeedDelay;
	}

	public boolean isVibrateConflict() {
		return isVibrateConflict;
	}

	void setVibrateConflict(boolean isVibrateConflict) {
		this.isVibrateConflict = isVibrateConflict;
	}
}
