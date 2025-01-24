<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        window.onload = function () {
            $('#term1').datepicker().on('changeDate', function (ev) {
                if (ev.viewMode == "days") {
                    $('#term1').datepicker('hide');
                }
            });
            $('#term2').datepicker().on('changeDate', function (ev) {
                if (ev.viewMode == "days") {
                    $('#term2').datepicker('hide');
                }
            });
        }

        ///////////////////////
        function fn_showCode(id) {
            $(id).modal("show");
        }
    </script>
</head>

<body>

<div id="wrapper">

    <jsp:include page="../common/navigation.jsp"/>

    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">샘플 2: 날짜 선택</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-1">
                기간 선택
            </div>
            <div class="col-lg-2">
                <input class="form-control" size="16" id="term1" type="text" value="<c:out value="${today}"/>" readonly>
            </div>
            <div class="col-lg-2">
                <input class="form-control" size="16" id="term2" type="text" value="<c:out value="${today}"/>" readonly>
            </div>
            <div class="col-lg-1">
                <button class="btn btn-default" type="button" onclick="fn_showCode('#popupCode')" title="코드 보기">
                    <i class="fa fa-code"></i> 코드 보기
                </button>
            </div>
        </div>
        <!-- /.row -->
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->

<div id="popupCode" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="mySmallModalLabel">필요 코드</h4>
            </div>
            <div class="modal-body">
                <!-- /.row -->
                <div class="row">
                        <pre style="height:150px">
    &lt;link href="js/datepicker/datepicker.css" rel="stylesheet" type="text/css"&gt;
    &lt;script src="js/datepicker/bootstrap-datepicker.js"&gt;&lt;/script&gt;
&lt;script&gt;
window.onload = function() {
    $('#term1').datepicker().on('changeDate', function(ev) {
        if (ev.viewMode=="days"){
            $('#term1').datepicker('hide');
        }
    });
    $('#term2').datepicker().on('changeDate', function(ev) {
        if (ev.viewMode=="days"){
            $('#term2').datepicker('hide');
        }
    });
}
&lt;/script&gt;                             
                        </pre>
                    <pre>
    &lt;div class="col-lg-2"&gt;
        &lt;input class="form-control" size="16" id="term1" type="text" value="&lt;c:out value="${today}"/&gt;" readonly&gt;
    &lt;/div&gt;
    &lt;div class="col-lg-2"&gt;
        &lt;input class="form-control" size="16" id="term2" type="text" value="&lt;c:out value="${today}"/&gt;" readonly&gt;
    &lt;/div&gt;      
                        </pre>
                </div>
                <!-- /.row -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" id="close"><s:message code="common.btnClose"/></button>
            </div>
        </div>
    </div>
</div>
</body>

<%@include file="../inc/footer.jsp" %>