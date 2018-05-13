package com.data.trans.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.data.trans.common.ApiResponse;
import com.data.trans.common.ResponseEnum;
import com.data.trans.exception.AjaxException;
import com.data.trans.mapper.SystemUserMapper;
import com.data.trans.model.SystemUser;
import com.data.trans.service.SystemUserService;
import com.data.trans.util.EncryptUtil;
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
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 添加用户 userId account password 必填
	 * @param model
	 * @return
	 */
	@Override
	public ApiResponse<String> save(SystemUser model) {
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
		
		return systemUserMapper.save(model)==1?ApiResponse.success():ApiResponse.response(ResponseEnum.FAILED);
	}
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据 id userId account identity 四个属性之一删除用户
	 * @param model
	 * @return
	 */
	@Override
	public ApiResponse<String> delete(SystemUser model) {
		//没有传递唯一身份标识抛异常，防止账号全删操作
		checkSafeOpt(model);
		
		return systemUserMapper.delete(model)==1?ApiResponse.success():ApiResponse.response(ResponseEnum.FAILED);
	}
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据 id userId account identity 四个属性之一更新用户
	 * @param model
	 * @return
	 */
	@Override
	public ApiResponse<String> update(SystemUser model) {
		//没有传递唯一身份标识抛异常，防止账号全改操作
		checkSafeOpt(model);
		
		return systemUserMapper.update(model)==1?ApiResponse.success():ApiResponse.response(ResponseEnum.FAILED);
	}
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据 id userId account identity 四个属性之一查找用户
	 * @param id
	 * @return
	 */
	@Override
	public SystemUser findOne(SystemUser model) {
		//没有传递唯一身份标识抛异常，防止查出多个账号
		checkSafeOpt(model);
		SystemUser user = systemUserMapper.findOne(model);
		return user;
	}
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据任何用户属性查询用户列表
	 * @param model
	 * @return
	 */
	@Override
	public SystemUser findList(SystemUser model) {
		
		Integer total = systemUserMapper.findListNum(model);
		if(total != null && total.intValue() != 0){
			List<SystemUser> dataList = systemUserMapper.findList(model);
			model.setTotal(total);
			model.setDataList(dataList);
		}
		return model;
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

	@Override
	public ApiResponse<String> repeat(SystemUser model) {
		Integer id = model.getId();
		model.setId(null);
		SystemUser user = systemUserMapper.findOne(model);
		if(null == user){
			return ApiResponse.success();
		}
		if(id != null && id.intValue() == user.getId().intValue()){
			return ApiResponse.success();
		}
		return ApiResponse.error("此账号已被占用！");
	}
}
