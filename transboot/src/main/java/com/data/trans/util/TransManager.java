package com.data.trans.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.data.trans.service.SourceTableService;
import com.data.trans.service.TranslogService;

/**
 * @author dnc
 * @since 2017年11月19日 下午4:23:07
 * 数据迁移任务管理类
 */
@Component
public class TransManager {
	
	private static final Logger logger = LoggerFactory.getLogger(TransJob.class);
	
	@Autowired
	DataSource dataSource;//需要直接使用dataSource进行jdbc操作因此这里注入过来
	
	@Autowired
	ElasticDataSource esSource;
	
	@Autowired
	TranslogService translogService;
	
	@Autowired
	SourceTableService sourceTableService;
	
	@Value("${elastic.client.import.index}")  
	private String index;//es数据导入索引库
	
	@Value("${elastic.client.import.type}")  
	private String type;//es数据导入类型
	
	@Value("${trans.datasource.table.name}")  
	private String tableName;//es数据导入数据来源表
	
	@Value("${trans.thread.pool.size}")  
	private Integer threads;//es数据导入批量提交每批数目
	
	@Value("${elastic.client.import.bulkSize}")  
	private Integer bulkSize;//es数据导入批量提交每批数目
	
	@Value("${trans.datasource.table.fetchSize}")  
	private Integer fetchSize;//每次加载到内存中的表中记录数
	
	//线程池
	private ExecutorService fixedThreadPool = null;
	
	//数据迁移启动方法
	public Integer startTrans(){
		if(fixedThreadPool == null){
			fixedThreadPool = Executors.newFixedThreadPool(threads);
		}
		//获取数据转移总记录数
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement prepareStatement = connection.prepareStatement(String.format("select max(id) as maxId,count(*) as totalNum from %s", tableName));
			
			ResultSet executeQuery = prepareStatement.executeQuery();
			int maxId = 0;
			int totalNum = 0;
			while (executeQuery.next()) {
				maxId = executeQuery.getInt("maxId");
				totalNum = executeQuery.getInt("totalNum");
			}
			
			logger.info("数据转移主键ID区间为 [ "+1+" , "+(maxId+1)+" ) 目标总数为 [ "+totalNum+" ] 条,共分给 [ "+threads+" ] 个job执行！");
			
			//划分任务
			int jobAvegeCount =  maxId / threads;
			int leaveCount = maxId % threads;
			
			for(int i=0;i<threads;i++){
				Integer fetchIdMin = jobAvegeCount*i;
				Integer fetchIdMax = fetchIdMin+jobAvegeCount;
				if(i==(threads-1)){//最后一个job
					fetchIdMax = fetchIdMax+leaveCount+1;
				}
				if(fetchIdMin == 0){//id最小从1开始
					fetchIdMin = 1;
				}
				TransJob transJob = new TransJob(dataSource, esSource, index, type, bulkSize, fetchIdMin, fetchIdMax, fetchSize, tableName,null,translogService,sourceTableService);
				fixedThreadPool.submit(transJob);
			}
			return CmdUtil.SUCCESS;
		} catch (SQLException e) {
			logger.error("数据转移job生成失败："+e);
			return CmdUtil.ERR;
		}
	}
	
	//数据迁移启动方法
	public Integer startTrans(Integer translogId){
		if(fixedThreadPool == null){
			fixedThreadPool = Executors.newFixedThreadPool(threads);
		}
		TransJob transJob = new TransJob(dataSource, esSource, index, type, bulkSize, null, null, fetchSize, tableName,null,translogService,sourceTableService);
		fixedThreadPool.submit(transJob);
		return CmdUtil.SUCCESS;
	}
}