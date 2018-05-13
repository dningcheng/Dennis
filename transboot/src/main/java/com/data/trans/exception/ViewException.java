package com.data.trans.exception;

import com.data.trans.common.ResponseEnum;

/**
 * @Date 2018年3月26日
 * @author dnc
 * @Description 返回视图异常
 */
public class ViewException extends AjaxException {
	
	private static final long serialVersionUID = 1L;
	
	private String viewName ="error"; //返回视图名称
	
	public ViewException(){}
	
	public ViewException(ResponseEnum msgenum,String viewName){
		if(msgenum != null){
			this.setCode(msgenum.getCode());
			this.setMessage(msgenum.getMessage());
		}
		this.viewName = viewName;
	}
	
	public ViewException(ResponseEnum msgenum,Object data,String viewName){
		this(msgenum,viewName);
		this.setData(data);
		this.viewName = viewName;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}
