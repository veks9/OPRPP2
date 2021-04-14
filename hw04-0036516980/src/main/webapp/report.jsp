<%@page import="java.util.Random"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Funny story</title>
		<meta charset="UTF-8">
		<style>
			body {
				background-color: #${pickedBgCol != null ? pickedBgCol : "FFFFFF"}
			}
		</style>
	</head>
	<body>
		<h1>OS usage</h1>
		<p>Here are the results of OS usage in survey that we completed.</p>
		<img src="reportImage">
		<br><br><br>
		<a href="/webapp2">Home</a>
	</body>
</html>	