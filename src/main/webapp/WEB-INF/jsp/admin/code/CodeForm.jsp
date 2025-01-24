<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/ad_header.jsp" %>

    <script>
        function fn_formSubmit() {
            if (!chkInputValue("#classno", "<s:message code="common.classno"/>")) return false;
            if (!chkInputValue("#codecd", "<s:message code="common.codecd"/>")) return false;
            if (!chkInputValue("#codenm", "<s:message code="common.codenm"/>")) return false;

            $("#form1").submit();
        }
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
            <form id="form1" name="form1" role="form" action="adCodeSave" method="post" onsubmit="return fn_formSubmit();">
                <input type="hidden" name="codeFormType" value="<c:out value="${codeFormType}"/>">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="common.classno"/></label>
                            <div class="col-lg-1">
                                <input type="text" class="form-control" id="classno" name="classno" maxlength="10"
                                       value="<c:out value="${codeInfo.classno}"/>" <c:out value="${readonly}"/> />
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="common.codecd"/></label>
                            <div class="col-lg-1">
                                <input type="text" class="form-control" id="codecd" name="codecd" maxlength="10"
                                       value="<c:out value="${codeInfo.codecd}"/>" <c:out value="${readonly}"/> />
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="common.codenm"/></label>
                            <div class="col-lg-5">
                                <input type="text" class="form-control" id="codenm" name="codenm" maxlength="30" value="<c:out value="${codeInfo.codenm}"/>">
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

<%@include file="../inc/ad_footer.jsp" %>