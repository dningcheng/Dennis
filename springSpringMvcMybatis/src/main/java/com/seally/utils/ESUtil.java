package com.seally.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ESUtil {
	private static final Integer defaultServerPort = 9300;
	private static final String defaultServerHost = "localhost";
	private static final String defaultClusterName = "elasticsearch";
	private static final Logger logger = Logger.getLogger(ESUtil.class);
	
	/**
	 * 打开客户端连接
	 * @param clusterName 
	 * @param serverHost
	 * @param serverPort
	 * @return Client
	 */
	@SuppressWarnings("resource")
	public static Client openClient(String clusterName,String serverHost,Integer serverPort){
		
		TransportClient client = null;
		
		if(clusterName==null){
			clusterName = defaultClusterName;
		}
		if(serverHost==null){
			serverHost = defaultServerHost;
		}
		if(serverPort==null){
			serverPort = defaultServerPort;
		}
		
		Settings settings = Settings.builder()
				.put("cluster.name", clusterName)
				.put("client.transport.sniff", true)//开启自动嗅探机制，可以自动链接集群中的其他节点
				.build();
		try {
			client = new PreBuiltTransportClient(settings).addTransportAddress(
					 new InetSocketTransportAddress(InetAddress.getByName(serverHost), serverPort));
		} catch (UnknownHostException e) {
			logger.error("create elasticsearch client error :", e);
		}
		return client;
	}
	
	/**
	 * 关闭客户端
	 * @param client
	 */
	public static void closeClient(Client client){
		client.close();
	}
	
	//第一部分：索引操作
	/**
	 * 新建索引
	 * @param client 连接客户端
	 * @param index 索引名称
	 * @return true|false
	 */
	public static boolean createIndex(Client client,String index){
		boolean result=false;
		try {
	        CreateIndexResponse indexResponse = client
	                                .admin()
	                                .indices()
	                                .prepareCreate(index)
	                                .get();
	        result = indexResponse.isAcknowledged();
	    } catch (ElasticsearchException e) {
	    	logger.error("create index error :", e);
	    }finally {
	    	client.close();
		}
		return result;
	}
	
	/**
	 * 删除索引
	 * @param client 连接客户端
	 * @param index 索引名称
	 * @return true|false
	 */
	public static boolean deleteIndex(Client client,String index){
		DeleteIndexResponse deleteIndexResponse = client
									.admin()
									.indices()
									.prepareDelete(index)
									.get();
		client.close();
		return deleteIndexResponse.isAcknowledged();
	}
	
}
