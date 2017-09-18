package com.seally.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.UUID;

public class UUIDGenerator {
	
	public static String generate(){
		String uuid = UUID.randomUUID().toString(); //��ȡUUID��ת��ΪString����  
        uuid = uuid.replace("-", "");//��ΪUUID����Ϊ32λֻ������ʱ���ˡ�-�������Խ�����ȥ��Ϳ�  
        return uuid;
	}
	static Properties properties=new Properties();
	static{
		InputStream resourceAsStream = UUIDGenerator.class.getClassLoader().getResourceAsStream("test.properties");
		try {
			properties.load(resourceAsStream);
			resourceAsStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateProperties(String key,String value){
		properties.setProperty(key, value);
		try {
			Writer out = new FileWriter(new File(UUIDGenerator.class.getResource("/").getPath()+"test.properties"));
			properties.store(out , "test update");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		UUIDGenerator.updateProperties("test", "nihao");
	}
}
