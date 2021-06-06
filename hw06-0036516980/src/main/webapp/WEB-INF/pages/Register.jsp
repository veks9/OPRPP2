<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Register</title>
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
		<h1>Register</h1>
		<form action="register" method="post">
			<label for="firstName">First name:</label><br>
			<input type="text" name="firstName" value=""><br>
			<c:if test="${form.hasError('firstName')}">
			<div class="error"><c:out value="${form.getError('firstName')}"/></div>
			</c:if>
			
			<label for="lastName">Last name:</label><br>
			<input type="text" name="lastName" value=""><br>
			<c:if test="${form.hasError('lastName')}">
			<div class="error"><c:out value="${form.getError('lastName')}"/></div>
			</c:if>
			
			<label for="text">E-mail:</label><br>
			<input type="text" name="email" value=""><br>
			<c:if test="${form.hasError('email')}">
			<div class="error"><c:out value="${form.getError('email')}"/></div>
			</c:if>
			
			<label for="nickName">Nickname:</label><br>
			<input type="text" name="nickName" value=""><br>
			<c:if test="${form.hasError('nickName')}">
			<div class="error"><c:out value="${form.getError('nickName')}"/></div>
			</c:if>
			
			<label for="password">Password:</label><br>
			<input type="password" name="password" value=""><br>
			<c:if test="${form.hasError('password')}">
			<div class="error"><c:out value="${form.getError('password')}"/></div>
			</c:if>
			
			<br>
			
			<input type="submit" value="Register">
		</form>	
	</body>
</html>