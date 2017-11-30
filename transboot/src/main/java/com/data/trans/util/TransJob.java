package com.data.trans.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.data.trans.model.SystemLog;
import com.data.trans.model.Translog;
import com.data.trans.service.SourceTableService;
import com.data.trans.service.TranslogService;

/**
 * @author dnc
 * @since 2017年11月19日 下午7:30:25
 * 具体数据迁移执行类
 */
public class TransJob implements Runnable{
	
	private Logger logger = LoggerFactory.getLogger(TransJob.class);  
	
	private DataSource dataSource;
	
	private ElasticDataSource esSource;
	
	private TranslogService translogService;
	
	private SourceTableService sourceTableService;
	
	private String index;//提交es集群中索引库索引
	
	private String type;//提交es集群中索引库类型
	
	private Integer bulkSize;//每批提交数目
	
	private Integer fetchIdMin;//任务抓取表主键id下限值（包含fetchIdMin）
	
	private Integer fetchIdMax;//任务抓取表主键id上限值（不包含fetchIdMax）
	
	private Integer fetchSize;//每次抓取表记录数
	
	private String fetchTableName;//抓取表名
	
	private Integer translogId;//指定执行记录id,根据此值判断执行模式
	
	public TransJob(){};
	
	public TransJob(DataSource dataSource, ElasticDataSource esSource, String index, String type,
			Integer bulkSize, Integer fetchIdMin, Integer fetchIdMax, Integer fetchSize, String fetchTableName,
			Integer translogId,TranslogService translogService,SourceTableService sourceTableService) {
		this.dataSource = dataSource;
		this.esSource = esSource;
		this.index = index;
		this.type = type;
		this.bulkSize = bulkSize;
		this.fetchIdMin = fetchIdMin;
		this.fetchIdMax = fetchIdMax;
		this.fetchSize = fetchSize;
		this.fetchTableName = fetchTableName;
		this.translogId = translogId;
		this.translogService = translogService;
		this.sourceTableService = sourceTableService;
	}

	public void trans(Integer beginId,Integer endId,Connection connection,Integer translogId){
		List<SystemLog> logs = new ArrayList<SystemLog>();
    	logs.add(new SystemLog());//占位
		
    	//--------------------mysql读取记录开始--------------------
    	try{
			PreparedStatement prepareStatement = connection.prepareStatement(String.format("select * from %s where id >= ? and id < ?",fetchTableName));
			prepareStatement.setInt(1,beginId);
			prepareStatement.setInt(2,endId);
			ResultSet executeQuery = prepareStatement.executeQuery();
	    	while (executeQuery.next()) {
	    		SystemLog log = new SystemLog();
	    		log.setId(executeQuery.getInt("id"));
	    		log.setOrgId(executeQuery.getInt("org_id"));
	    		log.setUserId(executeQuery.getInt("user_id"));
	    		log.setUnitId(executeQuery.getInt("unit_id"));
	    		log.setModuleCode(executeQuery.getString("module_code"));
	    		log.setApiCode(executeQuery.getString("api_code"));
	    		log.setUserAccount(executeQuery.getString("user_account"));
	    		log.setUnitName(executeQuery.getString("unit_name"));
	    		log.setOpMethod(executeQuery.getString("op_method"));
	    		log.setOpContent(executeQuery.getString("op_content"));
	    		log.setOpResult(executeQuery.getString("op_result"));
	    		log.setOpTime(executeQuery.getTime("op_time"));
	    		log.setModuleParkPlate(executeQuery.getString("module_park_plate"));
	    		logs.add(log);
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("mysql拉取数据失败:", e);
			return ;
		}
    	
    	logger.info(Thread.currentThread().getName()+" 获取id区间为：[ "+beginId+" , "+endId+" ) 的数据 [ "+(logs.size()-1)+" ] 条转移到ES!");
    	if(logs.size()<=1){//没有实际数据
    		updateTranslog(beginId,endId,null,Translog.NONE_TRANCE,translogId);//更新成功记录
    		return ;
    	}
    	
    	//--------------------es转移开始--------------------
    	int estransIdMin = logs.get(1).getId();//用于记录批量插入的开始id
    	int count = 0;//计数器
    	int estransIdMax = logs.get(logs.size()-1).getId()+1;//用于记录批量插入的结束id
    	try{
    		Client client = esSource.getClient();
        	BulkRequestBuilder bulkRequest = client.prepareBulk();
        	for (int i = 1; i < logs.size(); i++) {
        		count++;
        	    bulkRequest.add(client.prepareIndex(index, type).setSource(JSON.toJSONString(logs.get(i)), XContentType.JSON));
        	    // 每bulkSize条提交一次
        	    if (i % bulkSize == 0 || i == (logs.size()-1)) {
        	        bulkRequest.execute().actionGet();
        	        estransIdMax = logs.get(i).getId()+1;//最大id作为本次截止记录
        	        Integer tempMin = estransIdMin;
        	        updateTranslog(tempMin,estransIdMax,count,Translog.SUCCE_TRANCE,translogId);//更新成功记录
        	        bulkRequest = client.prepareBulk();//新开一个批次
        	        estransIdMin = estransIdMax ; //最后一次成功提交作为下一次开始记录
        	    	count = 0;//计数器归零
        	    }
        	}
        	esSource.releaseClient(client);
    	}catch (Exception e) {
    		e.printStackTrace();
    		logger.error("es服务存储数据失败:", e);
    		//记录失败区间[estransIdMin,estransIdMax)
    		updateTranslog(estransIdMin,estransIdMax,count,Translog.FAIL_TRANCE,translogId);//更新失败记录
		}
		
	}
	
	
	/**
	 * 根据无数据、成功、失败 记录转移结果
	 * 
	 * @author dnc
	 * @Date 2017-11-30
	 * 
	 * @param beginId id开始记录
	 * @param endId   id结束记录
	 * @param count   实际数据量（对于成功才有用）
	 * @param updateType 0：无数据记录   1：成功记录   2：失败记录
	 * @param translogId 记录表主键id值
	 */
	private void updateTranslog(Integer beginId,Integer endId,Integer count,Integer updateType,Integer translogId){
		
		Translog translog = translogService.getTranslogById(translogId);
		
		String oldFailBetween = translog.getFailBetween();
		String nowBetween = beginId+"-"+endId;
		if(updateType==Translog.NONE_TRANCE){//没有数据的记录
			String oldNonBetween = translog.getNoneBetween();
			if(StringUtils.hasText(oldNonBetween)){
				nowBetween += ("*"+oldNonBetween);
			}
			translog.setNoneBetween(nowBetween);
		}else if(updateType==Translog.SUCCE_TRANCE){//成功记录
			String failBetween = translog.getFailBetween();
			if(StringUtils.hasText(failBetween)){
				translog.setFailBetween(failBetween.replace(nowBetween, ""));//失败记录中去除现在成功的记录
			}
			translog.setFailCount(translog.getFailCount()-count);
		}else if(updateType==Translog.FAIL_TRANCE){//失败记录
			if(StringUtils.hasText(oldFailBetween)){
				nowBetween += ("*"+oldFailBetween);
			}
			translog.setFailBetween(nowBetween);
		}
		translogService.updateTranslogById(translog);
	}
	
	@Override
	public void run() {
		try {
			Connection connection = dataSource.getConnection();
			if(translogId != null){
				Translog translog = translogService.getTranslogById(translogId);
				String failBetween = translog.getFailBetween();//失败区间 格式：  1000-4000*6000-8000
				if(StringUtils.hasText(failBetween)){
					//解析failBetween
					String[] split = failBetween.split("*");
					for(String between:split){
						if(StringUtils.hasText(between)){
							String[] split2 = between.split("-");
							trans(Integer.valueOf(split2[0]),Integer.valueOf(split2[1]),connection,translog.getId());
						}
					}
				}
			}else{
				//计算一共需要抓取多少趟（可能有余）
				Integer num = (fetchIdMax-fetchIdMin)%fetchSize==0?(fetchIdMax-fetchIdMin)/fetchSize:(fetchIdMax-fetchIdMin)/fetchSize+1;
				
				//查询记录是否存在并传递主键
				String transName = Thread.currentThread().getName();
				Integer allCount = sourceTableService.countRealNumByBetween(fetchIdMin, fetchIdMax, fetchTableName);
				Translog translog = new Translog(transName,fetchTableName,fetchIdMin+"-"+fetchIdMax,allCount);
				Integer count = translogService.addTranslog(translog);
				if(count!=1){
					throw new Exception("初始化转移记录表translog失败");
				}
				
				for(int i=0;i<num;i++){//自动计算区间
					int beginId = i*fetchSize;
					int endId = beginId+fetchSize;
					if(i!=(num-1)){
						trans(fetchIdMin+beginId,fetchIdMin+endId,connection,translog.getId());
					}else{
						trans(fetchIdMin+beginId,fetchIdMax,connection,translog.getId());
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
}
