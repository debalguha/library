package com.aug.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.aug.dao.entities.BaseEntity;
//import com.aug.dao.entities.Book;
import com.aug.dao.utils.ConnectionUtils;
import com.aug.exception.LibraryAppException;

public abstract class BaseDAO {

	private Connection connection = null;
	
	protected boolean operationSuccess = true;

	public Connection getConnection() {
		
		if(connection == null) {
			connection = ConnectionUtils.getConnection();
		}
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void closeResources(ResultSet rs, PreparedStatement pstmt) {
		try {
			if(rs != null) {
				rs.close();
				rs = null;
			} 
			
			if(pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			
			if(connection != null) {
				if(operationSuccess) 
					connection.commit();
				else 
					connection.rollback();
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract String getSelectSQL();
	
	public abstract String getInsertSQL();
	
	public abstract String getUpdateSQL();
	
	public abstract String getDeleteSQL();
	
	public abstract boolean insert(BaseEntity be) throws LibraryAppException;
	
	public abstract boolean update(BaseEntity be) throws LibraryAppException;
	
	public abstract boolean delete(BaseEntity be) throws LibraryAppException;
	
	public abstract List<BaseEntity> retrieve(BaseEntity be) throws LibraryAppException;


	public LibraryAppException handleException(Exception e) {
		
		operationSuccess = false;
		
		LibraryAppException lae = new LibraryAppException();
		if(e instanceof SQLException) {
			lae.setCause(e);
			lae.setUserMessage("Database error occurred. Please check the logs.");
		} else {
			lae.setCause(e);
			lae.setUserMessage("Runtime error occurred. Please check the logs.");
		}
		
		return lae;
	}

}
















