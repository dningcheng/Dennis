package com.seally.singleton;
/**
 * 线程安全的单例模式01：使用内部类维护单例，利用jvm内部机制保证单例的创建
 * @author Administrator
 *
 */
public class Singleton01 {
	
	private Singleton01(){};
	
	private static class SingletonFactory{
		private static Singleton01 instance = new Singleton01();
	}
	
	public static Singleton01 getinstance(){
		return SingletonFactory.instance;
	}
}
