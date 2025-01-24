<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<c:forEach var="listview" items="${listview}" varStatus="status">
    <li>
        <a href="boardRead?brdno=<c:out value="${listview.brdno}"/>">
            <div>
                <i class="fa fa-<c:out value="${listview.extfield1}"/> fa-fw"></i> <c:out value="${listview.brdtitle}"/>
                <span class="pull-right text-muted small"><c:out value="${listview.regdate}"/></span>
            </div>
        </a>
    </li>
    <li class="divider"></li>
</c:forEach>
<li>
    <a class="text-center" href="alertList">
        <strong>See All Alerts</strong>
        <i class="fa fa-angle-right"></i>
    </a>
</li>
