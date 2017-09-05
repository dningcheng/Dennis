package com.seally.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.seally.dao.UserDao;
import com.seally.service.UserServive;

@Service
public class UserServiceImpl implements UserServive {
	
	@Resource
	UserDao userDao;
}
