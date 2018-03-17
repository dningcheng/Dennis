package com.data.trans.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * @Date 2018年3月17日
 * @author dnc
 * @Description Druid的DataResource配置类
 * 获取配置信息方式有两种：
 * 方式一：凡是被Spring管理的类，实现接口 EnvironmentAware 重写方法 setEnvironment 可以在工程启动时，获取到系统环境变量和application配置文件中的变量。  
 * 方式二：采用注解的方式获取 @value("${变量的key值}") 获取application配置文件中的变量。 
 * 
 * 这里采用方式一要方便些  
 */
@Configuration
public class DruidDBConfig implements EnvironmentAware{
	
	protected static Logger logger = LoggerFactory.getLogger(DruidDBConfig.class);  
    
	//方式二获取参数
	/*@Value("${dbhost:localhost}")  
	private String dbhost;*/
	
	//方式一获取参数
	private RelaxedPropertyResolver propertyResolver;  
	 
	@Override
    public void setEnvironment(Environment env) {  
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");//设置统一前缀，方便获取参数时省略该前缀
    } 
	
	@Bean  
    public DataSource dataSource() throws SQLException, Exception {  
        DruidDataSource datasource = new DruidDataSource();  
        datasource.setUrl(propertyResolver.getProperty("url"));  
        datasource.setDriverClassName(propertyResolver.getProperty("driverClassName"));  
        datasource.setUsername(propertyResolver.getProperty("username"));  
        datasource.setPassword(propertyResolver.getProperty("password"));  
          
        datasource.setInitialSize(propertyResolver.getProperty("initialSize",Integer.class));  
        datasource.setMinIdle(propertyResolver.getProperty("minIdle",Integer.class));  
        datasource.setMaxWait(propertyResolver.getProperty("maxWait",Long.class));  
        datasource.setMaxActive(propertyResolver.getProperty("maxActive",Integer.class));  
        datasource.setMinEvictableIdleTimeMillis(propertyResolver.getProperty("minEvictableIdleTimeMillis",Long.class)); 
        datasource.setTimeBetweenEvictionRunsMillis(propertyResolver.getProperty("timeBetweenEvictionRunsMillis",Long.class));  
        datasource.setValidationQuery(propertyResolver.getProperty("validationQuery"));  
        datasource.setTestWhileIdle(propertyResolver.getProperty("testWhileIdle", Boolean.class));  
        datasource.setTestOnBorrow(propertyResolver.getProperty("testOnBorrow", Boolean.class));  
        datasource.setTestOnReturn(propertyResolver.getProperty("testOnReturn", Boolean.class));  
        datasource.setPoolPreparedStatements(propertyResolver.getProperty("poolPreparedStatements",Boolean.class));  
        datasource.setMaxPoolPreparedStatementPerConnectionSize(propertyResolver.getProperty("maxPoolPreparedStatementPerConnectionSize",Integer.class));  
        datasource.setConnectionProperties(propertyResolver.getProperty("connectionProperties"));  
        try {  
        	datasource.setFilters(propertyResolver.getProperty("filters")); 
        } catch (SQLException e) {  
            e.printStackTrace();  
        }
        
        //测试连接并判断是否存在记录表
        checkNeedTable(datasource);
        
        return datasource;  
    }

	private void checkNeedTable(DruidDataSource datasource) throws SQLException, Exception {
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
