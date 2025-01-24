<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="../inc/header.jsp" %>
    <script>
        window.onload = function () {
            Morris.Bar({
                element: 'morris-bar-chart',
                data: [
                    <c:forEach var="listview" items="${listview}" varStatus="status">
                    {x: '<c:out value="${listview.field1}"/>', y: <c:out value="${listview.cnt1}"/>}<c:if test="${!status.last}">, </c:if>
                    </c:forEach>
                ],
                xkey: 'x',
                ykeys: 'y',
                labels: 'Count',
                resize: true
            });
            Morris.Donut({
                element: 'morris-donut-chart',
                data: [
                    <c:forEach var="listview" items="${listview}" varStatus="status">
                    {label: "<c:out value="${listview.field1}"/>", value: <c:out value="${listview.cnt1}"/>}<c:if test="${!status.last}">, </c:if>
                    </c:forEach>
                ],
                resize: true
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
                <h1 class="page-header">샘플 3: 챠트</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <div class="col-lg-5">
                <div id="morris-bar-chart"></div>

                <button class="btn btn-default" type="button" onclick="fn_showCode('#popupCodeBar')" title="코드 보기">
                    <i class="fa fa-code"></i> 코드 보기
                </button>
            </div>

            <div class="col-lg-5">
                <div id="morris-donut-chart"></div>

                <button class="btn btn-default" type="button" onclick="fn_showCode('#popupCodeDonut')" title="코드 보기">
                    <i class="fa fa-code"></i> 코드 보기
                </button>
            </div>
        </div>
        <!-- /.row -->
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
    </div>
    <!-- /#page-wrapper -->

</div>
<!-- /#wrapper -->

<div id="popupCodeBar" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
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
                        <pre style="height:200px">
    &lt;link href="css/sb-admin/morris.css" rel="stylesheet"&gt;
    &lt;script src="css/sb-admin/raphael-min.js"&gt;&lt;/script&gt;    
    &lt;script src="css/sb-admin/morris.min.js"&gt;&lt;/script&gt;    
&lt;script&gt;
window.onload = function() {
    Morris.Bar({
        element: 'morris-bar-chart',
        data: [
                {x: '자료실', y: 19},
                {x: 'QnA', y: 25},
                {x: '일반게시판', y: 51}
              ],
        xkey: 'x',
        ykeys: 'y',
        labels: 'Count',
        resize: true
    });
}
&lt;/script&gt;                       
                        </pre>
                    <pre>
  &lt;div id="morris-bar-chart"&gt;&lt;/div&gt;
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

<div id="popupCodeDonut" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
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
                        <pre style="height:200px">
    &lt;link href="css/sb-admin/morris.css" rel="stylesheet"&gt;
    &lt;script src="css/sb-admin/raphael-min.js"&gt;&lt;/script&gt;
    &lt;script src="css/sb-admin/morris.min.js"&gt;&lt;/script&gt;    
&lt;script&gt;
window.onload = function() {
    Morris.Donut({
        element: 'morris-donut-chart',
        data: [
                {label: '자료실', value: 19},
                {label: 'QnA', value: 25},
                {label: '일반게시판', value: 51}
              ],
        resize: true
    });
}
&lt;/script&gt;                       
                        </pre>
                    <pre>
  &lt;div id="morris-donut-chart"&gt;&lt;/div&gt;
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