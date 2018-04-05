package com.data.trans.service;

import java.util.List;

import com.data.trans.model.SystemUser;
import com.data.trans.util.ApiResponse;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统用户相关
 */
public interface SystemUserService {
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 添加用户 userId account password 必填
	 * @param model
	 * @return
	 */
	ApiResponse<String> addSystemUser(SystemUser model);
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据 id userId account identity 四个属性之一删除用户
	 * @param model
	 * @return
	 */
	ApiResponse<String> delSystemUser(SystemUser model);

	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据 id userId account identity 四个属性之一更新用户
	 * @param model
	 * @return
	 */
	ApiResponse<String> updateSystemUser(SystemUser model);
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据 id userId account identity 四个属性之一查找用户
	 * @param id
	 * @return
	 */
	SystemUser getSystemUser(SystemUser model);
	
	/**
	 * @Date 2018年3月25日
	 * @author dnc
	 * @Description 根据任何用户属性查询用户列表
	 * @param model
	 * @return
	 */
	List<SystemUser> listSystemUser(SystemUser model);
	
}
