<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>

<c:choose>
    <c:when test="${sessionScope.userid eq null}">
        <jsp:forward page="/memberLogin"/>
    </c:when>
    <c:otherwise>
        <jsp:forward page="/index"/>
    </c:otherwise>


</c:choose>
