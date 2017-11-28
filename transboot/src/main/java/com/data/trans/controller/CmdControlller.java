package com.data.trans.controller;

import javax.annotation.Resource;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.data.trans.util.CmdUtil;
import com.data.trans.util.TransManager;

@Controller
public class CmdControlller {
	
	@Resource
	TransManager transManager;
	
	/**
	 * @author dnc
	 * @since 2017年11月29日 上午7:24:05
	 * 消息接受及返回交互方法：
	 * @MessageMapping 注解对应页面发送消息的参数一 stompClient.send("/myCmd", {}, JSON.stringify({'name': name}));发送消息的参数一
	 * @SendTo 注解对应客户端的订阅字符串（注意前缀为WebSocketConfig.registry.enableSimpleBroker("/myTopic");）
	 */
	@MessageMapping("/myCmd") 
    @SendTo("/myTopic/myCmdInter")
    public CmdUtil cmdInter(CmdUtil message) throws Exception {
		
		switch (message.getCmd()) {
			case CmdUtil.CMD_START_TRANCE:
				return CmdUtil.getNormal(transManager.startTrans());
			case CmdUtil.CMD_STOP_TRANCE:
				return CmdUtil.getSuccess();
			default:
				return CmdUtil.getError();
		}
		
    }
	
}
