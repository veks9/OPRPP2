<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<style type="text/css">
	body {
		background-color: #${pickedBgCol!= null? pickedBgCol: "FFFFFF"}
	}
	table.rez td {
		text-align: center;
	}
</style>
</head>
<body>

	<h1>Voting results</h1>
	<p>These are the voting results.</p>
	<table border="1" cellspacing="0" class="rez">
		<thead>
			<tr>
				<th>Band</th>
				<th>Number of votes</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="result" items="${result}">
				<tr><td>${result.bandName}</td><td>${result.numberOfVotes}</td></tr>
			</c:forEach>
		</tbody>
	</table>

	<h2>Results as graph</h2>
	<img alt="Pie-chart" src="glasanje-grafika" width="400" height="400" />

	<h2>Results as XLS document</h2>
	<p>
		Results in XLS format are available <a href="glasanje-xls">here</a>
	</p>

	<h2>Other</h2>
	<p>Songs from winning band(s):</p>
	<ul>
		<c:forEach var="winner" items="${winners}">
		<li><a href="${winner.songUrl}"
			target="_blank">${winner.bandName}</a></li>
		</c:forEach>
	</ul>
	<br>
	<a href="/webapp2">Home</a>
</body>
</html>