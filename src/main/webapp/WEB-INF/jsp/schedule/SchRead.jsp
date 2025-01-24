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
                <h1 class="page-header"><i class="fa fa-gear fa-fw"></i> 일정관리</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="row">
            <form id="form1" name="form1" role="form" action="schSave" method="post" onsubmit="return fn_formSubmit();">
                <input type="hidden" name="ssno" value="<c:out value="${schInfo.ssno}"/>">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row form-group">
                            <label class="col-lg-1">일정명</label>
                            <div class="col-lg-8"><c:out value="${schInfo.sstitle}"/></div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1">구분</label>
                            <div class="col-lg-8"><c:out value="${schInfo.sstype}"/></div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1">일시</label>
                            <div class="col-lg-8">
                                <c:out value="${schInfo.ssstartdate}"/> <c:out value="${schInfo.ssstarthour}"/>:<c:out value="${schInfo.ssstartminute}"/>
                                ~
                                <c:out value="${schInfo.ssenddate}"/> <c:out value="${schInfo.ssendhour}"/>:<c:out value="${schInfo.ssendminute}"/>
                            </div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1">반복</label>
                            <div class="col-lg-8"><c:out value="${schInfo.ssrepeattype}"/></div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1">공개</label>
                            <div class="col-lg-8"><c:out value="${schInfo.ssisopen}"/></div>
                        </div>
                        <div class="row form-group">
                            <label class="col-lg-1">내용</label>
                            <div class="col-lg-8"><c:out value="${schInfo.sscontents}"/></div>
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