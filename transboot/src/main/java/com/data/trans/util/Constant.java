package com.data.trans.util;
/**
*
*@author dnc
*@version 创建时间：2017年12月1日 下午4:04:31 
*
*常量类
*/
public interface Constant {
	static final Integer STATE_TRANS_UNSTART  = 0; //任务转移状态 未开始
	static final Integer STATE_TRANS_STARTING = 1; //任务转移状态 进行中
	static final Integer STATE_TRANS_FINISHED = 2; //任务转移状态 已完成
}
