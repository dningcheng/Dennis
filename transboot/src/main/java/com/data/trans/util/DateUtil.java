package com.data.trans.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author dnc
 * 2018年1月11日
 *
 */
public class DateUtil {
	/**
	 * @Date 2017年12月13日
	 * @author dnc
	 * @Description 计算指定格式sourcePatten时间字符串对指定调整字段（年/月/日/时/分/秒/毫秒）加/减offsetValue后的指定格式returnPatten新日期字符串，日期格式错误抛出异常
	 * @param sourcePatten 传入日期字符串格式如：yyyy-MM-dd HH:mm:ss
	 * @param returnPatten 返回日期字符串格式如：yyyy-MM-dd HH:mm:ss
	 * @param sourceDate  传入日期字符串
	 * @param calcType 需要加减的调整字段建议使用Calendar常量字段如：Calendar.MINUTE 分钟
	 * @param offsetValue  >0：加  <0：减 
	 * @return returnPatten格式话后的字符串
	 * @throws ParseException 
	 */
	public static String adjustDateString(String sourcePatten,String returnPatten,String sourceDate,int calcType,int offsetValue) throws ParseException{ 
		if(sourceDate==null || offsetValue==0){
			return sourceDate;
		}
		return new SimpleDateFormat(returnPatten).format(adjustDate(sourcePatten,sourceDate,calcType,offsetValue));
	}
	/**
	 * @Date 2017年12月13日
	 * @author dnc
	 * @Description 计算指定格式sourcePatten时间字符串对指定调整字段（年/月/日/时/分/秒/毫秒）加/减offsetValue后的新日期，日期格式错误抛出异常
	 * @param sourcePatten 传入日期字符串格式如：yyyy-MM-dd HH:mm:ss
	 * @param sourceDate  传入日期字符串
	 * @param calcType 需要加减的调整字段建议使用Calendar常量字段如：Calendar.MINUTE 分钟
	 * @param offsetValue  >0：加  <0：减 
	 * @return Date
	 * @throws ParseException 
	 */
	public static Date adjustDate(String sourcePatten,String sourceDate,int calcType,int offsetValue) throws ParseException{ 
		if(sourceDate==null)	return null;
		Date date = new SimpleDateFormat(sourcePatten).parse(sourceDate);
		Calendar instance = Calendar.getInstance();
		instance.setTime(date);
		instance.add(calcType,offsetValue);
		return instance.getTime();
	}
	
	 /**
	 * @Date 2017年12月13日
	 * @author dnc
	 * @Description 计算指定时间指定调整字段（年/月/日/时/分/秒/毫秒）加/减offsetValue后的指定格式returnPatten新日期字符串，日期格式错误抛出异常
	 * @param returnPatten 返回日期字符串格式如：yyyy-MM-dd HH:mm:ss
	 * @param sourceDate 传入计算的日期
	 * @param calcType 需要加减的调整字段建议使用Calendar常量字段如：Calendar.MINUTE 分钟
	 * @param offsetValue  >0：加  <0：减 
	 * @return returnPatten格式话后的字符串
	 * @throws ParseException 
	 */
	public static String adjustDateString(String returnPatten,Date sourceDate,int calcType,int offsetValue){
		if(sourceDate==null)	return null;
		return new SimpleDateFormat(returnPatten).format(adjustDate(sourceDate,calcType,offsetValue));
	}
	/**
	 * @Date 2017年12月13日
	 * @author dnc
	 * @Description 计算指定时间指定调整字段（年/月/日/时/分/秒/毫秒）加/减offsetValue后的新日期，日期格式错误抛出异常
	 * @param sourceDate 传入计算的日期
	 * @param calcType 需要加减的调整字段建议使用Calendar常量字段如：Calendar.MINUTE 分钟
	 * @param offsetValue  >0：加  <0：减 
	 * @return Date
	 * @throws ParseException 
	 */
	public static Date adjustDate(Date sourceDate,int calcType,int offsetValue){
		if(sourceDate==null)	return null;
		Calendar instance = Calendar.getInstance();
		instance.setTime(sourceDate);
		instance.add(calcType,offsetValue);
		return instance.getTime();
	}
}
