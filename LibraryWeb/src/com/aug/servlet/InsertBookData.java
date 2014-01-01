package com.aug.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aug.dao.entities.Author;
import com.aug.dao.entities.Book;
import com.aug.dao.entities.Publisher;
import com.aug.dao.impl.AutherDAO;
import com.aug.dao.impl.BookDAO;
import com.aug.exception.LibraryAppException;

/**
 * Servlet implementation class InsertBookData
 */
public class InsertBookData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertBookData() {
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
			String title = request.getParameter("bookTitle");
			String author = null;
			String publisher = request.getParameter("bookPublisher");
			
			if("on".equals(request.getParameter("existingAuthor"))) {
				author = request.getParameter("bookAuthorExisting");
			} else {
				author = request.getParameter("bookAuthorNew");
			}
			
			BookDAO bookDAO = new BookDAO();
			
			int bookId = bookDAO.getNewBookId();
			
			Author auth = new Author();
			auth.setAuthorName(author);
			auth.setBookId(bookId);
			
			Publisher pub = new Publisher();
			pub.setPublisherName(publisher);
			
			Book b = new Book();
			b.setBookId(bookId);
			b.setBookName(title);
			b.setAuthor(auth);
			b.setPublisher(pub);
			
			bookDAO.insert(b);
			
			RequestDispatcher rd = getServletContext().
									getRequestDispatcher("/GetDataForInsert?success=yes");
			rd.forward(request, response);
		} catch(LibraryAppException e) {
			e.getCause().printStackTrace();
			RequestDispatcher rd = getServletContext().
				getRequestDispatcher("/GetDataForInsert?success=no&userMessage=" + e.getUserMessage());
			rd.forward(request, response);
			
		}
	}

}
