<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        function fn_formSubmit() {
            document.form1.submit();
        }
    </script>
</head>
<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-bell fa-fw"></i><s:message code="board.file"/><s:message code="board.locate"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <c:forEach var="listview" items="${listview}" varStatus="status">
                    <c:out value="${listview.filename}"/>
                    <c:out value="${listview.realname}"/>
                    <c:out value="${listview.filesize}"/> Kbyte
                    <c:out value="${listview.uri}"/><br>
                </c:forEach>
                <br/>

            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>