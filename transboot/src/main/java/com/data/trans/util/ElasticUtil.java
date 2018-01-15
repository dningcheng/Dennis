package com.data.trans.util;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.data.trans.annotation.EsDocument;
import com.data.trans.annotation.EsField;
import com.data.trans.model.SystemLog;

/**
 * @Date 2018年1月12日
 * @author dnc
 * @Description Elastic相关工具类
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！ 
@SpringBootTest(classes=ElasticUtil.class)
public class ElasticUtil {
	
	private static Client client = null;
	
	@BeforeClass
	public static void initClient(){
		// 设置集群名字
    	Settings settings = Settings.builder()
				.put("cluster.name", "escluster")
				.put("client.transport.sniff", true)//开启自动嗅探机制，可以自动链接集群中的其他节点
				//.put("client.transport.ignore_cluster_name", true)//客户端连接时是否验证集群名称
				.put("client.transport.ping_timeout", "5s")//ping节点的超时时间
				.put("client.transport.nodes_sampler_interval", "5s")//节点的超时时间
				.build();
	    try {
	    	// 读取的ip列表是以逗号分隔的
	    	client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.33"), 9300));
	    } catch (UnknownHostException e) {
	    	e.printStackTrace();
	    }
	}
	

	public static void testResult(){
		initClient();
		SearchResponse resp = multiMatchSearch(client,"logindex","systemlog","8888",new String[]{"id"});
		
		 List<Object> list = ElasticUtil.getDataListByHits(resp.getHits().getHits(), SystemLog.class);
		 System.out.println(list.size());
		 
		 SystemLog log = new SystemLog();
		 log.setId(8888);
		 log.setModuleCode("test");
		 log.setModuleParkPlate("鄂Qx1245");
		 log.setOpContent("测试数据");
		 log.setOpMethod("测试方法");
		 log.setOpResult("失败");
		 log.setOpTime(new Date());
		 System.out.println(insertDocument(client,log));
	}
	
	/**
	 * @Date 2018年1月12日
	 * @author dnc
	 * @Description 验证索引是否存在
	 * @param client
	 * @param index
	 * @return
	 */
	public static boolean existIndex(Client client,String index){
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	} 
	
	public static void main(String[] args) {
		initClient();
		SearchResponse resp = multiMatchSearch("logindex","systemlog","sbgl",new String[]{"apiCode"});
	    List<Object> list = ElasticUtil.getDataListByHits(resp.getHits().getHits(), SystemLog.class);
	    System.out.println(list.size()+":"+JSON.toJSONString(list));
//		
//		Map<String, Object> map = new HashMap<>();
//		
//		map.put("id", 999999);
//		IndexResponse  resp2 = insertDocument(client,"logindex","systemlog",map );
//		System.out.println(resp2.status().getStatus());
//		System.out.println(delDocumentById(client, "logindex2","systemlog2", "AWD1HFM6tQsuvrC2Q_Bh"));
	}
	
	
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 插入文档
	 * @param client
	 * @param index 索引库
	 * @param type 类型
	 * @param mapDocument 文档内容
	 * @return
	 */
	public static boolean insertDocument(Client client,String index,String type,Map<String,Object> mapDocument){
		IndexResponse response = client.prepareIndex(index, type)
		        .setSource(mapDocument)
		        .get();
		return response==null?false:response.status().getStatus()==201;
	}
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 插入文档
	 * @param client
	 * @param index 索引库
	 * @param type 类型
	 * @param jsonDocument 文档内容
	 * @return
	 */
	public static boolean insertDocument(Client client,String index,String type,String jsonDocument){
		IndexResponse response = client.prepareIndex(index, type)
		        .setSource(jsonDocument,XContentType.JSON)
		        .get();
		return response==null?false:response.status().getStatus()==201;
	}
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 插入文档
	 * @param client
	 * @param obj 需要注解EsDocument 用来获取文档存储的索引库和类型参数
	 */
	public static boolean insertDocument(Client client,Object obj){
		Class<?> clazz = obj.getClass();
		EsDocument classAnnotation  = clazz.getDeclaredAnnotation(EsDocument.class);
		//获取索引和类型
		if(classAnnotation == null)	return false;
		String index = classAnnotation.index();
		String type = classAnnotation.type();
		if(index == null || type == null)	return false;
		return insertDocument(client,index,type,obj);
	}
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 插入文档
	 * @param client
	 * @param index
	 * @param type
	 * @param obj
	 * @return
	 */
	public static boolean insertDocument(Client client,String index,String type,Object obj){
		try {
			Map<String,Object> map = new HashMap<>();
			Field[] clazzFields = obj.getClass().getDeclaredFields();
			for(Field field : clazzFields){
				field.setAccessible(true);
				EsField fieldAnnotation = field.getDeclaredAnnotation(EsField.class);
				
				if(fieldAnnotation == null)	continue;
				
				//实体bean的真实字段类型名
				String objFieldTypeName = field.getType().getSimpleName();
				
				//实体bean的真实字段名
				String objFieldName = field.getName();
				//es中对应的映射属性名称
				String esFieldName = fieldAnnotation.value();
				
				if("".equals(esFieldName)){
					esFieldName = objFieldName;
				}
				//获取实体bean属性值
				Object object = field.get(obj);//获取属性值
				
				//对于日期，保存时间戳
				if(objFieldTypeName.equals("Date") && object != null){
					map.put(esFieldName,((Date)object).getTime());
				}else{
					map.put(esFieldName,object);
				}
			}
			return insertDocument(client,index,type,map);
		} catch (Exception e) {
			System.out.println("文档保存异常："+e.getMessage());
			return false;
		}
	}
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 删除文档
	 * @param client
	 * @param index 索引
	 * @param type 类型
	 * @param id 文档id
	 * @return
	 */
	public static boolean delDocumentById(Client client,String index,String type,String id){
		DeleteResponse response = client.prepareDelete(index, type, id)
				.get();
		return response==null?false:response.status().getStatus()==200;
	}
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 删除索引
	 * @param indices
	 * @return
	 */
	public static boolean delIndex(Client client,String... indices){
		DeleteIndexResponse deleteIndexResponse = client
				.admin()
				.indices()
				.prepareDelete(indices)
				.get();
		return deleteIndexResponse.isAcknowledged();
	}
	
	/**
	 * @Date 2018年1月12日
	 * @author dnc
	 * @Description 全文检索
	 * @return SearchResponse
	 */
	public static SearchResponse multiMatchSearch(Client client,String index,String type,String text,String... fields){
		/*{
			  "query": {
			    "multi_match": {
			        "query":    "基础管理  成功",
			        "fields":   [ "apiCode", "opResult" ]
			    }
			  }
		  }*/
		SearchRequestBuilder searchBuilder = client.prepareSearch().setIndices(index).setTypes(type);
		MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(text, fields);
		//multiMatchQuery.operator(Operator.AND);
		multiMatchQuery.type(Type.BEST_FIELDS);
		multiMatchQuery.minimumShouldMatch("30%");
		searchBuilder.setQuery(multiMatchQuery);
		
		return searchBuilder.get();
	}
	
	/**
	 * @Date 2018年1月14日
	 * @author dnc
	 * @Description 从es结果集获取实体集合
	 * @param hits
	 * @param clazz
	 * @return
	 */
	public static List<Object> getDataListByHits(SearchHit[] hits,Class<?> clazz){
		List<Object> list  =new ArrayList<>();
		for(SearchHit hit:hits){//遍历获取每条记录
			//获取源文档
			Map<String, Object> source = hit.getSource();
			try {
				Object newInstance = clazz.newInstance();
				if(newInstance == null) continue;
				boolean addFlag = false;
				//获取该类所有属性
				Field[] logFields = clazz.getDeclaredFields();
				for(Field field : logFields){
					field.setAccessible(true);
					//尝试获取该字段所添加的es字段注解EsField
					EsField fieldAnnotation = field.getDeclaredAnnotation(EsField.class);
					
					//如果没有被注解，忽略类的该属性处理
					if(fieldAnnotation == null) continue;
					
					//获取实体类该属性的真实类型
					String beamFieldTypeName = field.getType().getSimpleName();
					
					//获取实体类该属性对应es中的映射字段名称
					String annotationFieldName = fieldAnnotation.value();
					String esFieldName = "".equals(annotationFieldName)?field.getName():annotationFieldName;
					
					//根据注解的es映射字段名获取结果中查出来的字段值
					Object object = source.get(esFieldName);
					
					//字段值为空，不对类的该字段进行处理
					if(object==null) continue;
					
					//处理日期格式的字段
					if(beamFieldTypeName.equals("Date")){
						String value = object.toString();
						String pattern = "[0-9]*";
						if(Pattern.matches(pattern, value)){
							field.set(newInstance, new Date(Long.valueOf(value)));
						}else if(value!=null && value.length()>0){
							try {
								field.set(newInstance, new SimpleDateFormat(fieldAnnotation.format()).parse(value));
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println("日期转化异常："+e.getMessage());
							}
						}
					}else{
						field.set(newInstance, object);
					}
					addFlag = true;
				}
				if(addFlag)	list.add(newInstance);//加入集合
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("es结果集到实体封装异常："+e.getMessage());
			}
		}
		return list;
	}
}
