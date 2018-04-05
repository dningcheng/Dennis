package com.data.trans.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.data.trans.model.Translog;
import com.data.trans.exception.ViewException;
import com.data.trans.model.SystemUser;
import com.data.trans.service.SystemUserService;
import com.data.trans.service.TranslogService;
import com.data.trans.util.ApiResponse;
import com.data.trans.util.EncryptUtil;
import com.data.trans.util.ResponseEnum;

@Controller
@RequestMapping("user/")
public class UserController {
	
	@Autowired
    private RedisTemplate redisTemplate;
	
	@Autowired
	private TranslogService translogService;
	
	@Autowired
	private SystemUserService systemUserService;
	
	@RequestMapping("login")
	public String login(Map<String,Object> map,SystemUser user){
		
		/*ValueOperations<String,Object> valueOpera = redisTemplate.opsForValue();
		
		User loginUser = (User)valueOpera.get("user");
		if(loginUser == null){
			System.out.println("不存在，存储缓存："+user.getUserName());
			valueOpera.set("user", user, 20, TimeUnit.SECONDS);
		}else{
			System.out.println("存在，存储获取到："+loginUser.getUserName());
		}*/
		
		if(StringUtils.isEmpty(user.getAccount()) && StringUtils.isEmpty(user.getPassword())){
			throw new ViewException(ResponseEnum.USER_UNKNOW,user.getAccount(),"/login");
		}
		
		SystemUser loginUser = systemUserService.getSystemUser(user);
		if(loginUser == null ){
			throw new ViewException(ResponseEnum.USER_UNKNOW,user.getAccount(),"/login");
		}
		//登陆成功
		if(loginUser.getPassword().equals(EncryptUtil.Encrypt(user.getPassword(), true))){
			
			List<Translog> logs = translogService.getTranslogList(null);
			
			map.put("logs", logs);
			
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
		List<SystemUser> users = systemUserService.listSystemUser(user);
		map.put("users", users);
		return "user/list";
	}
	
	@RequestMapping("edit")
	public String userEdit(Map<String,Object> map,SystemUser user){
		if(user.getId() != null){
			user = systemUserService.getSystemUser(user);
			map.put("user", user);
		}
		return "user/edit";
	}
	
	@ResponseBody
	@RequestMapping("save")
	public ApiResponse<String> userSave(SystemUser user){
		if(user.getId() != null){//修改
			return systemUserService.updateSystemUser(user);
		}else{//新增
			return systemUserService.addSystemUser(user);
		}
	}
	
	@ResponseBody
	@RequestMapping("delete")
	public ApiResponse<String> userDelete(SystemUser user){
		return systemUserService.delSystemUser(user);
	}
	
}
