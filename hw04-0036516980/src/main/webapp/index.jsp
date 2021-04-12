<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Home</title>
		<meta charset="UTF-8">
		<style>
			body {
				background-color: #${pickedBgCol};
			}
		</style>
	</head>
	<body>
		<a href="setcolor">Background color chooser</a><br>
		<hr>
		<a href="trigonometric?a=0&b=90">Trigonometric functions</a>
	</body>
</html>	