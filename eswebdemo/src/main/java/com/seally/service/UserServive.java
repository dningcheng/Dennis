package com.seally.service;

import java.util.List;

import com.seally.entity.User;
import com.seally.utils.PageModule;

public interface UserServive {

	void findUser(PageModule<User> pm);

}
