package com.seally.utils;

public class ClientResult {
	
	private int code=200;
	
	private String msg;
	
	private String content;

	public ClientResult(){}
	
	public ClientResult(int code, String msg, String content) {
		this.code = code;
		this.msg = msg;
		this.content = content;
	}
	
	public static ClientResult success(String msg, String content){
		return new ClientResult(200,msg,content);
	}
	
	public static ClientResult error(int code, String msg){
		return new ClientResult(code,msg,null);
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
