package com.data.trans.config;

import org.ajaxanywhere.AAFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class DefaultViewConfig extends WebMvcConfigurerAdapter {
	
	/**
	 * 配置视图映射
	 * addViewController配置对外暴露映射，也就是请求映射
	 * setViewName配置返回的视图名称
	 * @param registry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//如何是直接的跳转而不需要其它逻辑处理，则可以直接配置在此如登录首页
		registry.addViewController("/").setViewName("index");
		//registry.addViewController("/").setViewName("forward:/login");//重定向
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}
	
	/**
     * 配置静态访问资源
     * addResourceHandler配置对外暴露映射，也就是请求映射
     * addResourceLocations配置实际访问文件目录，也可以指定到本地磁盘上，（springboot默认映射到 /static/）
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
	
    /**
    * 注册拦截器
    * addPathPatterns 用于添加拦截规则,多个以,号分隔
    * excludePathPatterns 用户排除拦截,多个以,号分隔
    * @param registry 暂时还有问题
    */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");
        super.addInterceptors(registry);
    }
    
    /**
     * @Date 2018年4月7日
     * @author dnc
     * @Description springboot整合ajaxAnywhere服务端（除开配置pom依赖外）的唯一配置
     * springboot 注入自己过滤器的方式：每一个都以一个FilterRegistrationBean包装注入
     * 由ajaxAnywhere的执行原理可知，它处理的时渲染过后的页面，因此注入ajaxAnywhere过滤器，过滤规则为.jsp后缀的所有请求。
     * @return
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AAFilter()); //注入一个ajaxAnywhere过滤器
        registration.addUrlPatterns("*.jsp"); //设置该过滤器过滤的文件类型
        //registration.setOrder(Integer.MAX_VALUE); //设置该过滤器的执行顺序，可以省略，因为指定了处理文件为.jsp后缀的请求，因此能够保证在请求被springmvc渲染结束后被该过滤器拦截到
        return registration;
    }
    
}
