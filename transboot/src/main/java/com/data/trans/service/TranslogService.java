package com.data.trans.service;

import java.util.List;

import com.data.trans.model.EchartsModel;
import com.data.trans.model.Translog;

/**
*
*@author dnc
*@version 创建时间：2017年11月30日
*
*
*/
public interface TranslogService {
	
	Integer addTranslog(Translog log);
	
	Integer deleteTranslog(Translog log);
	
	Integer updateTranslogById(Translog log);
	
	Integer updateTranslogByJobName(Translog log);
	
	Translog getTranslogById(Integer id);
	
	List<Translog> getTranslogList(Translog log);

	EchartsModel getCurTransEchartsOption();

	int clearTranslog();
	
}
