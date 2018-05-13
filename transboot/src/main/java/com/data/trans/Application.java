package com.data.trans;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Date 2017年11月20日
 * @author dnc
 */
@SpringBootApplication
@EnableTransactionManagement  //开启注解事务管理，等同于xml配置文件中的 <tx:annotation-driven />
@MapperScan("com.data.trans.mapper") //mybatis的mapper接口包路径
public class Application /*extends SpringBootServletInitializer*/{
	
	/*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }*/
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
