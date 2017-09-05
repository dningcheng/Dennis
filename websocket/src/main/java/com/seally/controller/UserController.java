package com.seally.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.seally.service.UserServive;

@Controller
public class UserController {
	
	@Resource
	UserServive userServive;
}
