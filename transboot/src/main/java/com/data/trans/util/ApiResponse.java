package com.data.trans.util;
/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 统一接口返回
 */
public class ApiResponse<T> {
	
	private Integer code;
	private T result;
	
	public ApiResponse(){}
	
	public ApiResponse(Integer code, T result) {
		this.code = code;
		this.result = result;
	}
	
	public static <T> ApiResponse<T> success(T result) {
		return new ApiResponse<T>(ResponseEnum.SUCCESS.getCode(),result);
	}
	
	public static ApiResponse<String> success() {
		return new ApiResponse<String>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMessage());
	}
	
	public static <T> ApiResponse<T> error(T result) {
		return new ApiResponse<T>(ResponseEnum.ERROR.getCode(),result);
	}
	
	public static ApiResponse<String> error() {
		return new ApiResponse<String>(ResponseEnum.ERROR.getCode(),ResponseEnum.ERROR.getMessage());
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
}
