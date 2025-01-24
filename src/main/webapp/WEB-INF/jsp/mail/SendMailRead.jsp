<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
    </script>
</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-envelope-o fa-fw"></i> 보낸 메일</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="panel panel-default">
                <div class="panel-heading"><c:out value="${mailInfo.emsubject}"/></div>
                <div class="panel-body">
                    <div class="list-group">
                        <div class="list-group-item"><label>보낸 사람</label> &nbsp;&nbsp;&nbsp;<c:out value="${mailInfo.emfrom}"/> <c:out value="${mailInfo.regdate}"/></div>
                        <div class="list-group-item"><label>받는 사람</label> &nbsp;&nbsp;&nbsp;
                            <c:forEach var="item" items="${mailInfo.emto}" varStatus="status">
                                <c:out value="${item.eaaddress}"/><c:if test="${!status.last}">;</c:if>
                            </c:forEach>
                        </div>
                        <c:if test="${mailInfo.emcc.size() > 0}">
                            <div class="list-group-item"><label>참조</label> &nbsp;&nbsp;&nbsp;
                                <c:forEach var="item" items="${mailInfo.emcc}" varStatus="status">
                                    <c:out value="${item.eaaddress}"/><c:if test="${!status.last}">;</c:if>
                                </c:forEach>
                            </div>
                        </c:if>
                        <c:if test="${mailInfo.embcc.size() > 0}">
                            <div class="list-group-item"><label>숨은 참조</label> &nbsp;&nbsp;&nbsp;
                                <c:forEach var="item" items="${mailInfo.embcc}" varStatus="status">
                                    <c:out value="${item.eaaddress}"/><c:if test="${!status.last}">;</c:if>
                                </c:forEach>
                            </div>
                        </c:if>
                        <c:if test="${mailInfo.files.size() > 0}">
                            <div class="list-group-item"><label>파일</label> &nbsp;&nbsp;&nbsp;
                                <c:forEach var="item" items="${mailInfo.files}" varStatus="status">
                                    <a href="fileDownload?filename=<c:out value="${item.filename}"/>&downname=<c:out value="${item.realname }"/>">
                                        <c:out value="${item.filename}"/></a> <c:out value="${item.size2String()}"/><br/>
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>
                    <div class="col-lg-12">
                        <c:out value="${mailInfo.emcontents}" escapeXml="false"/>
                    </div>
                </div>
            </div>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('sendMails')"><s:message code="common.btnList"/></button>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('sendMailDelete?emno=<c:out value="${mailInfo.emno}"/>', '<s:message code="common.btnDelete"/>')"><s:message code="common.btnDelete"/></button>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>