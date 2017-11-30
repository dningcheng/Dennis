package com.data.trans.model;

/**
 * @author dnc
 * 2017年11月30日
 * 转移记录实体
 */
public class Translog {
	
	public static final Integer NONE_TRANCE  = 0; //无数据转移
	public static final Integer SUCCE_TRANCE = 1; //转移成功
	public static final Integer FAIL_TRANCE  = 2; //转移失败
	
	
	private Integer id;         //主键
	private String transName;   //转移任务名
	private String transTable;  //转移表名
	private String allBetween;  //该任务转移的id总区间
	private Integer allCount;   //该任务转移的实际存在记录数
	private String nonBetween;  //该任务转移没有实际记录的id区间
	private Integer sucCount;   //该任务转移成功实际记录数
	private String failBetween; //该任务转移失败的id区间
	private Integer failCount;  //该任务转移失败实际记录数
	
	public Translog(){}
	
	
	public Translog(String transName, String transTable, String allBetween,Integer allCount) {
		this.transName = transName;
		this.transTable = transTable;
		this.allBetween = allBetween;
		this.allCount = allCount;
		this.failCount = allCount;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTransName() {
		return transName;
	}
	public void setTransName(String transName) {
		this.transName = transName;
	}
	public String getTransTable() {
		return transTable;
	}
	public void setTransTable(String transTable) {
		this.transTable = transTable;
	}
	public String getAllBetween() {
		return allBetween;
	}
	public void setAllBetween(String allBetween) {
		this.allBetween = allBetween;
	}
	public Integer getAllCount() {
		return allCount;
	}
	public void setAllCount(Integer allCount) {
		this.allCount = allCount;
	}
	public String getNonBetween() {
		return nonBetween;
	}
	public void setNonBetween(String nonBetween) {
		this.nonBetween = nonBetween;
	}
	public Integer getSucCount() {
		return sucCount;
	}
	public void setSucCount(Integer sucCount) {
		this.sucCount = sucCount;
	}
	public String getFailBetween() {
		return failBetween;
	}
	public void setFailBetween(String failBetween) {
		this.failBetween = failBetween;
	}
	public Integer getFailCount() {
		return failCount;
	}
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	
}
