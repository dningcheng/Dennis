package com.data.trans.service;

import com.data.trans.common.ApiResponse;
import com.data.trans.model.SystemUser;

/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统用户相关
 */
public interface SystemUserService extends BaseService<SystemUser>{

	/**
	 * @Date 2018年4月14日
	 * @author dnc
	 * @Description 验证账号是否重复
	 * @param user
	 * @return
	 */
	ApiResponse<String> repeat(SystemUser model);
	
	
	
}
