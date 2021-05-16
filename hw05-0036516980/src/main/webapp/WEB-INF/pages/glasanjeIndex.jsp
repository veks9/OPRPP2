<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
		body {
			background-color: #${pickedBgCol!= null? pickedBgCol: "FFFFFF"
		}
		}
		</style>
	</head>
	
	<body>
		<h1>${pollTitle}</h1>
		<p>${pollMessage}</p>
		<ol>
			<c:forEach var="entry" items="${pollOptions}">
				<li><a href="glasanje-glasaj?pollID=${pollID}&pollOptionID=${entry.id}">${entry.title}</a></li>
			</c:forEach>
		</ol>
		<br>
		<a href="/voting-app">Home</a>
	</body>
</html>