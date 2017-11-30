package com.data.trans.mapper;

import org.apache.ibatis.annotations.Param;

import com.data.trans.model.Translog;

/**
 * @author dnc
 * 2017年11月30日
 *
 * 数据迁移记录mapper
 *
 */
public interface TranslogMapper {
	
	Integer addTranslog(Translog log);
	
	Integer deleteTranslog(Translog log);

	Integer updateTranslogById(Translog log);
	
	Integer updateTranslogByJobName(Translog log);
	
	Translog getTranslogById(@Param("id") Integer id);
	
	Translog getTranslogList(Translog log);
	
}
