<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/ad_header.jsp" %>

    <script>
    </script>
</head>

<body>

<div id="wrapper">

    <jsp:include page="../../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="common.codecd"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="row">
                        <label class="col-lg-2"><s:message code="common.classno"/></label>
                        <div class="col-lg-1"><c:out value="${codeInfo.classno}"/></div>
                    </div>
                    <div class="row">
                        <label class="col-lg-2"><s:message code="common.codecd"/></label>
                        <div class="col-lg-1"><c:out value="${codeInfo.codecd}"/></div>
                    </div>
                    <div class="row">
                        <label class="col-lg-2"><s:message code="common.codenm"/></label>
                        <div class="col-lg-5"><c:out value="${codeInfo.codenm}"/></div>
                    </div>
                </div>
            </div>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('adCodeList')"><s:message code="common.btnList"/></button>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('adCodeDelete?classno=<c:out value="${codeInfo.classno}"/>&codecd=<c:out value="${codeInfo.codecd}"/>', '<s:message code="common.btnDelete"/>')"><s:message code="common.btnDelete"/></button>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('adCodeForm?classno=<c:out value="${codeInfo.classno}"/>&codecd=<c:out value="${codeInfo.codecd}"/>')"><s:message code="common.btnUpdate"/></button>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/ad_footer.jsp" %>