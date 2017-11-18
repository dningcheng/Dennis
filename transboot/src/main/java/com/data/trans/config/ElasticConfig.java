package com.data.trans.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ElasticConfig {
	
	private Logger logger = LoggerFactory.getLogger(ElasticConfig.class);  
    
    @Value("${elastic.server.host}")  
    private String host;  
      
    @Value("${elastic.server.port}")  
    private Integer port;  
      
    @Value("${elastic.server.clusterName}")  
    private String clusterName; 
    
    @SuppressWarnings("resource")
	@Bean     //声明其为Bean实例  
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource  
    public Client esClient(){  
    	try {
    		Settings settings = Settings.builder().put("cluster.name", clusterName)
				.put("client.transport.sniff", true)//开启自动嗅探机制，可以自动链接集群中的其他节点
				//.put("client.transport.ignore_cluster_name", true)//客户端连接时是否验证集群名称
				//.put("client.transport.ping_timeout", "5s")//ping节点的超时时间
				//.put("client.transport.nodes_sampler_interval", "5s")//节点的超时时间
				.build();
    		logger.info("es客户端注入完毕......");
			return new PreBuiltTransportClient(settings).addTransportAddress( new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			logger.error("create elasticsearch client error :", e);
		}
		return null;
    }
    
}
