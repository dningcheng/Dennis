package com.data.trans.util;

import java.util.UUID;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description java生成uuid
 */
public class UUidUtil {
	
	public static String generateUUid(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
}
