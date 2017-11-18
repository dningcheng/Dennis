package com.data.trans.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

@Component
public class MysqlUtil {
	
	@Autowired
	DruidDataSource dataSource;
	
	public void test() throws SQLException{
		DruidPooledConnection connection = dataSource.getConnection();
		PreparedStatement prepareStatement = connection.prepareStatement("select * from t_pb_log limit 0,10000");
		
		ResultSet executeQuery = prepareStatement.executeQuery();
    	long start = new Date().getTime();
    	while (executeQuery.next()) {
    		String string = executeQuery.getString("unit_name");
    		System.out.println(string);
		}
    	System.out.println(new Date().getTime()-start);
		
	}
}
