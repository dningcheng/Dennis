package com.seally.utils;

import java.util.UUID;

public class UUIDGenerator {
	
	public static String generate(){
		String uuid = UUID.randomUUID().toString(); //��ȡUUID��ת��ΪString����  
        uuid = uuid.replace("-", "");//��ΪUUID����Ϊ32λֻ������ʱ���ˡ�-�������Խ�����ȥ��Ϳ�  
        return uuid;
	}
}
