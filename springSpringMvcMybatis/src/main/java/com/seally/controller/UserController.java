package com.seally.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seally.service.UserServive;
import com.seally.utils.ClientResult;

@RestController
public class UserController {
	
	@Resource
	UserServive userServive;
	
	@RequestMapping(value="/upimg")
	public String upImg(HttpServletRequest request,HttpServletResponse response){
		
		ClientResult result = userServive.handRequest(request,response);
		
		return "success";
	}
}
