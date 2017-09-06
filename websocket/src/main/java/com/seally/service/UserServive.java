package com.seally.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seally.utils.ClientResult;

public interface UserServive {

	ClientResult handRequest(HttpServletRequest request, HttpServletResponse response);

}
