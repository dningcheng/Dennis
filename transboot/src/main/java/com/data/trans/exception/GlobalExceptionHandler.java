package com.data.trans.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.data.trans.common.ApiResponse;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 统一异常捕获处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
    /**
     * @Date 2018年3月26日
     * @author dnc
     * @Description 如果时返回视图出现异常，抛出ViewException 并在这里捕获
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = ViewException.class)  
    public ModelAndView businessExceptionHandler(HttpServletRequest req, ViewException e) throws Exception {  
        ModelAndView view = new ModelAndView();
        view.addObject("code", e.getCode());  
        view.addObject("message", e.getMessage()); 
        view.addObject("data", e.getData()); 
        view.setViewName(e.getViewName());  
        return view;  
    }  
      
    /**
     * @Date 2018年3月26日
     * @author dnc
     * @Description 如果ajax请求数据出现异常，抛出AjaxException 并在这里捕获
     * @param req
     * @param e
     * @return
     */
    @ResponseBody  
    @ExceptionHandler(value = AjaxException.class)  
    public ApiResponse<Object> jsonExceptionHandler(HttpServletRequest req, AjaxException e) {  
        
    	return new ApiResponse<Object>(e.getCode(),e.getMessage(),e.getData());
    	
    }
}
