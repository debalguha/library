package com.aug.dao.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtils {


	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/library", 
					"root", "");
			
			conn.setAutoCommit(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}
}
