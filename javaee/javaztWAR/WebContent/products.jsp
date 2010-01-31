<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="f"  uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h"  uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags/" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	request.setAttribute("category", request.getParameter("cat"));
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="style.css" media="screen" />
<title>Sklep internetowy</title>
</head>
<body>
<f:view>
	<jsp:setProperty name="customer" property="category" value="${requestScope.category}" />
	<c:set var="p_index" scope="page" value="${customer.productId-1}"></c:set>

	<jsp:include page="include/begin.jspf"></jsp:include>

    	Lista produktów:<br/><br/>
    	<h:panelGrid width="400" columns="2">
    	<c:forEach var="product" items="#{customer.productsFromCategory}">
    		<h:panelGroup>
		  		<a href="product.jsp?product_id=${product.id}">${product.title}</a>
		  	</h:panelGroup><h:panelGroup>
		  		${product.priceZloty}.${product.priceGrosz} zł
		  	</h:panelGroup>
		</c:forEach>
		</h:panelGrid>

	<my:menu/>
</f:view>
</body>
</html>