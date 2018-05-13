package com.data.trans.common;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 统一接口返回
 */
public class ApiResponse<T> {
	
	private Integer code;
	private String message;
	private T result;
	
	public ApiResponse(){}
	
	public ApiResponse(Integer code, String message, T result) {
		this.code = code;
		this.message = message;
		this.result = result;
	}
	
	public static ApiResponse<String> success() {
		return new ApiResponse<String>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage(),null);
	}
	
	public static <T> ApiResponse<T> success(T result) {
		return new ApiResponse<T>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage(),result);
	}
	
	public static ApiResponse<String> error() {
		return new ApiResponse<String>(ResponseEnum.ERROR.getCode(),ResponseEnum.ERROR.getMessage(),null);
	}
	
	public static ApiResponse<String> error(String message) {
		return new ApiResponse<String>(ResponseEnum.ERROR.getCode(),message,null);
	}
	
	public static <T> ApiResponse<T> response(ResponseEnum resp) {
		return new ApiResponse<T>(resp.getCode(),resp.getMessage(),null);
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

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
}
