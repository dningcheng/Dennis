package com.seally.utils;

import java.io.IOException;
import java.io.InputStream;
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
}
