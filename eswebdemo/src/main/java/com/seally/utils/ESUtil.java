package com.seally.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
				//.put("client.transport.ignore_cluster_name", true)//客户端连接时是否验证集群名称
				//.put("client.transport.ping_timeout", "5s")//ping节点的超时时间
				//.put("client.transport.nodes_sampler_interval", "5s")//节点的超时时间
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
	
	/**
	 * json文档构建方式以下4种：
	 * 01、直接边写json字符串或以其他方式获取json字符串
	 * 02、使用map
	 * 03、序列化实体bean   （不方便使用）
	 * 04、使用 Elasticsearch 提供的工具类 XContentBuilder 
	 * @throws IOException 
	 */
	private static void xContentBuilderTest() throws IOException{
		XContentBuilder jsonBuilder = XContentFactory.jsonBuilder()
				.startObject()
					.startObject("user")
						.field("name","zhangsan")
						.field("age",23)
						.field("birth", new Date())
					.endObject()
				.endObject();
		System.out.println(jsonBuilder.string());
	}
	
	//第一部分：索引操作
	/**
	 * 新建索引
	 * @param client 客户端
	 * @param index 索引名称
	 * @return true | false
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
	    }/*finally {
	    	client.close();
		}*/
		return result;
	}
	
	/**
	 * 指定分片、副本数 创建索引
	 * @param client
	 * @param index
	 * @param shards
	 * @param replicas
	 * @return
	 */
	public static boolean createIndexWithSettings(Client client,String index,int shards,int replicas){
		boolean result=false;
		try {
	        CreateIndexResponse indexResponse;
				indexResponse = client.admin().indices().prepareCreate(index)
				                      .setSettings(
				                        	XContentFactory.jsonBuilder()
				                        	.startObject()
				                        	  	.field("number_of_shards", shards)
				                        	  	.field("number_of_replicas", replicas)
				                        	.endObject()
				                       ).get();
	        result = indexResponse.isAcknowledged();
	    } catch (ElasticsearchException | IOException e) {
	    	logger.error("create index error :", e);
	    }
		return result;
				
	}
	
	/**
	 * 创建索引的同时配置其基础设置和字段映射 （内部含 ex.）
	 * @param client
	 * @param index
	 * @param shards
	 * @param replicas
	 * @return
	 */
	public static boolean createIndexWithSettingsAndMapping(Client client, String index,int shards,int replicas,XContentBuilder sourceWithMapping){
		boolean result = false;
		// settings
        Settings settings = Settings.builder()
        		                    .put("index.number_of_shards",shards)
        							.put("index.number_of_replicas",replicas)
                                    .build();
        // mapping example
        XContentBuilder mappingBuilder=null;
        try {
        	 mappingBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                        .startObject(index)
                            .startObject("properties")
                                //store属性设置是否存储  ,使用 ignore_malformed属性对于畸形无法转换为数字型的丢弃而不是抛出异常而放弃整个文档
                                .startObject("orgId").field("type", "integer").field("store", false).field("ignore_malformed", true).endObject()
                                .startObject("userId").field("type", "integer").field("store", false).endObject()
                                .startObject("unitId").field("type", "integer").field("store", false).endObject()
                                //对于string类型可以分为两种：全文本、关键字
                                //全文本通常用于基于文本的相关性搜索，全文本字段可用于分词
                                .startObject("moduleCode").field("type", "string").field("store", false).endObject()
                                .startObject("apiCode").field("type", "string").field("store", false).endObject()
                                .startObject("userAccount").field("type", "string").field("store", false).field("analyzer", "english").field("search_analyzer", "english").endObject()
                                .startObject("unitNname").field("type", "string").field("store", false).endObject()
                                //设置该字段为string类型中的关键字，关键字字段通常用于过滤但是不用于分词，关键字使用属性：index : "not_analyzed"进行修饰，该属性还有analyzed（默认）、no两个值
                                .startObject("opMethod").field("type", "string").field("store", false).field("index", "not_analyzed").endObject()
                                //用search_analyzer属性设置搜索时用在该字段上的分析器
                                .startObject("opContent").field("type", "string").field("store", false).field("analyzer", "ik_smart").field("search_analyzer", "ik_smart").endObject()
                                //用search_quote_analyzer属性设置搜索短语时用在该字段上的分析器
                                .startObject("opResult").field("type", "string").field("store", false).endObject()
                                .startObject("opTime").field("type", "string").field("store", false).endObject()
                                .startObject("moduleParkPlate").field("type", "string").field("store", false).field("index", "not_analyzed").endObject()
                            .endObject()
                        .endObject()
                   .endObject();
        } catch (Exception e) {
            logger.error("create index with setting and mapping error :",e);
            return false;
        }
        
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        CreateIndexResponse response = indicesAdminClient.prepareCreate(index)
        		.setSettings(settings)
        		//.addMapping(index, sourceWithMapping)
        		.addMapping(index, mappingBuilder)
        		.execute().actionGet(); //.get()
        
        result = response.isAcknowledged();
        return result;
    }
	
	/**
	 * 关闭索引
	 * @param client
	 * @param index
	 */
	public static boolean closeIndex(Client client,String...indices){
		CloseIndexResponse closeIndexResponse = client.admin().indices().prepareClose(indices).execute().actionGet();
		return closeIndexResponse.isAcknowledged();
	}
	
	/**
	 * 打开索引
	 * @param client
	 * @param index
	 */
	public static boolean openIndex(Client client,String...indices){
		OpenIndexResponse openIndexResponse = client.admin().indices().prepareOpen(indices).execute().actionGet();
		return openIndexResponse.isAcknowledged();
	}
	
	/**
	 * 验证索引是否存在
	 * @param client
	 * @param index
	 * @return
	 */
	public static boolean existIndex(Client client,String index){
		/*boolean exist = false;
		try {
			exist = client.admin().indices().exists(new IndicesExistsRequest(index)).get().isExists();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("query exist index error :", e);
		}
		return exist;*/
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}
	
	/**
	 * 判断指定索引的指定类型是否存在
	 * @param client
	 * @param index
	 * @param type
	 * @return
	 */
	public static boolean existTypeOfIndex(Client client,String index,String type){
		return client.admin().indices().prepareTypesExists(index).setTypes(type).get().isExists();
	}
	/**
	 * 
	 * @param client
	 * @param indices
	 * 
	 * 分析器：分词器   字符过滤器   词源过滤器
	 * 分词器：whitespace    
	 */
	public static void updateIndexAnalyzers(Client client,String...indices){
		
		//关闭索引
		boolean closeIndex = ESUtil.closeIndex(client, indices);
		
		//更新分析器
		IndicesAdminClient admin = client.admin().indices();
		
		//重新开启索引
		boolean openIndex = ESUtil.openIndex(client, indices);
		
	}
	
	/**
	 * 删除索引
	 * @param client 客户端
	 * @param index 索引名称(可以使用通配符)
	 * @return true | false
	 */
	public static boolean deleteIndex(Client client,String index){
		DeleteIndexResponse deleteIndexResponse = client
									.admin()
									.indices()
									.prepareDelete(index)
									.get();
		return deleteIndexResponse.isAcknowledged();
	}
	
	/**
	 * 插入文档
	 * @param client
	 * @param index 索引名称 （必须为已经存在的索引）
	 * @param type  类型名称
	 * @param id    文档id
	 * @param jsonDocument 文档内容
	 * @return true | false
	 */
	public static IndexResponse insertDocument(Client client,String index,String type,String id,String jsonDocument){
		IndexResponse response = client.prepareIndex(index, type, id)
		        .setSource(jsonDocument, XContentType.JSON)
		        .setId(id)
		        .get("5s");
		logger.info(response.status().getStatus());
		return response;
	}
	
	/**
	 * 插入文档
	 * @param client
	 * @param index 索引名称 （必须为已经存在的索引）
	 * @param type  类型名称
	 * @param id    文档id
	 * @param mapDocument 文档内容
	 * @return true | false
	 */
	public static IndexResponse insertDocument(Client client,String index,String type,String id,Map<String,Object> mapDocument){
		IndexResponse response = client.prepareIndex(index, type, id)
		        .setSource(mapDocument)
		        .setId(id)
		        .get("5s");
		return response;
	}
	
	/**
	 * 根据索引、类型、文档id 获取文档
	 * @param client
	 * @param index
	 * @param type
	 * @param id
	 * @return GetResponse
	 */
	public GetResponse getDocument(Client client,String index,String type,String id) {
		GetResponse response = client.prepareGet(index, type, id)
				//.setOperationThreaded(false)//执行线程模式，默认为true:在不同的线程中执行
				.get();
		return response;
	}
	
	/**
	 * 单字段单值精确查询 ：term 查询， 可以用它处理数字（numbers）、布尔值（Booleans）、日期（dates）以及文本（text）
	 * 
	 * 如果对于某个字段的精确查询需要严格匹配而不受分词器分词影响，那么在该字段的映射时需要指定其属性 "index" : "not_analyzed"
	 * @param client
	 * @param field
	 * @param value
	 * @param indices 为null则不限定索引库
	 * @param types 为null则不限定类型
	 * @return SearchHits
	 */
	public static SearchHits termSearch(Client client,String field,String value,String[] indices,String[] types){
		
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		
		searchBuilder.setQuery(QueryBuilders.termQuery(field, value));
		
		searchBuilder.setFrom(0).setSize(10).setExplain(true);
		
		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		
		SearchHits hits = searchResponse.getHits();
		
		return hits;
		
	}
	
	
	/**
	 * 单字段多值精确查询 ：term 查询， 可以用它处理数字（numbers）、布尔值（Booleans）、日期（dates）以及文本（text）
	 * 
	 * 如果对于某个字段的精确查询需要严格匹配而不受分词器分词影响，那么在该字段的映射时需要指定其属性 "index" : "not_analyzed"
	 * @param client
	 * @param field
	 * @param value
	 * @param indices 为null则不限定索引库
	 * @param types 为null则不限定类型
	 * @return SearchHits
	 * 
	 * 注意，term和terms查询均为包含查询（因为索引的内容可能被分词器进行分此后匹配，如果一定要达到精确相等，需要为文档新增一个属性记录其词频，查询时加上词频字段值=1的条件）
	 * 
	 */
	public static SearchHits termsSearch(Client client,String field,String[] values,String[] indices,String[] types){
		
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		
		//与单值精确查询唯一的区别即该行 termsQuery
		searchBuilder.setQuery(QueryBuilders.termsQuery(field, values));
		
		searchBuilder.setFrom(0).setSize(250).setExplain(true);
		
		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		
		SearchHits hits = searchResponse.getHits();
		
		return hits;
		
	}
	
	/**
	 * 分离 最大化查询 （返回通常的结果，但排序受最佳匹配的控制）
	 * @param client
	 * @param indices
	 * @param types
	 * @return
	 */
	public static SearchHits disMaxQuerySearch(Client client,String[] indices,String[] types){
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		
		//分离 最大化查询:将任何与任一查询匹配的文档作为结果返回，但只将最佳匹配的评分作为查询的评分
		DisMaxQueryBuilder disMaxQuery = QueryBuilders.disMaxQuery();
		
		disMaxQuery.add(QueryBuilders.matchQuery("name", "张三"));//匹配一
		
		disMaxQuery.add(QueryBuilders.matchQuery("address", "湖北 恩施"));//匹配二
		
		searchBuilder.setQuery(disMaxQuery);
		//searchBuilder.setPostFilter(disMaxQuery);
		
		//分离最大化查询的优化：设置tie_breaker 参数，其值(0-1之间的浮点数，需要调试得出最佳值，通常在0.1-0.4之间)
		//disMaxQuery.tieBreaker(0.4F);
		
		searchBuilder.setFrom(0).setSize(250).setExplain(true);
		
		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		
		SearchHits hits = searchResponse.getHits();
		
		return hits;
	}
	
	/**
	 * 范围查询 （该方式查询最好最好用于数字日期型字段进行查询，否则效率较低）
	 * @param client
	 * @param field
	 * @param min 下限
	 * @param max 上限
	 * @param indices
	 * @param types
	 * @return
	 */
	public static SearchHits rangeSearch(Client client,String field,Object min,Object max,String[] indices,String[] types){
		
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		
		/**
		 * 范围过滤器，包含以下组合：
		 * gt: > 大于（greater than）
		 * lt: < 小于（less than）
		 * gte: >= 大于或等于（greater than or equal to）
		 * lte: <= 小于或等于（less than or equal to）
		 */
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field);
		
		rangeQuery.gt(min);
		
		//手动提升该查询的权重，可以达到提高该查询条件的评分目的从而提高相关性使其排在更前
		//rangeQuery.boost(2.0F);
		
		rangeQuery.lte(max);
		
		//rangeQuery.relation(relation);???
		
		searchBuilder.setQuery(rangeQuery);
		//searchBuilder.setPostFilter(rangeQuery);//过滤
		
		
		searchBuilder.setFrom(0).setSize(30).setExplain(true);//设置是否按查询匹配度排序
		
		SearchResponse response = searchBuilder.execute().actionGet();
		
		return response.getHits();
	}
	
	/**
	 * 精确短语查询
	 * @param client
	 * @param field
	 * @param phrase
	 * @param indices
	 * @param types
	 * @return
	 */
	public static SearchHits phraseSearch(Client client,String field,String phrase, String[] indices,String[] types){
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		
		MatchPhraseQueryBuilder matchPhraseQuery = QueryBuilders.matchPhraseQuery(field, phrase);
		
		//精确短语匹配 或许是过于严格了。也许我们想要包含 “quick brown fox” 的文档也能够匹配 “quick fox,”
		//那么就需要使用 slop 参数将灵活度引入短语匹配中，slop 参数告诉 match_phrase 查询词条相隔多远时仍然能将文档视为匹配
		//matchPhraseQuery.slop(2);
		
		//对于多值字段，防止将跨不同的值视为同一个短语的组成部分，需要在设置文档映射时使用position_increment_gap属性进行限定间隔数

		searchBuilder.setQuery(matchPhraseQuery);
		
		searchBuilder.setFrom(0).setSize(30).setExplain(true);//设置是否按查询匹配度排序
		
		SearchResponse response = searchBuilder.execute().actionGet();
		
		return response.getHits();
		
	}
	
	/**
	 * 字段存在过滤 
	 * @param client
	 * @param field
	 * @param indices
	 * @param types
	 * @return
	 */
	public static SearchHits existFieldValueSearch(Client client,String field,String[] indices,String[] types){
		
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		
		/**
		 * 存在过滤器 字段存在且不能为null值
		 */
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(field);
		
		searchBuilder.setQuery(existsQuery);
		
		searchBuilder.setFrom(0).setSize(30).setExplain(true);//设置是否按查询匹配度排序
		
		SearchResponse response = searchBuilder.execute().actionGet();
		
		return response.getHits();
	}
	
	/**
	 * 组合查询 可以组合任意条件
	 * @param client
	 * @param field
	 * @param value
	 * @param indices
	 * @param types
	 * @return
	 */
	public static SearchHits compoundSearch(Client client,String field,String value,String[] indices,String[] types){
		
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			searchBuilder.setTypes(types);
		}
		
		/**
		 * bool过滤器，每个bool过滤器包含3个部分：在各部分传入若干相应的其它过滤器（包括新的bool过滤器可以组合成任意的查询过滤逻辑）
		 * 1、must：必须（must） 匹配，与 AND 等价。
		 * 2、must_not：不能（must not） 匹配，与 NOT 等价。
		 * 3、should：至少有一个语句要匹配，与 OR 等价。
		 */
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		//field字段值必须以value开头
		boolQuery.must(QueryBuilders.prefixQuery(field, value));
		
		//moduleCode字段精确值必须不为3同时也不为6
		boolQuery.mustNot(QueryBuilders.termQuery("moduleCode", "3"));
		boolQuery.mustNot(QueryBuilders.termQuery("moduleCode", "6"));
		
		//字段名
		boolQuery.should(QueryBuilders.existsQuery("moduleParkPlate"));
		
		searchBuilder.setQuery(boolQuery);
		
		searchBuilder.setFrom(0).setSize(30).setExplain(true);//设置是否按查询匹配度排序
		
		SearchResponse response = searchBuilder.execute().actionGet();
		
		return response.getHits();
	}
	
	/**
	 * 通过多列索引、类型、id获取文档
	 * @param client
	 * @param infos
	 * @return List<GetResponse>
	 */
	public List<GetResponse> getMultiDocument(Client client,MultiQueryInfo infos){
		
		MultiGetRequestBuilder prepareMultiBuilder = client.prepareMultiGet();
		
		for(MultiQueryInfo every:infos.getInfos()){
			prepareMultiBuilder.add(every.getIndex(), every.getType(), every.getId());          
		}
		MultiGetResponse multiGetItemResponses = prepareMultiBuilder.get();

		List<GetResponse> resList = new ArrayList<GetResponse>();
		for (MultiGetItemResponse itemResponse : multiGetItemResponses) { 
		    GetResponse response = itemResponse.getResponse();
		    if (response.isExists()) { 
		    	resList.add(response);
		    }
		}
		return resList;
	}
	
	/**
	 * 全文检索文档
	 * 
	 * 对于ES的搜索类型有如下取值：SearchType.DFS_QUERY_THEN_FETCH
	 * 1、query_and_fetch：
	 * 通常是最快也是最简单的搜索类型。查询语句在所有需检查的分片上并行执行，并且所有分片返回结果的规划为size参数的取值。因此，该类型返回的文档数目最大为size参数的取值与分片数目的乘积。
	 * 
	 * 2、query_then_fetch：
	 * 查询语句首先得到将文档排序所需的信息，然后得到要获取的文档内容的相关分片。与query_and_fetch不同，该类搜索返回的文档数目最大为size参数的取值。
	 * 
	 * 3、dfs_query_and_fetch：
	 * 该类搜索类似于query_and_fetch。除了完成query_and_fetch的工作外，还执行初始查询阶段，该阶段计算分布式的词频以更精准地对返回文档打分。
	 * 
	 * 4、dfs_query_then_fetch：
	 * 该类搜索类似于query_then_fetch。除了完成query_then_fetch的工作外，还执行初始查询阶段，该阶段计算分布式的词频以更精准地对返回文档打分。
	 * 
	 * 5、count：
	 * 这是一种特殊的搜索类型，只返回匹配查询的文档数目。
	 * 
	 * @param client
	 * @param indices
	 * @param types
	 */
	public static SearchHits fullTextSearch(Client client,String[] indices,String[] types,String keyWords){
		
		SearchRequestBuilder searchBuilder = client.prepareSearch();
		if(indices!=null){
			//设置搜索索引库
			searchBuilder.setIndices(indices);
		}
		if(types!=null){
			//设置搜索索引库类型
			searchBuilder.setTypes(types);
		}
		
		//获取源文档
		searchBuilder.setFetchSource(true);
		
		//设置索引类型
		searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		
		//设置是否按查询匹配度排序  
		searchBuilder.setExplain(true); 
		
		//深度分页相关
		searchBuilder.setFrom(0);
		searchBuilder.setSize(10);
		
		//设置排序字段
		/*searchBuilder.addSort("age",SortOrder.DESC);
		searchBuilder.addSort("birthday",SortOrder.ASC);*/
		
		//使用bool查询包装
		//BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyWords);
		
		searchBuilder.setQuery(multiMatchQuery);
		//设置精确匹配字段
		//searchBuilder.setQuery(QueryBuilders.termQuery("multi", "test"));
		
		//设置范围匹配字段
		//searchBuilder.setQuery(QueryBuilders.rangeQuery("age").from(12).to(18));
		System.out.println(searchBuilder.toString());
		
		SearchResponse response = searchBuilder.execute().actionGet();
		
		return response.getHits();
	}
	
	/**
	 * 根据索引、类型、文档id 删除文档
	 * @param client
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static DeleteResponse deleteDocument(Client client,String index,String type,String id){
		DeleteResponse response = client.prepareDelete(index, type, id)
				.get();
		return response;
	}
	
	/**
	 * 根据条件批量删除文档
	 * @param client
	 * @param query 查询条件 可以使用 QueryBuilders 的系列静态方法构建
	 * @param indices 索引库系列
	 * @return long 删除文档数
	 */
	public static long deleteDocumentByQuery(Client client,AbstractQueryBuilder<?> query,String...indices){
		DeleteByQueryRequestBuilder filter = DeleteByQueryAction.INSTANCE.newRequestBuilder(client).filter(query); 
		if(indices!=null) {
			filter.source(indices);
		}
		BulkByScrollResponse response = filter.get();
		return response.getDeleted(); 
	}
	
	/**
	 * 异步方式删除文档
	 * @param client
	 * @param query 查询条件 可以使用 QueryBuilders 的系列静态方法构建
	 * @param listener 处理监听，操作返回后触发改监听类
	 * @param indices 索引库系列
	 */
	public static void deleteDocumentByQueryAsync(Client client,AbstractQueryBuilder<?> query,ActionListener listener,String...indices){
		DeleteByQueryRequestBuilder filter = DeleteByQueryAction.INSTANCE.newRequestBuilder(client).filter(query); 
		if(indices!=null) {
			filter.source(indices);
		}                                                  
		filter.execute(listener);
	}
	
	/**
	 * 更新文档 （只会更新文档中字段匹配的文档内容）
	 * @param client
	 * @param index
	 * @param type
	 * @param id
	 * @param mapDocument
	 * @return
	 */
	public static UpdateResponse updateDocument(Client client,String index,String type,String id,Map<String,Object> mapDocument){
		
		UpdateResponse updateResponse=null;
		try {
			UpdateRequest updateRequest = new UpdateRequest()
					.index(index)
					.type(type)
					.id(id)
					.doc(mapDocument);
			updateResponse = client.update(updateRequest).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("update document error :", e);
		}
		return updateResponse;
	}
	
	/**
	 * 修改或新增文档，存在则更新，不存在则修改
	 * @param client
	 * @param index
	 * @param type
	 * @param id
	 * @param mapDocument
	 * @return
	 */
	public static UpdateResponse updateOrInsertDocument(Client client,String index,String type,String id,Map<String,Object> mapDocument){
		UpdateResponse updateResponse =null;
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(mapDocument);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(mapDocument)
		        .upsert(indexRequest);              
		try {
			updateResponse = client.update(updateRequest).get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("opt document error :", e);
		}
		return updateResponse;
	}
	
	class MultiQueryInfo{
		List<MultiQueryInfo> infos = new ArrayList<MultiQueryInfo>();
		private String index;
		private String type;
		private String[] id;
		
		public MultiQueryInfo(){}
		
		public MultiQueryInfo(String index, String type, String[] id) {
			this.index = index;
			this.type = type;
			this.id = id;
		}
		
		public String getIndex() {
			return index;
		}

		public String getType() {
			return type;
		}

		public String[] getId() {
			return id;
		}

		public void putMultiQueryInfo(String index,String type,String...ids){
			infos.add(new MultiQueryInfo(index,type,ids));
		}
		
		public List<MultiQueryInfo> getInfos(){
			return infos;
		}
	}
	

	public static void main(String[] args) {
		Client client = ESUtil.openClient("escluster", "192.168.174.128", null);
		/*try {
			ESUtil.xContentBuilderTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Map<String,Object> user = new HashMap<String,Object>();
		user.put("name", "sdsd");
		String mapDocument="{\"name\":\"赵六sdd\",\"age\":25,\"gender\":\"fomale\"}";
		//System.out.println(ESUtil.insertDocument(client, "index02", "type02", 1+"", mapDocument));
		
		//System.out.println(ESUtil.deleteIndex(client, "eswuye"));
		
		//System.out.println(ESUtil.createIndex(client, "eswuye"));
		
		//System.out.println(ESUtil.insertDocument(client, "index01", "user", 6+"", mapDocument));
		
		//ESUtil.deleteDocument(client, "index01", "user", 2+"");
		
		//System.out.println(ESUtil.deleteDocumentByQuery(client,QueryBuilders.boolQuery(), new String[]{"index01","index02"}));
		
		//System.out.println(ESUtil.updateDocument(client, "index01", "user", 6+"", user));
		
		//System.out.println(ESUtil.closeIndex(client, new String[]{"wuye","index01"}));
		
		//System.out.println(ESUtil.openIndex(client, new String[]{"wuye","index01"}));
		
		//System.out.println(ESUtil.existIndex(client, "wuye"));	
		
		
		//SearchHits termSearch = ESUtil.termSearch(client, "apiCode", "延期缴费", new String[]{"wuye"}, null);
		//SearchHits termSearch = ESUtil.termsSearch(client, "moduleCode", new String[]{"2","5"}, null, null);
		
		//SearchHits termSearch = ESUtil.rangeSearch(client, "moduleCode", 2,6, null, null);
		
		SearchHits termSearch = ESUtil.fullTextSearch(client, new String[]{"eswuye"}, new String[]{"pblog"}, "发卡");
		
		SearchHit[] hits = termSearch.getHits();
		System.out.println(hits.length);
		for(SearchHit s:hits){
			System.out.println(s.getSourceAsString());
		}
		
		//ESUtil.createIndexWithSettingsAndMapping(client, "pblog", 5, 1, null);
	}
}
