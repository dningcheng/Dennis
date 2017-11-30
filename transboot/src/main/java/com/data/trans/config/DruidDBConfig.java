package com.data.trans.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
 * 2017年11月30日
 * Druid数据源
 */
@Configuration
public class DruidDBConfig {
	
	private static Logger logger = LoggerFactory.getLogger(DruidDBConfig.class);  
     
    @Value("${spring.datasource.url}")  
    private String dbUrl;  
      
    @Value("${spring.datasource.username}")  
    private String username;  
      
    @Value("${spring.datasource.password}")  
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
          
        datasource.setUrl(dbUrl);  
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
        PreparedStatement prepareStatement = connection.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_name ='translog'");
		ResultSet executeQuery = prepareStatement.executeQuery();
		if(!executeQuery.next()){
			throw new Exception("数据迁移记录表 [translog] 未找到，请先在目标库创建该表！");
		}
		logger.info("druid连接池初始化完毕!");
        return datasource;  
    }  
}
