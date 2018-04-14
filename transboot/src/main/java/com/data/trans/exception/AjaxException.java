package com.data.trans.exception;

import com.data.trans.common.ResponseEnum;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统统ajax调用异常
 */
public class AjaxException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private Integer code;
	private String message;
	private Object data;
	
	public AjaxException(){}
	
	public AjaxException(Integer code,String message){
		this.code = code;
		this.message = message;
	}
	
	public AjaxException(Integer code,String message,Object data){
		this(code,message);
		this.message = message;
	}
	
	public AjaxException(ResponseEnum msgenum){
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
