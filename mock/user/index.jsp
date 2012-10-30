<%@ page language="java" contentType="application/json;charset=UTF-8" %>
<%
String method = request.getMethod();
if (method.equals("GET")) {
	response.setStatus(404);
	
%>
{"errorCode":"13","errorMsg":"no device cookie","deviceId":"5b7442f1-37aa-439b-8750-fa7b582ea0a2"}
<%
} else if (method.equals("POST") || method.equals("PUT")){
%>
{
	"id" : "5b7442f1-37aa-439b-8750-fa7b582ea0a2",
	"userId" : "a2f76e58-93b7-45ff-8acc-6c0a773e3bbf",
	"created" : 1350982075,
	"modified" : 1350982075,
	"status" : 2,
	"name" : null,
	"authRetry" : 0,
	"lastLoginIp" : null,
	"lastLoginTime" : null,
	"user":{
		"id": "a2f76e58-93b7-45ff-8acc-6c0a773e3bbf",
		"loginId":null,
		"loginType":null	
	}
}
<%	
} else if (method.equals("DELETE")) {
%>
{"status":"SUCCESS"}
<%	
}
%>