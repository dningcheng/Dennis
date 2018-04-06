package com.data.trans.config;

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
		registry.addViewController("/").setViewName("login");
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
}
