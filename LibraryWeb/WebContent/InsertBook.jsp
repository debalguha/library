<%@ page import="java.util.ArrayList" %>
<%@ page import="com.aug.dao.entities.BaseEntity" %>
<%@ page import="com.aug.dao.entities.Author" %>
<%@ page import="com.aug.dao.entities.Publisher" %>

<%
	ArrayList<BaseEntity> authors = (ArrayList<BaseEntity>) request.getAttribute("authors");
	ArrayList<BaseEntity> publishers = (ArrayList<BaseEntity>) request.getAttribute("publishers");
	
	String success = request.getParameter("success");
%>
<html>
<title>Insert Book Data</title>
<head>
	<script language="javascript">
		function validateData() {
			var title = document.bookFrm.bookTitle.value;
			if(title == "") {
				alert("Please enter the name of the Book!");
				return false;
			}

			return true;
		}

		function submitForm() {
			document.bookFrm.submit();
		}

		function changeOtherBoxExisting() {
			if(document.bookFrm.newAuthor.checked == true) {
				document.bookFrm.newAuthor.checked = false;
			} else {
				document.bookFrm.newAuthor.checked = true;
			}
		}
		function changeOtherBoxNew() {
			if(document.bookFrm.existingAuthor.checked == true) {
				document.bookFrm.existingAuthor.checked = false;
			} else {
				document.bookFrm.existingAuthor.checked = true;
			}
		}
	</script>
</head>
<body>
<% if(success != null && success.equalsIgnoreCase("yes")) { %>
<font color="green">Book was successfully inserted!</font>
<% } else if(success != null && success.equalsIgnoreCase("no")) { %>
<font color="red">Book was not inserted! Reason:<%=request.getParameter("userMessage") %></font>
<% } %>
	<form name="bookFrm" action="InsertBookData" method="post"">
		Enter title of book: <input type="text" name="bookTitle" />

		<br/>

		Existing Author: <input type="checkBox" name="existingAuthor" onclick="javascript:changeOtherBoxExisting();" checked/>
		New Author: <input type="checkBox" name="newAuthor" onclick="javascript:changeOtherBoxNew();" />
		
		<br/>
		Pick Existing Author for book:		
		<select name="bookAuthorExisting">
		<% for(BaseEntity author : authors) { %>
			<option value="<%=((Author) author).getAuthorName()%>"><%=((Author) author).getAuthorName()%></option>
		<% } %>
		</select>
		<br/>
		Enter New Author for book: <input type="text" name="bookAuthorNew" />

		<br/>

		Enter Publisher of book: 
		<select name="bookPublisher">
		<% for(BaseEntity publisher : publishers) { %>
			<option value="<%=((Publisher) publisher).getPublisherName()%>"><%=((Publisher) publisher).getPublisherName()%></option>
		<% } %>
		</select>
		<br/>
		
		<input type="button" onClick="javascript:validateData();submitForm();" value="Save Book Data" />
	</form>
</body>
</html>