package com.data.trans.service;

import java.util.List;

import com.data.trans.common.ApiResponse;

/**
 * @Date 2018年4月6日
 * @author dnc
 * @Description 通用Service
 */
public interface BaseService<T> {

	ApiResponse<String> save(T model);

    ApiResponse<String> delete(T model);

    ApiResponse<String> update(T model);
    
    T findOne(T model);

    T findList(T model);
    
}
