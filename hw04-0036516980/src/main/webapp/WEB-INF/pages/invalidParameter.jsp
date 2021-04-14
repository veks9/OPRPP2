<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Invalid parameter</title>
		<meta charset="UTF-8">
		<style>
			body {
				background-color: #${pickedBgCol != null ? pickedBgCol : "FFFFFF"}
			}
		</style>	
	</head>
	<body>
		<a href="/webapp2">Home</a>
		<p>Specified parameters were invalid. Here are allowed:<br>
			a has to be from interval [-100, 100]<br>
			b has to be from interval [-100, 100]<br>
			n has to be from interval [1, 5]
		</p>
	</body>
</html>	