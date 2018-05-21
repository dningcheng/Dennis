//设置cookie，timeExp的格式为：d2、h20、s30  分别表示 2天、20小时、30秒
function setCookie(name,value,timeExp){
	var cookieStr = name + "="+ escape (value);
	var expire = getExpireTime(timeExp);
	if(expire && expire != 0){
		cookieStr += (";expires=" + expire);
	}
	document.cookie = cookieStr;
}
function getExpireTime(timeExp){
	var delay = 0;
	var timeNum=str.substring(1,str.length)*1;
	var unit=str.substring(0,1);
	if (unit=="s"){
		delay = timeNum*1000;
	}else if (unit=="h"){
		delay = timeNum*60*60*1000;
	}else if (unit=="d"){
		delay = timeNum*24*60*60*1000;
	}
	var exp = new Date();
	exp.setTime(exp.getTime() + delay);
	return exp.toGMTString();
}

//读取cookie
function getCookie(name){
	var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
	if(arr=document.cookie.match(reg))
		return unescape(arr[2]);
	else
		return null;
}

//删除cookie
function delCookie(name){
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval=getCookie(name);
	if(cval!=null)
		document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}

