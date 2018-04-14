package com.data.trans.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2018年4月14日
 * @author dnc
 * @Description 分页类
 */
public class PagerUtil<T> {
	
	private Integer curPage = 1;
	private Integer pageSize = 10;
	private Integer total = 0;
	private List<T> dataList = new ArrayList<>();
	
	public Integer getCurIndex() {
		return (getCurPage()-1)*pageSize;
	}
	public Integer getTopPage() {
		return 1;
	}
	public Integer getPrePage() {
		return getCurPage()-1>1?getCurPage()-1:1;
	}
	public Integer getCurPage() {
		return curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public Integer getNextPage() {
		return getCurPage()+1<getBottomPage()?getCurPage()+1:getBottomPage();
	}
	public Integer getBottomPage() {
		if(getTotal() == 0){
			return getTopPage();
		}
		return getTotal()%getPageSize() == 0?getTotal()/getPageSize():(getTotal()/getPageSize()+1);
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	
}
