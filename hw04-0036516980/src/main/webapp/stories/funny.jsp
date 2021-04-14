<%@page import="java.util.Random"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Funny story</title>
		<meta charset="UTF-8">
		<%
		int randomNumber = new Random().nextInt(0xFFFFFF);
		%>
		
		<style>
			body {
				background-color: #${pickedBgCol != null ? pickedBgCol : "FFFFFF"}
			}
			h1, p {
				color: #<%out.print(randomNumber);%>;
			}
		</style>
	</head>
	<body>
		<h1>What kind of tea is hard to swallow?</h1>
		<p>Reality.</p>
		<br><br><br>
		<a href="/webapp2">Home</a>
	</body>
</html>	