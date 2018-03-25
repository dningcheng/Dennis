package com.data.trans.exception;

import com.data.trans.util.ResponseEnum;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统统一异常
 */
public class SystemException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private Integer code;
	private String message;
	private Object data;
	
	public SystemException(){}
	
	public SystemException(Integer code,String message){
		this.code = code;
		this.message = message;
	}
	
	public SystemException(Integer code,String message,Object data){
		this(code,message);
		this.message = message;
	}
	
	public SystemException(ResponseEnum msgenum){
		this(msgenum.getCode(),msgenum.getMessage());
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
