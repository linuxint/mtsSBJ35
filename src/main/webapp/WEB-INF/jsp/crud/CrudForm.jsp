<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>

        function fn_formSubmit() {
            if (!chkInputValue("#crtitle", "<s:message code="crud.crtitle"/>")) return false;
            if (!chkInputValue("#crmemo", "<s:message code="crud.crmemo"/>")) return false;

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
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> <s:message code="crud.title"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="crudSave" method="post" onsubmit="return fn_formSubmit();">
                <input type="hidden" name="crno" value="<c:out value="${crudInfo.crno}"/>">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="crud.crtitle"/></label>
                            <div class="col-lg-8">
                                <input type="text" class="form-control" id="crtitle" name="crtitle" maxlength="255"
                                       value="<c:out value="${crudInfo.crtitle}"/>">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="crud.crmemo"/></label>
                            <div class="col-lg-8">
                                <textarea class="form-control" id="crmemo" name="crmemo"><c:out value="${crudInfo.crmemo}"/></textarea>
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