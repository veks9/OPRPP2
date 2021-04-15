<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Trigonometric functions</title>
		<meta charset="UTF-8">
		<style>
			body {
				background-color: #${pickedBgCol != null ? pickedBgCol : "FFFFFF"}
			}
		</style>	
	</head>
	<body>
		<a href="/webapp2">Home</a>
		<div>
			<table>
				<tr><td>Number</td><td>Sin(number)</td><td>Cos(number)</td></tr>
				<c:forEach var="trigonometricEntry" items="${list}">
					<tr><td>${trigonometricEntry.number}</td><td>${trigonometricEntry.sin}</td><td>${trigonometricEntry.cos}</td></tr>
				</c:forEach>
			</table>
		</div>
	</body>
</html>	