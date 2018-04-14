package com.data.trans.common;
/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统统一消息码值枚举
 */
public enum ResponseEnum {
	SUCCESS(200,"操作成功!"),
	ERROR(500,"系统异常!"),
	FAILED(501,"操作失败!"),
	NOT_FIND(404,"未能识别!"),
	PARAM_ERR(600,"参数错误!"),
	AUTH_ERR(601,"无权操作!"),
	USER_UNKNOW(602,"用户不存在!"),
	PASS_UNMATCH(603,"密码错误!"),
	;
	private int code;
	private String message;
	
	ResponseEnum(int code,String message){
		this.code = code;
		this.message=message;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
