package com.data.trans.model;

import java.io.Serializable;

import com.data.trans.util.PagerUtil;

/**
 * @Date 2018年3月22日
 * @author dnc
 * @Description 系统账号模型
 */
public class SystemUser extends PagerUtil<SystemUser> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id; //主键
	private String userId; //用户唯一编码
	private String account;
	private String password;
	private String nick;//用户昵称
	private String name; //用户名
	private Integer sex; //性别
	private String phone;//联系方式
	private String email; //邮箱
	private String headImg;//头像
	private String address;//住址
	private String identity;//身份证号
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
}
