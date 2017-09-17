package com.seally.singleton;
/**
 * �̰߳�ȫ�ĵ���ģʽ01��ʹ���ڲ���ά������������jvm�ڲ����Ʊ�֤�����Ĵ���
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
