package com.data.trans.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.trans.util.TransManager;

@RestController
public class ComControlller {
	
	@Resource
	TransManager transManager;
	
	@RequestMapping("/start")
	public void start(){
		transManager.startTrans();
	}
}
