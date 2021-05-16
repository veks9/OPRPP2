<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Home</title>
	</head>
	<body>
		<h1>Available polls:</h1>
		<c:forEach var="p" items="${polls}">
			${p.title}<br>
			${p.message} -> <a href="glasanje?pollID=${p.id}">link</a>
			<br><br><br>  
		</c:forEach>		
</body>
</html>