package com.data.trans.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.data.trans.exception.AjaxException;
import com.data.trans.mapper.SystemUserMapper;
import com.data.trans.model.SystemUser;
import com.data.trans.service.SystemUserService;
import com.data.trans.util.ApiResponse;
import com.data.trans.util.EncryptUtil;
import com.data.trans.util.ResponseEnum;
import com.data.trans.util.UUidUtil;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统用户相关
 */
@Service
public class SystemUserServiceImpl implements SystemUserService {
	
	@Autowired
	private SystemUserMapper systemUserMapper;
	
	@Override
	public ApiResponse<String> addSystemUser(SystemUser model) {
		
		String account = model.getAccount();
		String password = model.getPassword();
		
		if(StringUtils.isEmpty(account) || StringUtils.isEmpty(password)){
			throw new AjaxException(ResponseEnum.PARAM_ERR);
		}
		//密码加密存储
		model.setPassword(EncryptUtil.Encrypt(model.getPassword().trim(),true));
		//账号去空格
		model.setAccount(model.getAccount().trim());
		//uuid生成用户唯一标识
		model.setUserId(UUidUtil.generateUUid());
		
		return systemUserMapper.addSystemUser(model)==1?ApiResponse.success():ApiResponse.response(ResponseEnum.FAILED);
	}

	@Override
	public ApiResponse<String> delSystemUser(SystemUser model) {
		//没有传递唯一身份标识抛异常，防止账号全删操作
		checkSafeOpt(model);
		
		return systemUserMapper.delSystemUser(model)==1?ApiResponse.success():ApiResponse.response(ResponseEnum.FAILED);
	}

	@Override
	public ApiResponse<String> updateSystemUser(SystemUser model) {
		
		//没有传递唯一身份标识抛异常，防止账号全改操作
		checkSafeOpt(model);
		
		return systemUserMapper.updateSystemUser(model)==1?ApiResponse.success():ApiResponse.response(ResponseEnum.FAILED);
	}

	@Override
	public SystemUser getSystemUser(SystemUser model) {
		//没有传递唯一身份标识抛异常，防止查出多个账号
		checkSafeOpt(model);
		SystemUser user = systemUserMapper.getSystemUser(model);
		return user;
	}

	@Override
	public List<SystemUser> listSystemUser(SystemUser model) {
		
		return systemUserMapper.listSystemUser(model);
	}
	
	private void checkSafeOpt(SystemUser model){
		//没有传递唯一身份标识抛异常，防止账号全删/全改操作
		if(StringUtils.isEmpty(model.getId()) &&
				StringUtils.isEmpty(model.getUserId()) &&
				StringUtils.isEmpty(model.getAccount()) &&
				StringUtils.isEmpty(model.getIdentity())){
			throw new AjaxException(ResponseEnum.PARAM_ERR);
		}
	}
}