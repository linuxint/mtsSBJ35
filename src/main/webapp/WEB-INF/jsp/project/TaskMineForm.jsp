<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>

        function fn_formSubmit() {
            if (!chkInputValue("#tsrate", "<s:message code="project.rate"/>")) return false;

            $("#form1").submit();
        }
    </script>

</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="common.codecd"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="taskMineSave" method="post" enctype="multipart/form-data" onsubmit="return fn_formSubmit();">
                <input type="hidden" name="prno" value="<c:out value="${taskInfo.prno}"/>">
                <input type="hidden" name="tsno" value="<c:out value="${taskInfo.tsno}"/>">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="project.task"/></label>
                            <div class="col-lg-5">
                                <c:out value="${taskInfo.tstitle}"/>
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="project.rate"/></label>
                            <div class="col-lg-5">
                                <input type="text" class="form-control" id="tsrate" name="tsrate" maxlength="10"
                                       value="<c:out value="${taskInfo.tsrate}"/>">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="project.attach"/></label>
                            <div class="col-lg-5">
                                <c:forEach var="listview" items="${listview}" varStatus="status">
                                    <input type="checkbox" name="fileno" value="<c:out value="${listview.fileno}"/>">
                                    <a href="fileDownload?filename=<c:out value="${listview.filename}"/>&downname=<c:out value="${listview.realname }"/>">
                                        <c:out value="${listview.filename}"/></a> <c:out value="${listview.size2String()}"/><br/>
                                </c:forEach>
                                <input type="file" id="uploadfile" name="uploadfile">
                            </div>
                        </div>
                    </div>
                </div>
                <button class="btn btn-outline btn-primary"><s:message code="common.btnSave"/></button>
            </form>

        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>