package com.seally.utils;

import java.util.List;

public class PageModule<T> {
	
//	private Integer topPage;
//	private Integer bottomPage;
	private Integer curPage=1;
//	private Integer prePage;
//	private Integer nexPage;
	private Integer totalRecods;
	private Integer totalPage;
	private int pageSize=10;
	private List<T> dataList;
	
	private String keyWords="门禁管理";
	
	public Integer getTopPage() {
		return 1;
	}
	public Integer getBottomPage() {
		return totalPage;
	}
	public Integer getCurPage() {
		return curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public Integer getPrePage() {
		return this.curPage>1?this.curPage-1:1;
	}
	public Integer getNexPage() {
		return this.curPage<this.totalPage?this.curPage+1:this.curPage;
	}
	public Integer getTotalRecods() {
		return totalRecods;
	}
	public void setTotalRecods(Integer totalRecods) {
		this.totalRecods = totalRecods;
		this.totalPage=(totalRecods%this.pageSize==0)?(totalRecods/this.pageSize):(totalRecods/this.pageSize+1);
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
}
