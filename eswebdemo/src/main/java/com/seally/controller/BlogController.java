package com.seally.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.seally.entity.Plog;
import com.seally.service.UserServive;
import com.seally.utils.PageModule;

@Controller
public class BlogController {
	
	@Resource
	UserServive userServive;
	
	@Resource
	HttpServletRequest request;
	
	@Resource
	HttpServletResponse response;
	
	@RequestMapping(value="/login")
	public String login(PageModule<Plog> pm){
		userServive.findPlog(pm);
		request.setAttribute("pm", pm);
		
		return "blog_index";
	}
	
	@RequestMapping(value="/findblog")
	public String findblog(PageModule<Plog> pm){
		userServive.findPlog(pm);
		request.setAttribute("pm", pm);
		
		return "blog_index";
	}
}
