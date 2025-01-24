<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>

    <script>
        window.onload = function () {
            $('#prstartdate').datepicker().on('changeDate', function (ev) {
                if (ev.viewMode == "days") {
                    $('#prstartdate').datepicker('hide');
                }
            });
            $('#prenddate').datepicker().on('changeDate', function (ev) {
                if (ev.viewMode == "days") {
                    $('#prenddate').datepicker('hide');
                }
            });
        }

        function fn_formSubmit() {

            if (!chkInputValue("#prtitle", "<s:message code="project.name"/>")) return false;

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
                <h1 class="page-header"><i class="fa fa-files-o fa-fw"></i> <s:message code="project.title"/></h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="projectSave" method="post" onsubmit="return fn_formSubmit();">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="project.name"/></label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" id="prtitle" name="prtitle" size="70" maxlength="100" value="<c:out value="${projectInfo.prtitle}"/>">
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="project.term"/></label>
                            <div class="col-lg-2">
                                <input class="form-control" size="16" id="prstartdate" name="prstartdate" type="text" value="<c:out value="${projectInfo.prstartdate}"/>" readonly>
                            </div>
                            <div class="col-lg-2">
                                <input class="form-control" size="16" id="prenddate" name="prenddate" type="text" value="<c:out value="${projectInfo.prenddate}"/>" readonly>
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-2"><s:message code="project.status"/></label>
                            <div class="col-lg-10 form-group">
                                <label class="radio-inline"><input type="radio" name="prstatus" value="0"
                                                                   <c:if test="${projectInfo.prstatus==0}">checked</c:if>><s:message code="project.status0"/></label>
                                <label class="radio-inline"><input type="radio" name="prstatus" value="1"
                                                                   <c:if test="${projectInfo.prstatus==1}">checked</c:if>><s:message code="project.status1"/></label>

                            </div>
                        </div>
                    </div>
                </div>
                <button class="btn btn-outline btn-primary"><i class="fa fa-save fa-fw"></i><s:message code="common.btnSave"/></button>
                <c:if test="${projectInfo.prno!=null}">
                    <button type="button" class="btn btn-default" onclick="fn_moveToURL('projectDelete?prno=<c:out value="${projectInfo.prno}"/>')"><i class="fa fa-minus fa-fw"></i><s:message code="common.btnDelete"/></button>
                </c:if>
                <input type="hidden" name="prno" value="<c:out value="${projectInfo.prno}"/>">
            </form>

        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->
</body>

<%@include file="../inc/footer.jsp" %>