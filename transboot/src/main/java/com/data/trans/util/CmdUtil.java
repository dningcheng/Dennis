package com.data.trans.util;

/**
 * @author dnc
 * @since 2017年11月28日 下午10:05:28
 * 
 * 交互命令类
 * 
 */
public class CmdUtil {
	
	public static final Integer SUCCESS = 200;//处理成功
	
	public static final Integer ERR = 500;//处理失败或服务器错误
	
	public static final Integer UNKNOW = 404;//命令不识别
	
	public static final int CMD_START_TRANCE = 1;//开始转换
	
	public static final int CMD_STOP_TRANCE = 2;//停止转换
	
	public static final int CMD_GET_PROGRESS = 3;//获取处理进度
	
	private Integer cmd;//客户端请求代码
	
	private Integer code = SUCCESS;//服务端响应
	
	private Object data;//交换附加数据

	public CmdUtil(){}
	
	public CmdUtil(Integer code,Integer cmd){
		this.code = code;
		this.cmd = cmd;
	}
	
	public CmdUtil(Object data,Integer cmd){
		this.data = data;
		this.cmd = cmd;
	}
	
	public CmdUtil(Integer code,Object data,Integer cmd){
		this.code = code;
		this.data = data;
		this.cmd = cmd;
	}
	
	public static CmdUtil getNormal(Integer code,Integer cmd){
		return new CmdUtil(code,cmd);
	}
	
	public static CmdUtil getSuccess(){
		return new CmdUtil();
	}
	
	public static CmdUtil getSuccess(Object data,Integer cmd){
		return new CmdUtil(data,cmd);
	}
	
	public static CmdUtil getError(Integer cmd){
		return new CmdUtil(CmdUtil.ERR,cmd);
	}
	
	public static CmdUtil getError(Object data,Integer cmd){
		return new CmdUtil(CmdUtil.ERR,data,cmd);
	}
	
	public Integer getCmd() {
		return cmd;
	}

	public void setCmd(Integer cmd) {
		this.cmd = cmd;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
