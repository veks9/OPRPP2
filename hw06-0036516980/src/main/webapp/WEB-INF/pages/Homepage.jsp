<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	
	
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Login</title>
		<style type="text/css">
			.error {
			   font-family: fantasy;
			   font-weight: bold;
			   font-size: 0.9em;
			   color: #FF0000;
			}
			.formLabel {
			   display: inline-block;
			   width: 100px;
	                   font-weight: bold;
			   text-align: right;
	                   padding-right: 10px;
			}
			.formControls {
			  margin-top: 10px;
			}
		</style>
	</head>
	<body>
		<%
			String firstName = (String) session.getAttribute("current.user.fn");
			String lastName = (String) session.getAttribute("current.user.ln");
			String nickName = (String) session.getAttribute("current.user.nickName");
		%>
		<c:choose>
			<c:when test="<%= nickName == null %>">
				<h1>Login</h1>
				<form action="main" method="post">
					<label for="nickName">Nickname:</label><br>
					<input type="text" name="nickName" value=""><br>
					
					<label for="password">Password:</label><br>
					<input type="password" name="password" value=""><br>
					
					<c:if test="${loginError != null}">
					<div class="error"><c:out value="${loginError}"/></div>
					</c:if>
					
					<br>
					<input type="submit" value="Log in">
				</form>	
				<a href="register">Register here!</a>
			</c:when>
			<c:otherwise>
	        	Logged in as <%= firstName + " " + lastName %>
	        	<a href="logout" >Logout</a>
	    	</c:otherwise>
    	</c:choose>
    	
    	<div>
			<h3>List of registered authors</h3>
			<c:forEach var="rU" items="${registeredUsers}">
					<a href="author/${rU.nickName}">${rU.nickName}</a><br>
			</c:forEach>
		</div>
		
	</body>
</html>