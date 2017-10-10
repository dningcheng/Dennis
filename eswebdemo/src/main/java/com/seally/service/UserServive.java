package com.seally.service;

import com.seally.entity.Plog;
import com.seally.utils.PageModule;

public interface UserServive {

	void findPlog(PageModule<Plog> pm);

}
