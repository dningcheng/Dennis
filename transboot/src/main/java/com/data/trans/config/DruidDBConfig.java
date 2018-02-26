package com.data.trans.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * @author dnc
 * 2017年11月30日  将主机信息等配置到输入参数传入
 * Druid数据源
 */
@Configuration
public class DruidDBConfig {
	
	private static Logger logger = LoggerFactory.getLogger(DruidDBConfig.class);  
     
//    @Value("${spring.datasource.url}")  
//    private String dbUrl;  //使用下面的dbhost和dbport取代
    
	@Value("${dbhost:localhost}")  
	private String dbhost;
	
    @Value("${dbport:3306}")  
    private String dbport;
    
    //@Value("${spring.datasource.username}")
    @Value("${dbusername:root}")
    private String username;  
      
    //@Value("${spring.datasource.password}")
    @Value("${dbpassword:root}")
    private String password;  
      
    @Value("${spring.datasource.driverClassName}")  
    private String driverClassName;  
      
    @Value("${spring.datasource.initialSize}")  
    private int initialSize;  
      
    @Value("${spring.datasource.minIdle}")  
    private int minIdle;  
      
    @Value("${spring.datasource.maxActive}")  
    private int maxActive;  
      
    @Value("${spring.datasource.maxWait}")  
    private int maxWait;  
      
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")  
    private int timeBetweenEvictionRunsMillis;  
      
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")  
    private int minEvictableIdleTimeMillis;  
      
    @Value("${spring.datasource.validationQuery}")  
    private String validationQuery;  
      
    @Value("${spring.datasource.testWhileIdle}")  
    private boolean testWhileIdle;  
      
    @Value("${spring.datasource.testOnBorrow}")  
    private boolean testOnBorrow;  
      
    @Value("${spring.datasource.testOnReturn}")  
    private boolean testOnReturn;  
      
    @Value("${spring.datasource.poolPreparedStatements}")  
    private boolean poolPreparedStatements;  
      
    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")  
    private int maxPoolPreparedStatementPerConnectionSize;  
      
    @Value("${spring.datasource.filters}")  
    private String filters;  
      
    @Value("{spring.datasource.connectionProperties}")  
    private String connectionProperties;  
      
    @Primary//默认数据源  
    @Bean(name = "dataSource",destroyMethod = "close")//声明其为Bean实例  
    public DruidDataSource dataSource() throws Exception{  
        DruidDataSource datasource = new DruidDataSource();  
          
        //datasource.setUrl(dbUrl);
        datasource.setUrl("jdbc:mysql://"+dbhost+":"+dbport+"/db_unitpropertybase?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        
        datasource.setUsername(username);  
        datasource.setPassword(password);  
        datasource.setDriverClassName(driverClassName);  
        
        //configuration  
        datasource.setInitialSize(initialSize);  
        datasource.setMinIdle(minIdle);  
        datasource.setMaxActive(maxActive);  
        datasource.setMaxWait(maxWait);  
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);  
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);  
        datasource.setValidationQuery(validationQuery);  
        datasource.setTestWhileIdle(testWhileIdle);  
        datasource.setTestOnBorrow(testOnBorrow);  
        datasource.setTestOnReturn(testOnReturn);  
        datasource.setPoolPreparedStatements(poolPreparedStatements);  
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);  
        datasource.setFilters(filters);   
        datasource.setConnectionProperties(connectionProperties);  
        
        //测试连接并判断是否存在记录表
        DruidPooledConnection connectCount = datasource.getConnection();
        Connection connection = connectCount.getConnection();
		if(!checkTableExist(connection)){
			Statement createStatement = connection.createStatement();
			
			//创建数据转移结果记录表
			String sql ="CREATE TABLE `translog` ("
					+ "`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',"
					+ "`trans_name` varchar(64) DEFAULT NULL COMMENT '执行转移的线程名称，job名称',"
					+ "`trans_table` varchar(32) DEFAULT NULL COMMENT '转移的目标表名，暂时不用',"
					+ "`all_between` varchar(64) DEFAULT NULL COMMENT '整体转移id区间，格式：1000-2000',"
					+ "`all_count` int(11) DEFAULT '0' COMMENT '总共需要转移的条数',"
					+ "`none_between` text COMMENT '空数据id区间',"
					+ "`suc_between` text COMMENT '已经转移成功的id区间，格式：2000-3000*6000-8000',"
					+ "`suc_count` int(11) DEFAULT '0' COMMENT '已经转移成功的条数',"
					+ "PRIMARY KEY (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=424 DEFAULT CHARSET=utf8;";
			
			createStatement.execute(sql);
			if(checkTableExist(connection)){
				logger.info("数据迁移记录表 [translog] 未找到，系统已经自动创建该表！");
			}else{
				throw new Exception("数据迁移记录表 [translog] 未找到，系统尝试创建该表失败！");
			}
		}
		logger.info("druid连接池初始化完毕!");
        return datasource;  
    }
    
    //检测目标表是否存在
    private boolean checkTableExist(Connection connection) throws SQLException{
    	if(connection == null){
    		return false;
    	}
        PreparedStatement prepareStatement = connection.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_name = 'translog'");
 		ResultSet executeQuery = prepareStatement.executeQuery();
 		if(!executeQuery.next()){
 			return false;
 		}
 		return true;
    }
}
