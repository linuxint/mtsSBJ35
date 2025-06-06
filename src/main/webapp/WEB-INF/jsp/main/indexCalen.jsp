<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>

<div class="col-lg-12">
    <h1 class="page-header">
        <a href="javascript: fn_moveDate('<c:out value="${preWeek}"/>')"><i class="fa fa-angle-left fa-fw"></i></a>

        <c:out value="${month}"/>월 <c:out value="${week}"/>째주
        <a href="javascript: fn_moveDate('<c:out value="${nextWeek}"/>')"><i class="fa fa-angle-right fa-fw"></i></a>
    </h1>
</div>

<div class="col-lg-12" id="weekDiv">
    <c:forEach var="calenList" items="${calenList}" varStatus="status">
        <div class="calendarColumn <c:if test="${calenList.istoday}">today</c:if>">
            <div class="panel <c:if test="${calenList.istoday}">panel-red</c:if> <c:if test="${!calenList.istoday}">panel-default</c:if> height100">
                <div class="panel-heading" style="text-align:center">
                    <c:out value="${calenList.month}"/>월 <c:out value="${calenList.day}"/>일 (<c:out value="${calenList.week}"/>)
                </div>
                <div class="panel-body">
                    <c:forEach var="items" items="${calenList.list}" varStatus="status">
                        <div class="calendarDay" onmouseover="calendarDayMouseover(event, '<c:out value="${items.ssno}"/>', '<c:out value="${calenList.date}"/>')" onmouseout="calendarDayMouseout()">
                            <c:if test='${items.userno==sessionScope.userno}'>
                                <a href="schForm?ssno=<c:out value="${items.ssno}"/>&sdseq=<c:out value="${items.sdseq}"/>"><c:out value="${items.sstitle}"/></a>
                            </c:if>
                            <c:if test='${items.ssno!=null and items.userno!=sessionScope.userno}'>
                                <a href="schRead?ssno=<c:out value="${items.ssno}"/>&sdseq=<c:out value="${items.sdseq}"/>"><c:out value="${items.sstitle}"/></a>
                            </c:if>
                            <c:if test='${items.ssno==null}'>
                                <span style="color:<c:out value="${items.fontcolor}"/>"><c:out value="${items.sstitle}"/></span>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:forEach>
    <div class="calenSlideButton calenSlideButton_left" onclick="ev_prevSlide()">&#10094;</div>
    <div class="calenSlideButton calenSlideButton_right" onclick="ev_nextSlide()">&#10095;</div>
</div>
