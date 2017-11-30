package com.data.trans.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.trans.mapper.TranslogMapper;
import com.data.trans.model.Translog;
import com.data.trans.service.TranslogService;

/**
*
*@author dnc
*@version 创建时间：2017年11月30日 上午11:18:26 
*
*
*/
@Service
public class TranslogServiceImpl implements TranslogService {
	
	@Autowired
	TranslogMapper translogMapper;

	@Override
	public Integer addTranslog(Translog log) {
		return translogMapper.addTranslog(log);
	}

	@Override
	public Integer deleteTranslog(Translog log) {
		return translogMapper.deleteTranslog(log);
	}

	@Override
	public Integer updateTranslogById(Translog log) {
		return translogMapper.updateTranslogById(log);
	}

	@Override
	public Integer updateTranslogByJobName(Translog log) {
		return translogMapper.updateTranslogByJobName(log);
	}
	
	@Override
	public Translog getTranslogById(Integer id) {
		return translogMapper.getTranslogById(id);
	}

	@Override
	public Translog getTranslogList(Translog log) {
		return translogMapper.getTranslogList(log);
	}
}
