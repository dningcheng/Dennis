package com.data.trans.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.logging.log4j.core.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSON;
import com.data.trans.TransbootApplication;
import com.data.trans.annotation.EsField;
import com.data.trans.model.EsWyglLog;

import ch.qos.logback.core.net.SyslogOutputStream;

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
	    	client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.174.128"), 9300));
	    } catch (UnknownHostException e) {
	    	e.printStackTrace();
	    }
	}
	
	@Test
	public void testResult(){
		initClient();
		SearchResponse resp = multiMatchSearch("房屋认证www","logindex","systemlog",new String[]{"apiCode"});
		
		 List<Object> list = ElasticUtil.getDataListByHits(resp.getHits().getHits(), EsWyglLog.class);
		 System.out.println(list.size());
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
		SearchResponse resp = multiMatchSearch("logindex","systemlog","191673",new String[]{"id"});
	    List<Object> list = ElasticUtil.getDataListByHits(resp.getHits().getHits(), EsWyglLog.class);
	    System.out.println(JSON.toJSONString(list));
	}
	
	/**
	 * @Date 2018年1月12日
	 * @author dnc
	 * @Description 全文检索
	 * @return SearchResponse
	 */
	public static SearchResponse multiMatchSearch(String index,String type,String text,String... fields){
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
		//multiMatchQuery.operator(Operator.OR);
		searchBuilder.setQuery(multiMatchQuery);
		
		return searchBuilder.get();
	}
	
	/**
	 * 结果集合到bean的数据封装
	 * @param hits
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
				}
				list.add(newInstance);//加入集合
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("es结果集到实体封装异常："+e.getMessage());
			}
		}
		return list;
	}
	
	public static Map<String,Object> beanToESMapDocument(Class<?> logClazz, Object logInstance) throws Exception{
		Map<String,Object> map = new HashMap<>();
		//获取所有映射的属性名称及对应属性值
		Field[] logFields = logClazz.getDeclaredFields();
		for(Field field : logFields){
			field.setAccessible(true);
			EsField fieldAnnotation = field.getDeclaredAnnotation(EsField.class);
			if(fieldAnnotation!=null){//如果是被注解的属性
				String beamFieldTypeName = field.getType().getSimpleName();//实体类真实类型
				String esFieldName = fieldAnnotation.value();//es中的属性名称
				String declareFieldType = fieldAnnotation.value();//字符串日期类型传入时格式
				
				Object object = field.get(logInstance);//获取值
				if(object!=null){
					//处理日期格式的字段
					/*if(beamFieldTypeName.equals("Date") || "Date".equals(declareFieldType)){
						Date fieldValue = null;
						if(!"".equals(object.toString().trim())){//存在值
							if(!beamFieldTypeName.equals("Date")){
								String dateFormat = fieldAnnotation.format();//字符串日期类型的传入的格式
								SimpleDateFormat format = new SimpleDateFormat(dateFormat);
								fieldValue = format.parse(object.toString().trim());
							}else{
								fieldValue = (Date)object;
							}
							map.put(esFieldName, fieldValue);
						}
					}*/
					map.put(esFieldName,object);
				}
			}
		}
		//logger.info("es插入文档主体为："+JSON.toJSONString(map));
		return map;
	}
	
}

class TestClazz{
	private Integer id;
	private String name;
	private ElasticUtil util;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ElasticUtil getUtil() {
		return util;
	}
	public void setUtil(ElasticUtil util) {
		this.util = util;
	}
	
}