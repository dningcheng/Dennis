<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<%@ include file="includes/common.jsp"%>
<head>
<style type="text/css">
	.container{width: 500px;height: 300px;border: solid 2px white;border-radius: 2px;margin-top: 100px;padding-top:70px;opacity: 0.8;color: #ECCD5D;}
	body{
		margin: 0px;padding: 0px;
	    width: 100%;height:auto;
	    background:url("${pageContext.request.contextPath}/images/login.jpg") no-repeat;
	    background-size: 100%;
	}
</style>
<script type="text/javascript">
	
</script>
</head>
<body>

<div class="container"> 
  <form:form method="post" commandName="${commandName}" htmlEscape="true" class="form-horizontal col-sm-offset-3 col-md-offset-3">
	  <!-- 登录页面只要提交5个属性：lt、execution、_eventId、username、password -->
	  <input type="hidden" name="lt" value="${loginTicket}" />
      <input type="hidden" name="execution" value="${flowExecutionKey}" />
      <input type="hidden" name="_eventId" value="submit" />
	  
	  <div class="form-group">
	    <label for="inputUserName" class="col-sm-2 control-label" >账号:</label>
	    <div class="col-sm-9">
	      <input type="text" name="username" value="${data}" class="form-control" id="inputUserName" placeholder="请输入用户名">
	    </div>
	  </div>
	  
	  <div class="form-group">
	    <label for="inputPassword" class="col-sm-2 control-label">密码:</label>
	    <div class="col-sm-9">
	      <input type="password" name="password" class="form-control" id="inputPassword" placeholder="请输入密码">
	    </div>
	  </div>
	  
	  <div class="form-group">
		<div class="col-sm-offset-2 col-sm-10">
		    <input class="btn-submit btn btn-info" name="submit" accesskey="l" value="<spring:message code="screen.welcome.button.login" />" tabindex="4" type="submit" />
		    <input class="btn-reset btn btn-info" name="reset" accesskey="c" value="<spring:message code="screen.welcome.button.clear" />" tabindex="5" type="reset" />
		    <!-- 以下这一句form:errors取出登录失败的异常信息 -->
		    <form:errors style="color: yellow;"/>
		</div>
	  </div>
	  
  </form:form>
</div>
</body>
