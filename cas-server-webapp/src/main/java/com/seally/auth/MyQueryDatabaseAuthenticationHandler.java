package com.seally.auth;

import java.security.GeneralSecurityException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.StringUtils;

import com.seally.utils.EncryptUtil;

/**
 * @author dennis
 * 自定义登录验证类
 * 在验证失败的地方可抛出以下异常：
 * FailedLoginException 密码错误异常
 * AccountNotFoundException 账号未找到异常
 * AccountDisabledException 账号不可用异常
 * AccountLockedException 账号被锁定异常
 * CredentialExpiredException 密码已失效异常
 * InvalidLoginLocationException 非法地点登录异常
 * InvalidLoginTimeException 非法时间登录异常
 * 这些异常抛出后可由 WEB-INF下cas-servlet.xml文件中的<bean id="authenticationExceptionHandler" class="org.jasig.cas.web.flow.AuthenticationExceptionHandler" />
 * 捕获，并根据 WEB-INF/spring-configuration下applicationContext.xml文件中<util:list id="basenames">配置的生效配置文件中如messages_zh_CN.properties中定义的以authenticationFailure.异常类名 = 自定义异常提示信息
 * 在cas登录界面上由<form:errors path="*" id="msg" cssClass="errors" element="div" htmlEscape="false" />标签打印出
 * 
 * 备注：配置时需要注意为<bean id="terminateWebSessionListener" class="org.jasig.cas.web.flow.TerminateWebSessionListener" p:timeToDieInSeconds="10" /> 增加一个超时属性p:timeToDieInSeconds="10"
 */
public class MyQueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

	@NotNull
	private String sql;

	@Override
	protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential)
			throws GeneralSecurityException, PreventedException {
		
		
		
		final String username = credential.getUsername();
		//final String encryptedPassword2 = this.getPasswordEncoder().encode(credential.getPassword());
		
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(credential.getPassword())){
			throw new FailedLoginException();
		}
		
		final String encryptedPassword = EncryptUtil.Encrypt(credential.getPassword().trim(), EncryptUtil.ENC_SHA256,true);
		
		try {
			final String dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, username);
			if (!dbPassword.equals(encryptedPassword)) {
				throw new FailedLoginException();
			}
		} catch (final IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() == 0) {
				throw new AccountNotFoundException();
			} else {
				throw new FailedLoginException();
			}
		} catch (final DataAccessException e) {
			throw new PreventedException(e);
		}
		return createHandlerResult(credential, new SimplePrincipal(username), null);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
