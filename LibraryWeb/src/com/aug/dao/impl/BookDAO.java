package com.aug.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aug.dao.entities.BaseEntity;
import com.aug.dao.entities.Book;
import com.aug.exception.LibraryAppException;

public class BookDAO extends BaseDAO {
	
	private static final String selectSQL = "select * from book where book_id = ?";
	private static final String selectAllSQL = "select * from";
	private static final String insertSQL = "insert into book (book_id, title, publisher_name) values (?,?,?)";
	private static final String getBookIdSQL = "select max(book_id)+1 as new_book_id from book";

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeleteSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insert(BaseEntity be) throws LibraryAppException {
		
		boolean success = false;
		Book b = (Book) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getInsertSQL());
			pstmt.setInt(1, b.getBookId());
			pstmt.setString(2, b.getBookName());
			pstmt.setString(3, b.getPublisher().getPublisherName());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
			
			AutherDAO auth = new AutherDAO(conn);
			auth.insert(b.getAuthor());
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public boolean update(BaseEntity be) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(BaseEntity be) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<BaseEntity> retrieve(BaseEntity be) throws LibraryAppException {
		
		List<BaseEntity> bookList = new ArrayList<BaseEntity>();
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(getSelectSQL());
			pstmt.setInt(1, ((Book) be).getBookId());
			
			rs = pstmt.executeQuery();
			Book b = null;
			while(rs.next()) {
				b = new Book();
				b.setBookId(rs.getInt("book_id"));
				b.setBookName(rs.getString("title"));
				bookList.add(b);
			}
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(rs, pstmt);
		}
		
		return bookList;
	}

	public int getNewBookId() throws LibraryAppException {
		
		int newBookId = 0;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(getGetbookidsql());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				newBookId = rs.getInt("new_book_id");
			}
		} catch(SQLException e) {
			throw handleException(e);
		} finally {
			closeResources(rs, pstmt);
		}
		
		return newBookId;
	}
	
	public static String getSelectallsql() {
		return selectAllSQL;
	}

	public static String getGetbookidsql() {
		return getBookIdSQL;
	}

}
