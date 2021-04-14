<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	
<!DOCTYPE html>
<html>
	<head>
		<title>Colors</title>
		<meta charset="UTF-8">
		<style>
			body {
				background-color: #${pickedBgCol != null ? pickedBgCol : "FFFFFF"}
			}
		</style>
	</head>
	<body>
		<a href="setcolor?color=FFFFFF">WHITE</a><br>
		<a href="setcolor?color=FF0000">RED</a><br>
		<a href="setcolor?color=00FF00">GREEN</a><br>
		<a href="setcolor?color=00CCFF">CYAN</a><br>
		<br><br><br>
		<a href="/webapp2">Home</a>
	</body>
</html>