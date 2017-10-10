package com.seally.service.impl;

import javax.annotation.Resource;

import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import com.seally.dao.UserDao;
import com.seally.entity.Plog;
import com.seally.service.UserServive;
import com.seally.utils.ESUtil;
import com.seally.utils.PageModule;

@Service
public class UserServiceImpl implements UserServive {
	
	@Resource
	UserDao userDao;
	
	Client openClient = ESUtil.openClient("escluster", "", 9300);

	@Override
	public void findPlog(PageModule<Plog> pm) {
		Client openClient = ESUtil.openClient("escluster", "192.168.31.164", 9300);
		SearchHits searchHits = ESUtil.fullTextSearch(openClient, new String[]{"test06"}, null, pm);
		pm.setTotalRecods(searchHits.getTotalHits());
		PageModule.initModuleData(pm, searchHits.getHits());
	}

	
}
