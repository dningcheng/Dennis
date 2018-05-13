package com.data.trans.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.data.trans.model.SystemUser;

public class LoginInterceptor implements HandlerInterceptor {
	
	/**
	 * 简单判断session是否存在登陆用户，存在及放行，不存在则重定向到登陆页面
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
        SystemUser loginUser=(SystemUser)request.getSession().getAttribute("loginUser");
        
        if(null==loginUser){
            response.sendRedirect("/");
            return false;
        }else{
            return true;
        }
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
