<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>用户列表</title>
 <!-- <link rel="stylesheet" href="../theme/default/style.css" type="text/css"> -->
 <script src="js/jquery-3.2.1.js"></script>
<script type="text/javascript">
	function search(curPage){
		var keywords = $("#keywords").val();
		$("#curPage").val(curPage);
		//window.location.href="finduser.action?curPage="+curPage+"&keywords="+keywords;
		window.location.href="finduser.action";
		console.log(keywords+"  "+curPage);
	}
</script>
</head>
<body>
	<h1>用户列表信息</h1>
	<form action="finduser.action" method="post" id="findForm">
		<input type="text" name="keyWords" id="keywords" size="80" value="${pm.keyWords}">
		<input type="hidden" readonly="readonly" name="curPage" id="curPage" value="${pm.curPage}">
		当前页：${pm.curPage}&nbsp;&nbsp;总页数：${pm.totalPage}&nbsp;&nbsp;总记录数：${pm.totalRecods}
		<button value="nextPage" onclick="search(${pm.topPage});">首页</button>
		<button value="nextPage" onclick="search(${pm.prePage});">上一页</button>
		<button value="nextPage" onclick="search(${pm.nexPage});">下一页</button>
		<button value="nextPage" onclick="search(${pm.totalPage});">尾页</button>
	</form>
	<table border="1">
		<tr>
			<th>用户Id</th>
			<th>用户账号</th>
			<th>模块类型</th>
			<th>操作方式</th>
			<th>车牌信息</th>
			<th>备注信息</th>
		</tr>
		<c:forEach items="${pm.dataList}" var="user">
			<tr>
				<td>${user.userId}</td>
				<td>${user.userAccount}</td>
				<td>${user.apiCode}</td>
				<td>${user.opMethod}</td>
				<td>${user.moduleCode}</td>
				<td>${user.opContent}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>