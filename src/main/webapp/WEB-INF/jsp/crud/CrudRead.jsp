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
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="crud.title"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <c:out value="${crudInfo.crtitle}"/> (<c:out value="${crudInfo.usernm}"/>)
                </div>
                <div class="panel-body">
                    <c:out value="${crudInfo.crmemo}"/>
                </div>
            </div>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('crudList')"><s:message code="common.btnList"/></button>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('crudDelete?crno=<c:out value="${crudInfo.crno}"/>', '<s:message code="common.btnDelete"/>')"><s:message code="common.btnDelete"/></button>
            <button class="btn btn-outline btn-primary" onclick="fn_moveToURL('crudForm?crno=<c:out value="${crudInfo.crno}"/>')"><s:message code="common.btnUpdate"/></button>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>