<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="x" uri="jakarta.tags.xml" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<c:if test="${searchVO.totPage>1}">

    <div id="dataTables-example_paginate" class="dataTables_paginate paging_simple_numbers">
        <ul class="pagination">
            <li title="First" class="paginate_button <c:if test="${searchVO.page==1}">disabled</c:if>"
                <c:if test="${searchVO.page!=1}">onclick="fnSubmitForm(1)"</c:if>>
                <a href="#"><i class="fa fa-angle-double-left fa-fw"></i> </a>
            </li>
            <li title="Previous" class="paginate_button <c:if test="${searchVO.page==1}">disabled</c:if>"
                <c:if test="${searchVO.page!=1}">onclick="fnSubmitForm(${searchVO.page-1})"</c:if>>
                <a href="#"><i class="fa fa-angle-left  fa-fw"></i> </a>
            </li>

            <c:forEach var="i" begin="${searchVO.pageStart}" end="${searchVO.pageEnd}" step="1">
                <c:choose>
                    <c:when test="${i eq searchVO.page}">
                        <li class="paginate_button active"><a href="#"><c:out value="${i}"/></a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="paginate_button " onclick="fnSubmitForm(${i})"><a href="#"><c:out value="${i}"/></a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <li title="Next" class="paginate_button <c:if test="${searchVO.page==searchVO.totPage}">disabled</c:if>"
                <c:if test="${searchVO.page!=searchVO.totPage}"> onclick="fnSubmitForm(${searchVO.page+1})"</c:if>>
                <a href="#"><i class="fa fa-angle-right  fa-fw"></i></a>
            </li>
            <li title="Last" class="paginate_button <c:if test="${searchVO.page==searchVO.totPage}">disabled</c:if>"
                <c:if test="${searchVO.page!=searchVO.totPage}"> onclick="fnSubmitForm(${searchVO.totPage})"</c:if>>
                <a href="#"><i class="fa fa-angle-double-right  fa-fw"></i></a>
            </li>
        </ul>
    </div>
</c:if>
