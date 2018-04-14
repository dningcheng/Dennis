package com.data.trans.mapper;

import java.util.List;

/**
 * @Date 2018年4月6日
 * @author dnc
 * @Description 通用Mapper
 */
public interface BaseMapper<T> {
	
	Integer save(T model);

	Integer delete(T model);

	Integer update(T model);
    
    T findOne(T model);

    List<T> findList(T model);
    
    Integer findListNum(T model);
    
}
