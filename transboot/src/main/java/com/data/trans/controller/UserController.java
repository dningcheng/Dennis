package com.data.trans.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.data.trans.model.Translog;
import com.data.trans.exception.ViewException;
import com.data.trans.model.SystemUser;
import com.data.trans.service.SystemUserService;
import com.data.trans.service.TranslogService;
import com.data.trans.util.EncryptUtil;
import com.data.trans.util.ResponseEnum;

@Controller
public class UserController {
	
	@Autowired
    private RedisTemplate redisTemplate;
	
	@Autowired
	private TranslogService translogService;
	
	@Autowired
	private SystemUserService systemUserService;
	
	@RequestMapping("/login")
	public String login(Map<String,Object> map,SystemUser user){
		
		/*ValueOperations<String,Object> valueOpera = redisTemplate.opsForValue();
		
		User loginUser = (User)valueOpera.get("user");
		if(loginUser == null){
			System.out.println("不存在，存储缓存："+user.getUserName());
			valueOpera.set("user", user, 20, TimeUnit.SECONDS);
		}else{
			System.out.println("存在，存储获取到："+loginUser.getUserName());
		}*/
		try {
			SystemUser loginUser = systemUserService.getSystemUser(user);
			if(loginUser == null ){
				throw new ViewException(ResponseEnum.USER_UNKNOW);
			}
			//登陆成功
			if(loginUser.getPassword().equals(EncryptUtil.Encrypt(user.getPassword(), true))){
				
				List<Translog> logs = translogService.getTranslogList(null);
				
				map.put("logs", logs);
				
				return "/index";
			}
			throw new ViewException(ResponseEnum.USER_UNKNOW);
		} catch (Exception e) {
			throw new ViewException(ResponseEnum.USER_UNKNOW);
		}
	}
	
}
