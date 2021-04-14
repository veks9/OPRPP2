<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<head>
<style type="text/css">
	body {
		background-color: #${pickedBgCol!= null? pickedBgCol: "FFFFFF"}
	}
</style>
</head>
<html>
<body>
	<h1>Vote for your favorite band</h1>
	<p>Which band is your favorite? Follow the link to vote!</p>
	<ol>
		<c:forEach var="entry" items="${bands}">
			<li><a href="glasanje-glasaj?id=${entry.value.id}">${entry.value.bandName}</a></li>
		</c:forEach>
	</ol>
	<br>
	<a href="/webapp2">Home</a>
</body>
</html>