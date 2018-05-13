package com.data.trans.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.data.trans.datasource.ElasticDataSource;

@Configuration
public class ElasticConfig extends ElasticDataSource implements EnvironmentAware{
	
	private Logger logger = LoggerFactory.getLogger(ElasticConfig.class);  
    
	private RelaxedPropertyResolver propertyResolver;
	
	@Override
    public void setEnvironment(Environment env) {  
        this.propertyResolver = new RelaxedPropertyResolver(env, "elasticsearch.");//设置统一前缀，方便获取参数时省略该前缀
    } 
    
	@Bean     //声明其为Bean实例  
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource  
    public ElasticDataSource EsDataSource() throws Exception{  
    	
		ElasticDataSource dataSource = new ElasticDataSource();
		
    	dataSource.setHost(propertyResolver.getProperty("serverHost"));
    	dataSource.setClusterName(propertyResolver.getProperty("serverName"));
    	dataSource.setPort(propertyResolver.getProperty("serverPort",Integer.class));
    	dataSource.setInitialSize(propertyResolver.getProperty("clientPoolInitialSize",Integer.class));
    	dataSource.setMaxSize(propertyResolver.getProperty("clientPoolMaxSize",Integer.class));
    	dataSource.setMinSize(propertyResolver.getProperty("clientPoolMinSize",Integer.class));
    	dataSource.setMaxWait(propertyResolver.getProperty("clientPoolMaxWait",Integer.class));
    	logger.info("begin init espool...");
    	dataSource.initDataSource();
    	logger.info("init espool completed");
		return dataSource;
    }
    
}
