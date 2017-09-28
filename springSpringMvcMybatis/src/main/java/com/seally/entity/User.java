package com.seally.entity;

public class User {
	
	private String orgId;
	private String userId;
	private String unitId;
	private String moduleCode;
	private String apiCode;
	private String userAccount;
	private String unitNname;
	private String opMethod;
	private String opContent;
	private String opResult;
	private String opTime;
	
	public User(){}
	
	public User(String userId, String moduleCode, String apiCode, String userAccount, String opMethod, String opContent,
			String opResult, String opTime) {
		this.userId = userId;
		this.moduleCode = moduleCode;
		this.apiCode = apiCode;
		this.userAccount = userAccount;
		this.opMethod = opMethod;
		this.opContent = opContent;
		this.opResult = opResult;
		this.opTime = opTime;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	public String getApiCode() {
		return apiCode;
	}
	public void setApiCode(String apiCode) {
		this.apiCode = apiCode;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUnitNname() {
		return unitNname;
	}
	public void setUnitNname(String unitNname) {
		this.unitNname = unitNname;
	}
	public String getOpMethod() {
		return opMethod;
	}
	public void setOpMethod(String opMethod) {
		this.opMethod = opMethod;
	}
	public String getOpContent() {
		return opContent;
	}
	public void setOpContent(String opContent) {
		this.opContent = opContent;
	}
	public String getOpResult() {
		return opResult;
	}
	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}
	public String getOpTime() {
		return opTime;
	}
	public void setOpTime(String opTime) {
		this.opTime = opTime;
	}
}
