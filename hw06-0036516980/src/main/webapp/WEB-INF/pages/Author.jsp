<%@page import="hr.fer.zemris.java.tecaj_13.model.BlogUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
	</head>
	<body>
		<% String sessionNickName = (String) session.getAttribute("current.user.nickName"); 
			BlogUser author = (BlogUser) request.getAttribute("author");%>
		<div>
			<h3>List of blog entries</h3>
			<c:forEach var="entry" items="${authorBlogEntries}">
					<a href="${entry.id}">${entry.title}</a>
					<c:if test="<%= sessionNickName.equals(author.getNickName()) %>">
						<a href="<%= sessionNickName %>/new">New</a>
						<a href="<%= sessionNickName %>/edit">Edit</a>
					</c:if>
					</br>
			</c:forEach>
		</div>
	</body>
</html>