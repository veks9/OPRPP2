<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	
	
<%
	long currentMillis = System.currentTimeMillis();
	long timeStarted = (Long)session.getServletContext().getAttribute("time");
	
	long timeElapsed = currentMillis - timeStarted;
	
	long d = 24 * 60 * 60 * 1000;
	long days = timeElapsed / d;
	d /= 24;
	long hours = (timeElapsed / d) % 24;
	d /= 60;
	long minutes = (timeElapsed / d) % 60;
	d /= 60;
	long seconds = (timeElapsed / d) % 60;
	d /= 1000;
	long miliseconds = timeElapsed % 1000;
	
	StringBuilder sb = new StringBuilder();
	sb.append(days == 0 ? "" : days + " days ");
	sb.append(hours == 0 ? "" : hours + " hours ");
	sb.append(minutes == 0 ? "" : minutes + " minutes ");
	sb.append(seconds == 0 ? "" : seconds + " seconds ");
	sb.append("and " + (miliseconds == 0 ? "" : miliseconds + " miliseconds."));
	String timeRunning = sb.toString();
%>


	
<!DOCTYPE html>
<html>
	<head>
		<title>App info</title>
		<meta charset="UTF-8">
		<style>
			body {
				background-color: #${pickedBgCol != null ? pickedBgCol : "FFFFFF"}
			}
		</style>
	</head>
	<body>
		<h2>This web application has been running for: <% out.print(timeRunning); %> </h2>
		<br>
		<a href="/webapp2">Home</a>
	</body>
</html>