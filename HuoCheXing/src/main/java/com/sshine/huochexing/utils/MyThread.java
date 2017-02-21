package com.sshine.huochexing.utils;

public abstract class MyThread extends Thread {
	private boolean suspend = false;
	private String control = "";
	public void setSupsend(boolean supsend){
		if (supsend){
			synchronized (control){
				try {
					control.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else{
			synchronized (control){
				control.notifyAll();
			}
		}
		this.suspend = supsend;
	}
	public boolean isSuspend(){
		return this.suspend;
	}
}
