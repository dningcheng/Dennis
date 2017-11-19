package com.data.trans.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.data.trans.model.SystemLog;

@Component
public class MysqlUtil {
	
	private Logger logger = LoggerFactory.getLogger(MysqlUtil.class);  
	
	@Autowired
	DruidDataSource dataSource;
	
	@Autowired
	ElasticDataSource esSource;
	
	@Value("${elastic.client.import.index}")  
	private String index;
	
	@Value("${elastic.client.import.type}")  
	private String type;
	
	@Value("${elastic.client.import.bulkSize}")  
	private Integer bulkSize;
	
	
	public void test() throws SQLException{
		DruidPooledConnection connection = dataSource.getConnection();
		
		logger.info("读取mysql数据...");
		long start = new Date().getTime();
		
		PreparedStatement prepareStatement = connection.prepareStatement("select * from t_pb_log limit 0,10000");
		ResultSet executeQuery = prepareStatement.executeQuery();
    	
    	List<SystemLog> logs = new ArrayList<SystemLog>();
    	while (executeQuery.next()) {
    		SystemLog log = new SystemLog();
    		log.setId(executeQuery.getInt("id"));
    		log.setOrgId(executeQuery.getInt("org_id"));
    		log.setUserId(executeQuery.getInt("user_id"));
    		log.setUnitId(executeQuery.getInt("unit_id"));
    		log.setModuleCode(executeQuery.getString("module_code"));
    		log.setApiCode(executeQuery.getString("api_code"));
    		log.setUserAccount(executeQuery.getString("user_account"));
    		log.setUnitName(executeQuery.getString("unit_name"));
    		log.setOpMethod(executeQuery.getString("op_method"));
    		log.setOpContent(executeQuery.getString("op_content"));
    		log.setOpResult(executeQuery.getString("op_result"));
    		log.setOpTime(executeQuery.getTime("op_time"));
    		log.setModuleParkPlate(executeQuery.getString("module_park_plate"));
    		logs.add(log);
		}
    	
    	logger.info("mysql数据封装结束："+(new Date().getTime()-start));
    	
    	logger.info("准备提交到es...");
    	//Client client = initDataSource();
    	Client client = esSource.getClient();
    	BulkRequestBuilder bulkRequest = client.prepareBulk();
    	for (int i = 0; i < logs.size(); i++) {
    	    bulkRequest.add(client.prepareIndex(index, type).setSource(JSON.toJSONString(logs.get(i)), XContentType.JSON));
    	    // 每10000条提交一次
    	    if (i % bulkSize == 0 && i != 0) {
    	        bulkRequest.execute().actionGet();
    	        logger.info("提交一批到es...");
    	        bulkRequest = client.prepareBulk();//新开一个批次
    	    }
    	}
    	logger.info("提交到es结束："+(new Date().getTime()-start));
		
	}
	
}
