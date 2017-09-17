package com.seally.singleton;

public class Singleton02 {
	
	private static Singleton02 instance=null;
	
	private Singleton02(){}
	
	private synchronized static void syncInit(){
		if(instance==null){
			instance=new Singleton02();
		}
	}
	
	public static Singleton02 getInstance(){
		if(instance==null){
			syncInit();
		}
		return instance;
	}
}
