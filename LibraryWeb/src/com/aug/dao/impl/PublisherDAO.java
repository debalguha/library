package com.aug.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aug.dao.entities.Publisher;
import com.aug.dao.entities.BaseEntity;

public class PublisherDAO extends BaseDAO{
	
	private static final String selectSQL = "select * from publisher where name = ?";
	private static final String selectAllSQL = "select * from publisher ";
	private static final String insertSQL = "insert into publisher  (name , address, phone) values (?,?,?)";
	private static final String updateSQL = "UPDATE publisher  SET name  = ?, address = ?, phone = ? WHERE book_id = ?";
	private static final String deleteSQL = "DELETE FROM publisher  WHERE name  = ?";


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
	public boolean insert(BaseEntity be) {
		boolean success = false;
		Publisher p = (Publisher) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getInsertSQL());
			pstmt.setString(1, p.getPublisherName());
			pstmt.setString(2, p.getAddress());
			pstmt.setString(3, p.getPhNumber());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public boolean update(BaseEntity be) {
		boolean success = false;
		Publisher  p = (Publisher) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getUpdateSQL());
			pstmt.setString(1, p.getPublisherName());
			pstmt.setString(2, p.getAddress());
			pstmt.setString(3, p.getPhNumber());
			pstmt.setString(4, p.getPublisherName());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public boolean delete(BaseEntity be) {
		boolean success = false;
		Publisher  p = (Publisher) be;
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(getDeleteSQL());
			pstmt.setString(1, p.getPublisherName());
			
			int i = pstmt.executeUpdate();
			
			if(i > 0) success = true;
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(null, pstmt);
		}
		
		return success;
	}

	@Override
	public List<BaseEntity> retrieve(BaseEntity be) {
		List<BaseEntity> publisherList = new ArrayList<BaseEntity>();
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(getSelectSQL());
			pstmt.setString(1, ((Publisher) be).getPublisherName());
			
			rs = pstmt.executeQuery();
			Publisher p = null;
			while(rs.next()) {
				p = new Publisher ();
				pstmt.setString(1, p.getPublisherName());
				pstmt.setString(2, p.getAddress());
				pstmt.setString(3, p.getPhNumber());
				publisherList.add(p);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(rs, pstmt);
		}
		
		return publisherList;
	}

	public List<BaseEntity> retrieveAll() {
		List<BaseEntity> publisherList = new ArrayList<BaseEntity>();
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(getSelectallsql());
			
			rs = pstmt.executeQuery();
			Publisher p = null;
			while(rs.next()) {
				p = new Publisher();
				p.setAddress(rs.getString("address"));
				p.setPhNumber(rs.getString("phone"));
				p.setPublisherName(rs.getString("name"));
				publisherList.add(p);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(rs, pstmt);
		}
		
		return publisherList;
	}

	public static String getSelectallsql() {
		return selectAllSQL;
	}
	
}
