package com.aug.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aug.dao.entities.BaseEntity;
import com.aug.dao.entities.Author;
import com.aug.exception.LibraryAppException;


public class AutherDAO extends BaseDAO{
	
	private static final String selectSQL = "select * from book_authors where book_id = ?";
	private static final String selectAllSQL = "select distinct author_name from book_authors";
	private static final String insertSQL = "insert into book_authors (book_id,author_name) values (?,?)";
	private static final String updateSQL = "UPDATE book_authors SET book_id = ?, author_name = ? WHERE book_id = ?";
	private static final String deleteSQL = "DELETE FROM book_authors WHERE book_id = ?";

	public AutherDAO() {
		
	}
	
	public AutherDAO(Connection conn) {
		this.setConnection(conn);
	}
	
	@Override
	public String getSelectSQL() {
		return selectSQL;
	}

	@Override
	public String getInsertSQL() {
		return insertSQL;
	}

	@Override
	public String getUpdateSQL() {
		return updateSQL;
	}

	@Override
	public String getDeleteSQL() {
		return deleteSQL;
	}

	@Override
	public boolean insert(BaseEntity be) throws LibraryAppException {
		boolean success = false;
		Author a = (Author) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getInsertSQL());
			pstmt.setInt(1, a.getBookId());
			pstmt.setString(2, a.getAuthorName());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
//			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public boolean update(BaseEntity be) throws LibraryAppException {
		boolean success = false;
		Author  a = (Author ) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getUpdateSQL());
			pstmt.setInt(1, a.getBookId());
			pstmt.setString(2, a.getAuthorName());
			pstmt.setInt(3, a.getBookId());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public boolean delete(BaseEntity be) throws LibraryAppException {
		boolean success = false;
		Author a = (Author) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getDeleteSQL());
			pstmt.setInt(1, a.getBookId());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public List<BaseEntity> retrieve(BaseEntity be) throws LibraryAppException {
		List<BaseEntity> authorList = new ArrayList<BaseEntity>();
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(getSelectSQL());
			pstmt.setInt(1, ((Author) be).getBookId());
			
			rs = pstmt.executeQuery();
			Author a = null;
			while(rs.next()) {
				a = new Author();
				a.setBookId(rs.getInt("book_id"));
				a.setAuthorName(rs.getString("authername"));
				authorList.add(a);
			}
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(rs, pstmt);
		}
		
		return authorList;
	}
	
	public List<BaseEntity> retrieveAll() throws LibraryAppException {
		List<BaseEntity> authorList = new ArrayList<BaseEntity>();
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(getSelectallsql());
			
			rs = pstmt.executeQuery();
			Author a = null;
			while(rs.next()) {
				a = new Author();
				//a.setBookId(rs.getInt("book_id"));
				a.setAuthorName(rs.getString("author_name"));
				authorList.add(a);
			}
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(rs, pstmt);
		}
		
		return authorList;
	}

	public static String getSelectallsql() {
		return selectAllSQL;
	}

}
