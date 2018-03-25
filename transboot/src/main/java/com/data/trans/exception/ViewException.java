package com.data.trans.exception;

import com.data.trans.util.ResponseEnum;

/**
 * @Date 2018年3月26日
 * @author dnc
 * @Description 返回视图异常
 */
public class ViewException extends AjaxException {
	
	public ViewException(){}
	
	public ViewException(ResponseEnum msgenum){
		this.setCode(msgenum.getCode());
		this.setMessage(msgenum.getMessage());
	}
	
	private static final long serialVersionUID = 1L;
}
