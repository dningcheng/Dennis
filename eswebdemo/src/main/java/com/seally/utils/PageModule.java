package com.seally.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import com.seally.annotation.EsField;
import com.seally.entity.Plog;

public class PageModule<T> {
	
//	private Integer topPage;
//	private Integer bottomPage;
	private Long curPage=1L;
//	private Integer prePage;
//	private Integer nexPage;
	private Long totalRecods;
	private Long totalPage;
	private int pageSize=10;
	private List<T> dataList;
	private List<Long> pagination = new ArrayList<Long>();
	
	private String keyWords="";
	
	public Integer getTopPage() {
		return 1;
	}
	public Long getBottomPage() {
		return totalPage;
	}
	public Long getCurPage() {
		return curPage;
	}
	public void setCurPage(Long curPage) {
		this.curPage = curPage>0?curPage:1;
	}
	public Long getPrePage() {
		return this.curPage>1?this.curPage-1:1;
	}
	public Long getNexPage() {
		return this.curPage<this.totalPage?this.curPage+1:this.curPage;
	}
	public Long getTotalRecods() {
		return totalRecods;
	}
	public void setTotalRecods(Long totalRecods) {
		this.totalRecods = totalRecods;
		this.totalPage=(totalRecods%this.pageSize==0)?(totalRecods/this.pageSize):(totalRecods/this.pageSize+1);
		for(int i=1;i<5;i++){
			Long next = this.curPage+i;
			if(next>this.totalPage)
				break;
			this.pagination.add(next);
		}
	}
	public Long getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Long totalPage) {
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
	
	public List<Long> getPagination() {
		return pagination;
	}
	public void setPagination(List<Long> pagination) {
		this.pagination = pagination;
	}
	public static void initModuleData(PageModule<Plog> pm,SearchHit[] hits){
		List<Plog> datas = new ArrayList<Plog>();
		for(SearchHit hit:hits){
			
			//获取每条记录的源文档
			Map<String, Object> source = hit.getSource();
			//获取每条记录的高亮字段Map
			Map<String, HighlightField> highlightFieldsMap = hit.getHighlightFields();
			
			Plog temp = new Plog();
			Field[] declaredFields = Plog.class.getDeclaredFields();
			for(Field field:declaredFields){
				field.setAccessible(true);
				if(field.isAnnotationPresent(EsField.class)){
					EsField declaredAnnotation = field.getDeclaredAnnotation(EsField.class);
					//获取该注解的属性值
					String esField = declaredAnnotation.esField();
					//获取该字段高亮记录
					HighlightField highlightField = highlightFieldsMap.get(esField);
					//如果该字段拥有匹配的高亮部分
					if(highlightField!=null){
						String highFieldContent="";
						//获取该字段被匹配分割后的所有部分
						Text[] fragments = highlightField.fragments();
						
						for(Text t:fragments){
							//拼接各部分（包括加入了高亮前后缀（匹配的部分）的内容和剩余内容）
							highFieldContent+=t.string();
						}
						//用加入了高亮标签并拼接好的字段替换原有该字段值以获的页面高亮显示效果
						source.put(esField, highFieldContent);
					}
					
					try {
						String simpleName = field.getType().getSimpleName();
						if("String".equals(simpleName)){
							if(source.get(esField)!=null)
							field.set(temp, source.get(esField).toString());
						}else if("Integer".equals(simpleName)){
							if(source.get(esField)!=null)
							field.set(temp, Integer.parseInt(source.get(esField).toString()));
						}else if("Double".equals(simpleName)){
							field.set(temp, Double.valueOf(source.get(esField).toString()));
						}else if("Date".equals(simpleName)){
							//field.set(temp, source.get(esField).toString());
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			datas.add(temp);
		}
		pm.setDataList(datas);
	}
}
