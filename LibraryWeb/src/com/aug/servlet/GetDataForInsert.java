package com.aug.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aug.dao.entities.Author;
import com.aug.dao.entities.BaseEntity;
import com.aug.dao.impl.AutherDAO;
import com.aug.dao.impl.PublisherDAO;
import com.aug.exception.LibraryAppException;

/**
 * Servlet implementation class GetDataForInsert
 */
public class GetDataForInsert extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDataForInsert() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			AutherDAO auth = new AutherDAO();
			List<BaseEntity> authorList;
				authorList = auth.retrieveAll();
			
			PublisherDAO pub = new PublisherDAO();
			List<BaseEntity> publisherList = pub.retrieveAll();
			
			request.setAttribute("authors", authorList);
			request.setAttribute("publishers", publisherList);
			
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/InsertBook.jsp");
			rd.forward(request, response);
		} catch (LibraryAppException e) {
			e.getCause().printStackTrace();
			RequestDispatcher rd = getServletContext().
				getRequestDispatcher("/InsertBook.jsp?success=no&userMessage=" + e.getUserMessage());
			rd.forward(request, response);
		}
		
	}

}






