package com.seally.utils;

public class FileUtil {
	
	public static String parsePrefixName(String fileName){
		if(fileName==null)
			return null;
		String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
	    return prefix;
	}
}
