package com.data.trans.controller;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.trans.jdbc.MysqlUtil;

@RestController
public class ComControlller {
	
	@Resource
	MysqlUtil sqlutil;
	
	@RequestMapping("/start")
	public void start(){
		try {
			sqlutil.test();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
