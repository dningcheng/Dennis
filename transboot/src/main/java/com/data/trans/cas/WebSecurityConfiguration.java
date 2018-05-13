package com.data.trans.cas;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import net.unicon.cas.client.configuration.CasClientConfigurerAdapter;
import net.unicon.cas.client.configuration.EnableCasClient;

/**
 * @Date 2018年5月13日
 * @author dnc
 * @Description
 */
@Configuration
@EnableCasClient
public class WebSecurityConfiguration extends CasClientConfigurerAdapter {
	public void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter) {
        this.configureAuthenticationFilter(authenticationFilter);
        //authenticationFilter.getInitParameters().put("authenticationRedirectStrategyClass", "com.patterncat.CustomAuthRedirectStrategy");
    }

}
