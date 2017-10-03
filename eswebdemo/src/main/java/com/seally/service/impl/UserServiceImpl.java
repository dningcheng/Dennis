package com.seally.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.stereotype.Service;

import com.seally.dao.UserDao;
import com.seally.entity.User;
import com.seally.service.UserServive;
import com.seally.utils.ESUtil;
import com.seally.utils.PageModule;

@Service
public class UserServiceImpl implements UserServive {
	
	@Resource
	UserDao userDao;

	@Override
	public void findUser(PageModule<User> pm) {
		Client client = getClient();
		//搜索1 根据id搜索数据
		/*GetResponse response = client.prepareGet("wuye", "pblog", "909190").execute().actionGet();
		System.out.println(response.getSourceAsString());*/
		MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("apiCode", pm.getKeyWords());
		MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
		
		HighlightBuilder hiBuilder = new HighlightBuilder();
		hiBuilder.preTags("<font color='red'>");
		hiBuilder.postTags("</font>");
		hiBuilder.field("apiCode");
		// 搜索数据
		SearchRequestBuilder builder = client.prepareSearch("wuye").setTypes(new String[]{"pblog"});
		if(pm.getKeyWords()==null || "".equals(pm.getKeyWords())){
			builder.setQuery(matchAllQuery);
		}else{
			builder.setQuery(matchQuery);
		}
		
		SearchResponse response2 = builder
				.setQuery(matchAllQuery)
				.highlighter(hiBuilder)
				.setFrom((pm.getCurPage()-1)*pm.getPageSize())//开始记录位置
				.setSize(pm.getPageSize())//返回记录数（单页记录）
				.execute()
				.actionGet();
		
		// 获取查询结果集
		SearchHits searchHits = response2.getHits();
		
		/*
		SearchRequestBuilder srb1 = client
			    .prepareSearch().setQuery(QueryBuilders.queryStringQuery("escluster")).setSize(1);
		SearchRequestBuilder srb2 = client
			    .prepareSearch().setQuery(QueryBuilders.matchQuery("apiCode", pm.getKeyWords())).setSize(1);

		MultiSearchResponse sr = client.prepareMultiSearch()
		        .add(srb1)
		        .add(srb2)
		        .get();

		long nbHits = 0;
		for (MultiSearchResponse.Item item : sr.getResponses()) {
		    SearchResponse response = item.getResponse();
		    nbHits += response.getHits().getTotalHits();
		    System.out.println("nbHits------------------"+nbHits);
		}
		*/
		
		initPageModule(searchHits,pm);
		
		// 关闭client
		ESUtil.closeClient(client);
	}

	private Client getClient() {
		return ESUtil.openClient("escluster", "192.168.174.128", 9300);
	}
	
	private void initPageModule(SearchHits searchHits,PageModule<User> pm){
		List<User> data = new ArrayList<User>();
		
		pm.setTotalRecods((int)searchHits.getTotalHits());
		
		// 遍历结果
		for (SearchHit hit : searchHits) {
			//System.out.println(hit.getSourceAsString());
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			
			//获取用户id
			String userId =sourceAsMap.get("userId").toString();
			String moduleCode = sourceAsMap.get("moduleCode").toString();
			String apiCode = sourceAsMap.get("apiCode").toString();
			String userAccount = sourceAsMap.get("userAccount").toString();
			String opContent = sourceAsMap.get("opContent").toString();
			String opMethod = sourceAsMap.get("opMethod").toString();
			String opTime = sourceAsMap.get("opTime").toString();
			String opResult = sourceAsMap.get("opResult").toString();
			
//			System.out.println("String方式打印文档搜索内容:");
//			System.out.println(hit.getSourceAsString());
			User user = new User(userId, moduleCode, apiCode, userAccount, opMethod, opContent,
					opResult, opTime);
			data.add(user);
		}
		pm.setDataList(data);
	}
	
	public void testAddIndex(String indexName){
		Client client = getClient();
	    try {
	        CreateIndexResponse indexResponse = client
	                                .admin()
	                                .indices()
	                                .prepareCreate(indexName)
	                                .get();
	        System.out.println(indexResponse.isAcknowledged()); // true表示创建成功
	    } catch (ElasticsearchException e) {
	        e.printStackTrace();
	    }finally {
	    	ESUtil.closeClient(client);
		}
	}
	
	private void testDeleteIndex(String indexName){
		Client client = getClient();
		DeleteIndexResponse deleteIndexResponse = client.admin()
				.indices()
				.prepareDelete(indexName)
				.get();
		System.out.println("删除结果："+deleteIndexResponse.isAcknowledged());
		ESUtil.closeClient(client);
	}
	
	public void testAddMapping(String index,String type){
		Client client = getClient();
		try {
	        // 使用XContentBuilder创建Mapping
	        XContentBuilder builder = 
	            XContentFactory.jsonBuilder()
	                            .startObject()
	                                .field("properties")
	                                    .startObject()
	                                        .field("name")
	                                            .startObject()
	                                                .field("index", "not_analyzed")
	                                                .field("type", "string")
	                                            .endObject()
	                                        .field("age")
	                                            .startObject()
	                                                .field("index", "not_analyzed")
	                                                .field("type", "integer")
	                                            .endObject()
	                                    .endObject()
	                            .endObject();
	        System.out.println(builder.string());           
	        PutMappingRequest mappingRequest = Requests.putMappingRequest(index).source(builder).type(type);
	        client.admin().indices().putMapping(mappingRequest).actionGet();
	    } catch (ElasticsearchException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	    	ESUtil.closeClient(client);
		}
	}
	
	private void testAddDocument(){
		Client client = getClient();
		
	}
	
	public static void main(String[] args) {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
//		userServiceImpl.testAddIndex("dncindex01");
//		userServiceImpl.testAddMapping("dncindex01","user");
		userServiceImpl.testDeleteIndex("dncindex01");
	}
}
