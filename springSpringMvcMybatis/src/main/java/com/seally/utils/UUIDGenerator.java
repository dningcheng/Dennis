package com.seally.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class UUIDGenerator {
	
	public static String generate(){
		String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象  
        uuid = uuid.replace("-", "");//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可  
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
