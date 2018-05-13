package com.data.trans.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.data.trans.model.Translog;
import com.data.trans.common.ApiResponse;
import com.data.trans.common.ResponseEnum;
import com.data.trans.exception.ViewException;
import com.data.trans.model.SystemUser;
import com.data.trans.service.SystemUserService;
import com.data.trans.service.TranslogService;
import com.data.trans.util.EncryptUtil;

import ch.qos.logback.core.net.SyslogOutputStream;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Controller
@RequestMapping("user/")
public class UserController {
	
	@Autowired
    private RedisTemplate redisTemplate;
	
	@Autowired
	private TranslogService translogService;
	
	@Autowired
	private SystemUserService systemUserService;
	
	@RequestMapping("index")
	public String login(Map<String,Object> map,SystemUser user,HttpServletRequest request){
		/*ListOperations<String,Integer> listOpera = redisTemplate.opsForList();*/
		//ValueOperations<String,Object> valueOpera = redisTemplate.opsForValue();
		
		/*JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),"www.seally.cn",6379,2000);  
		
		System.out.println(jedisPool.getResource().getDB());;*/
		
		/*SystemUser loginUser = (SystemUser)valueOpera.get("user");
		if(loginUser == null){
			System.out.println("不存在，存储缓存："+user.getName());
			valueOpera.set("user", loginUser, 20, TimeUnit.SECONDS);
		}else{
			System.out.println("存在，存储获取到："+loginUser.getName());
		}*/
		/*long begin = System.currentTimeMillis();
		for(int i=50000000;i<50010000;i++){
			listOpera.leftPush("testKey", i);
		}
		System.out.println("存储10000消耗时间："+(System.currentTimeMillis() - begin) +"ms");
		
		System.out.println("testKey[size]："+listOpera.size("testKey"));
		*/
		if(StringUtils.isEmpty(user.getAccount()) && StringUtils.isEmpty(user.getPassword())){
			throw new ViewException(ResponseEnum.USER_UNKNOW,user.getAccount(),"/login");
		}
		
		SystemUser loginUser = systemUserService.findOne(user);
		if(loginUser == null ){
			throw new ViewException(ResponseEnum.USER_UNKNOW,user.getAccount(),"/login");
		}
		//登陆成功
		if(loginUser.getPassword().equals(EncryptUtil.Encrypt(user.getPassword(), true))){
			
			List<Translog> logs = translogService.getTranslogList(null);
			
			map.put("logs", logs);
			request.getSession().setAttribute("loginUser",loginUser);
			return "index";
		}
		throw new ViewException(ResponseEnum.PASS_UNMATCH,user.getAccount(),"login");
	}
	
	@RequestMapping("main")
	public String main(Map<String,Object> map,SystemUser user){
		
		/*ValueOperations<String,Object> valueOpera = redisTemplate.opsForValue();
		
		User loginUser = (User)valueOpera.get("user");
		if(loginUser == null){
			System.out.println("不存在，存储缓存："+user.getUserName());
			valueOpera.set("user", user, 20, TimeUnit.SECONDS);
		}else{
			System.out.println("存在，存储获取到："+loginUser.getUserName());
		}*/
		
		List<Translog> logs = translogService.getTranslogList(null);
		
		map.put("logs", logs);
		
		return "trans";
	}
	
	
	@RequestMapping("list")
	public String userList(Map<String,Object> map,SystemUser user){
		systemUserService.findList(user);
		map.put("user", user);
		return "user/list";
	}
	
	@RequestMapping("edit")
	public String userEdit(Map<String,Object> map,SystemUser user){
		if(user.getId() != null){
			user = systemUserService.findOne(user);
			map.put("user", user);
		}
		return "user/edit";
	}
	
	@ResponseBody
	@RequestMapping("save")
	public ApiResponse<String> userSave(SystemUser user){
		if(user.getId() != null){//修改
			return systemUserService.update(user);
		}else{//新增
			return systemUserService.save(user);
		}
	}
	
	@ResponseBody
	@RequestMapping("delete")
	public ApiResponse<String> userDelete(SystemUser user){
		return systemUserService.delete(user);
	}
	
	
	@ResponseBody
	@RequestMapping("validRepeat")
	public ApiResponse<String> repeat(SystemUser user){
		return systemUserService.repeat(user);
	}
}
