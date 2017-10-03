package com.seally.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.seally.entity.User;
import com.seally.service.UserServive;
import com.seally.utils.PageModule;

@Controller
public class UserController {
	
	@Resource
	UserServive userServive;
	
	@Resource
	HttpServletRequest request;
	
	@Resource
	HttpServletResponse response;
	
	@RequestMapping(value="/finduser")
	public String finduser(PageModule<User> pm,Integer curPage){
		userServive.findUser(pm);
		request.setAttribute("pm", pm);
		
		return "user_index";
	}
}
