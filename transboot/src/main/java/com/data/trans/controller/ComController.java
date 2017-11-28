package com.data.trans.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ComController {
	
	@RequestMapping("/")
	public String index(Map<String,Object> map){
		
		map.put("tip", "你好，欢迎来到数据迁移中心！请执行开始");
		
		return "/index";
	}
	
}
